package com.wavesplatform.wavesj;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitcoinj.core.Base58;
import org.whispersystems.curve25519.Curve25519;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Transaction {
    private static final byte ISSUE        = 3;
    private static final byte TRANSFER     = 4;
    private static final byte REISSUE      = 5;
    private static final byte BURN         = 6;
    private static final byte LEASE        = 8;
    private static final byte LEASE_CANCEL = 9;
    private static final byte ALIAS        = 10;

    private static final int MIN_BUFFER_SIZE = 120;
    private static final Curve25519 cipher = Curve25519.getInstance(Curve25519.BEST);

    private final String endpoint;
    private final Map<String, Object> data;

    Transaction(String endpoint, Object... items) {
        this.endpoint = endpoint;
        HashMap<String, Object> map = new HashMap<>();
        for (int i=0; i<items.length; i+=2) {
            Object value = items[i+1];
            if (value != null) {
                map.put((String) items[i], value);
            }
        }
        this.data = Collections.unmodifiableMap(map);
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String getJson() {
        try {
            return new ObjectMapper().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            // not expected to ever happen
            return null;
        }
    }

    private static String sign(PrivateKeyAccount account, ByteBuffer buffer) {
        byte[] bytesToSign = new byte[buffer.position()];
        buffer.position(0);
        buffer.get(bytesToSign);
        byte[] signature = cipher.calculateSignature(account.getPrivateKey(), bytesToSign);
        return Base58.encode(signature);
    }

    private static void putAsset(ByteBuffer buffer, String assetId) {
        if (assetId == null || assetId.isEmpty()) {
            buffer.put((byte) 0);
        } else {
            buffer.put((byte) 1).put(Base58.decode(assetId));
        }
    }

    static String normalizeAsset(String assetId) {
        return assetId == null || assetId.isEmpty() ? "WAVES" : assetId;
    }

    public static Transaction makeIssueTx(PrivateKeyAccount account,
            String name, String description, long quantity, int decimals, boolean reissuable, long fee)
    {
        long timestamp = System.currentTimeMillis();
        int desclen = description == null ? 0 : description.length();
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE + name.length() + desclen);
        buf.put(ISSUE).put(account.getPublicKey())
                .putShort((short) name.length()).put(name.getBytes())
                .putShort((short) desclen);
        if (desclen > 0) {
            buf.put(description.getBytes());
        }
        buf.putLong(quantity)
                .put((byte) decimals)
                .put((byte) (reissuable ? 1 : 0))
                .putLong(fee).putLong(timestamp);

        String signature = sign(account, buf);
        return new Transaction("/assets/broadcast/issue",
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "name", name,
                "description", description,
                "quantity", quantity,
                "decimals", decimals,
                "reissuable", reissuable,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeReissueTx(PrivateKeyAccount account, String assetId, long quantity, boolean reissuable, long fee) {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(REISSUE).put(account.getPublicKey()).put(Base58.decode(assetId)).putLong(quantity)
                .put((byte) (reissuable ? 1 : 0))
                .putLong(fee).putLong(timestamp);
        String signature = sign(account, buf);
        return new Transaction("/assets/broadcast/reissue",
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "assetId", assetId,
                "quantity", quantity,
                "reissuable", reissuable,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeTransferTx(PrivateKeyAccount account, String toAddress,
            long amount, String assetId, long fee, String feeAssetId, String attachment)
    {
        byte[] attachmentBytes = (attachment == null ? "" : attachment).getBytes();
        int datalen = (assetId == null ? 0 : 32) +
                (feeAssetId == null ? 0 : 32) +
                attachmentBytes.length + MIN_BUFFER_SIZE;
        long timestamp = System.currentTimeMillis();

        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(TRANSFER).put(account.getPublicKey());
        putAsset(buf, assetId);
        putAsset(buf, feeAssetId);
        buf.putLong(timestamp).putLong(amount).putLong(fee).put(Base58.decode(toAddress))
                .putShort((short) attachmentBytes.length).put(attachmentBytes);

        String signature = sign(account, buf);
        return new Transaction("/assets/broadcast/transfer",
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "recipient", toAddress,
                "amount", amount,
                "assetId", assetId,
                "fee", fee,
                "feeAssetId", feeAssetId,
                "timestamp", timestamp,
                "attachment", Base58.encode(attachmentBytes));
    }

    public static Transaction makeBurnTx(PrivateKeyAccount account, String assetId, long amount, long fee) {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(BURN).put(account.getPublicKey()).put(Base58.decode(assetId))
                .putLong(amount).putLong(fee).putLong(timestamp);
        String signature = sign(account, buf);
        return new Transaction("/assets/broadcast/burn",
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "assetId", assetId,
                "quantity", amount,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeLeaseTx(PrivateKeyAccount account, String toAddress, long amount, long fee) {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(LEASE).put(account.getPublicKey()).put(Base58.decode(toAddress))
                .putLong(amount).putLong(fee).putLong(timestamp);
        String signature = sign(account, buf);
        return new Transaction("/leasing/broadcast/lease",
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "recipient", toAddress,
                "amount", amount,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeLeaseCancelTx(PrivateKeyAccount account, String txId, long fee) {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(LEASE_CANCEL).put(account.getPublicKey()).putLong(fee).putLong(timestamp).put(Base58.decode(txId));
        String signature = sign(account, buf);
        return new Transaction("/leasing/broadcast/cancel",
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "txId", txId,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeAliasTx(PrivateKeyAccount account, String alias, char scheme, long fee) {
        long timestamp = System.currentTimeMillis();
        int aliaslen = alias.length();
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE + aliaslen);
        buf.put(ALIAS).put(account.getPublicKey())
                .putShort((short) (alias.length() + 4)).put((byte) 0x02).put((byte) scheme)
                .putShort((short) alias.length()).put(alias.getBytes()).putLong(fee).putLong(timestamp);
        String signature = sign(account, buf);
        return new Transaction("/alias/broadcast/create",
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "alias", alias,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeOrderTx(PrivateKeyAccount sender, String matcherKey, Order.Type orderType,
            String amountAssetId, String priceAssetId, long price, long amount, long expiration, long matcherFee)
    {
        long timestamp = System.currentTimeMillis();
        int datalen = MIN_BUFFER_SIZE +
                (amountAssetId == null ? 0 : 32) +
                (priceAssetId == null ? 0 : 32);
        if (datalen == MIN_BUFFER_SIZE) {
            throw new IllegalArgumentException("Both spendAsset and receiveAsset are WAVES");
        }
        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(sender.getPublicKey()).put(Base58.decode(matcherKey));
        putAsset(buf, amountAssetId);
        putAsset(buf, priceAssetId);
        buf.put((byte) orderType.ordinal()).putLong(price).putLong(amount)
                .putLong(timestamp).putLong(expiration).putLong(matcherFee);
        String signature = sign(sender, buf);

        return new Transaction("/matcher/orderbook",
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "matcherPublicKey", matcherKey,
                "assetPair", assetPair(amountAssetId, priceAssetId),
                "orderType", orderType.json,
                "price", price,
                "amount", amount,
                "timestamp", timestamp,
                "expiration", expiration,
                "matcherFee", matcherFee,
                "signature", signature);
    }

    private static Map<String, String> assetPair(String amountAssetId, String priceAssetId) {
        Map<String, String> assetPair = new HashMap<>();
        assetPair.put("amountAsset", amountAssetId);
        assetPair.put("priceAsset", priceAssetId);
        return assetPair;
    }

    public static Transaction makeOrderCancelTx(PrivateKeyAccount sender,
            String amountAssetId, String priceAssetId, String orderId, long fee)
    {
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(sender.getPublicKey()).put(Base58.decode(orderId));
        String signature = sign(sender, buf);
        amountAssetId = normalizeAsset(amountAssetId);
        priceAssetId = normalizeAsset(priceAssetId);
        return new Transaction("/matcher/orderbook/" + amountAssetId + '/' + priceAssetId + '/' + "cancel",
                "sender", Base58.encode(sender.getPublicKey()),
                "orderId", orderId,
                "signature", signature);
    }
}
