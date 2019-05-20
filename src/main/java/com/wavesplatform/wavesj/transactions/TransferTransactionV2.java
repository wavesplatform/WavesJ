package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.*;

public class TransferTransactionV2 extends TransactionWithProofs<TransferTransactionV2> implements TransferTransaction {
    private final PublicKeyAccount senderPublicKey;
    private final String recipient;
    private final long amount;
    private final String assetId;
    private final long fee;
    private final String feeAssetId;
    private final ByteString attachment;
    private final long timestamp;
    private static final int MAX_TX_SIZE = KBYTE;

    @JsonCreator
    public TransferTransactionV2(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                                 @JsonProperty("recipient") String recipient,
                                 @JsonProperty("amount") long amount,
                                 @JsonProperty("address") String assetId,
                                 @JsonProperty("fee") long fee,
                                 @JsonProperty("feeAssetId") String feeAssetId,
                                 @JsonProperty("attachment") ByteString attachment,
                                 @JsonProperty("timestamp") long timestamp,
                                 @JsonProperty("proofs") List<ByteString> proofs) {
        setProofs(proofs);
        this.senderPublicKey = senderPublicKey;
        this.recipient = recipient;
        this.amount = amount;
        this.assetId = assetId;
        this.fee = fee;
        this.feeAssetId = feeAssetId;
        this.attachment = attachment;
        this.timestamp = timestamp;
    }

    public TransferTransactionV2(PrivateKeyAccount senderPublicKey,
                                 String recipient,
                                 long amount,
                                 String assetId,
                                 long fee,
                                 String feeAssetId,
                                 ByteString attachment,
                                 long timestamp) {
        this.senderPublicKey = senderPublicKey;
        this.recipient = recipient;
        this.amount = amount;
        this.assetId = assetId;
        this.fee = fee;
        this.feeAssetId = feeAssetId;
        this.attachment = attachment;
        this.timestamp = timestamp;
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(senderPublicKey.sign(getBodyBytes()))));
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
    public int getTransactionMaxSize(){
        return MAX_TX_SIZE;
    }

    @Override
    public byte[] getBodyBytes() {
        ByteBuffer buf = ByteBuffer.allocate(getTransactionMaxSize());
        buf.put(TransferTransaction.TRANSFER).put(Transaction.V2);
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
        return TransferTransaction.TRANSFER;
    }

    @Override
    public byte getVersion() {
        return Transaction.V2;
    }

    public TransferTransactionV2 withProof(int index, ByteString proof) {
        List<ByteString> newProofs = updateProofs(index, proof);
        return new TransferTransactionV2(senderPublicKey,recipient, amount, assetId, fee, feeAssetId, attachment, timestamp, newProofs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransferTransactionV2 that = (TransferTransactionV2) o;

        if (getAmount() != that.getAmount()) return false;
        if (getFee() != that.getFee()) return false;
        if (getTimestamp() != that.getTimestamp()) return false;
        if (getSenderPublicKey() != null ? !getSenderPublicKey().equals(that.getSenderPublicKey()) : that.getSenderPublicKey() != null)
            return false;
        if (getRecipient() != null ? !getRecipient().equals(that.getRecipient()) : that.getRecipient() != null)
            return false;
        if (getAssetId() != null ? !getAssetId().equals(that.getAssetId()) : that.getAssetId() != null) return false;
        if (getFeeAssetId() != null ? !getFeeAssetId().equals(that.getFeeAssetId()) : that.getFeeAssetId() != null)
            return false;
        return getAttachment() != null ? getAttachment().equals(that.getAttachment()) : that.getAttachment() == null;
    }

    @Override
    public int hashCode() {
        int result = getSenderPublicKey() != null ? getSenderPublicKey().hashCode() : 0;
        result = 31 * result + (getRecipient() != null ? getRecipient().hashCode() : 0);
        result = 31 * result + (int) (getAmount() ^ (getAmount() >>> 32));
        result = 31 * result + (getAssetId() != null ? getAssetId().hashCode() : 0);
        result = 31 * result + (int) (getFee() ^ (getFee() >>> 32));
        result = 31 * result + (getFeeAssetId() != null ? getFeeAssetId().hashCode() : 0);
        result = 31 * result + (getAttachment() != null ? getAttachment().hashCode() : 0);
        result = 31 * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        return result;
    }
}
