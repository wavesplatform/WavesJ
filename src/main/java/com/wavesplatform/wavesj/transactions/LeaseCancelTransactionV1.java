package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.rmi.dgc.Lease;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class LeaseCancelTransactionV1 extends TransactionWithSignature implements LeaseCancelTransaction {
    public static final byte LEASE_CANCEL = 9;

    private final PublicKeyAccount senderPublicKey;
    private final byte chainId;
    private final String leaseId;
    private final long fee;
    private final long timestamp;

    @JsonCreator
    public LeaseCancelTransactionV1(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                                    @JsonProperty("chainId") byte chainId,
                                    @JsonProperty("leaseId") String leaseId,
                                    @JsonProperty("fee") long fee,
                                    @JsonProperty("timestamp") long timestamp,
                                    @JsonProperty("signature") ByteString signature) {
        super(signature);
        this.senderPublicKey = senderPublicKey;
        this.chainId = chainId;
        this.leaseId = leaseId;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public LeaseCancelTransactionV1(PrivateKeyAccount senderPublicKey,
                                    byte chainId,
                                    String leaseId,
                                    long fee,
                                    long timestamp) {
        super(senderPublicKey);
        this.senderPublicKey = senderPublicKey;
        this.chainId = chainId;
        this.leaseId = leaseId;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    public byte getChainId() {
        return chainId;
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
}
