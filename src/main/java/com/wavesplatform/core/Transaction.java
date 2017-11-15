package com.wavesplatform.core;

import com.google.common.io.BaseEncoding;
import org.bitcoinj.core.Base58;
import org.whispersystems.curve25519.Curve25519;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Transaction {
    private static final byte ISSUE         = 0x03;
    private static final byte TRANSFER      = 0x04;
    private static final byte REISSUE       = 0x05;
    private static final byte BURN          = 0x06;
    private static final byte LEASE         = 0x08;
    private static final byte LEASE_CANCEL  = 0x09;
    private static final byte ALIAS         = 0x10;

    private static final Curve25519 cipher = Curve25519.getInstance(Curve25519.BEST);

    private byte[] bytes;
    private Map<String, Object> data;

    private static String sign(Account account, ByteBuffer buffer, int pos, int len, int signaturePos) {
        if (len == -1) {
            len = buffer.limit() - pos;
        }
        byte[] dataToSign = new byte[len];
        buffer.position(pos);
        buffer.get(dataToSign, 0, len);
        byte[] signature = cipher.calculateSignature(account.getPrivateKey(), dataToSign);
        buffer.position(signaturePos);
        buffer.put(signature);
        return Base58.encode(signature);
    }

    private static HashMap<String, Object> makeDict(Object... items) {
        HashMap<String, Object> dict = new HashMap<>();
        for (int i=0; i<items.length; i+=2) {
            dict.put((String) items[i], items[i + 1]);
        }
        return dict;
    }

    public static Transaction makeIssueTx(Account account,
            String name, String description, long quantity, int decimals, boolean reissuable, long fee)
    {
        long timestamp = System.currentTimeMillis();
        int desclen = description == null ? 0 : description.length();
        ByteBuffer buf = ByteBuffer.allocate(128 + name.length() + desclen);
        buf.put(ISSUE).put(65, ISSUE)
                .put(account.getPublicKey())
                .putShort((short) name.length()).put(name.getBytes())
                .putShort((short) desclen);
        if (desclen > 0) {
            buf.put(description.getBytes());
        }
        buf.putLong(quantity)
                .put((byte) decimals)
                .put((byte) (reissuable ? 1 : 0))
                .putLong(fee).putLong(timestamp);

        String signature = sign(account, buf, 65, -1, 1);
        return new Transaction(buf, makeDict(
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "name", name,
                "description", description,
                "quantity", quantity,
                "decimals", decimals,
                "reissuable", reissuable,
                "fee", fee,
                "timestamp", timestamp));
    }

    public static Transaction makeReissueTx(Account account,
            byte[] assetId, long quantity, boolean reissuable, long fee)
    {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(155);
        buf.put(REISSUE).put(65, REISSUE)
                .put(account.getPublicKey()).put(assetId).putLong(quantity)
                .put((byte) (reissuable ? 1 : 0))
                .putLong(fee).putLong(timestamp);
        String signature = sign(account, buf, 65, -1, 1);
        return new Transaction(buf, makeDict(
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "assetId", Base58.encode(assetId),
                "quantity", quantity,
                "reissuable", reissuable,
                "fee", fee,
                "timestamp", timestamp));
    }

    public static Transaction makeTransferTx(Account account,
            AddressOrAlias address, long amount, byte[] assetId, long fee, byte[] feeAssetId, String message)
    {
        if (message == null) message = "";
        int datalen = (assetId == null ? 0 : 32) +
                (feeAssetId == null ? 0 : 32) +
                126 + address.length() + message.length();
        long timestamp = System.currentTimeMillis();

        ByteBuffer buf = ByteBuffer.allocate(datalen);
        buf.put(TRANSFER).put(65, TRANSFER).put(account.getPublicKey());
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
        buf.putLong(timestamp).putLong(amount).putLong(fee).put(address.getBytes())
                .putShort((short) message.length()).put(message.getBytes());

        String signature = sign(account, buf, 65, -1, 1);
        return new Transaction(buf, makeDict(
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "recipient", address.toString(),
                "amount", amount,
                "fee", fee,
                "timestamp", timestamp,
                "attachment", message));
    }

    public static Transaction makeBurnTx(Account account, byte[] assetId, long amount, long fee) {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(153);
        buf.put(BURN).put(account.getPublicKey()).put(assetId)
                .putLong(amount).putLong(fee).putLong(timestamp);
        String signature = sign(account, buf, 0, 89, 89);
        return new Transaction(buf, makeDict(
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "assetId", Base58.encode(assetId),
                "amount", amount,
                "fee", fee,
                "timestamp", timestamp));
    }

    public static Transaction makeLeaseTx(Account account, AddressOrAlias recipient, long amount, long fee) {
        long timestamp = System.currentTimeMillis();
        int addrlen = recipient.length();
        ByteBuffer buf = ByteBuffer.allocate(121 + addrlen);
        buf.put(LEASE).put(account.getPublicKey()).put(recipient.getBytes())
                .putLong(amount).putLong(fee).putLong(timestamp);
        String signature = sign(account, buf, 0, 57 + addrlen, 57 + addrlen);
        return new Transaction(buf, makeDict(
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "recipient", recipient.toString(),
                "amount", amount,
                "fee", fee,
                "timestamp", timestamp));
    }

    public static Transaction makeLeaseCancelTx(Account account, byte[] txId, long fee) {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(145);
        buf.put(LEASE_CANCEL).put(account.getPublicKey()).putLong(fee).putLong(timestamp).put(txId);
        String signature = sign(account, buf, 0, 81, 81);
        return new Transaction(buf, makeDict(
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "leaseId", Base58.encode(txId),
                "fee", fee,
                "timestamp", timestamp));
    }

    public static Transaction makeAliasTx(Account account, Alias alias, long fee) {
        long timestamp = System.currentTimeMillis();
        int aliaslen = alias.length();
        ByteBuffer buf = ByteBuffer.allocate(115 + aliaslen);
        buf.put(ALIAS).put(account.getPublicKey())
                .putShort((short) alias.length()).put(alias.getBytes()).putLong(fee).putLong(timestamp);
        String signature = sign(account, buf, 0, 51 + aliaslen, 51 + aliaslen);
        return new Transaction(buf, makeDict(
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "alias", alias.toString(),
                "fee", fee,
                "timestamp", timestamp));
    }

    private Transaction(ByteBuffer buffer, HashMap<String, Object> data) {
        this.bytes = buffer.array();
        this.data = data;
    }

    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public String getEncodedBytes() {
        return BaseEncoding.base16().upperCase().encode(bytes);
    }

    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(data);
    }
}
