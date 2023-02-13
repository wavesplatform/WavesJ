package com.wavesplatform.wavesj;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.transactions.Transaction;
import com.wavesplatform.transactions.WavesConfig;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.*;
import com.wavesplatform.transactions.data.DataEntry;
import com.wavesplatform.transactions.serializers.json.JsonSerializer;
import com.wavesplatform.wavesj.actions.EthRpcRequest;
import com.wavesplatform.wavesj.actions.EthRpcResponse;
import com.wavesplatform.wavesj.exceptions.NodeException;
import com.wavesplatform.wavesj.info.TransactionInfo;
import com.wavesplatform.wavesj.json.TypeRef;
import com.wavesplatform.wavesj.json.WavesJMapper;
import com.wavesplatform.wavesj.peers.BlacklistedPeer;
import com.wavesplatform.wavesj.peers.ConnectedPeer;
import com.wavesplatform.wavesj.peers.Peer;
import com.wavesplatform.wavesj.peers.SuspendedPeer;
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
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static com.wavesplatform.transactions.serializers.json.JsonSerializer.JSON_MAPPER;
import static com.wavesplatform.wavesj.Status.CONFIRMED;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

@SuppressWarnings("unused")
public class Node {

    private final byte chainId;
    private final HttpClient client;
    private final URI uri;
    private final WavesJMapper mapper;
    private final int blockInterval = 60;

    public Node(URI uri, HttpClient httpClient) throws IOException, NodeException {
        this.uri = uri;
        this.client = httpClient;
        this.mapper = new WavesJMapper();
        this.chainId = getAddresses().get(0).chainId();
        WavesConfig.chainId(this.chainId);
    }

    public Node(String url, HttpClient httpClient) throws URISyntaxException, IOException, NodeException {
        this(new URI(url), httpClient);
    }

    public Node(Profile profile, HttpClient httpClient) throws IOException, NodeException {
        this(profile.uri(), httpClient);
    }

    public Node(URI uri) throws IOException, NodeException {
        this.uri = uri;
        this.client = HttpClients
                .custom()
                .setDefaultRequestConfig(
                        RequestConfig.custom()
                                .setSocketTimeout(60000)
                                .setConnectTimeout(60000)
                                .setConnectionRequestTimeout(60000)
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
    // ACTIVATION
    //===============

    public ActivationStatus getActivationStatus() throws IOException, NodeException {
        return asType(get("/activation/status"), TypeRef.ACTIVATION_STATUS);
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

    public List<Balance> getBalances(List<Address> addresses) throws IOException, NodeException {
        ObjectNode jsonBody = JSON_MAPPER.createObjectNode();
        ArrayNode jsonAddresses = jsonBody.putArray("addresses");
        addresses.forEach(address -> jsonAddresses.add(address.toString()));

        StringEntity body = new StringEntity(JSON_MAPPER.writeValueAsString(jsonBody), StandardCharsets.UTF_8);

        return asType(post("/addresses/balance")
                .addHeader("Content-Type", "application/json")
                .setEntity(body), TypeRef.BALANCES);
    }

    public List<Balance> getBalances(List<Address> addresses, int height) throws IOException, NodeException {
        ObjectNode jsonBody = JSON_MAPPER.createObjectNode();
        ArrayNode jsonAddresses = jsonBody.putArray("addresses");
        addresses.forEach(address -> jsonAddresses.add(address.toString()));

        jsonBody.put("height", height);
        StringEntity body = new StringEntity(JSON_MAPPER.writeValueAsString(jsonBody), StandardCharsets.UTF_8);

        return asType(post("/addresses/balance")
                .addHeader("Content-Type", "application/json")
                .setEntity(body), TypeRef.BALANCES);
    }

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

    @Deprecated
    public long getEffectiveBalance(Address address) throws IOException, NodeException {
        return asJson(get("/addresses/effectiveBalance/" + address.toString()))
                .get("balance").asLong();
    }

    @Deprecated
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
        return getAssetDistribution(assetId, height, 1000);
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

    public List<AssetBalance> getAssetsBalance(Address address, List<AssetId> assetIds) throws IOException, NodeException {
        ObjectNode jsonBody = JSON_MAPPER.createObjectNode();
        ArrayNode jsonAssetIds = jsonBody.putArray("ids");
        assetIds.forEach(id -> jsonAssetIds.add(id.toString()));
        StringEntity body = new StringEntity(JSON_MAPPER.writeValueAsString(jsonBody), StandardCharsets.UTF_8);

        return mapper.readerFor(TypeRef.ASSET_BALANCES)
                .readValue(
                        asJson(
                                post("/assets/balance/" + address.toString())
                                        .setEntity(body)
                                        .addHeader("Content-Type", "application/json")
                        ).get("balances"));
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
        return this.getNft(address, 1000);
    }

    public List<AssetDetails> getNft(Address address, int limit) throws IOException, NodeException {
        return this.getNft(address, limit, null);
    }

    public List<AssetDetails> getNft(Address address, int limit, AssetId after) throws IOException, NodeException {
        RequestBuilder request = get("/assets/nft/" + address.toString() + "/limit/" + limit);
        if (after != null)
            request.addParameter("after", after.toString());

        return asType(request, TypeRef.ASSETS_DETAILS);
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

    public int getBlockHeight(long timestamp) throws IOException, NodeException {
        return asJson(get("/blocks/heightByTimestamp/" + timestamp))
                .get("height").asInt();
    }

    public long getBlocksDelay(Base58String startBlockId, int blocksNum) throws IOException, NodeException {
        return asJson(get("/blocks/delay/" + startBlockId.toString() + "/" + blocksNum))
                .get("delay").asLong();
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

    public NodeStatus getStatus() throws IOException, NodeException {
        return asType(get("/node/status"), TypeRef.NODE_STATUS);
    }

    //===============
    // PEERS
    //===============

    public List<Peer> getAllPeers() throws NodeException, IOException {
        return mapper.readerFor(TypeRef.ALL_PEERS)
                .readValue(asJson(get("/peers/all")).get("peers"));
    }

    public List<BlacklistedPeer> getBlacklistedPeers() throws NodeException, IOException {
        return asType(get("/peers/blacklisted"), TypeRef.BLACKLISTED_PEERS);
    }

    public List<ConnectedPeer> getConnectedPeers() throws NodeException, IOException {
        return mapper.readerFor(TypeRef.CONNECTED_PEERS)
                .readValue(asJson(get("/peers/connected")).get("peers"));
    }

    public List<SuspendedPeer> getSuspendedPeers() throws NodeException, IOException {
        return asType(get("/peers/suspended"), TypeRef.SUSPENDED_PEERS);
    }


    //===============
    // DEBUG
    //===============

    public List<HistoryBalance> getBalanceHistory(Address address) throws IOException, NodeException {
        return asType(get("/debug/balances/history/" + address.toString()), TypeRef.HISTORY_BALANCES);
    }

    public <T extends Transaction> Validation validateTransaction(T transaction) throws IOException, NodeException {
        return asType(post("/debug/validate")
                .setEntity(new StringEntity(transaction.toJson(), ContentType.APPLICATION_JSON)), TypeRef.VALIDATION);
    }

    //===============
    // LEASING
    //===============

    public List<LeaseInfo> getActiveLeases(Address address) throws IOException, NodeException {
        return asType(get("/leasing/active/" + address.toString()), TypeRef.LEASES_INFO);
    }

    public LeaseInfo getLeaseInfo(Id leaseId) throws IOException, NodeException {
        return asType(get("/leasing/info/" + leaseId.toString()), TypeRef.LEASE_INFO);
    }

    public List<LeaseInfo> getLeasesInfo(List<Id> leaseIds) throws IOException, NodeException {
        ObjectNode jsonBody = JSON_MAPPER.createObjectNode();
        ArrayNode jsonIds = jsonBody.putArray("ids");
        leaseIds.forEach(id -> jsonIds.add(id.toString()));
        StringEntity body = new StringEntity(JSON_MAPPER.writeValueAsString(jsonBody), StandardCharsets.UTF_8);

        return asType(post("/leasing/info")
                .addHeader("Content-Type", "application/json")
                .setEntity(body), TypeRef.LEASES_INFO);
    }

    public List<LeaseInfo> getLeasesInfo(Id... leaseIds) throws IOException, NodeException {
        return getLeasesInfo(asList(leaseIds));
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

    public EthRpcResponse broadcastEthTransaction(EthereumTransaction ethTransaction) throws IOException, NodeException {
        HttpUriRequest rq = buildSendRawTransactionRq(ethTransaction.toRawHexString());
        ObjectNode rs = sendEthRequest(rq);
        return handleEthResponse(rs);
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
     * Returns object by its ID.
     *
     * @param txId object ID
     * @return object object
     * @throws IOException if no object with the given ID exists
     */
    public <T extends TransactionInfo> T getTransactionInfo(Id txId, Class<T> transactionInfoClass) throws IOException, NodeException {
        return transactionInfoClass.cast(
                asType(get("/transactions/info/" + txId.toString()), TypeRef.TRANSACTION_INFO));
    }

    /**
     * @param txIds IDs of transactions
     * @return info about requested transactions
     * @throws IOException
     * @throws NodeException
     */
    public List<TransactionInfo> getTransactionsInfo(List<Id> txIds) throws IOException, NodeException {
        BasicNameValuePair[] params = txIds
                .stream()
                .map(id -> new BasicNameValuePair("id", id.toString()))
                .toArray(BasicNameValuePair[]::new);

        return asType(get("/transactions/info").addParameters(params), TypeRef.TRANSACTIONS_INFO);
    }

    /**
     * @param txIds                IDs of transactions
     * @param transactionInfoClass info class for all the requested transactions
     * @return typed info about requested transactions. Use this method ONLY if you're sure that all requested transactions are of the same type (for example, txIds contains only InvokeScript transactions)
     * @throws IOException
     * @throws NodeException
     */
    public <T extends TransactionInfo> List<T> getTransactionsInfo(List<Id> txIds, Class<T> transactionInfoClass) throws IOException, NodeException {
        return getTransactionsInfo(txIds)
                .stream()
                .map(transactionInfoClass::cast)
                .collect(toCollection(ArrayList::new));
    }

    /**
     * Returns 10 last transactions by address.
     *
     * @param address address
     * @return list of transactions
     * @throws IOException if something going wrong
     */
    public List<TransactionInfo> getTransactionsByAddress(Address address) throws IOException, NodeException {
        return getTransactionsByAddress(address, 1000);
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
                TypeRef.TRANSACTIONS_STATUS).get(0);
    }

    public List<TransactionStatus> getTransactionsStatus(List<Id> txIds) throws IOException, NodeException {
        ObjectNode jsonBody = JSON_MAPPER.createObjectNode();
        ArrayNode jsonIds = jsonBody.putArray("ids");
        txIds.forEach(id -> jsonIds.add(id.toString()));
        StringEntity body = new StringEntity(JSON_MAPPER.writeValueAsString(jsonBody), StandardCharsets.UTF_8);

        return asType(post("/transactions/status")
                .addHeader("Content-Type", "application/json")
                .setEntity(body), TypeRef.TRANSACTIONS_STATUS);
    }

    public List<TransactionStatus> getTransactionsStatus(Id... txIds) throws IOException, NodeException {
        return getTransactionsStatus(asList(txIds));
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

    /**
     * For local compilation use {@link com.wavesplatform.wavesj.util.CompilationUtil#compile(String, boolean, boolean)}
     */
    public ScriptInfo compileScript(String source) throws IOException, NodeException {
        return compileScript(source, false);
    }

    /**
     * For local compilation use {@link com.wavesplatform.wavesj.util.CompilationUtil#compile(String, boolean, boolean)}
     */
    public ScriptInfo compileScript(String source, boolean enableCompaction) throws IOException, NodeException {
        return asType(post("/utils/script/compileCode")
                        .addHeader("Content-Type", "text/plain")
                        .addParameter("compact", enableCompaction ? "true" : "false")
                        .setEntity(new StringEntity(source, StandardCharsets.UTF_8)),
                TypeRef.SCRIPT_INFO);
    }

    public String decompileScript(Base64String compiledScript) throws IOException, NodeException {
        return asJson(post("/utils/script/decompile")
                .addHeader("Content-Type", "application/json")
                .setEntity(new StringEntity(compiledScript.toString(), StandardCharsets.UTF_8)))
                .get("script")
                .asText();
    }

    public String ethToWavesAsset(String asset) throws NodeException, IOException {
        return asType(
                get("/eth/assets").addParameter("id", asset),
                TypeRef.ASSETS_DETAILS
        ).get(0).assetId().encoded();
    }

    //===============
    // WAITINGS
    //===============

    public TransactionInfo waitForTransaction(Id id, int waitingInSeconds) throws IOException {
        int pollingIntervalInMillis = 100;

        if (waitingInSeconds < 1)
            throw new IllegalStateException("waitForTransaction: waiting value must be positive. Current: " + waitingInSeconds);

        Exception lastException = null;
        for (long spentMillis = 0; spentMillis < waitingInSeconds * 1000L; spentMillis += pollingIntervalInMillis) {
            try {
                if (this.getTransactionStatus(id).status() == CONFIRMED) return this.getTransactionInfo(id);
                else Thread.sleep(pollingIntervalInMillis);
            } catch (Exception e) {
                lastException = e;
                try {
                    Thread.sleep(pollingIntervalInMillis);
                } catch (InterruptedException ignored) {
                }
            }
        }
        throw new IOException("Could not wait for transaction " + id + " in " + waitingInSeconds + " seconds", lastException);
    }

    public TransactionInfo waitForTransaction(Id id) throws IOException {
        return waitForTransaction(id, blockInterval);
    }

    public <T extends TransactionInfo> T waitForTransaction(Id id, Class<T> infoClass) throws IOException {
        return infoClass.cast(waitForTransaction(id));
    }

    public <T extends Transaction> TransactionInfo waitForTransaction(T tx) throws IOException {
        return waitForTransaction(tx.id());
    }

    public <TI extends TransactionInfo, T extends Transaction> TI waitForTransaction(T tx, Class<TI> infoClass) throws IOException {
        return waitForTransaction(tx.id(), infoClass);
    }

    public void waitForTransactions(List<Id> ids, int waitingInSeconds) throws IOException, NodeException {
        int pollingIntervalInMillis = 1000;

        if (waitingInSeconds < 1)
            throw new IllegalStateException("waitForTransaction: waiting value must be positive. Current: " + waitingInSeconds);

        Exception lastException = null;
        for (long spentMillis = 0; spentMillis < waitingInSeconds * 1000L; spentMillis += pollingIntervalInMillis) {
            try {
                List<TransactionStatus> statuses = this.getTransactionsStatus(ids);
                if (statuses.stream().allMatch(s -> CONFIRMED.equals(s.status())))
                    return;
            } catch (Exception e) {
                lastException = e;
                try {
                    Thread.sleep(pollingIntervalInMillis);
                } catch (InterruptedException ignored) {
                }
            }
        }

        List<TransactionStatus> statuses = this.getTransactionsStatus(ids);
        List<TransactionStatus> unconfirmed =
                statuses.stream().filter(s -> !CONFIRMED.equals(s.status())).collect(toList());
        throw new IOException("Could not wait for " + unconfirmed.size() + " of " + ids.size() +
                " transactions in " + waitingInSeconds + " seconds: " + unconfirmed, lastException);
    }

    public void waitForTransactions(List<Id> ids) throws IOException, NodeException {
        waitForTransactions(ids, blockInterval);
    }

    public void waitForTransactions(Id... ids) throws IOException, NodeException {
        waitForTransactions(asList(ids));
    }

    public int waitForHeight(int target, int waitingInSeconds) throws IOException, NodeException {
        int start = this.getHeight();
        int prev = start;
        int pollingIntervalInMillis = 100;

        if (waitingInSeconds < 1)
            throw new IllegalStateException("waitForHeight: value must be positive. Current: " + waitingInSeconds);

        for (long spentMillis = 0; spentMillis < waitingInSeconds * 1000L; spentMillis += pollingIntervalInMillis) {
            int current = this.getHeight();

            if (current >= target)
                return current;
            else if (current > prev) {
                prev = current;
                spentMillis = 0;
            }

            try {
                Thread.sleep(pollingIntervalInMillis);
            } catch (InterruptedException ignored) {
            }
        }
        throw new IllegalStateException("Could not wait for the height to rise from " + start + " to " + target +
                ": height " + prev + " did not grow for " + waitingInSeconds + " seconds");
    }

    public int waitForHeight(int expectedHeight) throws IOException, NodeException {
        return waitForHeight(expectedHeight, blockInterval * 3);
    }

    public int waitBlocks(int blocksCount, int waitingInSeconds) throws IOException, NodeException {
        if (waitingInSeconds < 1)
            throw new IllegalStateException("waitBlocks: waiting value must be positive. Current: " + waitingInSeconds);
        return waitForHeight(getHeight() + blocksCount, waitingInSeconds);
    }

    public int waitBlocks(int blocksCount) throws IOException, NodeException {
        return waitBlocks(blocksCount, blockInterval * 3);
    }

    //===============
    // HTTP REQUESTS
    //===============

    protected RequestBuilder get(String path) {
        return RequestBuilder.get(uri.resolve(path));
    }

    protected RequestBuilder post(String path) {
        return RequestBuilder.post(uri.resolve(path));
    }

    protected HttpResponse exec(HttpUriRequest request) throws IOException, NodeException {
        HttpResponse r = client.execute(request);
        if (r.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
            throw mapper.readValue(r.getEntity().getContent(), NodeException.class);
        return r;
    }

    protected InputStream asInputStream(RequestBuilder request) throws IOException, NodeException {
        return exec(request.build()).getEntity().getContent();
    }

    protected <T> T asType(RequestBuilder request, TypeReference<T> reference) throws IOException, NodeException {
        return mapper.readValue(asInputStream(request), reference);
    }

    protected JsonNode asJson(RequestBuilder request) throws IOException, NodeException {
        return JSON_MAPPER.readTree(asInputStream(request));
    }

    protected HttpUriRequest buildSendRawTransactionRq(String rawData) throws JsonProcessingException {
        return post("/eth")
                .setEntity(
                        new StringEntity(
                                new EthRpcRequest(
                                        "2.0",
                                        "eth_sendRawTransaction",
                                        Collections.singletonList(rawData),
                                        0
                                ).toJsonString(),
                                ContentType.APPLICATION_JSON)
                )
                .build();
    }

    protected ObjectNode sendEthRequest(HttpUriRequest rq) throws IOException {
        return mapper.readValue(
                client.execute(rq).getEntity().getContent(),
                ObjectNode.class
        );
    }

    protected EthRpcResponse handleEthResponse(ObjectNode rs) throws NodeException, JsonProcessingException {
        JsonNode result = rs.get("result");
        if (result.hasNonNull("error")) {
            throw new NodeException(
                    result.get("error").intValue(),
                    result.get("message").textValue()
            );
        }
        return mapper.treeToValue(rs, EthRpcResponse.class);
    }

}

