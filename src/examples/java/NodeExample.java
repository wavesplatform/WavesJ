import com.wavesplatform.wavesj.*;

import java.io.IOException;
import java.net.URISyntaxException;

public class NodeExample {

    public static void main(String[] args) throws IOException, URISyntaxException {
        final long TOKEN = 1_0000_0000L;
        final long FEE = 100_000;
        final long ISSUE_FEE = 1 * TOKEN;
        final long MATCHER_FEE = 300_000;

        final String WBTC = "Fmg13HEHJHuZYbtJq8Da8wifJENq8uBxDuWoP9pVe2Qe";

        // Create signing testnet account
        PrivateKeyAccount alice = new PrivateKeyAccount("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t", 'T');
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

        // Issue an asset
        String assetId = node.issueAsset(alice, "CleanAir", "The first air-backed blockchain asset ever",
                1_000_000 * TOKEN, 8, true, ISSUE_FEE);
        // Reissuing, making it no longer reissuable
        String txId = node.reissueAsset(alice, assetId, 100 * TOKEN, true, ISSUE_FEE);
        // Burning some coins
        txId = node.burnAsset(alice, assetId, 20 * TOKEN, ISSUE_FEE);

        // Leasing coins
        String leaseTxId = node.lease(alice, bob, 100 * TOKEN, FEE);
        // Canceling a lease by tx ID
        String cancelTxId = node.cancelLease(alice, leaseTxId, FEE);

        // Offline transaction signing
        Transaction tx = Transaction.makeTransferTx(alice, bob,
                1 * TOKEN, null, FEE, null,
                "Here's for the coffee");
        // tx.getEndpoint() == "/assets/broadcast/transfer" is the server endpoint to send this transaction to.
        // tx.getJson() is JSON-encoded transaction data. You can use Swagger UI to send it to the network.


        // Matcher interaction
        //
        Node matcher = new Node("https://testnode2.wavesnodes.com");
        String matcherKey = matcher.getMatcherKey();

        // Create an order
        String orderId = matcher.createOrder(alice, matcherKey,
                // buy 1 WBTC for 1000 WAVES
                WBTC, "", Order.Type.BUY, 1000, 1 * TOKEN,
                // make order valid for 1 hour
                System.currentTimeMillis() + 3_600_000, MATCHER_FEE);

        // Get order status by id
        matcher.getOrderStatus(orderId, WBTC, "");

        // Cancel order
        matcher.cancelOrder(alice, WBTC, "", orderId, MATCHER_FEE);
    }
}
