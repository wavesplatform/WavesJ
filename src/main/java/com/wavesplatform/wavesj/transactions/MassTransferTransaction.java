package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.*;

public class MassTransferTransaction extends TransactionWithProofs {
    public static final byte MASS_TRANSFER = 11;

    private final PublicKeyAccount senderPublicKey;
    private final String assetId;
    private final Collection<Transfer> transfers;
    private final long fee;
    private final ByteString attachment;
    private final long timestamp;

    @JsonCreator
    public MassTransferTransaction(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                                   @JsonProperty("assetId") String assetId,
                                   @JsonProperty("transfers") Collection<Transfer> transfers,
                                   @JsonProperty("fee") long fee,
                                   @JsonProperty("attachment") ByteString attachment,
                                   @JsonProperty("timestamp") long timestamp,
                                   @JsonProperty("proofs") List<ByteString> proofs) {
        setProofs(proofs);
        this.senderPublicKey = senderPublicKey;
        this.assetId = assetId;
        this.transfers = transfers;
        this.fee = fee;
        this.attachment = attachment;
        this.timestamp = timestamp;
    }

    public MassTransferTransaction(PrivateKeyAccount senderPublicKey,
                                   String assetId,
                                   Collection<Transfer> transfers,
                                   long fee,
                                   ByteString attachment,
                                   long timestamp) {
        this.senderPublicKey = senderPublicKey;
        this.assetId = assetId;
        this.transfers = transfers;
        this.fee = fee;
        this.attachment = attachment;
        this.timestamp = timestamp;
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(senderPublicKey.sign(getBytes()))));
    }

    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    public String getAssetId() {
        return Asset.toJsonObject(assetId);
    }

    public Collection<Transfer> getTransfers() {
        return transfers;
    }

    public long getFee() {
        return fee;
    }

    public ByteString getAttachment() {
        return attachment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(5 * KBYTE);
        buf.put(MassTransferTransaction.MASS_TRANSFER).put(Transaction.V1);
        buf.put(senderPublicKey.getPublicKey());
        putAsset(buf, assetId);
        buf.putShort((short) transfers.size());

        List<Transfer> tr = new ArrayList<Transfer>(transfers.size());
        for (Transfer t : transfers) {
            String rc = putRecipient(buf, senderPublicKey.getChainId(), t.getRecipient());
            buf.putLong(t.getAmount());
            tr.add(new Transfer(rc, t.getAmount()));
        }
        buf.putLong(timestamp).putLong(fee);
        putBytes(buf, attachment.getBytes());

        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte getType() {
        return MASS_TRANSFER;
    }

    @Override
    public byte getVersion() {
        return Transaction.V1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MassTransferTransaction that = (MassTransferTransaction) o;

        if (getFee() != that.getFee()) return false;
        if (getTimestamp() != that.getTimestamp()) return false;
        if (getSenderPublicKey() != null ? !getSenderPublicKey().equals(that.getSenderPublicKey()) : that.getSenderPublicKey() != null)
            return false;
        if (getAssetId() != null ? !getAssetId().equals(that.getAssetId()) : that.getAssetId() != null) return false;
        if (getTransfers() != null ? !getTransfers().equals(that.getTransfers()) : that.getTransfers() != null)
            return false;
        return getAttachment() != null ? getAttachment().equals(that.getAttachment()) : that.getAttachment() == null;
    }

    @Override
    public int hashCode() {
        int result = getSenderPublicKey() != null ? getSenderPublicKey().hashCode() : 0;
        result = 31 * result + (getAssetId() != null ? getAssetId().hashCode() : 0);
        result = 31 * result + (getTransfers() != null ? getTransfers().hashCode() : 0);
        result = 31 * result + (int) (getFee() ^ (getFee() >>> 32));
        result = 31 * result + (getAttachment() != null ? getAttachment().hashCode() : 0);
        result = 31 * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        return result;
    }
}
