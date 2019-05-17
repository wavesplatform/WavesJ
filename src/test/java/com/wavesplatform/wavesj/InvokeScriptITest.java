package com.wavesplatform.wavesj;

import com.wavesplatform.wavesj.json.WavesJsonMapper;
import com.wavesplatform.wavesj.transactions.InvokeScriptTransaction;
import com.wavesplatform.wavesj.transactions.InvokeScriptTransaction.FunctionalArg;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
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
    private static final String INV2_FUNC = "withdraw";
    private static final long INV1_PAYMENT = toWavelets(1);
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

    private static String inv1Id;
    private static String inv2Id;
    private static WavesJsonMapper mapper = new WavesJsonMapper(Config.getChainId());

    @Test
    public void t001_invocationCall() throws Exception {
        smartAcc = generateAcc("Smart1", true);
        investorAcc = generateAcc("Investor1", true);
        donationAcc = generateAcc("Donation1", true);

        String script = IOUtils.toString(InvokeScriptITest.class.getResourceAsStream("/ride4dapp/wallet.ride"));
        script = script.replace(PLACEHOLDER_DONATION_ADDRESS, donationAcc.getAddress());
        Assert.assertNotNull("script should be not null", script);
        LOGGER.info("t001_invocationCall STARTED: benz_balance={}", getBalance(benzAcc));
        long transferFee = toWavelets(0.001);

        // 1. transfer 3 Waves to investor's account
        long investorInitAmt = toWavelets(3);
        long investorBalance = 0;
        LOGGER.info("\t#00 START: transferring funds to investor - benz_balance={} smart_balance={} investor_balance={} " +
                        "investorInitAmt={} investor_address={} transfer_fee={}",
                getBalance(benzAcc), getBalance(smartAcc), getBalance(investorAcc), fromWavelets(investorInitAmt),
                investorAcc.getAddress(), fromWavelets(transferFee));
        node.transfer(benzAcc, investorAcc.getAddress(), investorInitAmt, transferFee, "");
        waitOnBalance(investorAcc.getAddress(), investorBalance, investorInitAmt, EQUALS, DEFAULT_TIMEOUT);
        LOGGER.info("\t#00 DONE: benz_balance={} smart_balance={} investor_balance={}",
                getBalance(benzAcc), getBalance(smartAcc), getBalance(investorAcc));

        // 2. deploy dApp
        // 2.1 transfer initial amount to smart
        long smartBalance = 0;
        long smartInitAmt = toWavelets(0.02);
        LOGGER.info("\t#01 START: transferring funds to smart - benz_balance={} transfer_amount={} transfer_fee={} smart_address={}",
                getBalance(benzAcc), fromWavelets(smartInitAmt), fromWavelets(transferFee), smartAcc.getAddress());
        String txId = node.transfer(benzAcc, smartAcc.getAddress(), smartInitAmt, transferFee, "");
        smartBalance = waitOnBalance(smartAcc.getAddress(), smartBalance, smartInitAmt, EQUALS, DEFAULT_TIMEOUT);
        LOGGER.info("\t#01 DONE: benz_balance={} smart_balance={} investor_balance={} txId={}",
                getBalance(benzAcc), getBalance(smartAcc), getBalance(investorAcc), txId);

        // 2.2 deploy script
        long setScriptFee = toWavelets(0.01);
        LOGGER.info("\t#02 START: setting script - benz_balance={} smart_balance={} script_fee={}",
                getBalance(benzAcc), getBalance(smartAcc), fromWavelets(setScriptFee));
        txId = node.setScript(smartAcc, script, chainId, setScriptFee);
        smartBalance = waitOnBalance(smartAcc.getAddress(), smartBalance, -1 * setScriptFee, EQUALS, DEFAULT_TIMEOUT);
        LOGGER.info("\t#02 DONE: benz_balance={} smart_balance={} investor_balance={}",
                getBalance(benzAcc), getBalance(smartAcc), getBalance(investorAcc));

        // 3. invest 2 Waves
        // (invoke function without arguments)
        long inv1Fee = toWavelets(0.005);

        Thread.sleep(5 * 1000);
        LOGGER.info("\t#03 START: invoke deposit - benz_balance={} smart_balance={} investor_balance={} invokerAddress={} invFunc={} invFee={} invAttachPayment={}",
                getBalance(benzAcc), getBalance(smartAcc), getBalance(investorAcc), benzAcc.getAddress(), INV1_FUNC, fromWavelets(inv1Fee), fromWavelets(INV1_PAYMENT));
        InvokeScriptTransaction depositTx =
                new InvokeScriptTransaction(chainId, investorAcc, smartAcc.getAddress(), INV1_FUNC,
                        inv1Fee, Asset.WAVES, System.currentTimeMillis())
                        .withPayment(INV1_PAYMENT, null)
                        .sign(investorAcc);

        inv1Id = txId = node.send(depositTx);


        smartBalance = waitOnBalance(smartAcc.getAddress(), smartBalance, INV1_PAYMENT, EQUALS, DEFAULT_TIMEOUT);
        LOGGER.info("\t#03 DONE: benz_balance={} smart_balance={} investor_balance={} txId={}",
                getBalance(benzAcc), getBalance(smartAcc), getBalance(investorAcc), txId);

        // 4. withdraw investments and donate some funds
        // (invoke function with arguments)
        long inv2Fee = toWavelets(0.005);
        LOGGER.info("\t#04 START: invoke withdraw - benz_balance={} smart_balance={} investor_balance={} invokerAddress={} invFunc={} invFee={} invAttachPayment={}",
                getBalance(benzAcc), getBalance(smartAcc), getBalance(investorAcc), benzAcc.getAddress(),
                INV2_FUNC, fromWavelets(inv2Fee), fromWavelets(INV1_PAYMENT));


        InvokeScriptTransaction withdrawTx =
                new InvokeScriptTransaction(chainId, investorAcc, smartAcc.getAddress(), INV2_FUNC,
                        inv2Fee, null, System.currentTimeMillis())
                        .withArg(INV2_ARG1)
                        .withArg(INV2_ARG2)
                        .withArg(INV2_ARG3)
                        .withArg(INV2_ARG4)
                        .withArg(Asset.WAVES)
                        .sign(investorAcc);

        inv2Id = txId = node.send(withdrawTx);
        smartBalance = waitOnBalance(smartAcc.getAddress(), smartBalance, -1 * INV1_PAYMENT, EQUALS, DEFAULT_TIMEOUT);
        LOGGER.info("\t#04 DONE: benz_balance={} smart_balance={} investor_balance={} txId={}",
                getBalance(benzAcc), getBalance(smartAcc), getBalance(investorAcc), txId);
        LOGGER.info("t001_invocationCall FINISHED");
    }

    @Test
    public void t005_readInvocationInfo() throws IOException {
        //inv1Id = "FDAsteLn2oMvyZJsxCQX9Fy1DkRXdgss4t7VbuAEkA5V";
        InvokeScriptTransaction inv1Tx = readInvocationAndVerifyDefaults(inv1Id);
        Assert.assertEquals(investorAcc.getAddress(), inv1Tx.getSenderPublicKey().getAddress());
        Assert.assertEquals("Function name is valid", INV1_FUNC, inv1Tx.getCall().getName());
        Assert.assertEquals("Function args count is valid", 0, inv1Tx.getCall().getArgs().size());
        Assert.assertEquals("Payments count is valid", 1, inv1Tx.getPayments().size());
        Assert.assertEquals("Payment amount is valid", INV1_PAYMENT, inv1Tx.getPayments().get(0).getAmount());

        //inv2Id = "4u5SPZobCkp1BVxBYTGHwF93AaqxXA3kapYJfgkzhhkS";
        InvokeScriptTransaction inv2Tx = readInvocationAndVerifyDefaults(inv2Id);
        Assert.assertEquals(investorAcc.getAddress(), inv2Tx.getSenderPublicKey().getAddress());
        Assert.assertEquals("Function name is valid", INV2_FUNC, inv2Tx.getCall().getName());

        List<FunctionalArg<?>> args = inv2Tx.getCall().getArgs();
        Assert.assertEquals("Function args count is valid", 5, args.size());
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
        Assert.assertTrue("Json deserialization should return InvokeScriptTransaction instance",
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

    private BigDecimal getBalance(PrivateKeyAccount acc) throws IOException {
        return fromWavelets(node.getBalance(acc.getAddress()));
    }
}