package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.*;

public class TransferTransactionV1 extends TransactionWithSignature implements TransferTransaction {
    private final PublicKeyAccount senderPublicKey;
    private final String recipient;
    private final long amount;
    private final String assetId;
    private final long fee;
    private final String feeAssetId;
    private final ByteString attachment;
    private final long timestamp;

    @JsonCreator
    public TransferTransactionV1(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                                 @JsonProperty("recipient") String recipient,
                                 @JsonProperty("amount") long amount,
                                 @JsonProperty("assetId") String assetId,
                                 @JsonProperty("fee") long fee,
                                 @JsonProperty("feeAssetId") String feeAssetId,
                                 @JsonProperty("attachment") ByteString attachment,
                                 @JsonProperty("timestamp") long timestamp,
                                 @JsonProperty("signature") ByteString signature) {
        super(signature);
        this.senderPublicKey = senderPublicKey;
        this.recipient = recipient;
        this.amount = amount;
        this.assetId = assetId;
        this.fee = fee;
        this.feeAssetId = feeAssetId;
        this.attachment = attachment;
        this.timestamp = timestamp;
    }

    public TransferTransactionV1(PrivateKeyAccount senderPublicKey,
                                 String recipient,
                                 long amount,
                                 String assetId,
                                 long fee,
                                 String feeAssetId,
                                 ByteString attachment,
                                 long timestamp) {
        super(senderPublicKey);
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
        buf.put(TransferTransaction.TRANSFER);
        buf.put(senderPublicKey.getPublicKey());
        putAsset(buf, assetId);
        putAsset(buf, feeAssetId);
        buf.putLong(timestamp).putLong(amount).putLong(fee);
        putRecipient(buf, senderPublicKey.getChainId(), recipient);
        if (attachment != null) {
            putBytes(buf, attachment.getBytes());
        } else {
            buf.put((byte) 0);
        }
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte getType() {
        return TRANSFER;
    }

    @Override
    public byte getVersion() {
        return Transaction.V1;
    }
}
