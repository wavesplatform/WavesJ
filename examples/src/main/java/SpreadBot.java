import com.wavesplatform.wavesj.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public class SpreadBot {
    private final long maxOrderSize = 1 * Asset.MILLI; // in priceAsset
    private final double halfSpread = 0.01; // 1%
    private final long fee = 3 * Asset.MILLI; // in WAVES
    private final long period = 60000; // 1 min

    private final PrivateKeyAccount account;
    private final AssetPair market;
    private final Node node, matcher;
    private final String matcherKey;

    public static SpreadBot testnetInstance() throws IOException, URISyntaxException {
        return new SpreadBot(
                PrivateKeyAccount.fromPrivateKey("25Um7fKYkySZnweUEVAn9RLtxN5xHRd7iqpqYSMNQEeT", Account.TESTNET),
                new AssetPair(Asset.WAVES, "Fmg13HEHJHuZYbtJq8Da8wifJENq8uBxDuWoP9pVe2Qe"),
                "https://testnode2.wavesnodes.com",
                "https://testnode2.wavesnodes.com");
    }

    public static SpreadBot mainnetInstance() throws IOException, URISyntaxException {
        String nodeUrl = "http://dex-aws-ir-1.wavesnodes.com";
        return new SpreadBot(
                PrivateKeyAccount.fromSeed(
                        "abandon ability able about above absent absorb abstract absurd abuse access accident account accuse achieve",
                        0, Account.MAINNET),
                new AssetPair(Asset.WAVES, "8LQW8f7P5d5PZM7GtZEBgaqRPGSzS3DfPuiXrURJ4AJS"),
                nodeUrl + ":6886",
                nodeUrl);
    }

    private SpreadBot(PrivateKeyAccount account, AssetPair market, String matcherUrl, String nodeUrl) throws IOException, URISyntaxException {
        this.account = account;
        this.market = market;
        this.node = new Node(nodeUrl);
        this.matcher = new Node(matcherUrl);
        this.matcherKey = matcher.getMatcherKey();
    }

    private void cancelOrder(String orderId) throws IOException, InterruptedException {
        String status = matcher.cancelOrder(account, market, orderId);
        System.out.printf("%s %s\n", status, orderId);
    }

    private String fileOrder(Order.Type type, long price, long amount) throws IOException, InterruptedException {
        long expiration = System.currentTimeMillis() + Math.max(period, 61000); // matcher requires expiration > 1 min in the future
        Order order = matcher.createOrder(account, matcherKey, market, type, price, amount, expiration, fee);
        System.out.printf("Filed order %s to %s %d at %d\n", order.id, type, amount, price);
        return order.id;
    }

    private void round() throws IOException, InterruptedException {
        System.out.println();

        // Cancel all orders filed at our market
        for (Order order: matcher.getOrders(account, market)) {
            if (order.isActive()) {
                cancelOrder(order.id);
            }
        }

        // Read order book
        OrderBook book = matcher.getOrderBook(market);
        if (book.bids.size() <= 0) {
            System.out.println("There are no bids, skipping this round");
            return;
        }
        if (book.asks.size() <= 0) {
            System.out.println("There are no asks, skipping this round");
            return;
        }

        // Determine buy and sell prices
        long bestBid = book.bids.get(0).price;
        long bestAsk = book.asks.get(0).price;
        System.out.printf("Spread %d : %d\n", bestBid, bestAsk);
        long meanPrice = (bestBid + bestAsk) / 2;
        long buyPrice = (long) (meanPrice * (1 - halfSpread));
        long sellPrice = (long) (meanPrice * (1 + halfSpread));

        // Find out how much we want to buy, and file an order
        long priceAssetAvail = node.getBalance(account.getAddress(), market.priceAsset) - 2 * fee;
        if (priceAssetAvail > 0) {
            long buyOrderSize = Math.min(priceAssetAvail, maxOrderSize) * Asset.TOKEN / buyPrice;
            fileOrder(Order.Type.BUY, buyPrice, buyOrderSize);
        }

        // Same for sell order
        long amountAssetAvail = node.getBalance(account.getAddress(), market.amountAsset) - 2 * fee;
        if (amountAssetAvail > 0) {
            long sellOrderSize = Math.min(amountAssetAvail, maxOrderSize * Asset.TOKEN / sellPrice);
            fileOrder(Order.Type.SELL, sellPrice, sellOrderSize);
        }
    }

    private class RoundTask extends TimerTask {
        @Override public void run() {
            try {
                round();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        Timer t = new Timer();
        t.schedule(new RoundTask(), 0, period);
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        SpreadBot.testnetInstance().run();
    }
}
