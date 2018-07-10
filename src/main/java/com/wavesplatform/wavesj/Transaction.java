package com.wavesplatform.wavesj;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wavesplatform.wavesj.transactions.TransferTransaction;

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

    static final byte REISSUE       = 5;
    static final byte BURN          = 6;
    static final byte LEASE         = 8;
    static final byte LEASE_CANCEL  = 9;
    static final byte ALIAS         = 10;
    static final byte MASS_TRANSFER = 11;
    static final byte DATA          = 12;
    static final byte SET_SCRIPT    = 13;
    static final byte SPONSOR       = 14;

    static final byte V1 = 1;

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


       return new Transaction(sender, buf, "/transactions/broadcast",
                );
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
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(REISSUE).put(V2).put(chainId).put(sender.getPublicKey()).put(Base58.decode(assetId)).putLong(quantity)
                .put((byte) (reissuable ? 1 : 0))
                .putLong(fee).putLong(timestamp);
        return new Transaction(sender, buf, "/transactions/broadcast",
                "type", REISSUE,
                "version", V2,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "assetId", assetId,
                "quantity", quantity,
                "reissuable", reissuable,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeReissueTx(PublicKeyAccount sender, byte chainId, String assetId, long quantity, boolean reissuable, long fee)
    {
        return makeReissueTx(sender, chainId, assetId, quantity, reissuable, fee, System.currentTimeMillis());
    }

    public static Transaction makeTransferTx(PublicKeyAccount sender, String recipient, long amount, String assetId,
                                             long fee, String feeAssetId, String attachment, long timestamp)
    {
        byte[] attachmentBytes = (attachment == null ? "" : attachment).getBytes();
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(TRANSFER).put(V2).put(sender.getPublicKey());
        putAsset(buf, assetId);
        putAsset(buf, feeAssetId);
        buf.putLong(timestamp).putLong(amount).putLong(fee);
        recipient = putRecipient(buf, sender.getChainId(), recipient);
        putString(buf, attachment);

        return new Transaction(sender, buf,"/transactions/broadcast",
                "type", TRANSFER,
                "version", V2,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "recipient", recipient,
                "amount", amount,
                "assetId", Asset.toJsonObject(assetId),
                "fee", fee,
                "feeAssetId", Asset.toJsonObject(feeAssetId),
                "timestamp", timestamp,
                "attachment", Base58.encode(attachmentBytes));
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
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(BURN).put(V2).put(chainId).put(sender.getPublicKey()).put(Base58.decode(assetId))
                .putLong(amount).putLong(fee).putLong(timestamp);
        return new Transaction(sender, buf,"/transactions/broadcast",
                "type", BURN,
                "version", V2,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "assetId", assetId,
                "quantity", amount,
                "fee", fee,
                "timestamp", timestamp);
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
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(SPONSOR).put(V1).put(sender.getPublicKey()).put(Base58.decode(assetId))
                .putLong(minAssetFee).putLong(fee).putLong(timestamp);

        return new Transaction(sender, buf,"/transactions/broadcast",
                "type", SPONSOR,
                "version", V1,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "assetId", assetId,
                "minSponsoredAssetFee", minAssetFee == 0L ? null : minAssetFee,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeSponsorTx(PublicKeyAccount sender, String assetId, long minAssetFee, long fee) {
        return makeSponsorTx(sender, assetId, minAssetFee, fee, System.currentTimeMillis());
    }

    public static Transaction makeLeaseTx(PublicKeyAccount sender, String recipient, long amount, long fee, long timestamp) {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(LEASE).put(V2).put(sender.getPublicKey());
        recipient = putRecipient(buf, sender.getChainId(), recipient);
        buf.putLong(amount).putLong(fee).putLong(timestamp);
        return new Transaction(sender, buf,"/transactions/broadcast",
                "type", LEASE,
                "version", V2,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "recipient", recipient,
                "amount", amount,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeLeaseTx(PublicKeyAccount sender, String recipient, long amount, long fee) {
        return makeLeaseTx(sender, recipient, amount, fee, System.currentTimeMillis());
    }

    public static Transaction makeLeaseCancelTx(PublicKeyAccount sender, byte chainId, String leaseId, long fee, long timestamp) {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(LEASE_CANCEL).put(V2).put(chainId).put(sender.getPublicKey()).putLong(fee).putLong(timestamp).put(Base58.decode(leaseId));
        return new Transaction(sender, buf,"/transactions/broadcast",
                "type", LEASE_CANCEL,
                "version", V2,
                "chainId", chainId,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "leaseId", leaseId,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeLeaseCancelTx(PublicKeyAccount sender, byte chainId, String leaseId, long fee) {
        return makeLeaseCancelTx(sender, chainId, leaseId, fee, System.currentTimeMillis());
    }

    public static Transaction makeAliasTx(PublicKeyAccount sender, String alias, byte chainId, long fee, long timestamp) {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(ALIAS).put(V2).put(sender.getPublicKey())
                .putShort((short) (alias.length() + 4)).put((byte) 0x02).put(chainId)
                .putShort((short) alias.length()).put(alias.getBytes()).putLong(fee).putLong(timestamp);
        return new Transaction(sender, buf,"/transactions/broadcast",
                "type", ALIAS,
                "version", V2,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "alias", alias,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeAliasTx(PublicKeyAccount sender, String alias, byte chainId, long fee) {
        return makeAliasTx(sender, alias, chainId, fee, System.currentTimeMillis());
    }

    public static Transaction makeMassTransferTx(PublicKeyAccount sender, String assetId, Collection<Transfer> transfers,
                                                 long fee, String attachment, long timestamp) {
        byte[] attachmentBytes = (attachment == null ? "" : attachment).getBytes();

        ByteBuffer buf = ByteBuffer.allocate(5 * KBYTE);
        buf.put(MASS_TRANSFER).put(V1).put(sender.getPublicKey());
        putAsset(buf, assetId);
        buf.putShort((short) transfers.size());

        List<Transfer> tr = new ArrayList<Transfer>(transfers.size());
        for (Transfer t: transfers) {
            String rc = putRecipient(buf, sender.getChainId(), t.recipient);
            buf.putLong(t.amount);
            tr.add(new Transfer(rc, t.amount));
        }
        buf.putLong(timestamp).putLong(fee);
        putString(buf, attachment);

        return new Transaction(sender, buf,"/transactions/broadcast",
                "type", MASS_TRANSFER,
                "version", V1,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "assetId", Asset.toJsonObject(assetId),
                "transfers", tr,
                "fee", fee,
                "timestamp", timestamp,
                "attachment", Base58.encode(attachmentBytes));
    }

    public static Transaction makeMassTransferTx(PublicKeyAccount sender, String assetId, Collection<Transfer> transfers,
                                                 long fee, String attachment) {
        return makeMassTransferTx(sender, assetId, transfers, fee, attachment, System.currentTimeMillis());
    }

    public static Transaction makeDataTx(PublicKeyAccount sender, Collection<DataEntry<?>> data, long fee, long timestamp) {
        int datalen = KBYTE;
        for (DataEntry<?> e: data) {
            datalen += e.size();
        }

        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(DATA).put(V1).put(sender.getPublicKey());
        buf.putShort((short) data.size());
        for (DataEntry<?> e: data) {
            e.write(buf);
        }
        buf.putLong(timestamp).putLong(fee);

        return new Transaction(sender, buf,"/transactions/broadcast",
                "type", DATA,
                "version", V1,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "data", data,
                "fee", fee,
                "timestamp", timestamp);
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
        byte[] rawScript = script == null ? new byte[0] : Base64.decode(script);
        ByteBuffer buf = ByteBuffer.allocate(KBYTE + rawScript.length);
        buf.put(SET_SCRIPT).put(V1).put(chainId).put(sender.getPublicKey());
        if (rawScript.length > 0) {
            buf.put((byte) 1).putShort((short) rawScript.length).put(rawScript);
        } else {
            buf.put((byte) 0);
        }
        buf.putLong(fee).putLong(timestamp);

        return new Transaction(sender, buf,"/transactions/broadcast",
                "type", SET_SCRIPT,
                "version", V1,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "script", script,
                "fee", fee,
                "timestamp", timestamp);
    }


    public static Transaction makeScriptTx(PublicKeyAccount sender, String script, byte chainId, long fee) {
        return makeScriptTx(sender, script, chainId, fee, System.currentTimeMillis());
    }

    public static Transaction makeOrderTx(PrivateKeyAccount account, String matcherKey, Order.Type orderType,
            AssetPair assetPair, long price, long amount, long expiration, long matcherFee, long timestamp)
    {
        if (isWaves(assetPair.amountAsset) && isWaves(assetPair.priceAsset)) {
            throw new IllegalArgumentException("Both spendAsset and receiveAsset are WAVES");
        }
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(account.getPublicKey()).put(Base58.decode(matcherKey));
        putAsset(buf, assetPair.amountAsset);
        putAsset(buf, assetPair.priceAsset);
        buf.put((byte) orderType.ordinal()).putLong(price).putLong(amount)
                .putLong(timestamp).putLong(expiration).putLong(matcherFee);

        return new Transaction(account, buf,"/matcher/orderbook",
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "matcherPublicKey", matcherKey,
                "assetPair", assetPair.toJsonObject(),
                "orderType", orderType.toJson(),
                "price", price,
                "amount", amount,
                "timestamp", timestamp,
                "expiration", expiration,
                "matcherFee", matcherFee);
    }

    public static Transaction makeOrderTx(PrivateKeyAccount account, String matcherKey, Order.Type orderType,
                                          AssetPair assetPair, long price, long amount, long expiration, long matcherFee)
    {
        return makeOrderTx(account, matcherKey, orderType, assetPair, price, amount, expiration, matcherFee, System.currentTimeMillis());
    }

    public static Transaction makeOrderCancelTx(PrivateKeyAccount account, AssetPair assetPair, String orderId) {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(account.getPublicKey()).put(Base58.decode(orderId));
        return new Transaction(account, buf,"/matcher/orderbook/" + assetPair.amountAsset + '/' + assetPair.priceAsset + '/' + "cancel",
                "sender", Base58.encode(account.getPublicKey()),
                "orderId", orderId);
    }

    public static Transaction makeDeleteOrder(PrivateKeyAccount account, AssetPair assetPair, String orderId) {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(account.getPublicKey()).put(Base58.decode(orderId));
        return new Transaction(account, buf,"/matcher/orderbook/" + assetPair.amountAsset + '/' + assetPair.priceAsset + '/' + "delete",
                "sender", Base58.encode(account.getPublicKey()),
                "orderId", orderId);
    }
}
