package com.wavesplatform.core;

import com.google.common.io.BaseEncoding;
import org.bitcoinj.core.Base58;

import java.nio.ByteBuffer;
import java.util.Arrays;

/// talk to node
/// node GETs: block height, balances etc
public class BlockchainTransaction extends Transaction {
    private static final byte ISSUE         = 0x03;
    private static final byte TRANSFER      = 0x04;
    private static final byte REISSUE       = 0x05;
    private static final byte BURN          = 0x06;
    private static final byte LEASE         = 0x08;
    private static final byte LEASE_CANCEL  = 0x09;
    private static final byte ALIAS         = 0x10;

    private byte[] bytes;

    private BlockchainTransaction(ByteBuffer buffer, Object... items) {
        super(items);
        this.bytes = buffer.array();
    }

    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public String getEncodedBytes() {
        return BaseEncoding.base16().upperCase().encode(bytes);
    }

    private static String sign(PrivateKeyAccount account, ByteBuffer buffer, int pos, int len, int signaturePos) {
        byte[] signature = sign(account, buffer, pos, len);
        buffer.position(signaturePos);
        buffer.put(signature);
        return Base58.encode(signature);
    }

    public static BlockchainTransaction makeIssueTx(PrivateKeyAccount account,
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
        return new BlockchainTransaction(buf,
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

    public static BlockchainTransaction makeReissueTx(PrivateKeyAccount account,
                                                      byte[] assetId, long quantity, boolean reissuable, long fee)
    {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(155);
        buf.put(REISSUE).put(65, REISSUE)
                .put(account.getPublicKey()).put(assetId).putLong(quantity)
                .put((byte) (reissuable ? 1 : 0))
                .putLong(fee).putLong(timestamp);
        String signature = sign(account, buf, 65, -1, 1);
        return new BlockchainTransaction(buf,
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "assetId", Base58.encode(assetId),
                "quantity", quantity,
                "reissuable", reissuable,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static BlockchainTransaction makeTransferTx(PrivateKeyAccount account,
                                                       AddressOrAlias address, long amount, byte[] assetId, long fee, byte[] feeAssetId, String message)
    {
        if (message == null) message = "";
        int datalen = (assetId == null ? 0 : 32) +
                (feeAssetId == null ? 0 : 32) +
                126 + address.length() + message.length();
        long timestamp = System.currentTimeMillis();/// /1000?

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
        return new BlockchainTransaction(buf,
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "recipient", address.toString(),
                "amount", amount,
                "fee", fee,
                "timestamp", timestamp,
                "attachment", message);
    }

    public static BlockchainTransaction makeBurnTx(PrivateKeyAccount account, byte[] assetId, long amount, long fee) {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(153);
        buf.put(BURN).put(account.getPublicKey()).put(assetId)
                .putLong(amount).putLong(fee).putLong(timestamp);
        String signature = sign(account, buf, 0, 89, 89);
        return new BlockchainTransaction(buf,
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "assetId", Base58.encode(assetId),
                "amount", amount,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static BlockchainTransaction makeLeaseTx(PrivateKeyAccount account, AddressOrAlias recipient, long amount, long fee) {
        long timestamp = System.currentTimeMillis();
        int addrlen = recipient.length();
        ByteBuffer buf = ByteBuffer.allocate(121 + addrlen);
        buf.put(LEASE).put(account.getPublicKey()).put(recipient.getBytes())
                .putLong(amount).putLong(fee).putLong(timestamp);
        String signature = sign(account, buf, 0, 57 + addrlen, 57 + addrlen);
        return new BlockchainTransaction(buf,
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "recipient", recipient.toString(),
                "amount", amount,
                "fee", fee,
                "timestamp", timestamp);
    }

    public static BlockchainTransaction makeLeaseCancelTx(PrivateKeyAccount account, byte[] txId, long fee) {
        long timestamp = System.currentTimeMillis();
        ByteBuffer buf = ByteBuffer.allocate(145);
        buf.put(LEASE_CANCEL).put(account.getPublicKey()).putLong(fee).putLong(timestamp).put(txId);
        String signature = sign(account, buf, 0, 81, 81);
        return new BlockchainTransaction(buf,
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "leaseId", Base58.encode(txId),
                "fee", fee,
                "timestamp", timestamp);
    }

    public static BlockchainTransaction makeAliasTx(PrivateKeyAccount account, Alias alias, long fee) {
        long timestamp = System.currentTimeMillis();
        int aliaslen = alias.length();
        ByteBuffer buf = ByteBuffer.allocate(115 + aliaslen);
        buf.put(ALIAS).put(account.getPublicKey())
                .putShort((short) alias.length()).put(alias.getBytes()).putLong(fee).putLong(timestamp);
        String signature = sign(account, buf, 0, 51 + aliaslen, 51 + aliaslen);
        return new BlockchainTransaction(buf,
                "senderPublicKey", Base58.encode(account.getPublicKey()),
                "signature", signature,
                "alias", alias.toString(),
                "fee", fee,
                "timestamp", timestamp);
    }
}
