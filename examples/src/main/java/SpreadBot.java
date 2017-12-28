import com.wavesplatform.wavesj.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public class SpreadBot {
    private final PrivateKeyAccount account = PrivateKeyAccount.fromPrivateKey("25Um7fKYkySZnweUEVAn9RLtxN5xHRd7iqpqYSMNQEeT", Account.TESTNET);
    private final String nodeUrl = "https://testnode2.wavesnodes.com";
    private final String amountAsset = Asset.WAVES;
    private final String priceAsset = "Fmg13HEHJHuZYbtJq8Da8wifJENq8uBxDuWoP9pVe2Qe"; // WBTC
    private final long maxOrderSize = 1 * Asset.MILLI; // in priceAsset
    private final double halfSpread = 0.01; // 1%
    private final long fee = 3 * Asset.MILLI; // in WAVES
    private final long period = 60_000; // 1 min

    private final Node node;
    private final String matcherKey;

    private String buyOrderId, sellOrderId;

    public SpreadBot() throws IOException, URISyntaxException {
        this.node = new Node(nodeUrl);
        this.matcherKey = node.getMatcherKey();
    }

    private void waitForStatus(String orderId, String status) throws IOException, InterruptedException {
        String s;
        do {
            Thread.sleep(500);
            s = node.getOrderStatus(orderId, amountAsset, priceAsset);
        } while (! status.equals(s));
    }

    private void cancelOrder(String orderId) throws IOException, InterruptedException {
        node.cancelOrder(account, amountAsset, priceAsset, orderId, fee);
        waitForStatus(orderId, "OrderCanceled");
        System.out.println("Canceled order " + orderId);
    }

    private void fileOrder(Order.Type type, long price, long amount) throws IOException, InterruptedException {
        String orderId = node.createOrder(account, matcherKey, amountAsset, priceAsset, type, price, amount, System.currentTimeMillis() + period, fee);
        System.out.printf("Filed %s order at %d\n", orderId, price);
    }

    private void round() throws IOException, InterruptedException {
        if (buyOrderId != null) {
            cancelOrder(buyOrderId);
            buyOrderId = null;
        }
        if (sellOrderId != null) {
            cancelOrder(sellOrderId);
            sellOrderId = null;
        }

        OrderBook book = node.getOrderBook(amountAsset, priceAsset);
        if (book.bids.size() <= 0) {
            System.out.println("There are no bids, skipping this round");
            return;
        }
        if (book.asks.size() <= 0) {
            System.out.println("There are no asks, skipping this round");
            return;
        }

        long amountAssetBalance = node.getBalance(account.getAddress(), amountAsset) - 2 * fee;
        long priceAssetBalance = node.getBalance(account.getAddress(), priceAsset) - 2 * fee;

        long meanPrice = (book.bids.get(0).price + book.asks.get(0).price) / 2;
        long buyPrice = (long) (meanPrice * (1 - halfSpread));
        long sellPrice = (long) (meanPrice * (1 + halfSpread));

        long buyOrderSize = Math.min(priceAssetBalance, maxOrderSize) / buyPrice;
        fileOrder(Order.Type.BUY, buyPrice, buyOrderSize);
        long sellOrderSize = Math.min(amountAssetBalance, maxOrderSize) / sellPrice;
        fileOrder(Order.Type.SELL, sellPrice, sellOrderSize);
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
        new SpreadBot().run();
    }
}
