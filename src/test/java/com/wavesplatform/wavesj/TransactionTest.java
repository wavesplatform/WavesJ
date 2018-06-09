package com.wavesplatform.wavesj;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class TransactionTest {
    private static final long AMOUNT = 100000000L;
    private static final long FEE = 40000;  // smaller than minimal

    private static final Node node = new Node();

    @Test
    public void test() {
        PrivateKeyAccount acc = PrivateKeyAccount.fromPrivateKey("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t", Account.TESTNET);
        String recipient = "3N9gDFq8tKFhBDBTQxR3zqvtpXjw5wW3syA";
        String assetId = "s53ji22qFabLHQTqXywuiTZgS5XHpCvESsZ6Xj5h7bN";

        check(Transaction.makeAliasTx(acc, "daphnie", Account.TESTNET, FEE));
        check(Transaction.makeIssueTx(acc, Account.TESTNET, "Pure Gold", "Gold backed asset",
                AMOUNT, (byte) 8, true, "base64:AQa3b8tH", FEE));
        check(Transaction.makeReissueTx(acc, Account.TESTNET, assetId, AMOUNT, false, FEE));
        check(Transaction.makeBurnTx(acc, Account.TESTNET, assetId, AMOUNT, FEE));
        check(Transaction.makeSponsorTx(acc, assetId, FEE, FEE));
        check(Transaction.makeLeaseTx(acc, recipient, AMOUNT, FEE));
        check(Transaction.makeLeaseCancelTx(acc, Account.TESTNET, "2377Zs327pifzBhqKSdUJrz9kPdiS54kHw4t5UvRnK7Q", FEE));
        check(Transaction.makeTransferTx(acc, recipient, AMOUNT, null, FEE, null, "Shut up & take my money"));
        check(Transaction.makeScriptTx(acc, "base64:AQa3b8tH", Account.TESTNET, FEE));

        List<Transfer> transfers = Arrays.asList(new Transfer(acc.getAddress(), AMOUNT), new Transfer(recipient, AMOUNT));
        check(Transaction.makeMassTransferTx(acc, Asset.WAVES, transfers, FEE, "mass transfer"));
        check(Transaction.makeMassTransferTx(acc, assetId, new LinkedList<Transfer>(), FEE, null));

        List<DataEntry<?>> data = new LinkedList<DataEntry<?>>();
        data.add(new DataEntry.BooleanEntry("\u05D5\u05EA\u05D9\u05D9\u05E8\u05D5\u05EA", false));
        data.add(new DataEntry.BinaryEntry("blob", Base64.decode("base64:FlappyBirdie063Q")));
        data.add(new DataEntry.LongEntry("Wave your hand that many times", 721010468593883L));
        data.add(new DataEntry.StringEntry("My Poem", "Oh waves!"));
        check(Transaction.makeDataTx(acc, data, FEE));
        check(Transaction.makeDataTx(acc, new LinkedList<DataEntry<?>>(), FEE));
    }

    @Test
    public void multiSigTest() {
        PublicKeyAccount sender = new PublicKeyAccount("8LbAU5BSrGkpk5wbjLMNjrbc9VzN9KBBYv9X8wGpmAJT", Account.TESTNET);
        PrivateKeyAccount signer1 = PrivateKeyAccount.fromPrivateKey("25Um7fKYkySZnweUEVAn9RLtxN5xHRd7iqpqYSMNQEeT", Account.TESTNET);
        PrivateKeyAccount signer2 = PrivateKeyAccount.fromPrivateKey("4n6L7rZYL2LAmwheLBketwXCCC4JZF3mHYEskySxeNqm", Account.TESTNET);

        Transaction tx = Transaction.makeLeaseTx(sender, signer1.getAddress(), Asset.TOKEN, FEE);
        assertEquals(0, tx.proofs.size());
        checkSendProven(tx);

        String proof = "some proof";
        Transaction provenTx = tx.withProof(1, proof);
        assertEquals(2, provenTx.proofs.size());
        assertEquals("", provenTx.proofs.get(0));
        assertEquals(proof, provenTx.proofs.get(1));
        checkSendProven(tx);

        String signature = signer1.sign(provenTx);
        Transaction signedTx = provenTx.withProof(0, signature);
        assertEquals(2, signedTx.proofs.size());
        assertEquals(signature, signedTx.proofs.get(0));
        assertEquals(proof, signedTx.proofs.get(1));
        checkSendProven(tx);

        try {
            signedTx.withProof(8, "bah!");
            fail("Was able to add 9 proofs to a transaction");
        } catch (IllegalArgumentException ex) {
            // okay
        }
    }

    private void check(Transaction tx) {
        assertNotNull(tx.id);
        assertFalse(tx.id.isEmpty());

        assertEquals(1, tx.proofs.size());
        assertFalse(tx.proofs.get(0).isEmpty());

        assertNotNull(tx.endpoint);
        assertFalse(tx.endpoint.isEmpty());

        checkSend(tx, "does not exceed minimal value", "Transaction should have failed because of insufficient fee");

        tx = tx.withProof(4, "proof");
        checkSendProven(tx);
    }

    private void checkSend(Transaction tx, String failure, String message) {
        try {
            node.send(tx);
            fail(message);
        } catch (IOException e) {
            assertTrue("Expected failure: " + failure + "\nbut was: " + e.getMessage(),
                    e.getMessage().contains(failure));
        }
    }

    private void checkSendProven(Transaction tx) {
        checkSend(tx, "Transactions from non-scripted accounts must have exactly 1 proof",
                "Multi proof transaction should have failed because account is non-scripted");
    }
}
