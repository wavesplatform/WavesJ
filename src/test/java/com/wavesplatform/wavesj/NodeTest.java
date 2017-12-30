package com.wavesplatform.wavesj;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class NodeTest {
    private static final long AMOUNT = 1_00000000L;
    private static final long FEE = 100_000;
    private static final long MFEE = 300_000;
    private static final String WBTC = "Fmg13HEHJHuZYbtJq8Da8wifJENq8uBxDuWoP9pVe2Qe";

    private static final PrivateKeyAccount alice =
            PrivateKeyAccount.fromPrivateKey("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t", Account.TESTNET);
    private static final PrivateKeyAccount bob =
            PrivateKeyAccount.fromPrivateKey("25Um7fKYkySZnweUEVAn9RLtxN5xHRd7iqpqYSMNQEeT", Account.TESTNET);

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

    @Test
    public void testMatcher() throws IOException, URISyntaxException {
        Node matcher = new Node("https://testnode2.wavesnodes.com");
        String matcherKey = "4oP8SPd7LiUo8xsokSTiyZjwg4rojdyXqWEq7NTwWsSU";

        OrderBook orderBook = matcher.getOrderBook(Asset.WAVES, WBTC);
        assertNotNull(orderBook);

        // Cancel all existing orders, just in case
        List<Order> orders = matcher.getOrders(alice);
        for (Order order: orders) {
            if (! "Cancelled".equals(order.getStatus())) {
                matcher.cancelOrder(alice, Asset.WAVES, WBTC, order.getId(), MFEE);
            }
        }

        // Create a new one
        String orderId = matcher.createOrder(alice, matcherKey, "", WBTC, Order.Type.SELL,
                1, 1_00000000,
                System.currentTimeMillis() + 65_000,
                MFEE);
        assertNotNull(orderId);

        // Check order status
        String status = matcher.getOrderStatus(orderId, "", WBTC);
        assertEquals("Accepted", status);

        // Verify the order appears in the list of orders
        orders = matcher.getOrders(alice);
        assertTrue(orders.stream().anyMatch(order -> order.getId().equals(orderId)));

        // Cancel the order
        matcher.cancelOrder(alice, "", WBTC, orderId, MFEE);
    }
}
