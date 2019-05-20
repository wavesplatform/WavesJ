package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.json.WavesJsonMapper;
import com.wavesplatform.wavesj.transactions.InvokeScriptTransaction.FunctionalArg;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.wavesplatform.wavesj.Asset.toWavelets;

public class InvokeScriptTransactionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvokeScriptTransaction.class);

    private static final String FUNC = "deposit";
    private static final long FEE = Asset.toWavelets(0.005);
    private static final Long ARG1 = 1L;
    private static final String ARG2 = "Arg2";
    private static final Boolean ARG3 = true;
    private static final Long PAYMENT_AMT = toWavelets(10);

    private byte chainId = Account.TESTNET;
    private WavesJsonMapper mapper = new WavesJsonMapper(chainId);

    @Test
    public void testJsonFormat() throws Exception {
        PrivateKeyAccount sender = generateAcc("Sender");
        PrivateKeyAccount recipient = generateAcc("Recipient");
        InvokeScriptTransaction tx = new InvokeScriptTransaction(chainId, sender, recipient.getAddress(),
                FUNC, FEE, null, System.currentTimeMillis())
            .withArg(ARG1)
            .withArg(ARG2)
            .withArg(ARG3)
            .withPayment(PAYMENT_AMT, null)
            .sign(sender);

        String txJson = mapper.writeValueAsString(tx);
        Transaction deserTx = mapper.readValue(txJson, Transaction.class);
        Assert.assertNotNull(deserTx);

        InvokeScriptTransaction invTx = (InvokeScriptTransaction) tx;
        Assert.assertEquals(FEE, invTx.getFee());
        Assert.assertEquals(sender.getAddress(), invTx.getSenderPublicKey().getAddress());
        Assert.assertEquals(1, invTx.getPayments().size());
        Assert.assertEquals(PAYMENT_AMT.longValue(), invTx.getPayments().get(0).getAmount());
        Assert.assertNotNull(invTx.getCall());
        Assert.assertEquals(FUNC, invTx.getCall().getName());
        List<FunctionalArg<?>> args = invTx.getCall().getArgs();
        Assert.assertTrue(ARG1.equals(args.get(0).getValue()));
        Assert.assertTrue(ARG2.equals(args.get(1).getValue()));
        Assert.assertTrue(ARG3.equals(args.get(2).getValue()));
    }

    private PrivateKeyAccount generateAcc(String name) {
        String seed = PrivateKeyAccount.generateSeed();
        PrivateKeyAccount acc = PrivateKeyAccount.fromSeed(seed, 0, chainId);
        LOGGER.info("{} generated: address={} public={} private={} seed={}",
                name, acc.getAddress(), Base58.encode(acc.getPublicKey()), Base58.encode(acc.getPrivateKey()), seed);
        return acc;
    }
}
