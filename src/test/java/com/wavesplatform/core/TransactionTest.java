package com.wavesplatform.core;

import org.junit.Test;

public class TransactionTest {
    private static final long AMOUNT =     100_000;
    private static final long FEE    = 100_000_000;

    @Test
    public void smokeTest() {
        Account acc = new Account("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t");
        Address recipient = new Address("3N9gDFq8tKFhBDBTQxR3zqvtpXjw5wW3syA");
        byte[] assetId = "AssetIDAssetIDAssetIDAssetIDAsse".getBytes();
        byte[] txId = "TxIDTxIDTxIDTxIDTxIDTxIDTxIDTxID".getBytes();

        dump("alias",
                Transaction.makeAliasTx(acc, new Alias("daphnie"), FEE));
        dump("burn",
                Transaction.makeBurnTx(acc, assetId, AMOUNT, FEE));
        dump("issue",
                Transaction.makeIssueTx(acc, "Pure Gold", "Gold backed asset", AMOUNT, 8, true, FEE));
        dump("reissue",
                Transaction.makeReissueTx(acc, assetId, AMOUNT, false, FEE));
        dump("lease",
                Transaction.makeLeaseTx(acc, recipient, AMOUNT, FEE));
        dump("lease cancel",
                Transaction.makeLeaseCancelTx(acc, txId, FEE));
        dump("xfer",
                Transaction.makeTransferTx(acc, recipient, AMOUNT, null, FEE, null, "Shut up & take my money"));
    }

    private void dump(String header, Transaction tx) {
        System.out.println("*** " + header + " ***");
        tx.getData().forEach((k, v) -> System.out.printf("%s: %s\n", k, v));
        System.out.println("Encoded: " + tx.getEncodedBytes());
        System.out.println();
    }
}
