package com.wavesplatform.core;

import org.bitcoinj.core.Base58;
import org.whispersystems.curve25519.Curve25519;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Transaction {
    private static final Curve25519 cipher = Curve25519.getInstance(Curve25519.BEST);

    private final Map<String, Object> data;

    Transaction(Object... items) {
        data = new HashMap<>();
        for (int i=0; i<items.length; i+=2) {
            Object value = items[i+1];
            if (value != null) {
                data.put((String) items[i], value);
            }
        }
    }

    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(data);
    }

    static byte[] sign(PrivateKeyAccount account, ByteBuffer buffer, int pos, int len) {
        if (len == -1) {
            len = buffer.limit() - pos;
        }
        byte[] dataToSign = new byte[len];
        buffer.position(pos);
        buffer.get(dataToSign, 0, len);
        return cipher.calculateSignature(account.getPrivateKey(), dataToSign);
    }

    static String base58(byte[] bytes) {
        return bytes == null ? null : Base58.encode(bytes);
    }

    public static Transaction makeLimitOrderTx(PrivateKeyAccount sender, PublicKeyAccount matcher,
                                               byte[] spendAssetId, byte[] receiveAssetId, long price, long amount, long expirationTime, long matcherFee)
    {
        int datalen = 96 +
                (spendAssetId == null ? 0 : 32) +
                (receiveAssetId == null ? 0 : 32);
        if (datalen == 96) {
            throw new IllegalArgumentException("Both spendAsset and receiveAsset are WAVES");
        }
        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(sender.getPublicKey()).put(matcher.getPublicKey());
        if (spendAssetId != null) {
            buf.put(spendAssetId);
        }
        if (receiveAssetId != null) {
            buf.put(receiveAssetId);
        }
        buf.putLong(price).putLong(amount).putLong(expirationTime).putLong(matcherFee);
        byte[] signature = sign(sender, buf, 0, 160);
        return new Transaction(
                "sender", base58(sender.getPublicKey()),
                "matcher", base58(matcher.getPublicKey()),
                "spendAssetId", base58(spendAssetId),
                "receiveAssetId", base58(receiveAssetId),
                "price", price,
                "amount", amount,
                "maxTimestamp", expirationTime,
                "matcherFee", matcherFee,
                "signature", base58(signature));
    }

    public static Transaction makeOrderBookTx(byte[] asset1, byte[] asset2, Integer depth) {
        return new Transaction(
                "asset1", base58(asset1),
                "asset2", base58(asset2),
                "depth", depth);
    }

    public static Transaction makeOrderStatusTx(byte[] orderId, byte[] asset1, byte[] asset2) {
        return new Transaction(
                "id", base58(orderId),
                "asset1", base58(asset1),
                "asset2", base58(asset2));
    }

    public static Transaction makeOrderCancelTx(PrivateKeyAccount sender,
                                                byte[] spendAssetId, byte[] receiveAssetId, byte[] orderId, long fee)
    {
        long timestamp = System.currentTimeMillis();
        int datalen = 96 +
                (spendAssetId == null ? 0 : 32) +
                (receiveAssetId == null ? 0 : 32);
        if (datalen == 96) {
            throw new IllegalArgumentException("Both spendAsset and receiveAsset are WAVES");
        }
        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(sender.getPublicKey());
        if (spendAssetId != null) {
            buf.put(spendAssetId);
        }
        if (receiveAssetId != null) {
            buf.put(receiveAssetId);
        }
        buf.put(orderId).putLong(fee).putLong(timestamp);
        byte[] signature = sign(sender, buf, 0, datalen);
        return new Transaction(
                "sender", base58(sender.getPublicKey()),
                        "spendAssetId", base58(spendAssetId),
                        "receiveAssetId", base58(receiveAssetId),
                        "orderId", base58(orderId),
                        "fee", fee,
                        "timestamp", timestamp,
                        "signature", base58(signature));
    }
}
