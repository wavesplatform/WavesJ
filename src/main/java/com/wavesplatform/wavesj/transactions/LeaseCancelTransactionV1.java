package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class LeaseCancelTransactionV1 extends TransactionWithSignature implements LeaseCancelTransaction {
    public static final byte LEASE_CANCEL = 9;

    private final PublicKeyAccount senderPublicKey;
    private final String leaseId;
    private final long fee;
    private final long timestamp;

    @JsonCreator
    public LeaseCancelTransactionV1(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                                    @JsonProperty("leaseId") String leaseId,
                                    @JsonProperty("fee") long fee,
                                    @JsonProperty("timestamp") long timestamp,
                                    @JsonProperty("signature") ByteString signature) {
        this.senderPublicKey = senderPublicKey;
        this.leaseId = leaseId;
        this.fee = fee;
        this.timestamp = timestamp;
        this.signature = signature;
    }



    public LeaseCancelTransactionV1(PrivateKeyAccount senderPublicKey,
                                    String leaseId,
                                    long fee,
                                    long timestamp) {
        this.senderPublicKey = senderPublicKey;
        this.leaseId = leaseId;
        this.fee = fee;
        this.timestamp = timestamp;
        this.signature = new ByteString(senderPublicKey.sign(getBytes()));
    }

    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    public String getLeaseId() {
        return leaseId;
    }

    public long getFee() {
        return fee;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(LeaseCancelTransaction.LEASE_CANCEL);
        buf.put(senderPublicKey.getPublicKey())
                .putLong(fee).putLong(timestamp)
                .put(Base58.decode(leaseId));
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte getType() {
        return LEASE_CANCEL;
    }

    @Override
    public byte getVersion() {
        return Transaction.V1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeaseCancelTransactionV1 that = (LeaseCancelTransactionV1) o;

        if (getFee() != that.getFee()) return false;
        if (getTimestamp() != that.getTimestamp()) return false;
        if (getSenderPublicKey() != null ? !getSenderPublicKey().equals(that.getSenderPublicKey()) : that.getSenderPublicKey() != null)
            return false;
        return getLeaseId() != null ? getLeaseId().equals(that.getLeaseId()) : that.getLeaseId() == null;
    }

    @Override
    public int hashCode() {
        int result = getSenderPublicKey() != null ? getSenderPublicKey().hashCode() : 0;
        result = 31 * result + (getLeaseId() != null ? getLeaseId().hashCode() : 0);
        result = 31 * result + (int) (getFee() ^ (getFee() >>> 32));
        result = 31 * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        return result;
    }
}
