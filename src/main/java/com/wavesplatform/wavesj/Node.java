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
import org.web3j.utils.Numeric;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static com.wavesplatform.transactions.serializers.json.JsonSerializer.JSON_MAPPER;
import static com.wavesplatform.wavesj.Status.CONFIRMED;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

/**
 * This class represents a node.
 */
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
    // ADDRESSES
    //===============

    /**
     * Get a list of account addresses in the node wallet
     *
     * @return sequences of addresses
     * @throws IOException
     * @throws NodeException
     */
    public List<Address> getAddresses() throws IOException, NodeException {
        return asType(get("/addresses"), TypeRef.ADDRESSES);
    }

    /**
     * Get a list addresses in the node wallet by a given range of indices. Max range {from}-{to} is 1000 addresses.
     *
     * @param fromIndex start index
     * @param toIndex end index
     * @return sequences of addresses
     * @throws IOException
     * @throws NodeException
     */
    public List<Address> getAddresses(int fromIndex, int toIndex) throws IOException, NodeException {
        return asType(get("/addresses/seq/" + fromIndex + "/" + toIndex), TypeRef.ADDRESSES);
    }

    /**
     * Get the regular balance in WAVES at a given address
     *
     * @param address address base58 encoded
     * @return the balance value
     * @throws IOException
     * @throws NodeException
     */
    public long getBalance(Address address) throws IOException, NodeException {
        return asJson(get("/addresses/balance/" + address.toString()))
                .get("balance").asLong();
    }

    /**
     * Get the minimum regular balance at a given address for {confirmations} blocks back from the current height.
     * Max number of confirmations is set by waves.db.max-rollback-depth, 2000 by default
     *
     * @param address address base58 encoded
     * @param confirmations confirmations
     * @return the balance value
     * @throws IOException
     * @throws NodeException
     */
    public long getBalance(Address address, int confirmations) throws IOException, NodeException {
        return asJson(get("/addresses/balance/" + address.toString() + "/" + confirmations))
                .get("balance").asLong();
    }

    /**
     * Get regular balances for multiple addresses.
     * Max number of addresses is set by waves.rest-api.transactions-by-address-limit, 1000 by default
     *
     * @param addresses sequences of addresses base58 encoded
     * @return sequences of Balance objects
     * @throws IOException
     * @throws NodeException
     */
    public List<Balance> getBalances(List<Address> addresses) throws IOException, NodeException {
        ObjectNode jsonBody = JSON_MAPPER.createObjectNode();
        ArrayNode jsonAddresses = jsonBody.putArray("addresses");
        addresses.forEach(address -> jsonAddresses.add(address.toString()));

        StringEntity body = new StringEntity(JSON_MAPPER.writeValueAsString(jsonBody), StandardCharsets.UTF_8);

        return asType(post("/addresses/balance")
                .addHeader("Content-Type", "application/json")
                .setEntity(body), TypeRef.BALANCES);
    }

    /**
     * Get regular balances for multiple addresses.
     * Max number of addresses is set by waves.rest-api.transactions-by-address-limit, 1000 by default
     *
     * @param addresses sequences of addresses base58 encoded
     * @param height blockchain height
     * @return sequences of Balance objects
     * @throws IOException
     * @throws NodeException
     */
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

    /**
     * Get the available, regular, generating, and effective balance
     *
     * @see <a href="https://docs.waves.tech/en/blockchain/account/account-balance#account-balance-in-waves">definitions</a>
     * @param address address base58 encoded
     * @return BalanceDetails object
     * @throws IOException
     * @throws NodeException
     */
    public BalanceDetails getBalanceDetails(Address address) throws IOException, NodeException {
        return asType(get("/addresses/balance/details/" + address.toString()), TypeRef.BALANCE_DETAILS);
    }

    /**
     * Read account data entries by given keys or a regular expression.
     * Limited by rest-api.data-keys-request-limit, 1000 by default.
     *
     * @param address address base58 encoded
     * @return sequence of DataEntry objects
     * @throws IOException
     * @throws NodeException
     */
    public List<DataEntry> getData(Address address) throws IOException, NodeException {
        return asType(get("/addresses/data/" + address.toString()), TypeRef.DATA_ENTRIES);
    }

    /**
     * Read account data entries by given keys. Limited by rest-api.data-keys-request-limit, 1000 by default.
     *
     * @param address address base58 encoded
     * @param keys sequence of keys of records
     * @return sequence of DataEntry objects
     * @throws IOException
     * @throws NodeException
     */
    public List<DataEntry> getData(Address address, List<String> keys) throws IOException, NodeException {
        ObjectNode jsonBody = JSON_MAPPER.createObjectNode();
        ArrayNode jsonKeys = jsonBody.putArray("keys");
        keys.forEach(jsonKeys::add);
        StringEntity body = new StringEntity(JSON_MAPPER.writeValueAsString(jsonBody), StandardCharsets.UTF_8);

        return asType(post("/addresses/data/" + address.toString())
                .addHeader("Content-Type", "application/json")
                .setEntity(body), TypeRef.DATA_ENTRIES);
    }

    /**
     * Read account data entries by given keys or a regular expression.
     * Limited by rest-api.data-keys-request-limit, 1000 by default.
     *
     * <pre>Pattern.compile("st.+")</pre>
     *
     * @param address address base58 encoded
     * @param regex regular expression for filter keys
     * @return sequence of DataEntry objects
     * @throws IOException
     * @throws NodeException
    */
    public List<DataEntry> getData(Address address, Pattern regex) throws IOException, NodeException {
        return asType(get("/addresses/data/" + address.toString())
                .addParameter("matches", regex.toString()), TypeRef.DATA_ENTRIES);
    }

    /**
     * Read account data entries by a given key
     *
     * @param address address base58 encoded
     * @param key key
     * @return DataEntry object
     * @throws IOException
     * @throws NodeException
     */
    public DataEntry getData(Address address, String key) throws IOException, NodeException {
        return asType(get("/addresses/data/" + address.toString() + "/" + key), TypeRef.DATA_ENTRY);
    }

    /**
     * Get the effective balance in WAVES at a given address
     *
     * @param address address base58 encoded
     * @return the balance value
     * @deprecated {@link #getBalance(Address)}
     * @throws IOException
     * @throws NodeException
     */
    @Deprecated
    public long getEffectiveBalance(Address address) throws IOException, NodeException {
        return asJson(get("/addresses/effectiveBalance/" + address.toString()))
                .get("balance").asLong();
    }

    /**
     * Get the minimum effective balance at a given address for {confirmations} blocks from the current height.
     * Max number of confirmations is set by waves.db.max-rollback-depth, 2000 by default
     *
     * @param address address base58 encoded
     * @param confirmations confirmations
     * @return the balance value
     * @deprecated {@link #getBalance(Address, int)}
     * @throws IOException
     * @throws NodeException
     */
    @Deprecated
    public long getEffectiveBalance(Address address, int confirmations) throws IOException, NodeException {
        return asJson(get("/addresses/effectiveBalance/" + address.toString() + "/" + confirmations))
                .get("balance").asLong();
    }

    /**
     * Get an account script or a dApp script with additional info by a given address
     *
     * @param address address base58 encoded
     * @return ScriptInfo object
     * @throws IOException
     * @throws NodeException
     */
    public ScriptInfo getScriptInfo(Address address) throws IOException, NodeException {
        return asType(get("/addresses/scriptInfo/" + address.toString()), TypeRef.SCRIPT_INFO);
    }

    /**
     * Get an account script meta
     *
     * @param address address base58 encoded
     * @return ScriptMeta object
     * @throws IOException
     * @throws NodeException
     */
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

    /**
     * Get a list of aliases associated with a given address
     *
     * @param address address base58 encoded
     * @return sequence of Alias object
     * @throws IOException
     * @throws NodeException
     */
    public List<Alias> getAliasesByAddress(Address address) throws IOException, NodeException {
        return asType(get("/alias/by-address/" + address.toString()), TypeRef.ALIASES);
    }

    /**
     * Get an address associated with a given alias. Alias should be plain text without an 'alias' prefix and chain ID.
     *
     * @param alias alias
     * @return Address object
     * @throws IOException
     * @throws NodeException
     */
    public Address getAddressByAlias(Alias alias) throws IOException, NodeException {
        return Address.as(asJson(get("/alias/by-alias/" + alias.name())).get("address").asText());
    }

    //===============
    // ASSETS
    //===============

    /**
     * Get asset balance distribution by addresses at a given height.
     * Max number of addresses is set by waves.rest-api.distribution-address-limit, 1000 by default.
     *
     * @param assetId asset ID base58 encoded
     * @param height blockchain height
     * @return AssetDistribution object
     * @throws IOException
     * @throws NodeException
     */
    public AssetDistribution getAssetDistribution(AssetId assetId, int height) throws IOException, NodeException {
        return getAssetDistribution(assetId, height, 1000);
    }

    /**
     * Get asset balance distribution by addresses at a given height.
     * Max number of addresses is set by waves.rest-api.distribution-address-limit, 1000 by default.
     *
     * @param assetId asset ID base58 encoded
     * @param height blockchain height
     * @param limit number of addresses to be returned
     * @return AssetDistribution object
     * @throws IOException
     * @throws NodeException
     */
    public AssetDistribution getAssetDistribution(AssetId assetId, int height, int limit) throws IOException, NodeException {
        return getAssetDistribution(assetId, height, limit, null);
    }

    /**
     * Get asset balance distribution by addresses at a given height.
     * Max number of addresses is set by waves.rest-api.distribution-address-limit, 1000 by default.
     *
     * @param assetId asset ID base58 encoded
     * @param height blockchain height
     * @param limit number of addresses to be returned
     * @param after address
     * @return AssetDistribution object
     * @throws IOException
     * @throws NodeException
     */
    public AssetDistribution getAssetDistribution(AssetId assetId, int height, int limit, Address after) throws IOException, NodeException {
        RequestBuilder request = get("/assets/" + assetId.toString() + "/distribution/" + height + "/limit/" + limit);
        if (after != null)
            request.addParameter("after", after.toString());
        return asType(request, TypeRef.ASSET_DISTRIBUTION);
    }

    /**
     * Get account balances in all or specified assets (excluding WAVES) at a given address.
     *
     * @param address address base58 encoded
     * @return sequence of AssetBalance object
     * @throws IOException
     * @throws NodeException
     */
    public List<AssetBalance> getAssetsBalance(Address address) throws IOException, NodeException {
        return mapper.readerFor(TypeRef.ASSET_BALANCES)
                .readValue(asJson(get("/assets/balance/" + address.toString())).get("balances"));
    }

    /**
     * Get the account balance in a given asset. 0 for non-existent asset
     *
     * @param address address base58 encoded
     * @param assetId asset ID base58 encoded
     * @return value of account balance in a given asset, 0 for non-existent asset
     * @throws IOException
     * @throws NodeException
     */
    public long getAssetBalance(Address address, AssetId assetId) throws IOException, NodeException {
        return asJson(get("/assets/balance/" + address.toString() + "/" + assetId.toString()))
                .get("balance").asLong();
    }

    /**
     * Get detailed information about a given asset
     *
     * @param assetId asset ID base58 encoded
     * @see <a href="https://docs.waves.tech/en/blockchain/token/#custom-token-parameters">fields descriptions</a>
     * @return AssetDetails object
     * @throws IOException
     * @throws NodeException
     */
    public AssetDetails getAssetDetails(AssetId assetId) throws IOException, NodeException {
        return asType(get("/assets/details/" + assetId.toString()).addParameter("full", "true"),
                TypeRef.ASSET_DETAILS);
    }

    //todo what if some asset doesn't exist? (error json with code and message) Either in java?
    /**
     * Get detailed information about a given asset
     *
     * @param assetIds sequence of assets ID's base58 encoded
     * @return sequence of AssetDetails
     * @throws IOException
     * @throws NodeException
     */
    public List<AssetDetails> getAssetsDetails(List<AssetId> assetIds) throws IOException, NodeException {
        RequestBuilder request = get("/assets/details").addParameter("full", "true");
        assetIds.forEach(id -> request.addParameter("id", id.toString()));

        return asType(request, TypeRef.ASSETS_DETAILS);
    }

    /**
     * Get a list of non-fungible tokens at a given address
     *
     * @param address address base58 encoded
     * @return sequence of AssetDetails
     * @throws IOException
     * @throws NodeException
     */
    public List<AssetDetails> getNft(Address address) throws IOException, NodeException {
        return this.getNft(address, 1000);
    }

    /**
     * Get a list of non-fungible tokens at a given address
     *
     * @param address address base58 encoded
     * @param limit number of addresses to be returned
     * @return sequence of AssetDetails
     * @throws IOException
     * @throws NodeException
     */
    public List<AssetDetails> getNft(Address address, int limit) throws IOException, NodeException {
        return this.getNft(address, limit, null);
    }

    /**
     * Get a list of non-fungible tokens at a given address
     *
     * @param address address base58 encoded
     * @param limit number of addresses to be returned
     * @param after ID of the token to paginate after
     * @return sequence of AssetDetails
     * @throws IOException
     * @throws NodeException
     */
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
     * @throws IOException
     * @throws NodeException
     */
    public BlockchainRewards getBlockchainRewards() throws IOException, NodeException {
        return asType(get("/blockchain/rewards"), TypeRef.BLOCKCHAIN_REWARDS);
    }

    /**
     * Returns miner’s reward status at height
     *
     * @return Rewards
     * @throws IOException
     * @throws NodeException
     */
    public BlockchainRewards getBlockchainRewards(int height) throws IOException, NodeException {
        return asType(get("/blockchain/rewards/" + height), TypeRef.BLOCKCHAIN_REWARDS);
    }

    //===============
    // BLOCKS
    //===============

    /**
     * Get the current blockchain height
     *
     * @return current blockchain height
     * @throws IOException
     * @throws NodeException
     */
    public int getHeight() throws IOException, NodeException {
        return asJson(get("/blocks/height")).get("height").asInt();
    }

    /**
     * Get a block by its ID
     *
     * @param blockId block ID base58 encoded
     * @return height of block with {blockId}
     * @throws IOException
     * @throws NodeException
     */
    public int getBlockHeight(Base58String blockId) throws IOException, NodeException {
        return asJson(get("/blocks/height/" + blockId.toString()))
                .get("height").asInt();
    }

    /**
     * Get height of the most recent block such that its timestamp does not exceed the given {timestamp}.
     *
     * @param timestamp timestamp
     * @return height of block
     * @throws IOException
     * @throws NodeException
     */
    public int getBlockHeight(long timestamp) throws IOException, NodeException {
        return asJson(get("/blocks/heightByTimestamp/" + timestamp))
                .get("height").asInt();
    }

    /**
     * Average delay in milliseconds between last blockNum blocks starting from block with id
     *
     * @param startBlockId block ID base58 encoded
     * @param blocksNum number of blocks to count delay
     * @return average delay in milliseconds
     * @throws IOException
     * @throws NodeException
     */
    public int getBlocksDelay(Base58String startBlockId, int blocksNum) throws IOException, NodeException {
        return asJson(get("/blocks/delay/" + startBlockId.toString() + "/" + blocksNum))
                .get("delay").asInt();
    }

    /**
     * Returns block header at given height.
     *
     * @param height blockchain height
     * @return block object without transactions
     * @throws IOException
     * @throws NodeException
     */
    public BlockHeaders getBlockHeaders(int height) throws IOException, NodeException {
        return asType(get("/blocks/headers/at/" + height), TypeRef.BLOCK_HEADERS);
    }

    /**
     * Returns headers of a given block
     *
     * @param blockId block ID base58 encoded
     * @return block object without transactions
     * @throws IOException
     * @throws NodeException
     */
    public BlockHeaders getBlockHeaders(Base58String blockId) throws IOException, NodeException {
        return asType(get("/blocks/headers/" + blockId.toString()), TypeRef.BLOCK_HEADERS);
    }

    /**
     * Returns seq of block headers
     *
     * @param fromHeight start block
     * @param toHeight   end block
     * @return sequences of block objects without transactions
     * @throws IOException
     * @throws NodeException
     */
    public List<BlockHeaders> getBlocksHeaders(int fromHeight, int toHeight) throws IOException, NodeException {
        return asType(get("/blocks/headers/seq/" + fromHeight + "/" + toHeight), TypeRef.BLOCKS_HEADERS);
    }

    /**
     * Returns last block header
     *
     * @return block object without transactions
     * @throws IOException
     * @throws NodeException
     */
    public BlockHeaders getLastBlockHeaders() throws IOException, NodeException {
        return asType(get("/blocks/headers/last"), TypeRef.BLOCK_HEADERS);
    }

    /**
     * Returns block at given height.
     *
     * @param height blockchain height
     * @return block object
     * @throws IOException
     * @throws NodeException
     */
    public Block getBlock(int height) throws IOException, NodeException {
        return asType(get("/blocks/at/" + height), TypeRef.BLOCK);
    }

    /**
     * Returns block by its id.
     *
     * @param blockId block id
     * @return block object
     * @throws IOException
     * @throws NodeException
     */
    public Block getBlock(Base58String blockId) throws IOException, NodeException {
        return asType(get("/blocks/" + blockId.toString()), TypeRef.BLOCK);
    }

    /**
     * Get blocks at a given range of heights.
     * Max range {from}-{to} is limited by rest-api.blocks-request-limit, 100 by default.
     *
     * @param fromHeight start block height
     * @param toHeight end block height
     * @return sequence of block objects
     * @throws IOException
     * @throws NodeException
     */
    public List<Block> getBlocks(int fromHeight, int toHeight) throws IOException, NodeException {
        return asType(get("/blocks/seq/" + fromHeight + "/" + toHeight), TypeRef.BLOCKS);
    }

    /**
     * Returns first block
     *
     * @return block object
     * @throws IOException
     * @throws NodeException
     */
    public Block getGenesisBlock() throws IOException, NodeException {
        return asType(get("/blocks/first"), TypeRef.BLOCK);
    }

    /**
     * Get the block at the current blockchain height
     *
     * @return block object
     * @throws IOException
     * @throws NodeException
     */
    public Block getLastBlock() throws IOException, NodeException {
        return asType(get("/blocks/last"), TypeRef.BLOCK);
    }

    /**
     * Get a list of blocks forged by a given address.
     * Max range {from}-{to} is limited by rest-api.blocks-request-limit, 100 by default.
     *
     * @param generator address base58 encoded
     * @param fromHeight start block height
     * @param toHeight end block height
     * @return sequence of block objects
     * @throws IOException
     * @throws NodeException
     */
    public List<Block> getBlocksGeneratedBy(Address generator, int fromHeight, int toHeight) throws IOException, NodeException {
        return asType(get(
                "/blocks/address/" + generator.toString() + "/" + fromHeight + "/" + toHeight), TypeRef.BLOCKS);
    }

    //===============
    // NODE
    //===============

    /**
     * Get Waves node version
     *
     * @return waves node version
     * @throws IOException
     * @throws NodeException
     */
    public String getVersion() throws IOException, NodeException {
        return asJson(get("/node/version")).get("version").asText();
    }

    //===============
    // DEBUG
    //===============

    /**
     * Get history of the regular balance at a given address.
     * Max depth is set by waves.db.max-rollback-depth, 2000 by default
     *
     * @param address address base58 encoded
     * @return sequence of HistoryBalance objects
     * @throws IOException
     * @throws NodeException
     */
    public List<HistoryBalance> getBalanceHistory(Address address) throws IOException, NodeException {
        return asType(get("/debug/balances/history/" + address.toString()), TypeRef.HISTORY_BALANCES);
    }

    /**
     * Validates a transaction and measures time spent in milliseconds.
     * You should use the JSON transaction format with proofs
     *
     * @param transaction signed transaction
     * @return validation result
     * @throws IOException
     * @throws NodeException
     */
    public <T extends Transaction> Validation validateTransaction(T transaction) throws IOException, NodeException {
        return asType(post("/debug/validate")
                .setEntity(new StringEntity(transaction.toJson(), ContentType.APPLICATION_JSON)), TypeRef.VALIDATION);
    }

    //===============
    // LEASING
    //===============

    /**
     * Get all active leases involving a given address
     *
     * @param address address base58 encoded
     * @return sequence of LeaseInfo objects
     * @throws IOException
     * @throws NodeException
     */
    public List<LeaseInfo> getActiveLeases(Address address) throws IOException, NodeException {
        return asType(get("/leasing/active/" + address.toString()), TypeRef.LEASES_INFO);
    }

    /**
     * Get lease parameters by lease ID
     *
     * @param leaseId lease ID base58 encoded
     * @return LeaseInfo object
     * @throws IOException
     * @throws NodeException
     */
    public LeaseInfo getLeaseInfo(Id leaseId) throws IOException, NodeException {
        return asType(get("/leasing/info/" + leaseId.toString()), TypeRef.LEASE_INFO);
    }

    /**
     * Get lease parameters by lease IDs
     *
     * @param leaseIds sequence of lease IDs base58 encoded
     * @return sequence of LeaseInfo objects
     * @throws IOException
     * @throws NodeException
     */
    public List<LeaseInfo> getLeasesInfo(List<Id> leaseIds) throws IOException, NodeException {
        ObjectNode jsonBody = JSON_MAPPER.createObjectNode();
        ArrayNode jsonIds = jsonBody.putArray("ids");
        leaseIds.forEach(id -> jsonIds.add(id.toString()));
        StringEntity body = new StringEntity(JSON_MAPPER.writeValueAsString(jsonBody), StandardCharsets.UTF_8);

        return asType(post("/leasing/info")
                .addHeader("Content-Type", "application/json")
                .setEntity(body), TypeRef.LEASES_INFO);
    }

    /**
     * Get lease parameters by lease IDs
     *
     * @param leaseIds sequence of lease IDs base58 encoded
     * @return sequence of LeaseInfo objects
     * @throws IOException
     * @throws NodeException
     */
    public List<LeaseInfo> getLeasesInfo(Id... leaseIds) throws IOException, NodeException {
        return getLeasesInfo(asList(leaseIds));
    }

    //===============
    // TRANSACTIONS
    //===============

    /**
     * Get the minimum fee for a given transaction
     *
     * @param transaction Transaction <a href="https://docs.waves.tech/en/blockchain/transaction/#json-representation">data in JSON</a>
     *                   including type and senderPublicKey.
     *                   To calculate a sponsored fee, specify feeAssetId. fee and sender are ignored.
     * @return amount fee with asset id
     * @throws IOException
     * @throws NodeException
     */
    public <T extends Transaction> Amount calculateTransactionFee(T transaction) throws IOException, NodeException {
        JsonNode json = asJson(post("/transactions/calculateFee").setEntity(new StringEntity(transaction.toJson(), ContentType.APPLICATION_JSON)));
        return Amount.of(json.get("feeAmount").asLong(), JsonSerializer.assetIdFromJson(json.get("feeAssetId")));
    }

    /**
     * Broadcast a signed transaction.
     * Check out <a href="https://docs.waves.tech/en/waves-node/node-api/transactions#step-4-broadcast-transaction">how to use this endpoint</a>
     *
     * @param transaction Transaction <a href="https://docs.waves.tech/en/blockchain/transaction/#json-representation">data in JSON</a>
     *                     including type and signature/proofs.
     * <pre>IssueTransaction tx = broadcast(IssueTransaction.with("", 1, 0).get());</pre>
     *
     * @return Transaction object
     * @throws IOException
     * @throws NodeException
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
     * @throws IOException
     * @throws NodeException
     */
    public TransactionInfo getTransactionInfo(Id txId) throws IOException, NodeException {
        return asType(get("/transactions/info/" + txId.toString()), TypeRef.TRANSACTION_INFO);
    }

    /**
     * Returns object by its ID.
     *
     * @param txId object ID
     * @return object object
     * @throws IOException
     * @throws NodeException
     */
    public <T extends TransactionInfo> T getTransactionInfo(Id txId, Class<T> transactionInfoClass) throws IOException, NodeException {
        return transactionInfoClass.cast(
                asType(get("/transactions/info/" + txId.toString()), TypeRef.TRANSACTION_INFO));
    }

    /**
     *
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
     * @param txIds IDs of transactions
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
     * @throws IOException
     * @throws NodeException
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
     * @throws IOException
     * @throws NodeException
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
     * @throws IOException
     * @throws NodeException
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

    /**
     * Get transaction statuses by their ID.
     * Transactions in the response are in the same order as in the request.
     *
     * @param txId transaction ID
     * @return TransactionStatus object
     * @throws IOException
     * @throws NodeException
     */
    public TransactionStatus getTransactionStatus(Id txId) throws IOException, NodeException {
        return asType(get("/transactions/status").addParameter("id", txId.toString()),
                TypeRef.TRANSACTIONS_STATUS).get(0);
    }

    /**
     * Get transaction statuses by their ID.
     * Max number of transactions is set by waves.rest-api.transactions-by-address-limit, 1000 by default.
     * Transactions in the response are in the same order as in the request.
     *
     * @param txIds sequence of transaction IDs
     * @return sequence of TransactionStatus objects
     * @throws IOException
     * @throws NodeException
     */
    public List<TransactionStatus> getTransactionsStatus(List<Id> txIds) throws IOException, NodeException {
        ObjectNode jsonBody = JSON_MAPPER.createObjectNode();
        ArrayNode jsonIds = jsonBody.putArray("ids");
        txIds.forEach(id -> jsonIds.add(id.toString()));
        StringEntity body = new StringEntity(JSON_MAPPER.writeValueAsString(jsonBody), StandardCharsets.UTF_8);

        return asType(post("/transactions/status")
                .addHeader("Content-Type", "application/json")
                .setEntity(body), TypeRef.TRANSACTIONS_STATUS);
    }

    /**
     * Get transaction statuses by their ID.
     * Max number of transactions is set by waves.rest-api.transactions-by-address-limit, 1000 by default.
     * Transactions in the response are in the same order as in the request.
     *
     * @param txIds sequence of transaction IDs
     * @return sequence of TransactionStatus objects
     * @throws IOException
     * @throws NodeException
     */
    public List<TransactionStatus> getTransactionsStatus(Id... txIds) throws IOException, NodeException {
        return getTransactionsStatus(asList(txIds));
    }

    /**
     * Get an unconfirmed transaction by its ID
     *
     * @param txId transaction ID
     * @return Transaction object
     * @throws IOException
     * @throws NodeException
     */
    public Transaction getUnconfirmedTransaction(Id txId) throws IOException, NodeException {
        return asType(get("/transactions/unconfirmed/info/" + txId.toString()), TypeRef.TRANSACTION);
    }

    /**
     * Get a list of transactions in node's UTX pool
     *
     * @return sequence of Transaction objects
     * @throws IOException
     * @throws NodeException
     */
    public List<Transaction> getUnconfirmedTransactions() throws IOException, NodeException {
        return asType(get("/transactions/unconfirmed"), TypeRef.TRANSACTIONS);
    }

    /**
     * Get the number of transactions in the UTX pool
     *
     * @return number of transactions in the UTX pool
     * @throws IOException
     * @throws NodeException
     */
    public int getUtxSize() throws IOException, NodeException {
        return asJson(get("/transactions/unconfirmed/size")).get("size").asInt();
    }

    //===============
    // UTILS
    //===============

    /**
     * Compiles string code to base64 script representation
     *
     * @param source script source
     * @return ScripInfo object
     * @throws IOException
     * @throws NodeException
     */
    public ScriptInfo compileScript(String source) throws IOException, NodeException {
        return compileScript(source, false);
    }

    /**
     * Compiles string code to base64 script representation
     *
     * @param source script source
     * @param enableCompaction if true, compacts the contract. False by default
     * @return ScripInfo object
     * @throws IOException
     * @throws NodeException
     */
    public ScriptInfo compileScript(String source, boolean enableCompaction) throws IOException, NodeException {
        return asType(post("/utils/script/compileCode")
                        .addHeader("Content-Type", "text/plain")
                        .addParameter("compact", enableCompaction ? "true" : "false")
                        .setEntity(new StringEntity(source, StandardCharsets.UTF_8)),
                TypeRef.SCRIPT_INFO);
    }

    /**
     * Decompiles base64 script representation to string code
     *
     * @param compiledScript base64 script
     * @return string code
     * @throws IOException
     * @throws NodeException
     */
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

