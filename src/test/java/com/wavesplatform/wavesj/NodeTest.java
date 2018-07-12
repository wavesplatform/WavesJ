package com.wavesplatform.wavesj;

import com.wavesplatform.wavesj.matcher.Order;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static com.wavesplatform.wavesj.DataEntry.*;
import static org.junit.Assert.*;

public class NodeTest {
    private static final long AMOUNT = 1 * Asset.TOKEN;
    private static final long FEE = 100000;
    private static final long MFEE = 300000;
    private static final String WBTC = "Fmg13HEHJHuZYbtJq8Da8wifJENq8uBxDuWoP9pVe2Qe";
    private static final AssetPair MARKET = new AssetPair(Asset.WAVES, WBTC);

    private static final PrivateKeyAccount alice =
            PrivateKeyAccount.fromPrivateKey("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t", Account.TESTNET);
    private static final PrivateKeyAccount bob =
            PrivateKeyAccount.fromPrivateKey("25Um7fKYkySZnweUEVAn9RLtxN5xHRd7iqpqYSMNQEeT", Account.TESTNET);

    @Test
    public void testGetters() throws IOException {
        Node node = new Node();
        assertNotNull(node.getVersion());
        assertTrue(node.getHeight() > 0);

        String address = bob.getAddress();
        assertTrue(node.getBalance(address, 100) >= 0);
        assertTrue(node.getBalance(address, WBTC) >= 0);

        long wavesBalance = node.getBalance(address);
        assertTrue(wavesBalance >= 0);
        assertEquals(wavesBalance, node.getBalance(address, null));
        assertEquals(wavesBalance, node.getBalance(address, ""));
        assertEquals(wavesBalance, node.getBalance(address, Asset.WAVES));
    }

    @Test
    public void testBlocksAndTransactions() throws IOException {
        Node node = new Node();

        Block block = node.getBlock(362294);
        assertNotNull(block);
        assertEquals(362294, block.height);
        assertEquals(3, block.version);

        for (Map<String, Object> tx: block.transactions) {
            String id = (String) tx.get("id");
            Map<String, Object> tx1 = node.getTransaction(id);
            assertEquals(id, tx1.get("id"));
        }

        Block block1 = node.getBlock(block.signature);
        assertEquals(block.signature, block1.signature);
        assertEquals(block.height, block1.height);
        assertEquals(block.size, block1.size);
        assertEquals(block.fee, block1.fee);
        assertEquals(block.timestamp, block1.timestamp);
    }

    @Test
    public void testTransfer() throws IOException {
        Node node = new Node();
        String txId = node.transfer(alice, bob.getAddress(), AMOUNT, FEE, "Hi Bob!");
        assertNotNull(txId);

        txId = node.transfer(alice, "", bob.getAddress(), AMOUNT, FEE, null, "One more");
        assertNotNull(txId);

        // transfer back so that Alice's balance is not drained
        txId = node.transfer(bob, null, alice.getAddress(), AMOUNT, FEE, Asset.WAVES, "Thanks, Alice");
        assertNotNull(txId);

        txId = node.transfer(bob, Asset.WAVES, "alice", AMOUNT, FEE, "", "Thanks again");
        assertNotNull(txId);
    }

    @Test
    public void testMassTransfer() throws IOException {
        Node node = new Node();
        List<Transfer> transfers = Arrays.asList(new Transfer(alice.getAddress(), AMOUNT), new Transfer(bob.getAddress(), AMOUNT));
        String txId = node.massTransfer(alice, Asset.WAVES, transfers, FEE * 2, "sharedrop");
        assertNotNull(txId);

        transfers = Collections.singletonList(new Transfer("alice", AMOUNT));
        txId = node.massTransfer(bob, null, transfers, FEE * 2, "same thing");
        assertNotNull(txId);
    }

    @Test
    public void testDataTransaction() throws IOException {
        Node node = new Node();

        BinaryEntry bin = new BinaryEntry("This data was proudly published using WavesJ (https://github.com/wavesplatform/wavesj)",
                Base58.decode("WavesJrocks"));
        BooleanEntry bool = new BooleanEntry("\u05D5\u05EA\u05D9\u05D9\u05E8\u05D5\u05EA", false);
        LongEntry integer = new LongEntry("\u0414\u043B\u0438\u043D\u0430 \u0437\u0438\u043C\u044B \u0432 \u041C\u043E\u0441\u043A\u0432\u0435", 160L);
        StringEntry string = new StringEntry("", "");

        List<DataEntry<?>> data = new LinkedList<DataEntry<?>>();
        data.add(bin);
        data.add(bool);
        data.add(integer);
        data.add(string);
        String txId = node.data(alice, data, FEE);
        assertNotNull(txId);
    }

    @Test
    public void testScript() throws IOException {
        Node node = new Node();

        String setScriptId = node.setScript(alice, "", Account.TESTNET, FEE * 4);
        assertNotNull(setScriptId);

        String compiledScript = node.compileScript("");
        assertEquals(null, compiledScript);
    }

    @Test
    public void testSendTransaction() throws IOException {
        Node node = new Node();

        Transaction tx1 = Transaction.makeTransferTx(alice, bob.getAddress(), AMOUNT, Asset.WAVES, FEE, Asset.WAVES, "To Bob");
        String id1 = node.send(tx1);
        assertNotNull(id1);
        assertEquals(id1, tx1.id);

        Transaction tx2 = Transaction.makeMassTransferTx(bob, Asset.WAVES,
                Collections.singletonList(new Transfer(alice.getAddress(), AMOUNT)), FEE * 2, "Back to Alice");
        String id2 = node.send(tx2);
        assertNotNull(id2);
        assertEquals(id2, tx2.id);
    }

    @Test
    public void testMatcher() throws IOException {
        Node matcher = new Node();
        String matcherKey = matcher.getMatcherKey();

        OrderBook orderBook = matcher.getOrderBook(MARKET);
        assertNotNull(orderBook);

        // Create an order
        Order order = matcher.createOrder(alice, matcherKey,
                MARKET, Order.Type.SELL,
                1, 1 * Asset.TOKEN,
                System.currentTimeMillis() + 65000,
                MFEE);
        assertNotNull(order.id);
        assertEquals(Order.Type.SELL, order.type);
        assertEquals(Order.Status.ACCEPTED, order.status);
        assertEquals(Asset.WAVES, order.assetPair.amountAsset);
        assertEquals(WBTC, order.assetPair.priceAsset);
        assertEquals(1 * Asset.TOKEN, order.amount);
        assertEquals(1, order.price);

        // Check order status
        String status = matcher.getOrderStatus(order.id, MARKET).status.toString();
        assertEquals("ACCEPTED", status);

        // Verify the order appears in the list of all orders
        List<Order> orders = matcher.getOrders(alice);
        boolean found = false;
        for (Order o: orders) {
            if (o.id.equals(order.id)) {
                found = true;
            }
        }
        assertTrue("The order just filed should appear in the list of all orders for the account", found);

        // Verify the order appears in the list of orders for this asset pair
        orders = matcher.getOrders(alice, MARKET);
        found = false;
        for (Order o: orders) {
            if (o.id.equals(order.id)) {
                found = true;
            }
        }
        assertTrue(found);

        for (Order o: orders) {
            assertNotNull(o.id);
            assertNotNull(o.type);
            assertNotNull(o.status);
            assertNotNull(o.assetPair);
            assertTrue(o.amount > 0);
            assertTrue(o.price > 0);
            assertTrue(o.timestamp > 0);
        }

        // Cancel order
        String canceled = matcher.cancelOrder(alice, MARKET, order.id);
        assertEquals("OrderCanceled", canceled);

        // Delete order from history
        String deleted = matcher.deleteOrder(alice, MARKET, order.id);
        assertEquals("OrderDeleted", deleted);
    }

    @Test
    public void testValidator() throws IOException {
        String addr = "3MzZCGFyuxgC4ZmtKRS7vpJTs75ZXdkbp1K";
        Node node = new Node();
        assertTrue(node.validateAddresses(addr));
        String nonAddr = "3Mbrokennonadddr";
        assertFalse(node.validateAddresses(nonAddr));
    }

    @Test
    public void testAliasGet() throws IOException, URISyntaxException {
        String addr = "3PA1KvFfq9VuJjg45p2ytGgaNjrgnLSgf4r";
        String alias = "blackturtle";
        Node node = new Node("https://nodes.wavesnodes.com/");
        assertEquals(node.getAddrByAlias(alias),addr);
    }
}
