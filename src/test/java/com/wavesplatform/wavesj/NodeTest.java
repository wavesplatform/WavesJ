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
    private static final AssetPair MARKET = new AssetPair(Asset.WAVES, WBTC);

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

        txId = node.transferAsset(alice, bob.getAddress(), AMOUNT, "", FEE, null, "One more");
        assertNotNull(txId);

        // transfer back so that Alice's balance is not drained
        txId = node.transferAsset(bob, alice.getAddress(), AMOUNT, null, FEE, Asset.WAVES, "Thanks, Alice");
        assertNotNull(txId);

        txId = node.transferAsset(bob, alice.getAddress(), AMOUNT, Asset.WAVES, FEE, "", "Thanks again");
        assertNotNull(txId);
    }

    @Test
    public void testMatcher() throws IOException, URISyntaxException {
        Node matcher = new Node("https://testnode2.wavesnodes.com");
        String matcherKey = matcher.getMatcherKey();

        OrderBook orderBook = matcher.getOrderBook(MARKET);
        assertNotNull(orderBook);

        // Cancel any active orders just in case
        for (Order o: matcher.getOrders(alice)) {
            if (o.status.isActive()) {
                matcher.cancelOrder(alice, MARKET, o.id);
            }
        }

        // Create an order
        Order order = matcher.createOrder(alice, matcherKey,
                MARKET, Order.Type.SELL,
                1, 1 * Asset.TOKEN,
                System.currentTimeMillis() + 65_000,
                MFEE);
        assertNotNull(order.id);
        assertEquals(Order.Type.SELL, order.type);
        assertEquals(Order.Status.ACCEPTED, order.status);
        assertEquals(Asset.WAVES, order.assetPair.amountAsset);
        assertEquals(WBTC, order.assetPair.priceAsset);
        assertEquals(1 * Asset.TOKEN, order.amount);
        assertEquals(1, order.price);

        // Check order status
        String status = matcher.getOrderStatus(order.id, MARKET);
        assertEquals("Accepted", status);

        // Verify the order appears in the list of orders
        List<Order> orders = matcher.getOrders(alice);
        assertTrue(orders.stream().anyMatch(o -> o.id.equals(order.id)));
        for (Order o: orders) {
            assertNotNull(o.id);
            assertNotNull(o.type);
            assertNotNull(o.status);
            assertNotNull(o.assetPair);
            assertTrue(o.amount > 0);
            assertTrue(o.price > 0);
            assertTrue(o.timestamp > 0);
        }
    }
}
