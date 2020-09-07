package com.wavesplatform.wavesj;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wavesplatform.wavesj.exceptions.NodeException;
import com.wavesplatform.wavesj.json.TypeRef;
import com.wavesplatform.wavesj.json.WavesJMapper;
import im.mak.waves.transactions.WavesConfig;
import im.mak.waves.transactions.account.Address;
import im.mak.waves.transactions.LeaseTransaction;
import im.mak.waves.transactions.Transaction;
import im.mak.waves.transactions.common.*;
import im.mak.waves.transactions.data.DataEntry;
import im.mak.waves.transactions.serializers.json.JsonSerializer;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static im.mak.waves.transactions.serializers.json.JsonSerializer.JSON_MAPPER;

@SuppressWarnings("unused")
public class Node {

    private final byte chainId;
    private final HttpClient client;
    private final URI uri;
    private final WavesJMapper mapper;

    public Node(URI uri) throws IOException, NodeException {
        this.uri = uri;
        this.client = HttpClients
                .custom()
                .setDefaultRequestConfig(
                        RequestConfig.custom()
                                .setSocketTimeout(5000)
                                .setConnectTimeout(5000)
                                .setConnectionRequestTimeout(5000)
                                .setCookieSpec(CookieSpecs.STANDARD)
                                .build())
                .build();
        this.mapper = new WavesJMapper();
        this.chainId = getAddresses().get(0).chainId();
        WavesConfig.chainId(this.chainId);
    }

    public Node(String url) throws URISyntaxException, IOException, NodeException {
        this(new URI(url));
    }

    public Node(Profile profile) throws IOException, NodeException {
        this(profile.uri());
    }

    public byte chainId() {
        return chainId;
    }

    public HttpClient client() {
        return client;
    }

    public URI uri() {
        return uri;
    }

    //===============
    // ADDRESSES
    //===============

    public List<Address> getAddresses() throws IOException, NodeException {
        return asType(get("/addresses"), TypeRef.ADDRESSES);
    }

    public List<Address> getAddresses(int fromIndex, int toIndex) throws IOException, NodeException {
        return asType(get("/addresses/seq/" + fromIndex + "/" + toIndex), TypeRef.ADDRESSES);
    }

    public long getBalance(Address address) throws IOException, NodeException {
        return asJson(get("/addresses/balance/" + address.toString()))
                .get("balance").asLong();
    }

    public long getBalance(Address address, int confirmations) throws IOException, NodeException {
        return asJson(get("/addresses/balance/" + address.toString() + "/" + confirmations))
                .get("balance").asLong();
    }

    //todo getBalances(height, Address...) for several addresses at height

    public BalanceDetails getBalanceDetails(Address address) throws IOException, NodeException {
        return asType(get("/addresses/balance/details/" + address.toString()), TypeRef.BALANCE_DETAILS);
    }

    public List<DataEntry> getData(Address address) throws IOException, NodeException {
        return asType(get("/addresses/data/" + address.toString()), TypeRef.DATA_ENTRIES);
    }

    public List<DataEntry> getData(Address address, List<String> keys) throws IOException, NodeException {
        ObjectNode jsonBody = JSON_MAPPER.createObjectNode();
        ArrayNode jsonKeys = jsonBody.putArray("keys");
        keys.forEach(jsonKeys::add);
        StringEntity body = new StringEntity(JSON_MAPPER.writeValueAsString(jsonBody), StandardCharsets.UTF_8);

        return asType(post("/addresses/data/" + address.toString())
                .addHeader("Content-Type", "application/json")
                .setEntity(body), TypeRef.DATA_ENTRIES);
    }

    /*
    example to javadoc: Pattern.compile("st.+")
    */
    public List<DataEntry> getData(Address address, Pattern regex) throws IOException, NodeException {
        return asType(get("/addresses/data/" + address.toString())
                .addParameter("matches", regex.toString()), TypeRef.DATA_ENTRIES);
    }

    public DataEntry getData(Address address, String key) throws IOException, NodeException {
        return asType(get("/addresses/data/" + address.toString() + "/" + key), TypeRef.DATA_ENTRY);
    }

    public long getEffectiveBalance(Address address) throws IOException, NodeException {
        return asJson(get("/addresses/effectiveBalance/" + address.toString()))
                .get("balance").asLong();
    }

    public long getEffectiveBalance(Address address, int confirmations) throws IOException, NodeException {
        return asJson(get("/addresses/effectiveBalance/" + address.toString() + "/" + confirmations))
                .get("balance").asLong();
    }

    public ScriptInfo getScriptInfo(Address address) throws IOException, NodeException {
        return asType(get("/addresses/scriptInfo/" + address.toString()), TypeRef.SCRIPT_INFO);
    }

    public ScriptMeta getScriptMeta(Address address) throws IOException, NodeException {
        JsonNode json = asJson(get("/addresses/scriptInfo/" + address.toString() + "/meta"));
        if (json.hasNonNull("meta"))
            return mapper.convertValue(json.get("meta"), TypeRef.SCRIPT_META);
        else
            return new ScriptMeta(0, new HashMap<>());
    }

    //===============
    // ALIAS
    //===============

    public List<Alias> getAliasesByAddress(Address address) throws IOException, NodeException {
        return asType(get("/alias/by-address/" + address.toString()), TypeRef.ALIASES);
    }

    public Address getAddressByAlias(Alias alias) throws IOException, NodeException {
        return Address.as(asJson(get("/alias/by-alias/" + alias.name())).get("address").asText());
    }

    //===============
    // ASSETS
    //===============

    public AssetDistribution getAssetDistribution(AssetId assetId, int height) throws IOException, NodeException {
        return getAssetDistribution(assetId, height, 10);
    }

    public AssetDistribution getAssetDistribution(AssetId assetId, int height, int limit) throws IOException, NodeException {
        return getAssetDistribution(assetId, height, limit, null);
    }

    public AssetDistribution getAssetDistribution(AssetId assetId, int height, int limit, Address after) throws IOException, NodeException {
        RequestBuilder request = get("/assets/" + assetId.toString() + "/distribution/" + height + "/limit/" + limit);
        if (after != null)
            request.addParameter("after", after.toString());
        return asType(request, TypeRef.ASSET_DISTRIBUTION);
    }

    public List<AssetBalance> getAssetsBalance(Address address) throws IOException, NodeException {
        return mapper.readerFor(TypeRef.ASSET_BALANCES)
                .readValue(asJson(get("/assets/balance/" + address.toString())).get("balances"));
    }

    public long getAssetBalance(Address address, AssetId assetId) throws IOException, NodeException {
        return asJson(get("/assets/balance/" + address.toString() + "/" + assetId.toString()))
                .get("balance").asLong();
    }

    public AssetDetails getAssetDetails(AssetId assetId) throws IOException, NodeException {
        return asType(get("/assets/details/" + assetId.toString()).addParameter("full", "true"),
                TypeRef.ASSET_DETAILS);
    }

    //todo what if some asset doesn't exist? (error json with code and message) Either in java?
    public List<AssetDetails> getAssetsDetails(List<AssetId> assetIds) throws IOException, NodeException {
        RequestBuilder request = get("/assets/details").addParameter("full", "true");
        assetIds.forEach(id -> request.addParameter("id", id.toString()));

        return asType(request, TypeRef.ASSETS_DETAILS);
    }

    public List<AssetDetails> getNft(Address address) throws IOException, NodeException {
        return this.getNft(address, 10);
    }

    public List<AssetDetails> getNft(Address address, int limit) throws IOException, NodeException {
        return this.getNft(address, limit, null);
    }

    public List<AssetDetails> getNft(Address address, int limit, AssetId after) throws IOException, NodeException {
        RequestBuilder request = get("/assets/nft/" + address.toString() + "/limit/" + limit);
        if (after != null)
            request.addParameter("after", after.toString());

        return mapper.readValue(asInputStream(request), TypeRef.ASSETS_DETAILS);
    }

    //===============
    // BLOCKCHAIN
    //===============

    /**
     * Returns current blockchain rewards info
     *
     * @return @return Rewards
     */
    public BlockchainRewards getBlockchainRewards() throws IOException, NodeException {
        return asType(get("/blockchain/rewards"), TypeRef.BLOCKCHAIN_REWARDS);
    }

    /**
     * Returns miner’s reward status at height
     *
     * @return Rewards
     */
    public BlockchainRewards getBlockchainRewards(int height) throws IOException, NodeException {
        return asType(get("/blockchain/rewards/" + height), TypeRef.BLOCKCHAIN_REWARDS);
    }

    //===============
    // BLOCKS
    //===============

    public int getHeight() throws IOException, NodeException {
        return asJson(get("/blocks/height")).get("height").asInt();
    }

    public int getBlockHeight(Base58String blockId) throws IOException, NodeException {
        return asJson(get("/blocks/height/" + blockId.toString()))
                .get("height").asInt();
    }

    public int getBlocksDelay(Base58String startBlockId, int blocksNum) throws IOException, NodeException {
        return asJson(get("/blocks/delay/" + startBlockId.toString() + "/" + blocksNum))
                .get("delay").asInt();
    }

    /**
     * Returns block header at given height.
     *
     * @param height blockchain height
     * @return block object without transactions
     * @throws IOException if no block exists at the given height
     */
    public BlockHeaders getBlockHeaders(int height) throws IOException, NodeException {
        return asType(get("/blocks/headers/at/" + height), TypeRef.BLOCK_HEADERS);
    }

    public BlockHeaders getBlockHeaders(Base58String blockId) throws IOException, NodeException {
        return asType(get("/blocks/headers/" + blockId.toString()), TypeRef.BLOCK_HEADERS);
    }

    /**
     * Returns seq of block headers
     *
     * @param fromHeight start block
     * @param toHeight   end block
     * @return sequences of block objects without transactions
     * @throws IOException if no block exists at the given height
     */
    public List<BlockHeaders> getBlocksHeaders(int fromHeight, int toHeight) throws IOException, NodeException {
        return asType(get("/blocks/headers/seq/" + fromHeight + "/" + toHeight), TypeRef.BLOCKS_HEADERS);
    }

    /**
     * Returns last block header
     *
     * @return block object without transactions
     * @throws IOException if no block exists at the given height
     */
    public BlockHeaders getLastBlockHeaders() throws IOException, NodeException {
        return asType(get("/blocks/headers/last"), TypeRef.BLOCK_HEADERS);
    }

    /**
     * Returns block at given height.
     *
     * @param height blockchain height
     * @return block object
     * @throws IOException if no block exists at the given height
     */
    public Block getBlock(int height) throws IOException, NodeException {
        return asType(get("/blocks/at/" + height), TypeRef.BLOCK);
    }

    /**
     * Returns block by its id.
     *
     * @param blockId block id
     * @return block object
     * @throws IOException if no block with the given signature exists
     */
    public Block getBlock(Base58String blockId) throws IOException, NodeException {
        return asType(get("/blocks/" + blockId.toString()), TypeRef.BLOCK);
    }

    public List<Block> getBlocks(int fromHeight, int toHeight) throws IOException, NodeException {
        return asType(get("/blocks/seq/" + fromHeight + "/" + toHeight), TypeRef.BLOCKS);
    }

    public Block getGenesisBlock() throws IOException, NodeException {
        return asType(get("/blocks/first"), TypeRef.BLOCK);
    }

    public Block getLastBlock() throws IOException, NodeException {
        return asType(get("/blocks/last"), TypeRef.BLOCK);
    }

    public List<Block> getBlocksGeneratedBy(Address generator, int fromHeight, int toHeight) throws IOException, NodeException {
        return asType(get(
                "/blocks/address/" + generator.toString() + "/" + fromHeight + "/" + toHeight), TypeRef.BLOCKS);
    }

    //===============
    // NODE
    //===============

    public String getVersion() throws IOException, NodeException {
        return asJson(get("/node/version")).get("version").asText();
    }

    //===============
    // DEBUG
    //===============

    public List<HistoryBalance> getBalanceHistory(Address address) throws IOException, NodeException {
        return asType(get("/debug/balances/history/" + address.toString()), TypeRef.HISTORY_BALANCES);
    }

    public TransactionDebugInfo getStateChanges(Id txId) throws IOException, NodeException {
        return asType(get("/debug/stateChanges/info/" + txId.toString()), TypeRef.TRANSACTION_DEBUG_INFO);
    }

    public List<TransactionDebugInfo> getStateChangesByAddress(Address address, int limit, Id afterTxId) throws IOException, NodeException {
        RequestBuilder request = get("/debug/stateChanges/address/" + address.toString() + "/limit/" + limit);
        if (afterTxId != null)
            request.addParameter("after", afterTxId.toString());

        return asType(request, TypeRef.TRANSACTIONS_DEBUG_INFO);
    }

    public List<TransactionDebugInfo> getStateChangesByAddress(Address address, int limit) throws IOException, NodeException {
        return getStateChangesByAddress(address, limit, null);
    }

    public List<TransactionDebugInfo> getStateChangesByAddress(Address address) throws IOException, NodeException {
        return getStateChangesByAddress(address, 10);
    }

    public <T extends Transaction> Validation validateTransaction(T transaction) throws IOException, NodeException {
        return asType(post("/debug/validate")
                .setEntity(new StringEntity(transaction.toJson(), ContentType.APPLICATION_JSON)), TypeRef.VALIDATION);
    }

    //===============
    // LEASING
    //===============

    public List<LeaseTransaction> getActiveLeases(Address address) throws IOException, NodeException {
        return asType(get("/leasing/active/" + address.toString()), TypeRef.ACTIVE_LEASES);
    }

    //===============
    // TRANSACTIONS
    //===============

    public <T extends Transaction> Amount calculateTransactionFee(T transaction) throws IOException, NodeException {
        JsonNode json = asJson(post("/transactions/calculateFee").setEntity(new StringEntity(transaction.toJson(), ContentType.APPLICATION_JSON)));
        return Amount.of(json.get("feeAmount").asLong(), JsonSerializer.assetIdFromJson(json.get("feeAssetId")));
    }

    /*
    example to javadoc: IssueTransaction tx = broadcast(IssueTransaction.with("", 1, 0).get());
     */
    public <T extends Transaction> T broadcast(T transaction) throws IOException, NodeException {
        //noinspection unchecked
        return (T) asType(post("/transactions/broadcast")
                        .setEntity(new StringEntity(transaction.toJson(), ContentType.APPLICATION_JSON)),
                TypeRef.TRANSACTION);
    }

    /**
     * Returns object by its ID.
     *
     * @param txId object ID
     * @return object object
     * @throws IOException if no object with the given ID exists
     */
    public TransactionInfo getTransactionInfo(Id txId) throws IOException, NodeException {
        return asType(get("/transactions/info/" + txId.toString()), TypeRef.TRANSACTION_INFO);
    }

    /**
     * Returns 10 last transactions by address.
     *
     * @param address address
     * @return list of transactions
     * @throws IOException if something going wrong
     */
    public List<TransactionInfo> getTransactionsByAddress(Address address) throws IOException, NodeException {
        return getTransactionsByAddress(address, 10);
    }

    /**
     * Returns transactions by address with limit.
     *
     * @param address address
     * @param limit   transactions limit
     * @return list of transactions
     * @throws IOException if something going wrong
     */
    public List<TransactionInfo> getTransactionsByAddress(Address address, int limit) throws IOException, NodeException {
        return getTransactionsByAddress(address, limit, null);
    }

    /**
     * Returns transactions by address with limit after passed transaction id.
     *
     * @param address   address
     * @param limit     transactions limit
     * @param afterTxId separate transaction id
     * @return list of transactions
     * @throws IOException if something going wrong
     */
    public List<TransactionInfo> getTransactionsByAddress(Address address, int limit, Id afterTxId) throws IOException, NodeException {
        RequestBuilder request = get("/transactions/address/" + address.toString() + "/limit/" + limit);
        if (afterTxId != null)
            request.addParameter("after", afterTxId.toString());

        //because there is a bug in the Node api: the array of transactions is nested in another array:
        // [ [ {}, {}, ... ] ]
        return mapper
                .readerFor(TypeRef.TRANSACTIONS_INFO)
                .readValue(asJson(request).get(0));
    }

    public TransactionStatus getTransactionStatus(Id txId) throws IOException, NodeException {
        return asType(get("/transactions/status").addParameter("id", txId.toString()),
                TypeRef.TRANSACTIONS_STATUSES).get(0);
    }

    public List<TransactionStatus> getTransactionsStatus(List<Id> txIds) throws IOException, NodeException {
        ObjectNode jsonBody = JSON_MAPPER.createObjectNode();
        ArrayNode jsonIds = jsonBody.putArray("ids");
        txIds.forEach(id -> jsonIds.add(id.toString()));
        StringEntity body = new StringEntity(JSON_MAPPER.writeValueAsString(jsonBody), StandardCharsets.UTF_8);

        return asType(post("/transactions/status")
                .addHeader("Content-Type", "application/json")
                .setEntity(body), TypeRef.TRANSACTIONS_STATUSES);
    }

    public List<TransactionStatus> getTransactionsStatus(Id... txIds) throws IOException, NodeException {
        return getTransactionsStatus(new ArrayList<>(Arrays.asList(txIds)));
    }

    public Transaction getUnconfirmedTransaction(Id txId) throws IOException, NodeException {
        return asType(get("/transactions/unconfirmed/info/" + txId.toString()), TypeRef.TRANSACTION);
    }

    public List<Transaction> getUnconfirmedTransactions() throws IOException, NodeException {
        return asType(get("/transactions/unconfirmed"), TypeRef.TRANSACTIONS);
    }

    public int getUtxSize() throws IOException, NodeException {
        return asJson(get("/transactions/unconfirmed/size")).get("size").asInt();
    }

    //===============
    // UTILS
    //===============

    public ScriptInfo compileScript(String source) throws IOException, NodeException {
        return asType(post("/utils/script/compileCode")
                        .addHeader("Content-Type", "text/plain")
                        .setEntity(new StringEntity(source, StandardCharsets.UTF_8)),
                TypeRef.SCRIPT_INFO);
    }

    //===============
    // HTTP REQUESTS
    //===============

    private RequestBuilder get(String path) {
        return RequestBuilder.get(uri.resolve(path));
    }

    private RequestBuilder post(String path) {
        return RequestBuilder.post(uri.resolve(path));
    }

    private HttpResponse exec(HttpUriRequest request) throws IOException, NodeException {
        HttpResponse r = client.execute(request);
        if (r.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
            throw mapper.readValue(r.getEntity().getContent(), NodeException.class);
        return r;
    }

    private InputStream asInputStream(RequestBuilder request) throws IOException, NodeException {
        return exec(request.build()).getEntity().getContent();
    }

    private <T> T asType(RequestBuilder request, TypeReference<T> reference) throws IOException, NodeException {
        return mapper.readValue(asInputStream(request), reference);
    }

    private JsonNode asJson(RequestBuilder request) throws IOException, NodeException {
        return JSON_MAPPER.readTree(asInputStream(request));
    }

}

