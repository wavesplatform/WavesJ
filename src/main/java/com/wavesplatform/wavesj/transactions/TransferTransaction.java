package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.Asset;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.*;

public class TransferTransaction extends Transaction {
    public static final byte TRANSFER = 4;

    private final PublicKeyAccount senderPublicKey;
    private final String recipient;
    private final long amount;
    private final String assetId;
    private final long fee;
    private final String feeAssetId;
    private final ByteString attachment;
    private final long timestamp;

    @JsonCreator
    public TransferTransaction(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                               @JsonProperty("recipient") String recipient,
                               @JsonProperty("amount") long amount,
                               @JsonProperty("assetId") String assetId,
                               @JsonProperty("fee") long fee,
                               @JsonProperty("feeAssetId") String feeAssetId,
                               @JsonProperty("attachment") ByteString attachment,
                               @JsonProperty("timestamp") long timestamp) {
        this.senderPublicKey = senderPublicKey;
        this.recipient = recipient;
        this.amount = amount;
        this.assetId = assetId;
        this.fee = fee;
        this.feeAssetId = feeAssetId;
        this.attachment = attachment;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    public String getRecipient() {
        return recipient;
    }

    public long getAmount() {
        return amount;
    }

    public String getAssetId() {
        return Asset.toJsonObject(assetId);
    }

    public long getFee() {
        return fee;
    }

    public String getFeeAssetId() {
        return Asset.toJsonObject(feeAssetId);
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
        buf.put(senderPublicKey.getPublicKey());
        putAsset(buf, assetId);
        putAsset(buf, feeAssetId);
        buf.putLong(timestamp).putLong(amount).putLong(fee);
        putRecipient(buf, senderPublicKey.getChainId(), recipient);
        putString(buf, attachment.getBase58String());
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
    }

    @Override
    public byte getType() {
        return TRANSFER;
    }
}
