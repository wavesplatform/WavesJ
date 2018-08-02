package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class LeaseCancelTransaction extends Transaction {
    public static final byte LEASE_CANCEL = 9;

    private final PublicKeyAccount senderPublicKey;
    private final byte chainId;
    private final String leaseId;
    private final long fee;
    private final long timestamp;

    @JsonCreator
    public LeaseCancelTransaction(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                                  @JsonProperty("chainId") byte chainId,
                                  @JsonProperty("leaseId") String leaseId,
                                  @JsonProperty("fee") long fee,
                                  @JsonProperty("timestamp") long timestamp) {
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
        buf.put(senderPublicKey.getPublicKey()).putLong(fee).putLong(timestamp).put(Base58.decode(leaseId));
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
    }

    @Override
    public byte getType() {
        return LEASE_CANCEL;
    }
}
