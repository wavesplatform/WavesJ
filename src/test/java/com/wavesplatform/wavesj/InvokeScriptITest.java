package com.wavesplatform.wavesj;

import com.wavesplatform.wavesj.json.WavesJsonMapper;
import com.wavesplatform.wavesj.transactions.InvokeScriptTransaction;
import com.wavesplatform.wavesj.transactions.InvokeScriptTransaction.FunctionalArg;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;

import static com.wavesplatform.wavesj.Asset.fromWavelets;
import static com.wavesplatform.wavesj.Asset.toWavelets;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

@FixMethodOrder(NAME_ASCENDING)
public class InvokeScriptITest extends BaseITest {

    private static final String INV1_FUNC = "deposit";
    private static final String INV_DEFAULT_FUNC = null;
    private static final String INV2_FUNC = "withdraw";
    private static final long INV1_PAYMENT = toWavelets(2);
    private static final long INV_DEFAULT_PAYMENT = toWavelets(2);
    private static final long INV2_ARG1 = INV1_PAYMENT / 2;
    private static final boolean INV2_ARG2 = true;
    private static final String INV2_ARG3 = "ABC АБВ";
    private static final ByteString INV2_ARG4;
    private static final String PLACEHOLDER_DONATION_ADDRESS = "$donationAddressInjection";

    private static PrivateKeyAccount smartAcc;
    private static PrivateKeyAccount donationAcc;
    private static PrivateKeyAccount investorAcc;

    static {
        try {
            INV2_ARG4 = new ByteString(Base58.encode("Hello, Ride4DApp!".getBytes("UTF-8")));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("UTF-8 is unsupported...", ex);
        }
    }

    private static long investorBalance = 0;
    private static long smartBalance = 0;

    private static String inv1Id;
    private static String inv2Id;
    private static WavesJsonMapper mapper = new WavesJsonMapper(Config.getChainId());

    @Before
    public void init() throws Exception {
        if (smartAcc == null) {
            smartAcc = generateAcc("Smart", true);
            investorAcc = generateAcc("Investor", true);
            donationAcc = generateAcc("Donation", true);

            stageStart("#00", "initialization");
            // 0.1 transfer 5 Waves to investor's account
            investorBalance = transfer(investorAcc.getAddress(), toWavelets(5), toWavelets(0.001));

            // 0.2 transfer 1 Waves to smart account
            smartBalance = transfer(smartAcc.getAddress(), toWavelets(1), toWavelets(0.001));
            stageDone("#00", "initialization");
        }
    }

    @Test
    public void t005_deployDApp() throws Exception {
        stageStart("#01", "deploying dApp script");
        String script = IOUtils.toString(InvokeScriptITest.class.getResourceAsStream("/ride4dapp/wallet.ride"));
        script = script.replace(PLACEHOLDER_DONATION_ADDRESS, donationAcc.getAddress());
        Assert.assertNotNull("script should be not null", script);

        long setScriptFee = toWavelets(0.01);
        LOGGER.info(level2() + "setting script fee={}", fromWavelets(setScriptFee));
        node.setScript(smartAcc, script, chainId, setScriptFee);
        smartBalance = waitOnBalance(smartAcc.getAddress(), smartBalance, -1 * setScriptFee, EQUALS, DEFAULT_TIMEOUT);
        stageDone("#01", "deploying dApp script");
    }

    /**
     * Invest 2 Waves - call deposit function
     */
    @Test
    public void t010_invokeFuncWithoutArgs() throws Exception {
        Thread.sleep(5 * 1000);
        stageStart("#02", "invoke deposit function without args (invest 2 Waves)");
        InvokeScriptTransaction deposit = createInvoke(INV1_FUNC, toWavelets(0.005), INV1_PAYMENT);
        inv1Id = sendInv(deposit);
        stageDone("#02", "invoke deposit function without args (invest 2 Waves)");
    }

    /**
     * Invest 2 Waves - call default function
     */
    @Test
    public void t015_invokeDefaultFunc() throws Exception {
        stageStart("#03", "invoke @Default function (invest 2 Waves)");
        InvokeScriptTransaction defaultInv = createInvoke(INV_DEFAULT_FUNC, toWavelets(0.005), INV_DEFAULT_PAYMENT);
        sendInv(defaultInv);
        stageDone("#03", "invoke @Default function (invest 2 Waves)");
    }

    @Test
    public void t020_invokeFuncWithAllTypesOfArgs() throws Exception {
        stageStart("#04", "invoke withdraw with all types of args");
        InvokeScriptTransaction withdrawTx = createInvoke(INV2_FUNC, toWavelets(0.005), 0);
        withdrawTx = withdrawTx
                .withArg(INV2_ARG1)
                .withArg(INV2_ARG2)
                .withArg(INV2_ARG3)
                .withArg(INV2_ARG4)
                .sign(investorAcc);
        inv2Id = sendInv(withdrawTx);
        stageDone("#04", "invoke withdraw with all types of args");
    }

    @Test
    public void t025_readInvokeNoArgs() throws IOException {
        //inv1Id = "FDAsteLn2oMvyZJsxCQX9Fy1DkRXdgss4t7VbuAEkA5V";
        InvokeScriptTransaction inv1Tx = readInvocationAndVerifyDefaults(inv1Id);
        Assert.assertEquals(investorAcc.getAddress(), inv1Tx.getSenderPublicKey().getAddress());
        Assert.assertEquals("Function name is valid", INV1_FUNC, inv1Tx.getCall().getName());
        Assert.assertEquals("Function args count is valid", 0, inv1Tx.getCall().getArgs().size());
        Assert.assertEquals("Payments count is valid", 1, inv1Tx.getPayments().size());
        Assert.assertEquals("Payment amount is valid", INV1_PAYMENT, inv1Tx.getPayments().get(0).getAmount());
    }

    @Test
    public void t030_readInvokeWithAllTypesOfArgs() throws IOException {
        //inv2Id = "4u5SPZobCkp1BVxBYTGHwF93AaqxXA3kapYJfgkzhhkS";
        InvokeScriptTransaction inv2Tx = readInvocationAndVerifyDefaults(inv2Id);
        Assert.assertEquals(investorAcc.getAddress(), inv2Tx.getSenderPublicKey().getAddress());
        Assert.assertEquals("Function name is valid", INV2_FUNC, inv2Tx.getCall().getName());

        List<FunctionalArg<?>> args = inv2Tx.getCall().getArgs();
        Assert.assertEquals("Function args count is valid", 4, args.size());
        assertFuncArg(args.get(0), "integer", INV2_ARG1);
        assertFuncArg(args.get(1), "boolean", INV2_ARG2);
        assertFuncArg(args.get(2), "string", INV2_ARG3);
        assertFuncArg(args.get(3), "binary", INV2_ARG4);
        Assert.assertEquals("Payments count is valid", 0, inv2Tx.getPayments().size());
    }

    private InvokeScriptTransaction readInvocationAndVerifyDefaults(String invId) throws IOException {
        Transaction inv = node.getTransaction(invId);
        assertInvocationTypeValid(inv);

        InvokeScriptTransaction invTx = (InvokeScriptTransaction) inv;
        assertInvocationFieldsNotNull(invTx);
        return invTx;
    }

    private static void assertInvocationTypeValid(Transaction inv) throws IOException {
        LOGGER.info(mapper.writeValueAsString(inv));
        Assert.assertTrue("Json deserialization should return ContractInvocationTransaction instance",
                inv.getClass().isAssignableFrom(InvokeScriptTransaction.class));
    }

    private static void assertInvocationFieldsNotNull(InvokeScriptTransaction invTx) {
        Assert.assertNotNull(invTx.getSenderPublicKey());
        Assert.assertNotNull(invTx.getdApp());
        Assert.assertNotNull(invTx.getProofs());
        Assert.assertNotNull(invTx.getCall());
        Assert.assertNotNull(invTx.getPayments());
    }

    private static <T> void assertFuncArg(FunctionalArg<?> arg, String expectedType, T expectedValue) {
        Assert.assertEquals("Arg type is valid", expectedType, arg.getType());
        Assert.assertTrue("Arg value is valid", expectedValue.equals(arg.getValue()));
    }

    private InvokeScriptTransaction createInvoke(String func, long fee, long payment) throws Exception {
        InvokeScriptTransaction inv =
                new InvokeScriptTransaction(chainId, investorAcc, smartAcc.getAddress(), func,
                        fee, null, System.currentTimeMillis());
        if (payment > 0) {
            inv = inv.withPayment(payment, null);
        }
        inv = inv.sign(investorAcc);
        LOGGER.info(level2() + "Invocation created - func={} invFee={} attachPayment={}",
                func, fromWavelets(fee), payment > 0 ? fromWavelets(payment) : "NONE");
        return inv;
    }

    private String sendInv(InvokeScriptTransaction inv) throws Exception {
        String txId = node.send(inv);
        if (inv.getPayments().size() > 0) {
            smartBalance = waitOnBalance(smartAcc.getAddress(), smartBalance, inv.getPayments().get(0).getAmount(), EQUALS, DEFAULT_TIMEOUT);
        } else {
            Thread.sleep(20 * 1000);
        }
        LOGGER.info(level2() + "Invocation sent - txId={}", txId);
        return txId;
    }

    private long transfer(String toAddress, long amount, long fee) throws Exception {
        LOGGER.info(level2() + "transferring funds to address={} amount={} transfer_fee={}",
                toAddress, fromWavelets(amount), fromWavelets(fee));
        long balance = toWavelets(getBalance(toAddress));
        node.transfer(benzAcc, toAddress, amount, fee, "");
        return waitOnBalance(toAddress, balance, amount, EQUALS, DEFAULT_TIMEOUT);
    }

    private static String level1() {
        return "\t";
    }

    private static String level2() {
        return "\t\t";
    }

    private String toStartStage(String stage) {
        return stage + " START";
    }

    private String toDoneStage(String stage) {
        return stage + " DONE";
    }

    private void stageStart(String stage, String description) throws Exception {
        LOGGER.info(level1() + "{}: {} - benz_balance={} smart_balance={} investor_balance={}",
                toStartStage(stage), description, getBalance(benzAcc), getBalance(smartAcc), getBalance(investorAcc));
    }

    private void stageDone(String stage, String description) throws Exception {
        LOGGER.info(level1() + "{}: {} - benz_balance={} smart_balance={} investor_balance={}",
                toDoneStage(stage), description, getBalance(benzAcc), getBalance(smartAcc), getBalance(investorAcc));
    }

    private BigDecimal getBalance(PrivateKeyAccount acc) throws IOException {
        return getBalance(acc.getAddress());
    }

    private BigDecimal getBalance(String acc) throws IOException {
        return fromWavelets(node.getBalance(acc));
    }
}