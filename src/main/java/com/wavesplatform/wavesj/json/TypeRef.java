package com.wavesplatform.wavesj.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wavesplatform.wavesj.*;
import im.mak.waves.transactions.account.Address;
import im.mak.waves.transactions.LeaseTransaction;
import im.mak.waves.transactions.Transaction;
import im.mak.waves.transactions.common.Alias;
import im.mak.waves.transactions.data.DataEntry;

import java.util.List;

public class TypeRef {

    public static final TypeReference<List<LeaseTransaction>> ACTIVE_LEASES = new TypeReference<List<LeaseTransaction>>() {};

    public static final TypeReference<List<Address>> ADDRESSES = new TypeReference<List<Address>>() {};

    public static final TypeReference<List<Alias>> ALIASES = new TypeReference<List<Alias>>() {};

    public static final TypeReference<List<AssetBalance>> ASSET_BALANCES = new TypeReference<List<AssetBalance>>() {};

    public static final TypeReference<AssetDistribution> ASSET_DISTRIBUTION = new TypeReference<AssetDistribution>() {};

    public static final TypeReference<AssetDetails> ASSET_DETAILS = new TypeReference<AssetDetails>() {};

    public static final TypeReference<List<AssetDetails>> ASSETS_DETAILS = new TypeReference<List<AssetDetails>>() {};

    public static final TypeReference<BalanceDetails> BALANCE_DETAILS = new TypeReference<BalanceDetails>() {};

    public static final TypeReference<Block> BLOCK = new TypeReference<Block>() {};

    public static final TypeReference<List<Block>> BLOCKS = new TypeReference<List<Block>>() {};

    public static final TypeReference<BlockHeaders> BLOCK_HEADERS = new TypeReference<BlockHeaders>() {};

    public static final TypeReference<List<BlockHeaders>> BLOCKS_HEADERS = new TypeReference<List<BlockHeaders>>() {};

    public static final TypeReference<BlockchainRewards> BLOCKCHAIN_REWARDS = new TypeReference<BlockchainRewards>() {};

    public static final TypeReference<List<DataEntry>> DATA_ENTRIES = new TypeReference<List<DataEntry>>() {};

    public static final TypeReference<DataEntry> DATA_ENTRY = new TypeReference<DataEntry>() {};

    public static final TypeReference<List<HistoryBalance>> HISTORY_BALANCES = new TypeReference<List<HistoryBalance>>() {};

    public static final TypeReference<ScriptInfo> SCRIPT_INFO = new TypeReference<ScriptInfo>() {};

    public static final TypeReference<ScriptMeta> SCRIPT_META = new TypeReference<ScriptMeta>() {};

    public static final TypeReference<TransactionInfo> TRANSACTION_INFO = new TypeReference<TransactionInfo>() {};

    public static final TypeReference<List<TransactionInfo>> TRANSACTIONS_INFO = new TypeReference<List<TransactionInfo>>() {};

    public static final TypeReference<TransactionDebugInfo> TRANSACTION_DEBUG_INFO = new TypeReference<TransactionDebugInfo>() {};

    public static final TypeReference<List<TransactionDebugInfo>> TRANSACTIONS_DEBUG_INFO = new TypeReference<List<TransactionDebugInfo>>() {};

    public static final TypeReference<Transaction> TRANSACTION = new TypeReference<Transaction>() {};

    public static final TypeReference<List<Transaction>> TRANSACTIONS = new TypeReference<List<Transaction>>() {};

    public static final TypeReference<List<TransactionStatus>> TRANSACTIONS_STATUSES = new TypeReference<List<TransactionStatus>>() {};

    public static final TypeReference<Validation> VALIDATION = new TypeReference<Validation>() {};

}
