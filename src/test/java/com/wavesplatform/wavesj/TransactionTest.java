package com.wavesplatform.wavesj;

import org.junit.Test;

public class TransactionTest {
    private static final long AMOUNT = 1_00000000L;
    private static final long FEE = 100_000;

    @Test
    public void smokeTest() {
        // doesn't validate transactions, just checks that all methods run to completion, no buffer overflows occur etc
        PrivateKeyAccount acc = PrivateKeyAccount.fromPrivateKey("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t", Account.TESTNET);
        String recipient = "3N9gDFq8tKFhBDBTQxR3zqvtpXjw5wW3syA";
        String assetId = "AssetAssetAssetAssetAssetAssetAs";
        String txId = "TransactionTransactionTransactio";

        Transaction.makeAliasTx(acc, "daphnie", Account.TESTNET, FEE);
        Transaction.makeBurnTx(acc, assetId, AMOUNT, FEE);
        Transaction.makeIssueTx(acc, "Pure Gold", "Gold backed asset", AMOUNT, 8, true, FEE);
        Transaction.makeReissueTx(acc, assetId, AMOUNT, false, FEE);
        Transaction.makeLeaseTx(acc, recipient, AMOUNT, FEE);
        Transaction.makeLeaseCancelTx(acc, txId, FEE);
        Transaction.makeTransferTx(acc, recipient, AMOUNT, null, FEE, null, "Shut up & take my money");
    }
}
