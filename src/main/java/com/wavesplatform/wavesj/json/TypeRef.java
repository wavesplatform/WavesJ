package com.wavesplatform.wavesj.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.transactions.Transaction;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Alias;
import com.wavesplatform.transactions.data.DataEntry;
import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.LeaseInfo;
import com.wavesplatform.wavesj.actions.EthRpcResponse;
import com.wavesplatform.wavesj.info.TransactionInfo;
import com.wavesplatform.wavesj.peers.BlacklistedPeer;
import com.wavesplatform.wavesj.peers.ConnectedPeer;
import com.wavesplatform.wavesj.peers.Peer;
import com.wavesplatform.wavesj.peers.SuspendedPeer;

import java.util.List;

public class TypeRef {

    public static final TypeReference<List<Address>> ADDRESSES = new TypeReference<List<Address>>() {};

    public static final TypeReference<List<Alias>> ALIASES = new TypeReference<List<Alias>>() {};

    public static final TypeReference<List<AssetBalance>> ASSET_BALANCES = new TypeReference<List<AssetBalance>>() {};

    public static final TypeReference<AssetDistribution> ASSET_DISTRIBUTION = new TypeReference<AssetDistribution>() {};

    public static final TypeReference<AssetDetails> ASSET_DETAILS = new TypeReference<AssetDetails>() {};

    public static final TypeReference<List<AssetDetails>> ASSETS_DETAILS = new TypeReference<List<AssetDetails>>() {};

    public static final TypeReference<List<Balance>> BALANCES = new TypeReference<List<Balance>>() {};

    public static final TypeReference<BalanceDetails> BALANCE_DETAILS = new TypeReference<BalanceDetails>() {};

    public static final TypeReference<Block> BLOCK = new TypeReference<Block>() {};

    public static final TypeReference<List<Block>> BLOCKS = new TypeReference<List<Block>>() {};

    public static final TypeReference<List<CommittedGenerator>> COMMITTED_GENERATORS = new TypeReference<List<CommittedGenerator>>() {};

    public static final TypeReference<BlockHeaders> BLOCK_HEADERS = new TypeReference<BlockHeaders>() {};

    public static final TypeReference<List<BlockHeaders>> BLOCKS_HEADERS = new TypeReference<List<BlockHeaders>>() {};

    public static final TypeReference<BlockchainRewards> BLOCKCHAIN_REWARDS = new TypeReference<BlockchainRewards>() {};

    public static final TypeReference<List<DataEntry>> DATA_ENTRIES = new TypeReference<List<DataEntry>>() {};

    public static final TypeReference<DataEntry> DATA_ENTRY = new TypeReference<DataEntry>() {};

    public static final TypeReference<List<HistoryBalance>> HISTORY_BALANCES = new TypeReference<List<HistoryBalance>>() {};

    public static final TypeReference<LeaseInfo> LEASE_INFO = new TypeReference<LeaseInfo>() {};

    public static final TypeReference<List<LeaseInfo>> LEASES_INFO = new TypeReference<List<LeaseInfo>>() {};

    public static final TypeReference<ScriptInfo> SCRIPT_INFO = new TypeReference<ScriptInfo>() {};

    public static final TypeReference<ScriptMeta> SCRIPT_META = new TypeReference<ScriptMeta>() {};

    public static final TypeReference<TransactionInfo> TRANSACTION_INFO = new TypeReference<TransactionInfo>() {};

    public static final TypeReference<List<TransactionInfo>> TRANSACTIONS_INFO = new TypeReference<List<TransactionInfo>>() {};

    public static final TypeReference<Transaction> TRANSACTION = new TypeReference<Transaction>() {};

    public static final TypeReference<EthRpcResponse> ETH_TRANSACTION_RS = new TypeReference<EthRpcResponse>() {};

    public static final TypeReference<List<Transaction>> TRANSACTIONS = new TypeReference<List<Transaction>>() {};

    public static final TypeReference<List<TransactionStatus>> TRANSACTIONS_STATUS = new TypeReference<List<TransactionStatus>>() {};

    public static final TypeReference<Validation> VALIDATION = new TypeReference<Validation>() {};

    public static final TypeReference<ActivationStatus> ACTIVATION_STATUS = new TypeReference<ActivationStatus>() {};

    public static final TypeReference<NodeStatus> NODE_STATUS = new TypeReference<NodeStatus>() {};

    public static final TypeReference<List<Peer>> ALL_PEERS = new TypeReference<List<Peer>>() {};

    public static final TypeReference<List<BlacklistedPeer>> BLACKLISTED_PEERS = new TypeReference<List<BlacklistedPeer>>() {};

    public static final TypeReference<List<ConnectedPeer>> CONNECTED_PEERS = new TypeReference<List<ConnectedPeer>>() {};

    public static final TypeReference<List<SuspendedPeer>> SUSPENDED_PEERS = new TypeReference<List<SuspendedPeer>>() {};

}
