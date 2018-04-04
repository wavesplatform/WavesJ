package com.wavesplatform.wavesj;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TransactionTest {
    private static final long AMOUNT = 1_00000000L;
    private static final long FEE = 100_000;

    @Test
    public void test() {
        // doesn't validate transactions, just checks that all methods run to completion, no buffer overflows occur etc
        PrivateKeyAccount acc = PrivateKeyAccount.fromPrivateKey("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t", Account.TESTNET);
        String recipient = "3N9gDFq8tKFhBDBTQxR3zqvtpXjw5wW3syA";
        String assetId = "AssetAssetAssetAssetAssetAssetAs";
        String txId = "TransactionTransactionTransactio";

        check(Transaction.makeAliasTx(acc, "daphnie", Account.TESTNET, FEE));
        check(Transaction.makeBurnTx(acc, assetId, AMOUNT, FEE));
        check(Transaction.makeIssueTx(acc, "Pure Gold", "Gold backed asset", AMOUNT, 8, true, FEE));
        check(Transaction.makeReissueTx(acc, assetId, AMOUNT, false, FEE));
        check(Transaction.makeLeaseTx(acc, recipient, AMOUNT, FEE));
        check(Transaction.makeLeaseCancelTx(acc, txId, FEE));
        check(Transaction.makeTransferTx(acc, recipient, AMOUNT, null, FEE, null, "Shut up & take my money"));

        List<Transfer> transfers = Arrays.asList(new Transfer(acc.getAddress(), AMOUNT), new Transfer(recipient, AMOUNT));
        check(Transaction.makeMassTransferTx(acc, Asset.WAVES, transfers, FEE, "mass transfer"));

        List<DataEntry<?>> data = Arrays.asList(
                new DataEntry.BooleanEntry("\u05D5\u05EA\u05D9\u05D9\u05E8\u05D5\u05EA", false),
                new DataEntry.BinaryEntry("blob", new byte[] { 7, 127, -33, 100, -40}),
                new DataEntry.LongEntry("Wave, wave your hand!", -721010468593883L));
        check(Transaction.makeDataTx(acc, data, FEE));
    }

    private void check(Transaction tx) {
        assertNotNull(tx.id);
        assertFalse(tx.id.isEmpty());

        assertNotNull(tx.signature);
        assertFalse(tx.signature.isEmpty());

        assertNotNull(tx.endpoint);
        assertFalse(tx.endpoint.isEmpty());
    }
}
