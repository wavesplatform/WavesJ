package com.wavesplatform.wavesj;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.whispersystems.curve25519.Curve25519;

import java.nio.ByteBuffer;
import java.util.*;

import static com.wavesplatform.wavesj.Asset.isWaves;

/**
 * This class represents a Waves transaction.
 */
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

    private static final byte DEFAULT_VERSION = 1;
    private static final int MIN_BUFFER_SIZE = 120;
    private static final Curve25519 cipher = Curve25519.getInstance(Curve25519.BEST);

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
    private final byte[] bytes;

    private Transaction(PrivateKeyAccount signer, ByteBuffer buffer, String endpoint, Object... items) {
        this(new PrivateKeyAccount[] { signer }, buffer, endpoint, items);
    }

    private Transaction(PrivateKeyAccount[] signers, ByteBuffer buffer, String endpoint, Object... items) {
        this.bytes = toBytes(buffer);
        this.id = hash(bytes);
        this.endpoint = endpoint;

        String[] signatures = new String[signers.length];
        for (int i=0; i<signers.length; i++) {
            signatures[i] = sign(signers[i], bytes);
        }
        this.proofs = Arrays.asList(signatures);

        HashMap<String, Object> map = new HashMap<String, Object>();
        for (int i=0; i<items.length; i+=2) {
            Object value = items[i+1];
            if (value != null) {
                map.put((String) items[i], value);
            }
        }
        this.data = Collections.unmodifiableMap(map);
    }

    private Transaction(Transaction tx, String... proofs) {
        this.id = tx.id;
        this.data = tx.data;
        this.endpoint = tx.endpoint;
        this.bytes = tx.bytes;

        List<String> p = new ArrayList<String>(tx.proofs);
        p.addAll(Arrays.asList(proofs));
        this.proofs = Collections.unmodifiableList(p);
    }

    public String getJson() {
        HashMap<String, Object> toJson = new HashMap<String, Object>(data);
        toJson.put("id", id);
        toJson.put("proofs", proofs);
        if (proofs.size() == 1) {
            // assume proof0 is a signature
            toJson.put("signature", proofs.get(0));
        }
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
     * @throws IllegalStateException if the transaction already has maximum number of proofs (currently 8)
     */
    public Transaction addProof(String proof) {
        if (proofs.size() >= MAX_PROOF_COUNT) {
            throw new IllegalStateException(String.format("Maximum number of proofs (%d) has been reached", MAX_PROOF_COUNT));
        }
        return new Transaction(this, proof);
    }

    /**
     * Adds new proofs to the transaction.
     * @param newProofs a Base58-encoded proof
     * @return new {@code Transaction} object with the new proofs added
     * @throws IllegalStateException if the resulting number of proofs exceeds maximum (currently 8)
     */
    public Transaction addProofs(String... newProofs) {
        if (proofs.size() + newProofs.length > MAX_PROOF_COUNT) {
            throw new IllegalStateException(String.format("Maximum number of proofs (%d) has been reached", MAX_PROOF_COUNT));
        }
        return new Transaction(this, newProofs);
    }

    /**
     * Signs the transaction using one or several accounts and adds the signatures as new proofs.
     * <p>Example usage of this method in a multisig scenario where 3 signers are to sign a lease transaction:
     * <code>
     *     PublicKeyAccount leaser = ...
     *     Transaction tx = makeLeaseTx(leaser, recipient, amount, fee);
     *     tx = tx.sign(signer1, signer2);
     *     node.send(tx);
     * </code>
     * @param signers accounts used to sign
     * @return new {@code Transaction} object with the signatures added
     * @throws IllegalStateException if the resulting number of proofs exceeds maximum (currently 8)
     */
    public Transaction sign(PrivateKeyAccount... signers) {
        String[] signatures = new String[signers.length];
        for (int i=0; i<signers.length; i++) {
            signatures[i] = sign(signers[i], bytes);
        }
        return addProofs(signatures);
    }

    private static byte[] toBytes(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.position()];
        buffer.position(0);
        buffer.get(bytes);
        return bytes;
    }

    private static String hash(byte[] bytes) {
        return Base58.encode(Hash.hash(bytes, 0, bytes.length, Hash.BLAKE2B256));
    }

    private static String sign(PrivateKeyAccount account, byte[] bytes) {
        return Base58.encode(cipher.calculateSignature(account.getPrivateKey(), bytes));
    }

    static String sign(PrivateKeyAccount account, ByteBuffer buffer) {
        return sign(account, toBytes(buffer));
    }

    private static void putAsset(ByteBuffer buffer, String assetId) {
        if (isWaves(assetId)) {
            buffer.put((byte) 0);
        } else {
            buffer.put((byte) 1).put(Base58.decode(assetId));
        }
    }

    private static PrivateKeyAccount[] getSigners(PublicKeyAccount sender, PrivateKeyAccount[] signers) {
        if (signers.length > 0) {
            return signers;
        } else if (sender instanceof PrivateKeyAccount) {
            return new PrivateKeyAccount[] { (PrivateKeyAccount) sender };
        } else {
            return new PrivateKeyAccount[0];
        }
    }

    public static Transaction makeIssueTx(PublicKeyAccount sender, String name, String description, long quantity,
                                          int decimals, boolean reissuable, long fee, PrivateKeyAccount... signers)
    {
        long timestamp = System.currentTimeMillis();
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

       return new Transaction(getSigners(sender, signers), buf,"/assets/broadcast/issue",
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "name", name,
                "description", description,
                "quantity", quantity,
                "decimals", decimals,
                "reissuable", reissuable,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeReissueTx(PublicKeyAccount sender, String assetId, long quantity, boolean reissuable,
                                            long fee, PrivateKeyAccount... signers)
    {
        if (isWaves(assetId)) {
            throw new IllegalArgumentException("Cannot reissue WAVES");
        }
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(REISSUE).put(sender.getPublicKey()).put(Base58.decode(assetId)).putLong(quantity)
                .put((byte) (reissuable ? 1 : 0))
                .putLong(fee).putLong(timestamp);
        return new Transaction(getSigners(sender, signers), buf, "/assets/broadcast/reissue",
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "assetId", assetId,
                "quantity", quantity,
                "reissuable", reissuable,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeTransferTx(PublicKeyAccount sender, String toAddress, long amount, String assetId,
                                             long fee, String feeAssetId, String attachment, PrivateKeyAccount... signers)
    {
        byte[] attachmentBytes = (attachment == null ? "" : attachment).getBytes();
        int datalen = (isWaves(assetId) ? 0 : 32) +
                (isWaves(feeAssetId) ? 0 : 32) +
                attachmentBytes.length + MIN_BUFFER_SIZE;
        long timestamp = System.currentTimeMillis();

        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(TRANSFER).put(sender.getPublicKey());
        putAsset(buf, assetId);
        putAsset(buf, feeAssetId);
        buf.putLong(timestamp).putLong(amount).putLong(fee).put(Base58.decode(toAddress))
                .putShort((short) attachmentBytes.length).put(attachmentBytes);

        return new Transaction(getSigners(sender, signers), buf,"/assets/broadcast/transfer",
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "recipient", toAddress,
                "amount", amount,
                "assetId", Asset.toJsonObject(assetId),
                "fee", fee,
                "feeAssetId", Asset.toJsonObject(feeAssetId),
                "timestamp", timestamp,
                "attachment", Base58.encode(attachmentBytes));
    }

    public static Transaction makeBurnTx(PublicKeyAccount sender, String assetId, long amount, long fee, PrivateKeyAccount... signers) {
        if (isWaves(assetId)) {
            throw new IllegalArgumentException("Cannot burn WAVES");
        }
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(BURN).put(sender.getPublicKey()).put(Base58.decode(assetId))
                .putLong(amount).putLong(fee).putLong(timestamp);
        return new Transaction(getSigners(sender, signers), buf,"/assets/broadcast/burn",
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "assetId", assetId,
                "quantity", amount,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeSponsorTx(PublicKeyAccount sender, String assetId, long minAssetFee, long fee, PrivateKeyAccount... signers) {
        if (isWaves(assetId)) {
            throw new IllegalArgumentException("Cannot burn WAVES");
        }
        if (minAssetFee < 0) {
            throw new IllegalArgumentException("minAssetFee must be positive or zero");
        }
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(SPONSOR).put(sender.getPublicKey()).put(Base58.decode(assetId))
                .putLong(minAssetFee).putLong(fee).putLong(timestamp);

        return new Transaction(getSigners(sender, signers), buf,"/transactions/broadcast",
                "type", SPONSOR,
                "version", DEFAULT_VERSION,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "assetId", assetId,
                "minSponsoredAssetFee", minAssetFee == 0L ? null : minAssetFee,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeLeaseTx(PublicKeyAccount sender, String toAddress, long amount, long fee, PrivateKeyAccount... signers) {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(LEASE).put(sender.getPublicKey()).put(Base58.decode(toAddress))
                .putLong(amount).putLong(fee).putLong(timestamp);
        return new Transaction(getSigners(sender, signers), buf,"/leasing/broadcast/lease",
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "recipient", toAddress,
                "amount", amount,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeLeaseCancelTx(PublicKeyAccount sender, String txId, long fee, PrivateKeyAccount... signers) {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(LEASE_CANCEL).put(sender.getPublicKey()).putLong(fee).putLong(timestamp).put(Base58.decode(txId));
        return new Transaction(getSigners(sender, signers), buf,"/leasing/broadcast/cancel",
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "txId", txId,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeAliasTx(PublicKeyAccount sender, String alias, char scheme, long fee, PrivateKeyAccount... signers) {
        long timestamp = System.currentTimeMillis();
        int aliaslen = alias.length();
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE + aliaslen);
        buf.put(ALIAS).put(sender.getPublicKey())
                .putShort((short) (alias.length() + 4)).put((byte) 0x02).put((byte) scheme)
                .putShort((short) alias.length()).put(alias.getBytes()).putLong(fee).putLong(timestamp);
        return new Transaction(getSigners(sender, signers), buf,"/alias/broadcast/create",
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "alias", alias,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeMassTransferTx(PublicKeyAccount sender, String assetId, Collection<Transfer> transfers,
                                                 long fee, String attachment, PrivateKeyAccount... signers) {
        long timestamp = System.currentTimeMillis();
        byte[] attachmentBytes = (attachment == null ? "" : attachment).getBytes();
        int datalen = (isWaves(assetId) ? 0 : 32) +
                34 * transfers.size() +
                attachmentBytes.length + MIN_BUFFER_SIZE;

        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(MASS_TRANSFER).put(DEFAULT_VERSION).put(sender.getPublicKey());
        putAsset(buf, assetId);
        buf.putShort((short) transfers.size());
        for (Transfer t: transfers) {
            buf.put(Base58.decode(t.recipient)).putLong(t.amount);
        }
        buf.putLong(timestamp).putLong(fee)
                .putShort((short) attachmentBytes.length).put(attachmentBytes);

        return new Transaction(getSigners(sender, signers), buf,"/transactions/broadcast",
                "type", MASS_TRANSFER,
                "version", DEFAULT_VERSION,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "assetId", Asset.toJsonObject(assetId),
                "transfers", transfers,
                "fee", fee,
                "timestamp", timestamp,
                "attachment", Base58.encode(attachmentBytes));
    }

    public static Transaction makeDataTx(PrivateKeyAccount sender, Collection<DataEntry<?>> data, long fee, PrivateKeyAccount... signers) {
        long timestamp = System.currentTimeMillis();
        int datalen = MIN_BUFFER_SIZE;
        for (DataEntry<?> e: data) {
            datalen += e.size();
        }

        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(DATA).put(DEFAULT_VERSION).put(sender.getPublicKey());
        buf.putShort((short) data.size());
        for (DataEntry<?> e: data) {
            e.write(buf);
        }
        buf.putLong(timestamp).putLong(fee);

        return new Transaction(getSigners(sender, signers), buf,"/transactions/broadcast",
                "type", DATA,
                "version", DEFAULT_VERSION,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "data", data,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeScriptTx(PublicKeyAccount sender, String script, char scheme, long fee, PrivateKeyAccount... signers) {
        if (scheme > Byte.MAX_VALUE) {
            throw new IllegalArgumentException("Scheme should be between 0 and 127");
        }
        long timestamp = System.currentTimeMillis();
        byte[] rawScript = script == null ? new byte[0] : Base58.decode(script);
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE + rawScript.length);
        buf.put(SET_SCRIPT).put(DEFAULT_VERSION).put((byte) scheme).put(sender.getPublicKey());
        if (rawScript.length > 0) {
            buf.put((byte) 1).putShort((short) rawScript.length).put(rawScript);
        } else {
            buf.put((byte) 0);
        }
        buf.putLong(fee).putLong(timestamp);

        return new Transaction(getSigners(sender, signers), buf,"/transactions/broadcast",
                "type", SET_SCRIPT,
                "version", DEFAULT_VERSION,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "script", script,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeOrderTx(PrivateKeyAccount account, String matcherKey, Order.Type orderType,
            AssetPair assetPair, long price, long amount, long expiration, long matcherFee)
    {
        long timestamp = System.currentTimeMillis();
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
