import com.wavesplatform.wavesj.*;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.wavesplatform.wavesj.Asset.WAVES;

public class NodeExample {

    public static void main(String[] args) throws IOException, URISyntaxException {
        final long FEE = 100_000;
        final long ISSUE_FEE = 1 * Asset.TOKEN;
        final long MATCHER_FEE = 300_000;

        final String WBTC = "Fmg13HEHJHuZYbtJq8Da8wifJENq8uBxDuWoP9pVe2Qe";

        // Create signing testnet account
        String seed = "health lazy lens fix dwarf salad breeze myself silly december endless rent faculty report beyond";
        PrivateKeyAccount alice = PrivateKeyAccount.fromSeed(seed, 0, Account.TESTNET);
        // Retrieve its public key
        byte[] publicKey = alice.getPublicKey();
        // and its address
        String address = alice.getAddress();

        // Create a Node ("https://testnode1.wavesnodes.com" by default, or you can pass another URL here)
        Node node = new Node();

        // Get blockchain height
        System.out.println("height: " + node.getHeight());

        // Learn address balance
        System.out.println("Alice's balance: " + node.getBalance(address));
        // Same, with the specified number of confirmations
        System.out.println("With 100 confirmations: " + node.getBalance(address, 100));
        // How much WBTC does Alice have?
        System.out.println("Alice's WBTC balance: " + node.getBalance(address, WBTC));


        // Transactions
        //
        String bob = "3N9gDFq8tKFhBDBTQxR3zqvtpXjw5wW3syA";

        // Create an alias
        String txId = node.alias(alice, "alice", Account.TESTNET, FEE);

        // Issue an asset
        String assetId = node.issueAsset(alice, "CleanAir", "The first air-backed blockchain asset ever",
                1_000_000 * Asset.TOKEN, 8, true, ISSUE_FEE);
        // Reissuing, making it no longer reissuable
        txId = node.reissueAsset(alice, assetId, 100 * Asset.TOKEN, false, ISSUE_FEE);
        // Burning some coins
        txId = node.burnAsset(alice, assetId, 20 * Asset.TOKEN, ISSUE_FEE);

        // Leasing coins
        String leaseTxId = node.lease(alice, bob, 100 * Asset.TOKEN, FEE);
        // Canceling a lease by tx ID
        String cancelTxId = node.cancelLease(alice, leaseTxId, FEE);


        // Offline transaction signing
        //
        Transaction tx = Transaction.makeTransferTx(alice, bob,
                1 * Asset.TOKEN, WAVES, FEE, WAVES,
                "Here's for the coffee");
        // tx.getEndpoint() == "/assets/broadcast/transfer" is the server endpoint to send this transaction to.
        // tx.getJson() is JSON-encoded transaction data. You can use Swagger UI to send it to the network.

        // Now send the signed transaction from an online computer
        node.send(tx);


        // Matcher interaction
        //
        Node matcher = new Node("https://testnode2.wavesnodes.com");
        String matcherKey = matcher.getMatcherKey();

        // Create an order
        AssetPair market = new AssetPair(Asset.WAVES, WBTC);
        Order order = matcher.createOrder(alice, matcherKey, market,
                // buy 10 WAVES at 0.00090000 WBTC each
                Order.Type.BUY, 90_000, 10 * Asset.TOKEN,
                // make order valid for 1 hour
                System.currentTimeMillis() + 3_600_000, MATCHER_FEE);
        String orderId = order.id;
        System.out.printf("Filed order %s to %s %d WAVES at %.8f\n",
                order.id, order.type, order.amount / Asset.TOKEN, ((float) order.price) / Asset.TOKEN);

        // Get order status by id
        matcher.getOrderStatus(orderId, market);

        // Cancel order
        matcher.cancelOrder(alice, market, orderId);
    }
}
