package com.wavesplatform.wavesj;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class TransactionTest {
    private static final long AMOUNT = 100000000L;
    private static final long FEE = 100000;

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
        check(Transaction.makeSponsorTx(acc, assetId, FEE, FEE));
        check(Transaction.makeTransferTx(acc, recipient, AMOUNT, null, FEE, null, "Shut up & take my money"));
        check(Transaction.makeMassTransferTx(acc, assetId, new LinkedList<Transfer>(), FEE, null));
        check(Transaction.makeDataTx(acc, new LinkedList<DataEntry<?>>(), FEE));
        check(Transaction.makeScriptTx(acc, "Base58", 'T', FEE));

        List<Transfer> transfers = Arrays.asList(new Transfer(acc.getAddress(), AMOUNT), new Transfer(recipient, AMOUNT));
        check(Transaction.makeMassTransferTx(acc, Asset.WAVES, transfers, FEE, "mass transfer"));

        List<DataEntry<?>> data = new LinkedList<DataEntry<?>>();
        data.add(new DataEntry.BooleanEntry("\u05D5\u05EA\u05D9\u05D9\u05E8\u05D5\u05EA", false));
        data.add(new DataEntry.BinaryEntry("blob", new byte[] { 7, 127, -33, 100, -40}));
        data.add(new DataEntry.LongEntry("Wave, wave your hand!", -721010468593883L));
        check(Transaction.makeDataTx(acc, data, FEE));
    }

    private void check(Transaction tx) {
        assertNotNull(tx.id);
        assertFalse(tx.id.isEmpty());

        assertEquals(1, tx.proofs.size());
        assertFalse(tx.proofs.get(0).isEmpty());

        assertNotNull(tx.endpoint);
        assertFalse(tx.endpoint.isEmpty());
    }

    @Test
    public void multiSigTest() {
        PublicKeyAccount sender = new PublicKeyAccount("8LbAU5BSrGkpk5wbjLMNjrbc9VzN9KBBYv9X8wGpmAJT", Account.TESTNET);
        PrivateKeyAccount signer1 = PrivateKeyAccount.fromPrivateKey("25Um7fKYkySZnweUEVAn9RLtxN5xHRd7iqpqYSMNQEeT", Account.TESTNET);
        PrivateKeyAccount signer2 = PrivateKeyAccount.fromPrivateKey("4n6L7rZYL2LAmwheLBketwXCCC4JZF3mHYEskySxeNqm", Account.TESTNET);

        Transaction tx = Transaction.makeLeaseTx(sender, signer1.getAddress(), Asset.TOKEN, Asset.MILLI, signer2, signer1);
        assertEquals(2, tx.proofs.size());

        tx = Transaction.makeLeaseTx(sender, signer1.getAddress(), Asset.TOKEN, Asset.MILLI);
        assertTrue(tx.proofs.isEmpty());

        Transaction provenTx = tx.addProofs("proof of work", "some other proof");
        assertEquals(2, provenTx.proofs.size());

        Transaction signedTx = provenTx.sign(signer1, signer2);
        assertEquals(4, signedTx.proofs.size());

        try {
            signedTx.addProofs("a", "b", "e", "zyx", "bah!");
            fail("Was able to add 9 proofs to a transaction");
        } catch (IllegalStateException ex) {
            // okay
        }
    }
}
