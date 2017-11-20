package com.wavesplatform.wavesj;

import org.junit.Test;

public class TransactionTest {
    private static final long AMOUNT = 1_00000000L;
    private static final long FEE = 100_000;

    @Test
    public void smokeTest() {
        // doesn't validate transactions, just checks that all methods run to completion, no buffer overflows occur etc
        PrivateKeyAccount acc = new PrivateKeyAccount("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t", 'T');
        String recipient = "3N9gDFq8tKFhBDBTQxR3zqvtpXjw5wW3syA";
        String assetId = "AssetAssetAssetAssetAssetAssetAs";
        String txId = "TransactionTransactionTransactio";

        dump("alias",
                Transaction.makeAliasTx(acc, "daphnie", FEE));
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
        System.out.println("Tx data: " + tx.getJson());
        System.out.println();
    }
}
