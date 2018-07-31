package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wavesplatform.wavesj.matcher.CancelOrder;
import com.wavesplatform.wavesj.matcher.DeleteOrder;
import com.wavesplatform.wavesj.matcher.Order;
import com.wavesplatform.wavesj.transactions.*;

import java.util.Collection;
import java.util.Map;

import static com.wavesplatform.wavesj.Asset.isWaves;
import static com.wavesplatform.wavesj.ByteUtils.hash;

/**
 * This class represents a Waves object. Instances are immutable, with data accessible through public final fields.
 * They are obtained using static factory methods defined in this class.
 *
 * <h2>Proofs and Signers</h2>
 * <p>Each object has a number of proofs associated with it that are used to validate the object. For non-scripted
 * accounts, the only proof needed is a signature made with the private key of the object sender. For scripted
 * accounts, the number and order of proofs are dictated by the account script. E.g. a 2-of-3 multisig account would
 * require 2 to 3 signatures made by holders of certain public keys.
 *
 * <p>Each proof is a byte array of 64 bytes max. There's a limit of 8 proofs per object.
 *
 * <p>There are two ways to sign a object:
 * <ul>
 *     <li>When an instance of {@link PrivateKeyAccount} is passed as the {@code sender} parameter to any of the factory
 *     methods, it is used to sign the object, and the signature is set as the proof number 0. This is needed for
 *     non-scripted accounts and for backward compatibility with older versions of the library.
 *     <li>It is possible to add up to 8 proofs to any object. The {@link #withProof(int, String)} method can be used
 *     to set arbitrary Base58-encoded proofs at arbitTransactionrary positions. Note that this method does not modify
 *     {@code Transaction} it is called on, but rather returns a new instance.
 * </ul>
 */
public abstract class Transaction extends JsonRepresented implements Proofable {
    /** Transaction ID. */
    @JsonIgnore
    public String getId() {
        return hash(getBytes());
    }

    public abstract byte getType();
    /** Transaction data. */
    @JsonIgnore
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

    public static ObjectWithProofs<IssueTransaction> makeIssueTx(PrivateKeyAccount sender, byte chainId, String name, String description,
                                                                 long quantity, byte decimals, boolean reissuable, String script, long fee, long timestamp)
    {
       return new ObjectWithProofs<IssueTransaction>(new IssueTransaction(sender, chainId, name, description, quantity, decimals, reissuable, script, fee, timestamp), sender);
    }

    public static ObjectWithProofs<IssueTransaction> makeIssueTx(PrivateKeyAccount sender, byte chainId, String name, String description, long quantity,
                                                                 byte decimals, boolean reissuable, String script, long fee) {
        return makeIssueTx(sender, chainId, name, description, quantity, decimals, reissuable, script, fee, System.currentTimeMillis());
    }

    public static ObjectWithProofs<ReissueTransaction> makeReissueTx(PrivateKeyAccount sender, byte chainId, String assetId, long quantity,
                                                                     boolean reissuable, long fee, long timestamp)
    {
        if (isWaves(assetId)) {
            throw new IllegalArgumentException("Cannot reissue WAVES");
        }
        return new ObjectWithProofs<ReissueTransaction>(new ReissueTransaction(sender, chainId, assetId, quantity, reissuable, fee, timestamp), sender);
    }

    public static ObjectWithProofs<ReissueTransaction> makeReissueTx(PrivateKeyAccount sender, byte chainId, String assetId, long quantity, boolean reissuable, long fee)
    {
        return makeReissueTx(sender, chainId, assetId, quantity, reissuable, fee, System.currentTimeMillis());
    }

    public static ObjectWithProofs<TransferTransaction> makeTransferTx(PrivateKeyAccount sender, String recipient, long amount, String assetId,
                                                                       long fee, String feeAssetId, String attachment, long timestamp)
    {
        return new ObjectWithProofs<TransferTransaction>(new TransferTransaction(sender, recipient, amount, assetId, fee, feeAssetId, new ByteString(attachment.getBytes()), timestamp), sender);
    }

    public static ObjectWithProofs<TransferTransaction> makeTransferTx(PrivateKeyAccount sender, String recipient, long amount, String assetId,
                                                                       long fee, String feeAssetId, String attachment)
    {
        return makeTransferTx(sender, recipient, amount, assetId, fee, feeAssetId, attachment, System.currentTimeMillis());
    }

    public static ObjectWithProofs<BurnTransaction> makeBurnTx(PrivateKeyAccount sender, byte chainId, String assetId, long amount, long fee, long timestamp) {
        if (isWaves(assetId)) {
            throw new IllegalArgumentException("Cannot burn WAVES");
        }

        return new ObjectWithProofs<BurnTransaction>(new BurnTransaction(sender, chainId, assetId, amount, fee, timestamp), sender);
    }

    public static ObjectWithProofs<BurnTransaction> makeBurnTx(PrivateKeyAccount sender, byte chainId, String assetId, long amount, long fee) {
        return makeBurnTx(sender, chainId, assetId, amount, fee, System.currentTimeMillis());
    }

    public static ObjectWithProofs<SponsorTransaction> makeSponsorTx(PrivateKeyAccount sender, String assetId, long minAssetFee, long fee, long timestamp) {
        if (isWaves(assetId)) {
            throw new IllegalArgumentException("Cannot sponsor WAVES");
        }
        if (minAssetFee < 0) {
            throw new IllegalArgumentException("minAssetFee must be positive or zero");
        }

        return new ObjectWithProofs<SponsorTransaction>(new SponsorTransaction(sender, assetId, minAssetFee, fee, timestamp), sender);
    }

    public static ObjectWithProofs<SponsorTransaction> makeSponsorTx(PrivateKeyAccount sender, String assetId, long minAssetFee, long fee) {
        return makeSponsorTx(sender, assetId, minAssetFee, fee, System.currentTimeMillis());
    }

    public static ObjectWithProofs<LeaseTransaction> makeLeaseTx(PrivateKeyAccount sender, String recipient, long amount, long fee, long timestamp) {
        return new ObjectWithProofs<LeaseTransaction>(new LeaseTransaction(sender, recipient, amount, fee, timestamp), sender);
    }

    public static ObjectWithProofs<LeaseTransaction> makeLeaseTx(PrivateKeyAccount sender, String recipient, long amount, long fee) {
        return makeLeaseTx(sender, recipient, amount, fee, System.currentTimeMillis());
    }

    public static ObjectWithProofs<LeaseCancelTransaction> makeLeaseCancelTx(PrivateKeyAccount sender, byte chainId, String leaseId, long fee, long timestamp) {
        return new ObjectWithProofs<LeaseCancelTransaction>(new LeaseCancelTransaction(sender, chainId, leaseId, fee, timestamp), sender);
    }

    public static ObjectWithProofs<LeaseCancelTransaction> makeLeaseCancelTx(PrivateKeyAccount sender, byte chainId, String leaseId, long fee) {
        return makeLeaseCancelTx(sender, chainId, leaseId, fee, System.currentTimeMillis());
    }

    public static ObjectWithProofs<AliasTransaction> makeAliasTx(PrivateKeyAccount sender, String alias, byte chainId, long fee, long timestamp) {
        return new ObjectWithProofs<AliasTransaction>(new AliasTransaction(sender, new Alias(alias, chainId), chainId, fee, timestamp), sender);
    }

    public static ObjectWithProofs<AliasTransaction> makeAliasTx(PrivateKeyAccount sender, String alias, byte chainId, long fee) {
        return makeAliasTx(sender, alias, chainId, fee, System.currentTimeMillis());
    }

    public static ObjectWithProofs<MassTransferTransaction> makeMassTransferTx(PrivateKeyAccount sender, String assetId, Collection<Transfer> transfers,
                                                                               long fee, String attachment, long timestamp) {
        return new ObjectWithProofs<MassTransferTransaction>(new MassTransferTransaction(sender, assetId, transfers, fee, attachment, timestamp), sender);
    }

    public static ObjectWithProofs<MassTransferTransaction> makeMassTransferTx(PrivateKeyAccount sender, String assetId, Collection<Transfer> transfers,
                                                                               long fee, String attachment) {
        return makeMassTransferTx(sender, assetId, transfers, fee, attachment, System.currentTimeMillis());
    }

    public static ObjectWithProofs<DataTransaction> makeDataTx(PrivateKeyAccount sender, Collection<DataEntry<?>> data, long fee, long timestamp) {
        return new ObjectWithProofs<DataTransaction>(new DataTransaction(sender, data, fee, timestamp), sender);
    }

    public static ObjectWithProofs<DataTransaction> makeDataTx(PrivateKeyAccount sender, Collection<DataEntry<?>> data, long fee) {
        return makeDataTx(sender, data, fee, System.currentTimeMillis());
    }

    /**
     * Creates a signed SetScript object.
     * @param sender the account to set the script for
     * @param script compiled script, base64 encoded
     * @param chainId chain ID
     * @param fee object fee
     * @param timestamp operation timestamp
     * @return object object
     * @see Account#MAINNET
     * @see Account#TESTNET
     */
    public static ObjectWithProofs<SetScriptTransaction> makeScriptTx(PrivateKeyAccount sender, String script, byte chainId, long fee, long timestamp) {
        return new ObjectWithProofs<SetScriptTransaction>(new SetScriptTransaction(sender, script, chainId, fee, timestamp), sender);
    }


    public static ObjectWithProofs<SetScriptTransaction> makeScriptTx(PrivateKeyAccount sender, String script, byte chainId, long fee) {
        return makeScriptTx(sender, script, chainId, fee, System.currentTimeMillis());
    }

    public static ObjectWithSignature<Order> makeOrderTx(PrivateKeyAccount account, String matcherKey, com.wavesplatform.wavesj.matcher.Order.Type orderType,
                                                         AssetPair assetPair, long price, long amount, long expiration, long matcherFee, long timestamp)
    {
        if (isWaves(assetPair.amountAsset) && isWaves(assetPair.priceAsset)) {
            throw new IllegalArgumentException("Both spendAsset and receiveAsset are WAVES");
        }
        Order co = new Order(orderType, assetPair, amount, price, timestamp, 0, Order.Status.ACCEPTED,
                expiration, matcherFee, account, new PublicKeyAccount(matcherKey, account.getChainId()));
        return new ObjectWithSignature<Order>(co, account);
    }

    public static ObjectWithSignature<Order> makeOrderTx(PrivateKeyAccount account, String matcherKey, Order.Type orderType,
                                                         AssetPair assetPair, long price, long amount, long expiration, long matcherFee)
    {
        return makeOrderTx(account, matcherKey, orderType, assetPair, price, amount, expiration, matcherFee, System.currentTimeMillis());
    }

    public static ObjectWithSignature<CancelOrder> makeOrderCancelTx(PrivateKeyAccount account, AssetPair assetPair, String orderId) {
        return new ObjectWithSignature<CancelOrder>(new CancelOrder(account, assetPair, orderId), account);
    }

    public static ObjectWithSignature<DeleteOrder> makeDeleteOrder(PrivateKeyAccount account, AssetPair assetPair, String orderId) {
        return new ObjectWithSignature<DeleteOrder>(new DeleteOrder(account, assetPair, orderId), account);
    }
}
