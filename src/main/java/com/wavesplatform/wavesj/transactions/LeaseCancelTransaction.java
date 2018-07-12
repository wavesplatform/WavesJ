package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class LeaseCancelTransaction extends Transaction {

    static final byte LEASE_CANCEL = 9;
    private PublicKeyAccount sender;
    private byte chainId;
    private String leaseId;
    private long fee;
    private long timestamp;

    public LeaseCancelTransaction(PublicKeyAccount sender, byte chainId, String leaseId, long fee, long timestamp) {
        this.sender = sender;
        this.chainId = chainId;
        this.leaseId = leaseId;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSender() {
        return sender;
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
        buf.put(sender.getPublicKey()).putLong(fee).putLong(timestamp).put(Base58.decode(leaseId));
        return buf.array();
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", LEASE_CANCEL);
        data.put("chainId", chainId);
        data.put("senderPublicKey", Base58.encode(sender.getPublicKey()));
        data.put("leaseId", leaseId);
        data.put("fee", fee);
        data.put("timestamp", timestamp);
        return data;
    }
}
