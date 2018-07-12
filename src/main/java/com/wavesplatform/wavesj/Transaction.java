package com.wavesplatform.wavesj;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wavesplatform.wavesj.matcher.CancelOrder;
import com.wavesplatform.wavesj.matcher.DeleteOrder;
import com.wavesplatform.wavesj.matcher.Order;
import com.wavesplatform.wavesj.transactions.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import static com.wavesplatform.wavesj.Asset.isWaves;
import static com.wavesplatform.wavesj.ByteUtils.*;

/**
 * This class represents a Waves transaction. Instances are immutable, with data accessible through public final fields.
 * They are obtained using static factory methods defined in this class.
 *
 * <h2>Proofs and Signers</h2>
 * <p>Each transaction has a number of proofs associated with it that are used to validate the transaction. For non-scripted
 * accounts, the only proof needed is a signature made with the private key of the transaction sender. For scripted
 * accounts, the number and order of proofs are dictated by the account script. E.g. a 2-of-3 multisig account would
 * require 2 to 3 signatures made by holders of certain public keys.
 *
 * <p>Each proof is a byte array of 64 bytes max. There's a limit of 8 proofs per transaction.
 *
 * <p>There are two ways to sign a transaction:
 * <ul>
 *     <li>When an instance of {@link PrivateKeyAccount} is passed as the {@code sender} parameter to any of the factory
 *     methods, it is used to sign the transaction, and the signature is set as the proof number 0. This is needed for
 *     non-scripted accounts and for backward compatibility with older versions of the library.
 *     <li>It is possible to add up to 8 proofs to any transaction. The {@link #withProof(int, String)} method can be used
 *     to set arbitrary Base58-encoded proofs at arbitrary positions. Note that this method does not modify
 *     {@code Transaction} it is called on, but rather returns a new instance.
 * </ul>
 */
@JsonDeserialize(using = Transaction.Deserializer.class)
public abstract class Transaction {
    /** Transaction ID. */
    public String getId() {
        return hash(getBytes());
    }
    public abstract byte[] getBytes();
    /** Transaction data. */
    public abstract Map<String, Object> getData();

    public static final int MAX_PROOF_COUNT = 8;

    private static final byte ISSUE         = 3;
    private static final byte TRANSFER      = 4;
    private static final byte REISSUE       = 5;
    private static final byte BURN          = 6;
    private static final byte LEASE         = 8;
    private static final byte LEASE_CANCEL  = 9;
    private static final byte ALIAS         = 10;
    private static final byte MASS_TRANSFER = 11;
    private static final byte DATA          = 12;
    private static final byte SET_SCRIPT    = 13;
    private static final byte SPONSOR       = 14;

    private static final byte V1 = 1;
    private static final byte V2 = 2;
    private static final int KBYTE = 1024;

    static final ObjectMapper mapper = new ObjectMapper();
    static final TypeReference<TransferTransaction> TRANSFER_TRANSACTION_INFO = new TypeReference<TransferTransaction>() {};
    static final TypeReference<Map<String, Object>> TX_INFO = new TypeReference<Map<String, Object>>() {};

    static class Deserializer extends JsonDeserializer<Transaction> {
        @Override
        public Transaction deserialize(JsonParser p, DeserializationContext context) throws IOException {
            Map<String, Object> data = mapper.convertValue(p.getCodec().readTree(p), TRANSFER_TRANSACTION_INFO);
            // todo is instance of
            throw new IllegalArgumentException();
        }
    }

    public static Transaction makeIssueTx(PublicKeyAccount sender, byte chainId, String name, String description,
            long quantity, byte decimals, boolean reissuable, String script, long fee, long timestamp)
    {
       return new IssueTransaction(sender, chainId, name, description, quantity, decimals, reissuable, script, fee, timestamp);
    }

    public static Transaction makeIssueTx(PublicKeyAccount sender, byte chainId, String name, String description, long quantity,
                                          byte decimals, boolean reissuable, String script, long fee) {
        return makeIssueTx(sender, chainId, name, description, quantity, decimals, reissuable, script, fee, System.currentTimeMillis());
    }

    public static Transaction makeReissueTx(PublicKeyAccount sender, byte chainId, String assetId, long quantity,
                                            boolean reissuable, long fee, long timestamp)
    {
        if (isWaves(assetId)) {
            throw new IllegalArgumentException("Cannot reissue WAVES");
        }
        return new ReissueTransaction(sender, chainId, assetId, quantity, reissuable, fee, timestamp);
    }

    public static Transaction makeReissueTx(PublicKeyAccount sender, byte chainId, String assetId, long quantity, boolean reissuable, long fee)
    {
        return makeReissueTx(sender, chainId, assetId, quantity, reissuable, fee, System.currentTimeMillis());
    }

    public static Transaction makeTransferTx(PublicKeyAccount sender, String recipient, long amount, String assetId,
                                             long fee, String feeAssetId, String attachment, long timestamp)
    {
        return new TransferTransaction(sender, recipient, amount, assetId, fee, feeAssetId, attachment, timestamp);
    }

    public static Transaction makeTransferTx(PublicKeyAccount sender, String recipient, long amount, String assetId,
                                             long fee, String feeAssetId, String attachment)
    {
        return makeTransferTx(sender, recipient, amount, assetId, fee, feeAssetId, attachment, System.currentTimeMillis());
    }

    public static Transaction makeBurnTx(PublicKeyAccount sender, byte chainId, String assetId, long amount, long fee, long timestamp) {
        if (isWaves(assetId)) {
            throw new IllegalArgumentException("Cannot burn WAVES");
        }

        return new BurnTransaction(sender, chainId, assetId, amount, fee, timestamp);
    }

    public static Transaction makeBurnTx(PublicKeyAccount sender, byte chainId, String assetId, long amount, long fee) {
        return makeBurnTx(sender, chainId, assetId, amount, fee, System.currentTimeMillis());
    }

    public static Transaction makeSponsorTx(PublicKeyAccount sender, String assetId, long minAssetFee, long fee, long timestamp) {
        if (isWaves(assetId)) {
            throw new IllegalArgumentException("Cannot sponsor WAVES");
        }
        if (minAssetFee < 0) {
            throw new IllegalArgumentException("minAssetFee must be positive or zero");
        }

        return new SponsorTransaction(sender, assetId, minAssetFee, fee, timestamp);
    }

    public static Transaction makeSponsorTx(PublicKeyAccount sender, String assetId, long minAssetFee, long fee) {
        return makeSponsorTx(sender, assetId, minAssetFee, fee, System.currentTimeMillis());
    }

    public static Transaction makeLeaseTx(PublicKeyAccount sender, String recipient, long amount, long fee, long timestamp) {
        return new LeaseTransaction(sender, recipient, amount, fee, timestamp);
    }

    public static Transaction makeLeaseTx(PublicKeyAccount sender, String recipient, long amount, long fee) {
        return makeLeaseTx(sender, recipient, amount, fee, System.currentTimeMillis());
    }

    public static Transaction makeLeaseCancelTx(PublicKeyAccount sender, byte chainId, String leaseId, long fee, long timestamp) {
        return new LeaseCancelTransaction(sender, chainId, leaseId, fee, timestamp);
    }

    public static Transaction makeLeaseCancelTx(PublicKeyAccount sender, byte chainId, String leaseId, long fee) {
        return makeLeaseCancelTx(sender, chainId, leaseId, fee, System.currentTimeMillis());
    }

    public static Transaction makeAliasTx(PublicKeyAccount sender, String alias, byte chainId, long fee, long timestamp) {
        return new AliasTransaction(sender, alias, chainId, fee, timestamp);
    }

    public static Transaction makeAliasTx(PublicKeyAccount sender, String alias, byte chainId, long fee) {
        return makeAliasTx(sender, alias, chainId, fee, System.currentTimeMillis());
    }

    public static Transaction makeMassTransferTx(PublicKeyAccount sender, String assetId, Collection<Transfer> transfers,
                                                 long fee, String attachment, long timestamp) {
        return new MassTransferTransaction(sender, assetId, transfers, fee, attachment, timestamp);
    }

    public static Transaction makeMassTransferTx(PublicKeyAccount sender, String assetId, Collection<Transfer> transfers,
                                                 long fee, String attachment) {
        return makeMassTransferTx(sender, assetId, transfers, fee, attachment, System.currentTimeMillis());
    }

    public static Transaction makeDataTx(PublicKeyAccount sender, Collection<DataEntry<?>> data, long fee, long timestamp) {
        return new DataTransaction(sender, data, fee, timestamp);
    }

    public static Transaction makeDataTx(PublicKeyAccount sender, Collection<DataEntry<?>> data, long fee) {
        return makeDataTx(sender, data, fee, System.currentTimeMillis());
    }

    /**
     * Creates a signed SetScript transaction.
     * @param sender the account to set the script for
     * @param script compiled script, base64 encoded
     * @param chainId chain ID
     * @param fee transaction fee
     * @param timestamp operation timestamp
     * @return transaction object
     * @see Account#MAINNET
     * @see Account#TESTNET
     */
    public static Transaction makeScriptTx(PublicKeyAccount sender, String script, byte chainId, long fee, long timestamp) {
        return new ScriptTransaction(sender, script, chainId, fee, timestamp);
    }


    public static Transaction makeScriptTx(PublicKeyAccount sender, String script, byte chainId, long fee) {
        return makeScriptTx(sender, script, chainId, fee, System.currentTimeMillis());
    }

    public static ApiJson makeOrderTx(PrivateKeyAccount account, String matcherKey, com.wavesplatform.wavesj.matcher.Order.Type orderType,
            AssetPair assetPair, long price, long amount, long expiration, long matcherFee, long timestamp)
    {
        if (isWaves(assetPair.amountAsset) && isWaves(assetPair.priceAsset)) {
            throw new IllegalArgumentException("Both spendAsset and receiveAsset are WAVES");
        }
        return new Order(orderType, assetPair, amount, price, timestamp, 0, Order.Status.ACCEPTED);
    }

    public static ApiJson makeOrderTx(PrivateKeyAccount account, String matcherKey, Order.Type orderType,
                                          AssetPair assetPair, long price, long amount, long expiration, long matcherFee)
    {
        return makeOrderTx(account, matcherKey, orderType, assetPair, price, amount, expiration, matcherFee, System.currentTimeMillis());
    }

    public static ApiJson makeOrderCancelTx(PublicKeyAccount account, AssetPair assetPair, String orderId) {
        return new CancelOrder(account, assetPair, orderId);
    }

    public static ApiJson makeDeleteOrder(PublicKeyAccount account, AssetPair assetPair, String orderId) {
        return new DeleteOrder(account, assetPair, orderId);
    }
}
