package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class SponsorTransaction extends TransactionWithProofs<Transaction> {
    public static final byte SPONSOR = 14;
    private PublicKeyAccount senderPublicKey;
    private String assetId;
    private long minSponsoredAssetFee;
    private long fee;
    private long timestamp;

    @JsonCreator
    public SponsorTransaction(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                              @JsonProperty("assetId") String assetId,
                              @JsonProperty("minSponsoredAssetFee") long minSponsoredAssetFee,
                              @JsonProperty("fee") long fee,
                              @JsonProperty("timestamp") long timestamp,
                              @JsonProperty("proofs") List<ByteString> proofs) {
        setProofs(proofs);
        this.senderPublicKey = senderPublicKey;
        this.assetId = assetId;
        this.minSponsoredAssetFee = minSponsoredAssetFee;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public SponsorTransaction(PrivateKeyAccount senderPublicKey,
                              String assetId,
                              long minSponsoredAssetFee,
                              long fee,
                              long timestamp) {
        this.senderPublicKey = senderPublicKey;
        this.assetId = assetId;
        this.minSponsoredAssetFee = minSponsoredAssetFee;
        this.fee = fee;
        this.timestamp = timestamp;
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(senderPublicKey.sign(getBodyBytes()))));
    }


    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    public String getAssetId() {
        return Asset.toJsonObject(assetId);
    }

    public long getMinSponsoredAssetFee() {
        return minSponsoredAssetFee;
    }

    public long getFee() {
        return fee;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public byte[] getBodyBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(SponsorTransaction.SPONSOR).put(Transaction.V1)
                .put(senderPublicKey.getPublicKey()).put(Base58.decode(assetId))
                .putLong(minSponsoredAssetFee).putLong(fee).putLong(timestamp);
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte getType() {
        return SPONSOR;
    }

    @Override
    public byte getVersion() {
        return Transaction.V1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SponsorTransaction that = (SponsorTransaction) o;

        if (getMinSponsoredAssetFee() != that.getMinSponsoredAssetFee()) return false;
        if (getFee() != that.getFee()) return false;
        if (getTimestamp() != that.getTimestamp()) return false;
        if (getSenderPublicKey() != null ? !getSenderPublicKey().equals(that.getSenderPublicKey()) : that.getSenderPublicKey() != null)
            return false;
        return getAssetId() != null ? getAssetId().equals(that.getAssetId()) : that.getAssetId() == null;
    }

    @Override
    public int hashCode() {
        int result = getSenderPublicKey() != null ? getSenderPublicKey().hashCode() : 0;
        result = 31 * result + (getAssetId() != null ? getAssetId().hashCode() : 0);
        result = 31 * result + (int) (getMinSponsoredAssetFee() ^ (getMinSponsoredAssetFee() >>> 32));
        result = 31 * result + (int) (getFee() ^ (getFee() >>> 32));
        result = 31 * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        return result;
    }

    @Override
    public Transaction withProof(int index, ByteString proof) {
        List<ByteString> newProofs = updateProofs(index, proof);
        return new SponsorTransaction(senderPublicKey, assetId, minSponsoredAssetFee,fee,timestamp, newProofs);
    }
}
