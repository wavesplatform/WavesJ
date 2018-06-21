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
import java.nio.charset.Charset;
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
 *     <li>It is possible to add up to 8 proofs to any transaction. The {@link #withProof(int, String)} method can be used
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
    private static final int KBYTE = 1024;

    private final static Charset UTF8 = Charset.forName("UTF-8");
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
    
    public byte[] getBytes() {
        return bytes.clone();
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
     * Returns a new {@code Transaction} object with the proof added.
     * @param proof a Base58-encoded proof
     * @return new {@code Transaction} object with the proof added
     * @throws IllegalArgumentException if index is not between 0 and 7
     */
    public Transaction withProof(int index, String proof) {
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

    private static void putString(ByteBuffer buffer, String s) {
        if (s == null) s = "";
        putBytes(buffer, s.getBytes(UTF8));
    }

    private static void putScript(ByteBuffer buffer, String script) {
        byte[] bytes = script == null ? new byte[0] : Base64.decode(script);
        buffer.put((byte) (bytes.length > 0 ? 1 : 0));
        putBytes(buffer, bytes);
    }

    private static void putBytes(ByteBuffer buffer, byte[] bytes) {
        buffer.putShort((short) bytes.length).put(bytes);
    }

    private static String putRecipient(ByteBuffer buffer, byte chainId, String recipient) {
        if (recipient.length() <= 30) {
            // assume an alias
            buffer.put((byte) 0x02).put(chainId).putShort((short) recipient.length()).put(recipient.getBytes(UTF8));
            return String.format("alias:%c:%s", chainId, recipient);
        } else {
            buffer.put(Base58.decode(recipient));
            return recipient;
        }
    }

    private static String hash(byte[] bytes) {
        return Base58.encode(Hash.hash(bytes, 0, bytes.length, Hash.BLAKE2B256));
    }

    static String sign(PrivateKeyAccount account, ByteBuffer buffer) {
        return account.sign(toBytes(buffer));
    }

    public static Transaction makeIssueTx(PublicKeyAccount sender, byte chainId, String name, String description,
            long quantity, byte decimals, boolean reissuable, String script, long fee, long timestamp)
    {
        ByteBuffer buf = ByteBuffer.allocate(10 * KBYTE);
        buf.put(ISSUE).put(V2).put(chainId).put(sender.getPublicKey());
        putString(buf, name);
        putString(buf, description);
        buf.putLong(quantity)
                .put(decimals)
                .put((byte) (reissuable ? 1 : 0))
                .putLong(fee)
                .putLong(timestamp);
        putScript(buf, script);

       return new Transaction(sender, buf,"/transactions/broadcast",
                "type", ISSUE,
                "version", V2,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "name", name,
                "description", description,
                "quantity", quantity,
                "decimals", decimals,
                "reissuable", reissuable,
                "script", script,
                "fee", fee,
                "timestamp", timestamp);
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
