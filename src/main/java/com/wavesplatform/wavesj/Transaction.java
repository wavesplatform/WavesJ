package com.wavesplatform.wavesj;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import static com.wavesplatform.wavesj.Asset.isWaves;

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
 *     <li>It is possible to add up to 8 proofs to any transaction. The {@link #setProof(int, String)} method can be used
 *     to set arbitrary Base58-encoded proofs at arbitrary positions. Note that this method does not modify
 *     {@code Transaction} it is called on, but rather returns a new instance.
 * </ul>
 */
@JsonDeserialize(using = Transaction.Deserializer.class)
public class Transaction {
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
    private static final int MIN_BUFFER_SIZE = 120;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> TX_INFO = new TypeReference<Map<String, Object>>() {};

    /** Transaction ID. */
    public final String id;
    /** Transaction data. */
    public final Map<String, Object> data;
    /**
     * List of proofs. Each proof is a Base58-encoded byte array of at most 64 bytes.
     * There's currently a limit of 8 proofs per transaction.
     */
    public final List<String> proofs;
    final String endpoint;
    final byte[] bytes;

    private Transaction(PublicKeyAccount signer, ByteBuffer buffer, String endpoint, Object... items) {
        this.bytes = toBytes(buffer);
        this.id = hash(bytes);
        this.endpoint = endpoint;

        if (signer instanceof PrivateKeyAccount) {
            this.proofs = Collections.singletonList(((PrivateKeyAccount) signer).sign(bytes));
        } else {
            this.proofs = Collections.emptyList();
        }

        HashMap<String, Object> map = new HashMap<String, Object>();
        for (int i=0; i<items.length; i+=2) {
            Object value = items[i+1];
            if (value != null) {
                map.put((String) items[i], value);
            }
        }
        this.data = Collections.unmodifiableMap(map);
    }

    private Transaction(Transaction tx, List<String> proofs) {
        this.id = tx.id;
        this.data = tx.data;
        this.endpoint = tx.endpoint;
        this.bytes = tx.bytes;
        this.proofs = Collections.unmodifiableList(proofs);
    }

    private Transaction(Map<String, Object> data) {
        this.data = Collections.unmodifiableMap(data);
        this.id = (String) data.get("id");
        this.proofs = (List<String>) data.get("proofs");
        this.endpoint = null;
        this.bytes = null;
    }

    static class Deserializer extends JsonDeserializer<Transaction> {
        @Override
        public Transaction deserialize(JsonParser p, DeserializationContext context) throws IOException {
            Map<String, Object> data = mapper.convertValue(p.getCodec().readTree(p), TX_INFO);
            return new Transaction(data);
        }
    }

    /**
     * Returns JSON-encoded transaction data.
     * @return a JSON string
     */
    public String getJson() {
        HashMap<String, Object> toJson = new HashMap<String, Object>(data);
        toJson.put("id", id);
        toJson.put("proofs", proofs);
        if (proofs.size() == 1) {
            // assume proof0 is a signature
            toJson.put("signature", proofs.get(0));
        }
        /// add version to json and bytes
        /// Add v2-producing methods where needed
        /// test sending with 0 fees
        /// setProof -> withProof ?
//        Byte type = (Byte) data.get("type");
//        if (type != null && type != EXCHANGE) {
//            toJson.put("version", type > ALIAS ? 1 : 2);
//        }
        try {
            return new ObjectMapper().writeValueAsString(toJson);
        } catch (JsonProcessingException e) {
            // not expected to ever happen
            return null;
        }
    }

    /**
     * Adds a new proof to the transaction.
     * @param proof a Base58-encoded proof
     * @return new {@code Transaction} object with the proof added
     * @throws IllegalArgumentException if index is not between 0 and 7
     */
    public Transaction setProof(int index, String proof) {
        if (index < 0 || index >= MAX_PROOF_COUNT) {
            throw new IllegalArgumentException("index should be between 0 and " + (MAX_PROOF_COUNT - 1));
        }
        List<String> newProofs = new LinkedList<String>(proofs);
        for (int i = proofs.size(); i <= index; i++) {
            newProofs.add("");
        }
        newProofs.set(index, proof);
        return new Transaction(this, newProofs);
    }

    private static byte[] toBytes(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.position()];
        buffer.position(0);
        buffer.get(bytes);
        return bytes;
    }

    private static void putAsset(ByteBuffer buffer, String assetId) {
        if (isWaves(assetId)) {
            buffer.put((byte) 0);
        } else {
            buffer.put((byte) 1).put(Base58.decode(assetId));
        }
    }

    private static String hash(byte[] bytes) {
        return Base58.encode(Hash.hash(bytes, 0, bytes.length, Hash.BLAKE2B256));
    }

    static String sign(PrivateKeyAccount account, ByteBuffer buffer) {
        return account.sign(toBytes(buffer));
    }

    public static Transaction makeIssueTx(PublicKeyAccount sender, String name, String description, long quantity,
                                          int decimals, boolean reissuable, long fee, long timestamp)
    {
        int desclen = description == null ? 0 : description.length();
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE + name.length() + desclen);
        buf.put(ISSUE).put(sender.getPublicKey())
                .putShort((short) name.length()).put(name.getBytes())
                .putShort((short) desclen);
        if (desclen > 0) {
            buf.put(description.getBytes());
        }
        buf.putLong(quantity)
                .put((byte) decimals)
                .put((byte) (reissuable ? 1 : 0))
                .putLong(fee).putLong(timestamp);

       return new Transaction(sender, buf,"/assets/broadcast/issue",
                "type", ISSUE,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "name", name,
                "description", description,
                "quantity", quantity,
                "decimals", decimals,
                "reissuable", reissuable,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeIssueTx(PublicKeyAccount sender, String name, String description, long quantity,
                                          int decimals, boolean reissuable, long fee) {
        return makeIssueTx(sender, name, description, quantity, decimals, reissuable, fee, System.currentTimeMillis());
    }

    public static Transaction makeReissueTx(PublicKeyAccount sender, String assetId, long quantity, boolean reissuable, long fee, long timestamp) {
        if (isWaves(assetId)) {
            throw new IllegalArgumentException("Cannot reissue WAVES");
        }
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(REISSUE).put(sender.getPublicKey()).put(Base58.decode(assetId)).putLong(quantity)
                .put((byte) (reissuable ? 1 : 0))
                .putLong(fee).putLong(timestamp);
        return new Transaction(sender, buf, "/assets/broadcast/reissue",
                "type", REISSUE,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "assetId", assetId,
                "quantity", quantity,
                "reissuable", reissuable,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeReissueTx(PublicKeyAccount sender, String assetId, long quantity, boolean reissuable, long fee)
    {
        return makeReissueTx(sender, assetId, quantity, reissuable, fee, System.currentTimeMillis());
    }

    public static Transaction makeTransferTx(PublicKeyAccount sender, String toAddress, long amount, String assetId,
                                             long fee, String feeAssetId, String attachment, long timestamp)
    {
        byte[] attachmentBytes = (attachment == null ? "" : attachment).getBytes();
        byte version = V2;
        int datalen = (isWaves(assetId) ? 0 : 32) +
                (isWaves(feeAssetId) ? 0 : 32) +
                attachmentBytes.length + MIN_BUFFER_SIZE;

        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(TRANSFER).put(version).put(sender.getPublicKey());
        putAsset(buf, assetId);
        putAsset(buf, feeAssetId);
        buf.putLong(timestamp).putLong(amount).putLong(fee).put(Base58.decode(toAddress))
                .putShort((short) attachmentBytes.length).put(attachmentBytes);

        return new Transaction(sender, buf,"/assets/broadcast/transfer",
                "type", TRANSFER,
                "version", version,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "recipient", toAddress,
                "amount", amount,
                "assetId", Asset.toJsonObject(assetId),
                "fee", fee,
                "feeAssetId", Asset.toJsonObject(feeAssetId),
                "timestamp", timestamp,
                "attachment", Base58.encode(attachmentBytes));
    }

    public static Transaction makeTransferTx(PublicKeyAccount sender, String toAddress, long amount, String assetId,
                                             long fee, String feeAssetId, String attachment)
    {
        return makeTransferTx(sender, toAddress, amount, assetId, fee, feeAssetId, attachment, System.currentTimeMillis());
    }

    public static Transaction makeBurnTx(PublicKeyAccount sender, String assetId, long amount, long fee, long timestamp) {
        if (isWaves(assetId)) {
            throw new IllegalArgumentException("Cannot burn WAVES");
        }
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(BURN).put(sender.getPublicKey()).put(Base58.decode(assetId))
                .putLong(amount).putLong(fee).putLong(timestamp);
        return new Transaction(sender, buf,"/assets/broadcast/burn",
                "type", BURN,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "assetId", assetId,
                "quantity", amount,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeBurnTx(PublicKeyAccount sender, String assetId, long amount, long fee) {
        return makeBurnTx(sender, assetId, amount, fee, System.currentTimeMillis());
    }

    public static Transaction makeSponsorTx(PublicKeyAccount sender, String assetId, long minAssetFee, long fee, long timestamp) {
        if (isWaves(assetId)) {
            throw new IllegalArgumentException("Cannot burn WAVES");
        }
        if (minAssetFee < 0) {
            throw new IllegalArgumentException("minAssetFee must be positive or zero");
        }
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(SPONSOR).put(sender.getPublicKey()).put(Base58.decode(assetId))
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

    public static Transaction makeLeaseTx(PublicKeyAccount sender, String toAddress, long amount, long fee, long timestamp) {
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(LEASE).put(sender.getPublicKey()).put(Base58.decode(toAddress))
                .putLong(amount).putLong(fee).putLong(timestamp);
        return new Transaction(sender, buf,"/leasing/broadcast/lease",
                "type", LEASE,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "recipient", toAddress,
                "amount", amount,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeLeaseTx(PublicKeyAccount sender, String toAddress, long amount, long fee) {
        return makeLeaseTx(sender, toAddress, amount, fee, System.currentTimeMillis());
    }

    public static Transaction makeLeaseCancelTx(PublicKeyAccount sender, String txId, long fee, long timestamp) {
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(LEASE_CANCEL).put(sender.getPublicKey()).putLong(fee).putLong(timestamp).put(Base58.decode(txId));
        return new Transaction(sender, buf,"/leasing/broadcast/cancel",
                "type", LEASE_CANCEL,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "txId", txId,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeLeaseCancelTx(PublicKeyAccount sender, String txId, long fee) {
        return makeLeaseCancelTx(sender, txId, fee, System.currentTimeMillis());
    }

    public static Transaction makeAliasTx(PublicKeyAccount sender, String alias, char scheme, long fee, long timestamp) {
        int aliaslen = alias.length();
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE + aliaslen);
        buf.put(ALIAS).put(sender.getPublicKey())
                .putShort((short) (alias.length() + 4)).put((byte) 0x02).put((byte) scheme)
                .putShort((short) alias.length()).put(alias.getBytes()).putLong(fee).putLong(timestamp);
        return new Transaction(sender, buf,"/alias/broadcast/create",
                "type", ALIAS,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "alias", alias,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeAliasTx(PublicKeyAccount sender, String alias, char scheme, long fee) {
        return makeAliasTx(sender, alias, scheme, fee, System.currentTimeMillis());
    }

    public static Transaction makeMassTransferTx(PublicKeyAccount sender, String assetId, Collection<Transfer> transfers,
                                                 long fee, String attachment, long timestamp) {
        byte[] attachmentBytes = (attachment == null ? "" : attachment).getBytes();
        int datalen = (isWaves(assetId) ? 0 : 32) +
                34 * transfers.size() +
                attachmentBytes.length + MIN_BUFFER_SIZE;

        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(MASS_TRANSFER).put(V1).put(sender.getPublicKey());
        putAsset(buf, assetId);
        buf.putShort((short) transfers.size());
        for (Transfer t: transfers) {
            buf.put(Base58.decode(t.recipient)).putLong(t.amount);
        }
        buf.putLong(timestamp).putLong(fee)
                .putShort((short) attachmentBytes.length).put(attachmentBytes);

        return new Transaction(sender, buf,"/transactions/broadcast",
                "type", MASS_TRANSFER,
                "version", V1,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "assetId", Asset.toJsonObject(assetId),
                "transfers", transfers,
                "fee", fee,
                "timestamp", timestamp,
                "attachment", Base58.encode(attachmentBytes));
    }

    public static Transaction makeMassTransferTx(PublicKeyAccount sender, String assetId, Collection<Transfer> transfers,
                                                 long fee, String attachment) {
        return makeMassTransferTx(sender, assetId, transfers, fee, attachment, System.currentTimeMillis());
    }

    public static Transaction makeDataTx(PublicKeyAccount sender, Collection<DataEntry<?>> data, long fee, long timestamp) {
        int datalen = MIN_BUFFER_SIZE;
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
     * @param scheme network byte
     * @param fee transaction fee
     * @param timestamp operation timestamp
     * @return transaction object
     * @see Account#MAINNET
     * @see Account#TESTNET
     */
    public static Transaction makeScriptTx(PublicKeyAccount sender, String script, char scheme, long fee, long timestamp) {
        if (scheme > Byte.MAX_VALUE) {
            throw new IllegalArgumentException("Scheme should be between 0 and 127");
        }
        byte[] rawScript = script == null ? new byte[0] : Base64.decode(script);
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE + rawScript.length);
        buf.put(SET_SCRIPT).put(V1).put((byte) scheme).put(sender.getPublicKey());
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


    public static Transaction makeScriptTx(PublicKeyAccount sender, String script, char scheme, long fee) {
        return makeScriptTx(sender, script, scheme, fee, System.currentTimeMillis());
    }

    public static Transaction makeOrderTx(PrivateKeyAccount account, String matcherKey, Order.Type orderType,
            AssetPair assetPair, long price, long amount, long expiration, long matcherFee, long timestamp)
    {
        int datalen = MIN_BUFFER_SIZE +
                (isWaves(assetPair.amountAsset) ? 0 : 32) +
                (isWaves(assetPair.priceAsset) ? 0 : 32);
        if (datalen == MIN_BUFFER_SIZE) {
            throw new IllegalArgumentException("Both spendAsset and receiveAsset are WAVES");
        }
        ByteBuffer buf = ByteBuffer.allocate(datalen);
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
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(account.getPublicKey()).put(Base58.decode(orderId));
        return new Transaction(account, buf,"/matcher/orderbook/" + assetPair.amountAsset + '/' + assetPair.priceAsset + '/' + "cancel",
                "sender", Base58.encode(account.getPublicKey()),
                "orderId", orderId);
    }

    public static Transaction makeDeleteOrder(PrivateKeyAccount account, AssetPair assetPair, String orderId) {
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(account.getPublicKey()).put(Base58.decode(orderId));
        return new Transaction(account, buf,"/matcher/orderbook/" + assetPair.amountAsset + '/' + assetPair.priceAsset + '/' + "delete",
                "sender", Base58.encode(account.getPublicKey()),
                "orderId", orderId);
    }
}
