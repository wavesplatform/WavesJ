package com.wavesplatform.wavesj;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wavesplatform.wavesj.json.WavesJsonMapper;
import com.wavesplatform.wavesj.matcher.CancelOrder;
import com.wavesplatform.wavesj.matcher.DeleteOrder;
import com.wavesplatform.wavesj.matcher.Order;
import com.wavesplatform.wavesj.transactions.InvokeScriptTransaction;
import com.wavesplatform.wavesj.transactions.LeaseTransaction;
import com.wavesplatform.wavesj.transactions.TransferTransactionV2;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.wavesplatform.wavesj.transactions.InvokeScriptTransaction.*;

public class Node {
    private static final String DEFAULT_NODE = "https://testnode4.wavesnodes.com";

    private static final TypeReference<OrderBook> ORDER_BOOK = new TypeReference<OrderBook>() {
    };
    private static final TypeReference<List<Order>> ORDER_LIST = new TypeReference<List<Order>>() {
    };
    private static final TypeReference<List<BlockHeader>> BLOCK_HEADER_LIST = new TypeReference<List<BlockHeader>>() {
    };
    private static final TypeReference<Map<String, Long>> ASSET_DISTRIBUTION = new TypeReference<Map<String, Long>>() {
    };
    private static final TypeReference<AssetDistribution> ASSET_DISTRIBUTION_BY_HEIGHT = new TypeReference<AssetDistribution>() {
    };
    private static final TypeReference<List<AssetBalance>> ASSET_BALANCE_LIST = new TypeReference<List<AssetBalance>>() {
    };
    private static final TypeReference<OrderStatusInfo> ORDER_STATUS = new TypeReference<OrderStatusInfo>() {
    };
    private static final TypeReference<BalanceDetails> BALANCE_DETAILS = new TypeReference<BalanceDetails>() {
    };
    private static final TypeReference<Map<String, Long>> RESERVED = new TypeReference<Map<String, Long>>() {
    };
    private static final TypeReference<Map<String, Object>> TX_INFO = new TypeReference<Map<String, Object>>() {
    };
    private static final TypeReference<List<DataEntry>> ADDRESS_DATA = new TypeReference<List<DataEntry>>() {
    };
    private static final TypeReference<DataEntry> ADDRESS_DATA_BY_KEY = new TypeReference<DataEntry>() {
    };

    private final URI uri;
    private final WavesJsonMapper wavesJsonMapper;
    private final HttpClient client;
    private final byte chainId;

    public Node() {
        try {
            this.uri = new URI(DEFAULT_NODE);
            this.wavesJsonMapper = new WavesJsonMapper((byte) 'T');
            this.client = createDefaultClient();
            this.chainId = (byte) 'T';
        } catch (URISyntaxException e) {
            // should not happen
            throw new RuntimeException(e);
        }
    }

    public Node(String uri, char chainId) throws URISyntaxException {
        this.uri = new URI(uri);
        this.wavesJsonMapper = new WavesJsonMapper((byte) chainId);
        this.client = createDefaultClient();
        this.chainId = (byte) chainId;
    }

    public Node(String uri, byte chainId) throws URISyntaxException {
        this.uri = new URI(uri);
        this.wavesJsonMapper = new WavesJsonMapper(chainId);
        this.client = createDefaultClient();
        this.chainId = chainId;
    }

    public Node(String uri, char chainId, HttpClient httpClient) throws URISyntaxException {
        this.uri = new URI(uri);
        this.wavesJsonMapper = new WavesJsonMapper((byte) chainId);
        this.client = httpClient;
        this.chainId = (byte) chainId;
    }

    public Node(String uri, byte chainId, HttpClient httpClient) throws URISyntaxException {
        this.uri = new URI(uri);
        this.wavesJsonMapper = new WavesJsonMapper(chainId);
        this.client = httpClient;
        this.chainId =  chainId;
    }

    private HttpClient createDefaultClient() {
        return HttpClients.custom().setDefaultRequestConfig(
                RequestConfig.custom()
                        .setSocketTimeout(5000)
                        .setConnectTimeout(5000)
                        .setConnectionRequestTimeout(5000)
                        .setCookieSpec(CookieSpecs.STANDARD)
                        .build())
                .build();
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

    public BalanceDetails getBalanceDetails(String address) throws IOException {
        return wavesJsonMapper.convertValue(send("/addresses/balance/details/" + address), BALANCE_DETAILS);
    }

    public long getBalance(String address, int confirmations) throws IOException {
        return send("/addresses/balance/" + address + "/" + confirmations, "balance").asLong();
    }

    public long getBalance(String address, String assetId) throws IOException {
        return Asset.isWaves(assetId)
                ? getBalance(address)
                : send("/assets/balance/" + address + "/" + assetId, "balance").asLong();
    }


    public Map<String, Long> getAssetDistribution(String assetId) throws IOException {
        String path = String.format("/assets/%s/distribution", assetId);
        HttpResponse r = exec(request(path));
        return parse(r, ASSET_DISTRIBUTION);
    }

    public AssetDistribution getAssetDistributionByHeight(String assetId, Integer height, Integer limit) throws IOException {
        String path = String.format("/assets/%s/distribution/%d/limit/%d", assetId, height, limit);
        HttpResponse r = exec(request(path));
        return parse(r, ASSET_DISTRIBUTION_BY_HEIGHT);
    }

    public AssetDistribution getAssetDistributionByHeight(String assetId, Integer height, Integer limit, String after) throws IOException {
        String path = String.format("/assets/%s/distribution/%d/limit/%d?after=%s", assetId, height, limit, after);
        HttpResponse r = exec(request(path));
        return parse(r, ASSET_DISTRIBUTION_BY_HEIGHT);
    }

    public List<AssetBalance> getAssetsBalance(String address) throws IOException {
        return wavesJsonMapper.convertValue(send("/assets/balance/" + address, "balances"), ASSET_BALANCE_LIST);
    }

    public List<DataEntry> getData(String address) throws IOException{
        return wavesJsonMapper.convertValue(send(String.format("/addresses/data/%s", address)),ADDRESS_DATA);
    }

    public DataEntry getDataByKey(String address, String key) throws IOException{
        return wavesJsonMapper.convertValue(send(String.format("/addresses/data/%s/%s", address, key)),ADDRESS_DATA_BY_KEY);
    }

    public AssetDetails getAssetDetails(String assetId) throws IOException {
        return wavesJsonMapper.convertValue(send("/assets/details/" + assetId), AssetDetails.class);
    }


    /**
     * Returns object by its ID.
     *
     * @param txId object ID
     * @return object object
     * @throws IOException if no object with the given ID exists
     */
    public Transaction getTransaction(String txId) throws IOException {
        return wavesJsonMapper.convertValue(send("/transactions/info/" + txId), Transaction.class);
    }

    public Map<String, Object> getTransactionData(String txId) throws IOException {
        return wavesJsonMapper.convertValue(send("/transactions/info/" + txId), TX_INFO);
    }

    /**
     * Returns transactions by address with limit.
     * @param address address
     * @param limit transactions limit
     * @return list of transactions
     * @throws IOException if something going wrong
     */
    public List<Transaction> getAddressTransactions(String address, int limit) throws IOException {
        return getAddressTransactions(address, limit, null);
    }

    /**
     * Returns transactions by address with limit after passed transaction id.
     * @param address address
     * @param limit transactions limit
     * @param after separate transaction id
     * @return list of transactions
     * @throws IOException if something going wrong
     */
    public List<Transaction> getAddressTransactions(String address, int limit, String after) throws IOException {
        String requestUrl = String.format("/transactions/address/%s/limit/%d", address, limit);
        if (after != null) {
            requestUrl += String.format("?after=%s", after);
        }
        return wavesJsonMapper.<List<List<Transaction>>>convertValue(send(requestUrl),new TypeReference<List<List<Transaction>>>(){}).get(0);
    }

    /**
     * Returns block at given height.
     *
     * @param height blockchain height
     * @return block object
     * @throws IOException if no block exists at the given height
     */
    public Block getBlock(int height) throws IOException {
        return wavesJsonMapper.convertValue(send("/blocks/at/" + height), Block.class);
    }

    /**
     * Returns block header at given height.
     *
     * @param height blockchain height
     * @return block object without transactions
     * @throws IOException if no block exists at the given height
     */
    public BlockHeader getBlockHeader(int height) throws IOException {
        return wavesJsonMapper.convertValue(send("/blocks/headers/at/" + height), BlockHeader.class);
    }


    /**
     * Returns last block header
     *
     * @return block object without transactions
     * @throws IOException if no block exists at the given height
     */
    public BlockHeader getLastBlockHeader() throws IOException {
        return wavesJsonMapper.convertValue(send("/blocks/headers/last"), BlockHeader.class);
    }

    /**
     * Returns seq of block headers
     *
     * @param from start block
     * @param to   end block
     * @return sequences of block objects without transactions
     * @throws IOException if no block exists at the given height
     */
    public List<BlockHeader> getBlockHeaderSeq(int from, int to) throws IOException {
        String path = String.format("/blocks/headers/seq/%s/%s", from, to);
        HttpResponse r = exec(request(path));
        return parse(r, BLOCK_HEADER_LIST);
    }

    /**
     * Returns block by its signature.
     *
     * @param signature block signature
     * @return block object
     * @throws IOException if no block with the given signature exists
     */
    public Block getBlock(String signature) throws IOException {
        return wavesJsonMapper.convertValue(send("/blocks/signature/" + signature), Block.class);
    }

    public boolean validateAddresses(String address) throws IOException {
        return send("/addresses/validate/" + address, "valid").asBoolean();
    }

    public String getAddrByAlias(String alias) throws IOException {
        return send("/alias/by-alias/" + alias, "address").textValue();
    }

    /**
     * Sends a signed object and returns its ID.
     *
     * @param tx signed object (as created by static methods in Transaction class)
     * @return Transaction ID
     * @throws IOException
     */
    public String send(Transaction tx) throws IOException {
        return parse(exec(request(tx)), "id").asText();
    }

    private JsonNode send(String path, String... key) throws IOException {
        return parse(exec(request(path)), key);
    }

    public String transfer(PrivateKeyAccount from, String recipient, long amount, long fee, ByteString attachment) throws IOException {
        TransferTransactionV2 tx = Transactions.makeTransferTx(from, recipient, amount, null, fee, null, attachment);
        return send(tx);
    }

    public String transfer(PrivateKeyAccount from, String recipient, long amount, long fee, String message) throws IOException {
        TransferTransactionV2 tx = Transactions.makeTransferTx(from, recipient, amount, null, fee, null, message);
        return send(tx);
    }

    public String transfer(PrivateKeyAccount from, String recipient,
                           long amount, String assetId, long fee, String feeAssetId, String message) throws IOException {
        TransferTransactionV2 tx = Transactions.makeTransferTx(from, recipient, amount, assetId, fee, feeAssetId, message);
        return send(tx);
    }

    public String lease(PrivateKeyAccount from, String recipient, long amount, long fee) throws IOException {
        LeaseTransaction tx = Transactions.makeLeaseTx(from, recipient, amount, fee);
        return send(tx);
    }

    public String cancelLease(PrivateKeyAccount account, byte chainId, String txId, long fee) throws IOException {
        return send(Transactions.makeLeaseCancelTx(account, chainId, txId, fee));
    }

    public String issueAsset(PrivateKeyAccount account, byte chainId, String name, String description, long quantity,
                             byte decimals, boolean reissuable, String script, long fee) throws IOException {
        return send(Transactions.makeIssueTx(account, chainId, name, description, quantity, decimals, reissuable, script, fee));
    }

    public String reissueAsset(PrivateKeyAccount account, byte chainId, String assetId, long quantity, boolean reissuable, long fee) throws IOException {
        return send(Transactions.makeReissueTx(account, chainId, assetId, quantity, reissuable, fee));
    }

    public String burnAsset(PrivateKeyAccount account, byte chainId, String assetId, long amount, long fee) throws IOException {
        return send(Transactions.makeBurnTx(account, chainId, assetId, amount, fee));
    }

    public String sponsorAsset(PrivateKeyAccount account, String assetId, long minAssetFee, long fee) throws IOException {
        return send(Transactions.makeSponsorTx(account, assetId, minAssetFee, fee));
    }

    public String alias(PrivateKeyAccount account, byte chainId, String alias, long fee) throws IOException {
        return send(Transactions.makeAliasTx(account, alias, chainId, fee));
    }

    public String massTransfer(PrivateKeyAccount from, String assetId, Collection<Transfer> transfers, long fee, String message) throws IOException {
        return send(Transactions.makeMassTransferTx(from, assetId, transfers, fee, message));
    }

    public String invokeScript(PrivateKeyAccount from, byte chainId, String dApp, FunctionCall call, List<Payment> payments, long fee, String feeAssetId, long timestamp)throws IOException{
        return send(Transactions.makeInvokeScriptTx(from, chainId, dApp, call, payments, fee, feeAssetId, timestamp));
    }

    public String invokeScript(PrivateKeyAccount from, byte chainId, String dApp, FunctionCall call, List<Payment> payments, long fee, String feeAssetId)throws IOException{
        return send(Transactions.makeInvokeScriptTx(from, chainId, dApp, call, payments, fee, feeAssetId));
    }

    public String invokeScript(PrivateKeyAccount from, byte chainId, String dApp, String functionName, long fee, String feeAssetId, long timestamp)throws IOException{
        return send(Transactions.makeInvokeScriptTx(from, chainId, dApp, functionName, fee, feeAssetId, timestamp));
    }

    /**
     * send invoke script tx with call function without arguments
     * @param from account private key
     * @param chainId chain id
     * @param dApp dapp address
     * @param functionName function name to call
     * @param fee threasaction fee
     * @param feeAssetId transaction fee
     * @return invoke script transaction id
     * @throws IOException
     */
    public String invokeScriptTx(PrivateKeyAccount from, byte chainId, String dApp, String functionName, long fee, String feeAssetId)throws IOException{
        return send(Transactions.makeInvokeScriptTx(from, chainId, dApp, functionName, fee, feeAssetId));
    }

    /**
     * send invoke script tx withour payments
     * @param from account private key
     * @param chainId chain id
     * @param dApp dapp address
     * @param call function call
     * @param fee threasaction fee
     * @param feeAssetId transaction fee
     * @param timestamp tx timestamp
     * @return invoke script transaction id
     * @throws IOException
     */
    public String invokeScriptTx(PrivateKeyAccount from, byte chainId, String dApp, FunctionCall call, long fee, String feeAssetId, long timestamp)throws IOException{
        return send(Transactions.makeInvokeScriptTx(from, chainId, dApp, call, fee, feeAssetId, timestamp));
    }

    /**
     * send invoke script tx withour payments
     * @param from account private key
     * @param chainId chain id
     * @param dApp dapp address
     * @param call function call
     * @param fee threasaction fee
     * @param feeAssetId transaction fee
     * @return invoke script transaction id
     * @throws IOException
     */
    public String invokeScriptTx(PrivateKeyAccount from, byte chainId, String dApp, FunctionCall call, long fee, String feeAssetId)throws IOException{
        return send(Transactions.makeInvokeScriptTx(from, chainId, dApp, call, fee, feeAssetId));
    }

    public String data(PrivateKeyAccount from, Collection<DataEntry<?>> data, long fee) throws IOException {
        return send(Transactions.makeDataTx(from, data, fee));
    }

    public String exchange(PrivateKeyAccount from, Order buyOrder, Order sellOrder, long amount,
                                     long price, long buyMatcherFee, long sellMatcherFee, long fee) throws IOException {
        return send(Transactions.makeExchangeTx(from, buyOrder, sellOrder, amount, price, buyMatcherFee, sellMatcherFee, fee));
    }



    /**
     * Sets a validating script for an account.
     *
     * @param from    the account
     * @param script  script text
     * @param chainId chain ID
     * @param fee     object fee
     * @return object ID
     * @throws IOException if an error occurs
     * @see Account#MAINNET
     * @see Account#TESTNET
     */
    public String setScript(PrivateKeyAccount from, String script, byte chainId, long fee) throws IOException {
        return send(Transactions.makeScriptTx(from, compileScript(script), chainId, fee));
    }

    /**
     * Compiles a script.
     *
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
        Order tx = Transactions.makeOrder(account, matcherKey, orderType, assetPair, price, amount, expiration, matcherFee);
        JsonNode tree = parse(exec(request(tx)));
        // fix order status
        ObjectNode message = (ObjectNode) tree.get("message");
        message.put("status", tree.get("status").asText());
        return wavesJsonMapper.treeToValue(tree.get("message"), Order.class);
    }

    public String cancelOrder(PrivateKeyAccount account, AssetPair assetPair, String orderId) throws IOException {
        ApiJson tx = Transactions.makeOrderCancel(account, assetPair, orderId);
        return parse(exec(request(tx)), "status").asText();
    }

    public String cancelOrdersbyPair(PrivateKeyAccount account, AssetPair assetPair) throws IOException {
        ApiJson tx = Transactions.makeOrderCancel(account, assetPair);
        return parse(exec(request(tx)), "status").asText();
    }

    public String cancelAllOrders(PrivateKeyAccount account) throws IOException {
        ApiJson tx = Transactions.makeOrderCancel(account);
        return parse(exec(request(tx)), "status").asText();
    }

    @Deprecated
    public String deleteOrder(PrivateKeyAccount account, AssetPair assetPair, String orderId) throws IOException {
        ApiJson tx = Transactions.makeDeleteOrder(account, assetPair, orderId);
        return parse(exec(request(tx)), "status").asText();
    }

    public OrderBook getOrderBook(AssetPair assetPair) throws IOException {
        String path = "/matcher/orderbook/" + assetPair.getAmountAsset() + '/' + assetPair.getPriceAsset();
        return parse(exec(request(path)), ORDER_BOOK);
    }

    public OrderStatusInfo getOrderStatus(String orderId, AssetPair assetPair) throws IOException {
        String path = "/matcher/orderbook/" + assetPair.getAmountAsset() + '/' + assetPair.getPriceAsset() + '/' + orderId;
        return parse(exec(request(path)), ORDER_STATUS);
    }

    public List<Order> getOrders(PrivateKeyAccount account) throws IOException {
        return getOrders(account, "/matcher/orderbook/" + Base58.encode(account.getPublicKey()));
    }

    public List<Order> getOrders(PrivateKeyAccount account, AssetPair market) throws IOException {
        return getOrders(account, String.format("/matcher/orderbook/%s/%s/publicKey/%s",
                market.getAmountAsset(), market.getPriceAsset(), Base58.encode(account.getPublicKey())));
    }

    public String getOrderHistorySignature(PrivateKeyAccount account, long timestamp) {
        ByteBuffer buf = ByteBuffer.allocate(40);
        buf.put(account.getPublicKey()).putLong(timestamp);
        return account.sign(buf.array());
    }

    private List<Order> getOrders(PrivateKeyAccount account, String path) throws IOException {
        long timestamp = System.currentTimeMillis();
        String signature = getOrderHistorySignature(account, timestamp);
        HttpResponse r = exec(request(path, "Timestamp", String.valueOf(timestamp), "Signature", signature));
        return parse(r, ORDER_LIST);
    }


    public Map<String, Long> getTradableBalance(AssetPair pair, String address) throws IOException {
        String path = String.format("/matcher/orderbook/%s/%s/tradableBalance/%s", pair.getAmountAsset(), pair.getPriceAsset(), address);
        HttpResponse r = exec(request(path));
        return parse(r, RESERVED);
    }

    public Map<String, Long> getReservedBalance(PrivateKeyAccount account) throws IOException {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(40);
        buf.put(account.getPublicKey()).putLong(timestamp);
        String signature = account.sign(buf.array());
        HttpResponse r = exec(request(String.format("/matcher/balance/reserved/%s", Base58.encode(account.getPublicKey())), "Timestamp", String.valueOf(timestamp), "Signature", signature));
        return parse(r, RESERVED);
    }

    private <T> HttpUriRequest request(String path, String... headers) {
        HttpUriRequest req = new HttpGet(uri.resolve(path));
        for (int i = 0; i < headers.length; i += 2) {
            req.addHeader(headers[i], headers[i + 1]);
        }
        return req;
    }

    private HttpUriRequest request(ApiJson obj) throws JsonProcessingException {
        String endpoint;
        if (obj instanceof Transaction) {
            endpoint = "/transactions/broadcast";
        } else if (obj instanceof Order) {
            endpoint = "/matcher/orderbook";
        } else if (obj instanceof DeleteOrder) {
            DeleteOrder d = (DeleteOrder) obj;
            endpoint = "/matcher/orderbook/" + d.getAssetPair().getAmountAsset() + '/' + d.getAssetPair().getPriceAsset() + "/delete";
        } else if (obj instanceof CancelOrder) {
            CancelOrder co = (CancelOrder) obj;
            if (co.getAssetPair() == null) {
                endpoint = "/matcher/orderbook/cancel";
            } else
                endpoint = "/matcher/orderbook/" + co.getAssetPair().getAmountAsset() + '/' + co.getAssetPair().getPriceAsset() + "/cancel";
        } else {
            throw new IllegalArgumentException();
        }
        HttpPost request = new HttpPost(uri.resolve(endpoint));
        request.setEntity(new StringEntity(wavesJsonMapper.writeValueAsString(obj), ContentType.APPLICATION_JSON));
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

    private <T> T parse(HttpResponse r, TypeReference<T> ref) throws IOException {
        return wavesJsonMapper.readValue(r.getEntity().getContent(), ref);
    }

    private JsonNode parse(HttpResponse r, String... keys) throws IOException {
        JsonNode tree = wavesJsonMapper.readTree(r.getEntity().getContent());
        for (String key : keys) {
            tree = tree.get(key);
        }
        return tree;
    }
}
