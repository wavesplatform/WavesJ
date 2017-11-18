package com.wavesplatform.wavesj;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class Node {
    public static final String DEFAULT_NODE = "https://testnode1.wavesnodes.com";

    private final String url;
    private final CloseableHttpClient client = HttpClients.createDefault();

    public Node() {
        this(DEFAULT_NODE);
    }

    public Node(String url) {
        this.url = url;
    }

    public int getHeight() throws IOException {
        return send("/blocks/height", "height", Integer.class);
    }

    public long getBalance(String address) throws IOException {
        return send("/addresses/balance/" + address, "balance", Long.class);
    }

    public long getBalance(String address, int confirmations) throws IOException {
        return send("/addresses/balance/" + address + "/" + confirmations, "balance", Long.class);
    }

    public long getBalance(String address, String assetId) throws IOException {
        return send("/assets/balance/" + address + "/" + assetId, "balance", Long.class);
    }

    public String transfer(PrivateKeyAccount from, String toAddress, long amount, long fee, String message) throws IOException {
        Transaction tx = Transaction.makeTransferTx(from, toAddress, amount, null, fee, null, message);
        return send(tx);
    }

    public String transferAsset(PrivateKeyAccount from, String toAddress,
            long amount, String assetId, long fee, String feeAssetId, String message) throws IOException
    {
        Transaction tx = Transaction.makeTransferTx(from, toAddress, amount, assetId, fee, feeAssetId, message);
        return send(tx);
    }

    public String lease(PrivateKeyAccount from, String toAddress, long amount, long fee) throws IOException {
        Transaction tx = Transaction.makeLeaseTx(from, toAddress, amount, fee);
        return send(tx);
    }

    public String cancelLease(PrivateKeyAccount account, String assetId, long fee) throws IOException {
        Transaction tx = Transaction.makeLeaseCancelTx(account, assetId, fee);
        return send(tx);
    }

    public String issueAsset(PrivateKeyAccount account,
            String name, String description, long quantity, int decimals, boolean reissuable, long fee) throws IOException
    {
        Transaction tx = Transaction.makeIssueTx(account, name, description, quantity, decimals, reissuable, fee);
        return send(tx);
    }

    public String reissueAsset(PrivateKeyAccount account, String assetId, long quantity, boolean reissuable, long fee) throws IOException {
        Transaction tx = Transaction.makeReissueTx(account, assetId, quantity, reissuable, fee);
        return send(tx);
    }

    public String burnAsset(PrivateKeyAccount account, String assetId, long amount, long fee) throws IOException {
        Transaction tx = Transaction.makeBurnTx(account, assetId, amount, fee);
        return send(tx);
    }

    public String alias(PrivateKeyAccount account, String alias, long fee) throws IOException {
        Transaction tx = Transaction.makeAliasTx(account, alias, fee);
        return send(tx);
    }

    public String createOrder(PrivateKeyAccount account, PublicKeyAccount matcher,
            String spendAssetId, String receiveAssetId, long price, long amount, long expirationTime, long matcherFee)
            throws IOException
    {
        Transaction tx = Transaction.makeOrderTx(account, matcher, spendAssetId, receiveAssetId, price, amount, expirationTime, matcherFee);
        return send(tx);///check json format
    }

    public String cancelOrder(PrivateKeyAccount account,
            String spendAssetId, String receiveAssetId, String orderId, long fee) throws IOException
    {
        Transaction tx = Transaction.makeOrderCancelTx(account, spendAssetId, receiveAssetId, orderId, fee);
        return send(tx); ///check json
    }

    public String getOrderBook(String asset1, String asset2, Integer depth) throws IOException {///return smth
        return send("/matcher/orderBook", "pair", String.class,
                "asset1", asset1,
                "asset2", asset2,
                "depth", String.valueOf(depth));
    }

    public String getOrderStatus(String orderId, String asset1, String asset2) throws IOException { ///return {status, filledAmt}
        return send("/matcher/orders/status/" + orderId, "status", String.class,
                "id", orderId,
                "asset1", asset1,
                "asset2", asset2);
    }

    private <T> T send(String path, String key, Class<T> type, String... params) throws IOException {
        try {
            URIBuilder builder = new URIBuilder(url + path);
            for (int i = 0; i < params.length; i += 2) {
                builder.addParameter(params[i], params[i + 1]);
            }
            HttpGet request = new HttpGet(builder.build());
            return exec(request, key, type);
        } catch (URISyntaxException e) {
            // should never happen
            throw new RuntimeException(e);
        }
    }

    private String send(Transaction tx) throws IOException {
        String json = new ObjectMapper().writeValueAsString(tx.getData());
        HttpPost request = new HttpPost(url + tx.getEndpoint());
        request.setEntity(new StringEntity(json));
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");
        return exec(request, "id", String.class);
    }

    private <T> T exec(HttpUriRequest request, String key, Class<T> type) throws IOException {
        HttpResponse r = client.execute(request);
        if (r.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            String error = parseResponse(r, "message", String.class);
            throw new IOException(error);
        }
        return parseResponse(r, key, type);
    }

    private static <T> T parseResponse(HttpResponse r, String key, Class<T> type) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        if (type == Long.class) {
            mapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);
        }
        Map data = mapper.readValue(r.getEntity().getContent(), Map.class);
        return type.cast(data.get(key));
    }
}
