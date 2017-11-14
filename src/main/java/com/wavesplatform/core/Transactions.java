package com.wavesplatform.core;

import org.bitcoinj.core.Base58;
import org.whispersystems.curve25519.Curve25519;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class Transactions {
    private static final byte ISSUE         = 3;
    private static final byte TRANSFER      = 4;
    private static final byte REISSUE       = 5;
    private static final byte BURN          = 6;
    private static final byte LEASE         = 8;
    private static final byte LEASE_CANCEL  = 9;
    private static final byte ALIAS         = 10;

    private static Curve25519 cipher = Curve25519.getInstance(Curve25519.BEST);

    private static HashMap<String, Object> toDict(Object... data) {
        HashMap<String, Object> result = new HashMap<>();
        for (int i=0; i<data.length; i+=2) {
            result.put((String) data[i], data[i + 1]);
        }
        return result;
    }

    public static HashMap<String, Object> makeIssueTx(byte[] publicKey, byte[] privateKey,
            String name, String description, long quantity, int decimals, boolean reissuable, long fee)
    {
        int desclen = description == null ? 0 : description.length();
        long timestamp = System.currentTimeMillis();

        ByteBuffer buf = ByteBuffer.allocate(63 + name.length() + desclen);
        buf.put(ISSUE).put(publicKey)
                .putShort((short) name.length()).put(name.getBytes())
                .putShort((short) desclen);
        if (desclen > 0) {
            buf.put(description.getBytes());
        }
        buf.putLong(quantity)
                .put((byte) decimals)
                .put((byte) (reissuable ? 1 : 0))
                .putLong(fee).putLong(timestamp);

        byte[] signature = cipher.calculateSignature(privateKey, buf.array());
        return toDict(
                "senderPublicKey", Base58.encode(publicKey),
                "name", name,
                "description", description,
                "quantity", quantity,
                "decimals", decimals,
                "reissuable", reissuable,
                "fee", fee,
                "timestamp", timestamp,
                "signature", Base58.encode(signature));
    }

    public static HashMap<String, Object> makeReissueTx(byte[] publicKey, byte[] privateKey,
            byte[] assetId, long quantity, boolean reissuable, long fee)
    {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(90);
        buf.put(REISSUE)
                .put(publicKey).put(assetId).putLong(quantity)
                .put((byte) (reissuable ? 1 : 0))
                .putLong(fee).putLong(timestamp);
        byte[] signature = cipher.calculateSignature(privateKey, buf.array());
        return toDict(
                "senderPublicKey", Base58.encode(publicKey),
                "assetId", Base58.encode(assetId),
                "quantity", quantity,
                "reissuable", reissuable,
                "fee", fee,
                "timestamp", timestamp,
                "signature", Base58.encode(signature));
    }

    public static HashMap<String, Object> makeTransferTx(byte[] publicKey, byte[] privateKey,
            AddressOrAlias address, long amount, byte[] assetId, long fee, byte[] feeAssetId, String message)
    {
        if (message == null) message = "";
        int datalen = (assetId == null ? 0 : 32) +
                (feeAssetId == null ? 0 : 32) +
                87 + message.length();
        long timestamp = System.currentTimeMillis();

        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(TRANSFER).put(publicKey);
        if (assetId == null) {
            buf.put((byte) 0);
        } else {
            buf.put((byte) 1).put(assetId);
        }
        if (feeAssetId == null) {
            buf.put((byte) 0);
        } else {
            buf.put((byte) 1).put(feeAssetId);
        }
        buf.putLong(timestamp).putLong(amount).putLong(fee).put(address.toBytes())
                .putShort((short) message.length()).put(message.getBytes());

        byte[] signature = cipher.calculateSignature(privateKey, buf.array());
        return toDict(
                "senderPublicKey", Base58.encode(publicKey),
                "recipient", address.repr(),
                "amount", amount,
                "fee", fee,
                "timestamp", timestamp,
                "attachment", message,
                "signature", Base58.encode(signature));
    }

    public static HashMap<String, Object> makeBurnTx(byte[] publicKey, byte[] privateKey, byte[] assetId, long amount, long fee) {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(89);
        buf.put(BURN).put(publicKey).put(assetId)
                .putLong(amount).putLong(fee).putLong(timestamp);
        byte[] signature = cipher.calculateSignature(privateKey, buf.array());
        return toDict(
                "senderPublicKey", Base58.encode(publicKey),
                "assetId", Base58.encode(assetId),
                "amount", amount,
                "fee", fee,
                "timestamp", timestamp,
                "signature", Base58.encode(signature));
    }

    public static HashMap<String, Object> makeLeaseTx(byte[] publicKey, byte[] privateKey, AddressOrAlias recipient, long amount, long fee) {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(57 + recipient.length());
        buf.put(LEASE).put(publicKey).put(recipient.toBytes())
                .putLong(amount).putLong(fee).putLong(timestamp);
        byte[] signature = cipher.calculateSignature(privateKey, buf.array());
        return toDict(
                "senderPublicKey", Base58.encode(publicKey),
                "recipient", recipient.repr(),
                "amount", amount,
                "fee", fee,
                "timestamp", timestamp,
                "signature", Base58.encode(signature));
    }

    public static HashMap<String, Object> makeLeaseCancelTx(byte[] publicKey, byte[] privateKey, byte[] txId, long fee) {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(81);
        buf.put(LEASE_CANCEL).put(publicKey).putLong(fee).putLong(timestamp).put(txId);
        byte[] signature = cipher.calculateSignature(privateKey, buf.array());
        return toDict(
                "senderPublicKey", Base58.encode(publicKey),
                "leaseId", Base58.encode(txId),
                "fee", fee,
                "timestamp", timestamp,
                "signature", Base58.encode(signature));
    }

    public static HashMap<String, Object> makeAliasTx(byte[] publicKey, byte[] privateKey, Alias alias, long fee) {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(51 + alias.length());
        buf.put(ALIAS).put(publicKey).putShort((short) alias.length()).put(alias.toBytes()).putLong(fee).putLong(timestamp);
        byte[] signature = cipher.calculateSignature(privateKey, buf.array());
        return toDict(
                "senderPublicKey", Base58.encode(publicKey),
                "alias", alias.repr(),
                "fee", fee,
                "timestamp", timestamp,
                "signature", Base58.encode(signature));
    }
}
