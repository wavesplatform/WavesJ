package com.wavesplatform.wavesj;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;

public class NodeTest {
    private static final long AMOUNT = 1_00000000L;
    private static final long FEE = 100_000;
    private static final String WBTC = "Fmg13HEHJHuZYbtJq8Da8wifJENq8uBxDuWoP9pVe2Qe";

    private static final PrivateKeyAccount alice =
            new PrivateKeyAccount("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t", Account.TESTNET);
    private static final PrivateKeyAccount bob =
            new PrivateKeyAccount("25Um7fKYkySZnweUEVAn9RLtxN5xHRd7iqpqYSMNQEeT", Account.TESTNET);

    @Test
    public void testGetters() throws IOException {
        Node node = new Node();
        assertTrue(node.getHeight() > 0);
        assertTrue(node.getBalance(bob.getAddress()) >= 0);
        assertTrue(node.getBalance(bob.getAddress(), 100) >= 0);
        assertTrue(node.getBalance(bob.getAddress(), WBTC) >= 0);
    }

    @Test
    public void testTransfer() throws IOException {
        Node node = new Node();
        String txId = node.transfer(alice, bob.getAddress(), AMOUNT, FEE, "Hi Bob!");
        assertNotNull(txId);

        // transfer back so that Alice's balance is not drained
        txId = node.transfer(bob, alice.getAddress(), AMOUNT, FEE, "Thanks, Alice");
        assertNotNull(txId);
    }

    @Ignore @Test
    public void testMatcher() throws IOException, URISyntaxException {
        Node matcher = new Node("https://testnode2.wavesnodes.com");
        String matcherKey = "4oP8SPd7LiUo8xsokSTiyZjwg4rojdyXqWEq7NTwWsSU";

        OrderBook orders = matcher.getOrderBook(null, WBTC);
        assertNotNull(orders);

        String orderId = matcher.createOrder(alice, matcherKey, "", WBTC, Order.Type.SELL,
                1, 1_00000000,
                System.currentTimeMillis() + 3_600_000,
                500_000);
        assertNotNull(orderId);

        String status = matcher.getOrderStatus(orderId, "", WBTC);
        assertEquals("Accepted", status);

        matcher.cancelOrder(alice, "", WBTC, orderId, 400_000);
    }
}
