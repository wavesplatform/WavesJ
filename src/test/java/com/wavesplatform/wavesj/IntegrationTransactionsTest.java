package com.wavesplatform.wavesj;

import com.wavesplatform.wavesj.transactions.LeaseTransaction;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class IntegrationTransactionsTest {
    private static final long AMOUNT = 100000000L;
    private static final long SMALL_FEE = 40000;  // smaller than minimal
    private static final long NORMAL_FEE = 100000;

    private static final Node node = new Node();

    @Test
    public void test() {
        PrivateKeyAccount acc = PrivateKeyAccount.fromPrivateKey("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t", Account.TESTNET);
        String recipient = "3N9gDFq8tKFhBDBTQxR3zqvtpXjw5wW3syA";
        String assetId = "s53ji22qFabLHQTqXywuiTZgS5XHpCvESsZ6Xj5h7bN";

        check(Transaction.makeAliasTx(acc, "daphnie", Account.TESTNET, SMALL_FEE));
        check(Transaction.makeIssueTx(acc, Account.TESTNET, "Pure Gold", "Gold backed asset",
                AMOUNT, (byte) 8, true, "base64:AQa3b8tH", SMALL_FEE));
        check(Transaction.makeReissueTx(acc, Account.TESTNET, assetId, AMOUNT, false, SMALL_FEE));
        check(Transaction.makeBurnTx(acc, Account.TESTNET, assetId, AMOUNT, SMALL_FEE));
        check(Transaction.makeSponsorTx(acc, assetId, SMALL_FEE, SMALL_FEE));
        check(Transaction.makeLeaseTx(acc, recipient, AMOUNT, SMALL_FEE));
        check(Transaction.makeLeaseCancelTx(acc, Account.TESTNET, "2377Zs327pifzBhqKSdUJrz9kPdiS54kHw4t5UvRnK7Q", SMALL_FEE));
        check(Transaction.makeTransferTx(acc, recipient, AMOUNT, null, SMALL_FEE, null, "Shut up & take my money"));
        check(Transaction.makeScriptTx(acc, "base64:AQa3b8tH", Account.TESTNET, SMALL_FEE));

        List<Transfer> transfers = Arrays.asList(new Transfer(acc.getAddress(), AMOUNT), new Transfer(recipient, AMOUNT));
        check(Transaction.makeMassTransferTx(acc, Asset.WAVES, transfers, SMALL_FEE, "mass transfer"));
        check(Transaction.makeMassTransferTx(acc, assetId, new LinkedList<Transfer>(), SMALL_FEE, null));

        List<DataEntry<?>> data = new LinkedList<DataEntry<?>>();
        data.add(new DataEntry.BooleanEntry("\u05D5\u05EA\u05D9\u05D9\u05E8\u05D5\u05EA", false));
        data.add(new DataEntry.BinaryEntry("blob", Base64.decode("base64:FlappyBirdie063Q")));
        data.add(new DataEntry.LongEntry("Wave your hand that many times", 721010468593883L));
        data.add(new DataEntry.StringEntry("My Poem", "Oh waves!"));
        check(Transaction.makeDataTx(acc, data, SMALL_FEE));
        check(Transaction.makeDataTx(acc, new LinkedList<DataEntry<?>>(), SMALL_FEE));
    }

    @Test
    public void multiSigTest() {
        PrivateKeyAccount sender = PrivateKeyAccount.fromPrivateKey("8LbAU5BSrGkpk5wbjLMNjrbc9VzN9KBBYv9X8wGpmAJT", Account.TESTNET);
        PrivateKeyAccount signer1 = PrivateKeyAccount.fromPrivateKey("25Um7fKYkySZnweUEVAn9RLtxN5xHRd7iqpqYSMNQEeT", Account.TESTNET);
        PrivateKeyAccount signer2 = PrivateKeyAccount.fromPrivateKey("4n6L7rZYL2LAmwheLBketwXCCC4JZF3mHYEskySxeNqm", Account.TESTNET);

        LeaseTransaction tx = new LeaseTransaction(sender, signer1.getAddress(), Asset.TOKEN, NORMAL_FEE, System.currentTimeMillis());

        String proof = Base58.encode("some proof".getBytes());
        ObjectWithProofs<LeaseTransaction> provenTx = new ObjectWithProofs<LeaseTransaction>(tx, sender);
        provenTx = provenTx.withProof(1, proof);
        assertEquals(2, provenTx.getProofs().size());
        assertEquals(proof, provenTx.getProofs().get(1));
        checkSendProven(provenTx);

        try {
            provenTx.withProof(8, "bah!");
            fail("Was able to add 9 proofs to a transaction");
        } catch (IllegalArgumentException ex) {
            // okay
        }
    }

    private <T extends Transaction> void check(ProofedObject<T> tx) {
        assertNotNull(tx.getObject().getId());
        assertFalse(tx.getObject().getId().isEmpty());

        checkSend((ApiJson) tx, "does not exceed minimal value", "Transaction should have failed because of insufficient fee");

    }

    private <T extends Transaction> void checkSend(ApiJson tx, String failure, String message) {
        try {
            node.send(tx);
            fail(message);
        } catch (IOException e) {
            assertTrue("Expected failure: " + failure + "\nbut was: " + e.getMessage(),
                    e.getMessage().contains(failure));
        }
    }

    private <T extends Transaction> void checkSendProven(ApiJson tx) {
        checkSend(tx, "Transactions from non-scripted accounts must have exactly 1 proof",
                "Multi proof object should have failed because account is non-scripted");
    }
}
