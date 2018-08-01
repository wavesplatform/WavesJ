package com.wavesplatform.wavesj;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Node {
    private static final String DEFAULT_NODE = "https://testnode1.wavesnodes.com";

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TypeReference<OrderBook> ORDER_BOOK = new TypeReference<OrderBook>() {};
    private static final TypeReference<List<Order>> ORDER_LIST = new TypeReference<List<Order>>() {};
    private static final TypeReference<OrderStatusInfo> ORDER_STATUS = new TypeReference<OrderStatusInfo>() {};
    private static final TypeReference<Map<String, Object>> TX_INFO = new TypeReference<Map<String, Object>>() {};
    private static final TypeReference<List<List<Map<String, Object>>>> TX_LIST = new TypeReference<List<List<Map<String, Object>>>>() {};

    private final URI uri;
    private final CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(
            RequestConfig.custom()
                    .setSocketTimeout(5000)
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .setCookieSpec(CookieSpecs.STANDARD)
                    .build())
            .build();

    public Node() {
        try {
            this.uri = new URI(DEFAULT_NODE);
        } catch (URISyntaxException e) {
            // should not happen
            throw new RuntimeException(e);
        }
    }

    public Node(String uri) throws URISyntaxException {
        this.uri = new URI(uri);
    }

    public String getVersion() throws IOException {
        return send("/node/version", "version").asText();
    }

    public int getHeight() throws IOException {
        return send("/blocks/height", "height").asInt();
    }

    public long getBalance(String address) throws IOException {
        return send("/addresses/balance/" + address, "balance").asLong();
    }

    public long getBalance(String address, int confirmations) throws IOException {
        return send("/addresses/balance/" + address + "/" + confirmations, "balance").asLong();
    }

    public long getBalance(String address, String assetId) throws IOException {
        return Asset.isWaves(assetId)
                ? getBalance(address)
                : send("/assets/balance/" + address + "/" + assetId, "balance").asLong();
    }

    /**
     * Returns transaction by its ID.
     * @param txId transaction ID
     * @return transaction object
     * @throws IOException if no transaction with the given ID exists
     */
    public Map<String, Object> getTransaction(String txId) throws IOException {
        return mapper.convertValue(send("/transactions/info/" + txId), TX_INFO);
    }

    /**
     * Returns a list of transactions of an address.
     * @param address address
     * @param limit amount of transactions
     * @return transaction list
     * @throws IOException if no transactions with the given address exist
     */
    public List<Map<String, Object>> getTransactionList(String address, int limit) throws IOException {
        List<List<Map<String, Object>>> result = mapper.convertValue(send("/transactions/address/"+address+"/limit/"+limit), TX_LIST);
        return result.get(0);
    }

    /**
     * Returns block at given height.
     * @param height blockchain height
     * @return block object
     * @throws IOException if no block exists at the given height
     */
    public Block getBlock(int height) throws IOException {
        return mapper.convertValue(send("/blocks/at/" + height), Block.class);
    }

    /**
     * Returns block by its signature.
     * @param signature block signature
     * @return block object
     * @throws IOException if no block with the given signature exists
     */
    public Block getBlock(String signature) throws IOException {
        return mapper.convertValue(send("/blocks/signature/" + signature), Block.class);
    }

    public boolean validateAddresses(String address) throws IOException {
        return send("/addresses/validate/" + address, "valid").asBoolean();
    }

    public String getAddrByAlias(String alias) throws IOException {
        return send("/alias/by-alias/" + alias, "address").textValue();
    }

    /**
     * Sends a signed transaction and returns its ID.
     * @param tx signed transaction (as created by static methods in Transaction class)
     * @return Transaction ID
     * @throws IOException
     */
    public String send(Transaction tx) throws IOException {
        return parse(exec(request(tx)), "id").asText();
    }

    private JsonNode send(String path, String... key) throws IOException {
        return parse(exec(request(path)), key);
    }

    public String transfer(PrivateKeyAccount from, String recipient, long amount, long fee, String message) throws IOException {
        Transaction tx = Transaction.makeTransferTx(from, recipient, amount, null, fee, null, message);
        return send(tx);
    }

    public String transfer(PrivateKeyAccount from, String assetId, String recipient,
            long amount, long fee, String feeAssetId, String message) throws IOException
    {
        Transaction tx = Transaction.makeTransferTx(from, recipient, amount, assetId, fee, feeAssetId, message);
        return send(tx);
    }

    public String lease(PrivateKeyAccount from, String recipient, long amount, long fee) throws IOException {
        Transaction tx = Transaction.makeLeaseTx(from, recipient, amount, fee);
        return send(tx);
    }

    public String cancelLease(PrivateKeyAccount account, byte chainId, String txId, long fee) throws IOException {
        Transaction tx = Transaction.makeLeaseCancelTx(account, chainId, txId, fee);
        return send(tx);
    }

    public String issueAsset(PrivateKeyAccount account, byte chainId, String name, String description, long quantity,
                             byte decimals, boolean reissuable, String script, long fee) throws IOException
    {
        Transaction tx = Transaction.makeIssueTx(account, chainId, name, description, quantity, decimals, reissuable, script, fee);
        return send(tx);
    }

    public String reissueAsset(PrivateKeyAccount account, byte chainId, String assetId, long quantity, boolean reissuable, long fee) throws IOException {
        Transaction tx = Transaction.makeReissueTx(account, chainId, assetId, quantity, reissuable, fee);
        return send(tx);
    }

    public String burnAsset(PrivateKeyAccount account, byte chainId, String assetId, long amount, long fee) throws IOException {
        Transaction tx = Transaction.makeBurnTx(account, chainId, assetId, amount, fee);
        return send(tx);
    }

    public String sponsorAsset(PrivateKeyAccount account, String assetId, long minAssetFee, long fee) throws IOException {
        Transaction tx = Transaction.makeSponsorTx(account, assetId, minAssetFee, fee);
        return send(tx);
    }

    public String alias(PrivateKeyAccount account, byte chainId, String alias, long fee) throws IOException {
        Transaction tx = Transaction.makeAliasTx(account, alias, chainId, fee);
        return send(tx);
    }

    public String massTransfer(PrivateKeyAccount from, String assetId, Collection<Transfer> transfers, long fee, String message) throws IOException {
        Transaction tx = Transaction.makeMassTransferTx(from, assetId, transfers, fee, message);
        return send(tx);
    }

    public String data(PrivateKeyAccount from, Collection<DataEntry<?>> data, long fee) throws IOException {
        Transaction tx = Transaction.makeDataTx(from, data, fee);
        return send(tx);
    }

    /**
     * Sets a validating script for an account.
     * @param from the account
     * @param script script text
     * @param chainId chain ID
     * @param fee transaction fee
     * @return transaction ID
     * @throws IOException if an error occurs
     * @see Account#MAINNET
     * @see Account#TESTNET
     */
    public String setScript(PrivateKeyAccount from, String script, byte chainId, long fee) throws IOException {
        Transaction tx = Transaction.makeScriptTx(from, compileScript(script), chainId, fee);
        return send(tx);
    }

    /**
     * Compiles a script.
     * @param script the script to compile
     * @return compiled script, base64 encoded
     * @throws IOException if the script is not well formed or some other error occurs
     */
    public String compileScript(String script) throws IOException {
        if (script == null || script.isEmpty()) {
            return null;
        }
        HttpPost request = new HttpPost(uri.resolve("/utils/script/compile"));
        request.setEntity(new StringEntity(script));
        return parse(exec(request), "script").asText();
    }

    // Matcher transactions

    public String getMatcherKey() throws IOException {
        return parse(exec(request("/matcher"))).asText();
    }

    public Order createOrder(PrivateKeyAccount account, String matcherKey, AssetPair assetPair, Order.Type orderType,
                             long price, long amount, long expiration, long matcherFee) throws IOException {
        Transaction tx = Transaction.makeOrderTx(account, matcherKey, orderType, assetPair, price, amount, expiration, matcherFee);
        JsonNode tree = parse(exec(request(tx)));
        // fix order status
        ObjectNode message = (ObjectNode) tree.get("message");
        message.put("status", tree.get("status").asText());
        return mapper.treeToValue(tree.get("message"), Order.class);
    }

    public String cancelOrder(PrivateKeyAccount account, AssetPair assetPair, String orderId) throws IOException {
        Transaction tx = Transaction.makeOrderCancelTx(account, assetPair, orderId);
        return parse(exec(request(tx)), "status").asText();
    }

    public String deleteOrder(PrivateKeyAccount account, AssetPair assetPair, String orderId) throws IOException {
        Transaction tx = Transaction.makeDeleteOrder(account, assetPair, orderId);
        return parse(exec(request(tx)), "status").asText();
    }

    public OrderBook getOrderBook(AssetPair assetPair) throws IOException {
        String path = "/matcher/orderbook/" + assetPair.amountAsset+ '/' + assetPair.priceAsset;
        return parse(exec(request(path)), ORDER_BOOK);
    }

    public OrderStatusInfo getOrderStatus(String orderId, AssetPair assetPair) throws IOException {
        String path = "/matcher/orderbook/" + assetPair.amountAsset+ '/' + assetPair.priceAsset+ '/' + orderId;
        return parse(exec(request(path)), ORDER_STATUS);
    }

    public List<Order> getOrders(PrivateKeyAccount account) throws IOException {
        return getOrders(account, "/matcher/orderbook/" + Base58.encode(account.getPublicKey()));
    }

    public List<Order> getOrders(PrivateKeyAccount account, AssetPair market) throws IOException {
        return getOrders(account, String.format("/matcher/orderbook/%s/%s/publicKey/%s",
                market.amountAsset, market.priceAsset, Base58.encode(account.getPublicKey())));
    }

    private List<Order> getOrders(PrivateKeyAccount account, String path) throws IOException {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(40);
        buf.put(account.getPublicKey()).putLong(timestamp);
        String signature = Transaction.sign(account, buf);
        HttpResponse r = exec(request(path, "Timestamp", String.valueOf(timestamp), "Signature", signature));
        return parse(r, ORDER_LIST);
    }

    private <T> HttpUriRequest request(String path, String... headers) {
        HttpUriRequest req = new HttpGet(uri.resolve(path));
        for (int i = 0; i < headers.length; i +=2) {
            req.addHeader(headers[i], headers[i + 1]);
        }
        return req;
    }

    private HttpUriRequest request(Transaction tx) {
        HttpPost request = new HttpPost(uri.resolve(tx.endpoint));
        request.setEntity(new StringEntity(tx.getJson(), ContentType.APPLICATION_JSON));
        return request;
    }

    private HttpResponse exec(HttpUriRequest request) throws IOException {
        HttpResponse r = client.execute(request);
        if (r.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            try {
                throw new IOException(EntityUtils.toString(r.getEntity()));
            } catch (JsonParseException e) {
                throw new RuntimeException("Server error " + r.getStatusLine().getStatusCode());
            }
        }
        return r;
    }

    private static <T> T parse(HttpResponse r, TypeReference<T> ref) throws IOException {
        return mapper.readValue(r.getEntity().getContent(), ref);
    }

    private static JsonNode parse(HttpResponse r, String... keys) throws IOException {
        JsonNode tree = mapper.readTree(r.getEntity().getContent());
        for (String key: keys) {
            tree = tree.get(key);
        }
        return tree;
    }
}
