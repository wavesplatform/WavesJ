package com.wavesplatform.wavesj;

import com.wavesplatform.wavesj.matcher.Order;
import com.wavesplatform.wavesj.transactions.MassTransferTransaction;
import com.wavesplatform.wavesj.transactions.TransferTransactionV1;
import com.wavesplatform.wavesj.transactions.TransferTransactionV2;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.wavesplatform.wavesj.DataEntry.*;
import static org.junit.Assert.*;

@Ignore
public class NodeTest {
    private static final long AMOUNT = 1 * Asset.TOKEN;
    private static final long FEE = 100000;
    private static final long MFEE = 300000;
    private static final String WBTC = "Fmg13HEHJHuZYbtJq8Da8wifJENq8uBxDuWoP9pVe2Qe";
    private static final AssetPair MARKET = new AssetPair(Asset.WAVES, WBTC);

    private static final PrivateKeyAccount alice =
            PrivateKeyAccount.fromPrivateKey("3ivuUQ7cCVxF3AtaLJ8nbdfEDD53EH3JyLn5ipgvo99v", Account.TESTNET);
    private static final PrivateKeyAccount bob =
            PrivateKeyAccount.fromPrivateKey("ABvpFcUobz1kN4tQeBcHA1WSMdwC6WSPhwCFv1MDZwPc", Account.TESTNET);

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

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

        Block block = node.getBlock(398485);
        assertNotNull(block);
        assertEquals(398485, block.getHeight());
        assertEquals(3, block.getVersion());
        assertEquals(2, block.getTransactions().size());

        for (Transaction tx : block.getTransactions()) {
            ByteString id = tx.getId();
            Transaction tx1 = node.getTransaction(id.getBase58String());
            assertEquals(id, tx1.getId());
        }

        Block block1 = node.getBlock(block.getSignature());
        assertEquals(block.getSignature(), block1.getSignature());
        assertEquals(block.getHeight(), block1.getHeight());
        assertEquals(block.getSize(), block1.getSize());
        assertEquals(block.getFee(), block1.getFee());
        assertEquals(block.getTimestamp(), block1.getTimestamp());
    }

    @Test
    public void testTransfer() throws IOException {
        Node node = new Node();

        long now = System.currentTimeMillis();
        String txId = node.send(new TransferTransactionV1(alice, bob.getAddress(), AMOUNT, null, FEE * 5, null, new ByteString("Hi Bob!".getBytes()), now));
        assertNotNull(txId);

        txId = node.send(new TransferTransactionV1(alice, bob.getAddress(), AMOUNT, "", FEE * 5, null, new ByteString("One more".getBytes()), now + 1));
        assertNotNull(txId);

        // transfer back so that Alice's balance is not drained
        txId = node.send(new TransferTransactionV1(bob, alice.getAddress(), AMOUNT, null, FEE * 5, null, new ByteString("Thanks, Alice".getBytes()), now + 2));
        assertNotNull(txId);

        txId = node.send(new TransferTransactionV1(bob, alice.getAddress(), AMOUNT, Asset.WAVES, FEE * 5, null, new ByteString("Thanks again".getBytes()), now + 3));
        assertNotNull(txId);
    }

    @Test
    public void testMassTransfer() throws IOException {
        expectedEx.expect(IOException.class);

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
        expectedEx.expect(IOException.class);
        expectedEx.expectMessage("{\n" +
                "  \"error\" : 199,\n" +
                "  \"message\" : \"Empty key found\"\n" +
                "}");

        Node node = new Node();

        BinaryEntry bin = new BinaryEntry("This data was proudly published using WavesJ (https://github.com/wavesplatform/wavesj)",
                new ByteString("WavesJrocks"));
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

        String setScriptId = node.setScript(alice, "", Account.TESTNET, FEE * 10);
        assertNotNull(setScriptId);

        String compiledScript = node.compileScript("");
        assertEquals(null, compiledScript);
    }

    @Test
    public void testSendTransaction() throws IOException {
        Node node = new Node();

        TransferTransactionV2 tx1 = Transactions.makeTransferTx(alice, bob.getAddress(), AMOUNT, Asset.WAVES, FEE * 5, Asset.WAVES, "To Bob");
        String id1 = node.send(tx1);
        assertNotNull(id1);
        assertEquals(id1, tx1.getId().getBase58String());

        MassTransferTransaction tx2 = Transactions.makeMassTransferTx(bob, Asset.WAVES,
                Collections.singletonList(new Transfer(alice.getAddress(), AMOUNT)), FEE * 2, "Back to Alice");
        String id2 = node.send(tx2);
        assertNotNull(id2);
        assertEquals(id2, tx2.getId().getBase58String());
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
        assertNotNull(order.getId());
        assertEquals(Order.Type.SELL, order.getOrderType());
        assertEquals(Order.Status.ACCEPTED, order.getStatus());
        assertEquals(Asset.WAVES, order.getAssetPair().getAmountAsset());
        assertEquals(WBTC, order.getAssetPair().getPriceAsset());
        assertEquals(1 * Asset.TOKEN, order.getAmount());
        assertEquals(1, order.getPrice());

        // Check order status
        String status = matcher.getOrderStatus(order.getId().getBase58String(), MARKET).getStatus().toString();
        assertEquals("ACCEPTED", status);

        // Verify the order appears in the list of all orders
        List<Order> orders = matcher.getOrders(alice);
        boolean found = false;
        for (Order o : orders) {
            if (o.getId().equals(order.getId())) {
                found = true;
            }
        }
        assertTrue("The order just filed should appear in the list of all orders for the account", found);

        // Verify the order appears in the list of orders for this asset pair
        orders = matcher.getOrders(alice, MARKET);
        found = false;
        for (Order o : orders) {
            if (o.getId().equals(order.getId())) {
                found = true;
            }
        }
        assertTrue(found);

        for (Order o : orders) {
            assertNotNull(o.getId());
            assertNotNull(o.getOrderType());
            assertNotNull(o.getStatus());
            assertNotNull(o.getAssetPair());
            assertTrue(o.getAmount() > 0);
            assertTrue(o.getPrice() > 0);
            assertTrue(o.getTimestamp() > 0);
        }

        // Cancel order
        String canceled = matcher.cancelOrder(alice, MARKET, order.getId().getBase58String());
        assertEquals("OrderCanceled", canceled);

        // Delete order from history
        String deleted = matcher.deleteOrder(alice, MARKET, order.getId().getBase58String());
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
        Node node = new Node("https://nodes.wavesplatform.com/", 'W');
        assertEquals(node.getAddrByAlias(alias), addr);
    }
}
