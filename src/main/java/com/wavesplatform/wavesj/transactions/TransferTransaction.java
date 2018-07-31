package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.wavesplatform.wavesj.ByteUtils.*;

public class TransferTransaction extends Transaction {
    public static final byte TRANSFER = 4;

    private final PublicKeyAccount sender;
    private final String recipient;
    private final long amount;
    private final String assetId;
    private final long fee;
    private final String feeAssetId;
    private final ByteString attachment;
    private final long timestamp;

    public TransferTransaction(PublicKeyAccount sender, String recipient, long amount, String assetId, long fee, String feeAssetId, ByteString attachment, long timestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.assetId = assetId;
        this.fee = fee;
        this.feeAssetId = feeAssetId;
        this.attachment = attachment;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public long getAmount() {
        return amount;
    }

    public String getAssetId() {
        return assetId;
    }

    public long getFee() {
        return fee;
    }

    public String getFeeAssetId() {
        return feeAssetId;
    }

    public ByteString getAttachment() {
        return attachment;
    }

    public long getTimestamp() {
        return timestamp;
    }


    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(sender.getPublicKey());
        putAsset(buf, assetId);
        putAsset(buf, feeAssetId);
        buf.putLong(timestamp).putLong(amount).putLong(fee);
        putRecipient(buf, sender.getChainId(), recipient);
        putString(buf, attachment.getBase58String());
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", TRANSFER);
        data.put("id", getId());
        data.put("senderPublicKey", Base58.encode(sender.getPublicKey()));
        data.put("recipient", recipient);
        data.put("amount", amount);
        data.put("assetId", Asset.toJsonObject(assetId));
        data.put("fee", fee);
        data.put("feeAssetId", Asset.toJsonObject(feeAssetId));
        data.put("timestamp", timestamp);
        data.put("attachment", attachment);
        return data;
    }

    @Override
    public byte getType() {
        return TRANSFER;
    }
}
