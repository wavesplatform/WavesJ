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
    private static final byte ALIAS        = 10;///fix wiki

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

    private static String base58(byte[] bytes) {
        return bytes == null ? null : Base58.encode(bytes);
    }///rm?

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
        if (assetId == null) {
            buf.put((byte) 0);
        } else {
            buf.put((byte) 1).put(Base58.decode(assetId));
        }
        if (feeAssetId == null) {
            buf.put((byte) 0);
        } else {
            buf.put((byte) 1).put(Base58.decode(feeAssetId));
        }
        buf.putLong(timestamp).putLong(amount).putLong(fee).put(Base58.decode(toAddress))
                .putShort((short) attachmentBytes.length).put(attachmentBytes);

        String signature = sign(account, buf);
        return new Transaction("/assets/broadcast/transfer",
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "recipient", toAddress,
                "amount", amount,
                "fee", fee,
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
                "amount", amount,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeLeaseTx(PrivateKeyAccount account, String toAddress, long amount, long fee) {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE);
        buf.put(LEASE).put(account.getPublicKey()).put(Base58.decode(toAddress))
                .putLong(amount).putLong(fee).putLong(timestamp);
        String signature = sign(account, buf);
        return new Transaction("/leasing/lease",
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
        return new Transaction("/leasing/cancel",
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "leaseId", txId,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static Transaction makeAliasTx(PrivateKeyAccount account, String alias, long fee) {
        long timestamp = System.currentTimeMillis();
        int aliaslen = alias.length();
        ByteBuffer buf = ByteBuffer.allocate(MIN_BUFFER_SIZE + aliaslen);
        ///write Alias object here
        buf.put(ALIAS).put(account.getPublicKey())
                .putShort((short) alias.length()).put(alias.getBytes()).putLong(fee).putLong(timestamp);
        String signature = sign(account, buf);
        return new Transaction("/alias/broadcast/create",
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "alias", alias,
                "fee", fee,
                "timestamp", timestamp);
    }

    /// test
    public static Transaction makeOrderTx(PrivateKeyAccount sender, PublicKeyAccount matcher,
            String spendAssetId, String receiveAssetId, long price, long amount, long expirationTime, long matcherFee)
    {
        int datalen = MIN_BUFFER_SIZE +
                (spendAssetId == null ? 0 : 32) +
                (receiveAssetId == null ? 0 : 32);
        if (datalen == MIN_BUFFER_SIZE) {
            throw new IllegalArgumentException("Both spendAsset and receiveAsset are WAVES");
        }
        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(sender.getPublicKey()).put(matcher.getPublicKey());
        if (spendAssetId != null) {
            buf.put(Base58.decode(spendAssetId));
        }
        if (receiveAssetId != null) {
            buf.put(Base58.decode(receiveAssetId));
        }
        buf.putLong(price).putLong(amount).putLong(expirationTime).putLong(matcherFee);
        String signature = sign(sender, buf);
        return new Transaction("/matcher/orders/place",
                "sender", base58(sender.getPublicKey()),
                "matcher", base58(matcher.getPublicKey()),
                "spendAssetId", spendAssetId,
                "receiveAssetId", receiveAssetId,
                "price", price,
                "amount", amount,
                "maxTimestamp", expirationTime,
                "matcherFee", matcherFee,
                "signature", signature);
    }

    public static Transaction makeOrderCancelTx(PrivateKeyAccount sender,
            String spendAssetId, String receiveAssetId, String orderId, long fee)
    {
        long timestamp = System.currentTimeMillis();
        int datalen = MIN_BUFFER_SIZE +
                (spendAssetId == null ? 0 : 32) +
                (receiveAssetId == null ? 0 : 32);
        if (datalen == MIN_BUFFER_SIZE) {
            throw new IllegalArgumentException("Both spendAsset and receiveAsset are WAVES");
        }
        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(sender.getPublicKey());
        if (spendAssetId != null) {
            buf.put(Base58.decode(spendAssetId));
        }
        if (receiveAssetId != null) {
            buf.put(Base58.decode(receiveAssetId));
        }
        buf.put(Base58.decode(orderId)).putLong(fee).putLong(timestamp);
        String signature = sign(sender, buf);
        return new Transaction("/matcher/orders/cancel",
                "sender", Base58.encode(sender.getPublicKey()),
                        "spendAssetId", spendAssetId,
                        "receiveAssetId", receiveAssetId,
                        "orderId", orderId,
                        "fee", fee,
                        "timestamp", timestamp,
                        "signature", signature);
    }
}
