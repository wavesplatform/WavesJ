package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

//@JsonDeserialize(using = LeaseCancelTransaction.Deserializer.class)
public class LeaseCancelTransaction extends Transaction {
    public static final byte LEASE_CANCEL = 9;

    public static final TypeReference<LeaseCancelTransaction> TRANSACTION_TYPE = new TypeReference<LeaseCancelTransaction>() {};
    public static final JavaType SIGNED_TRANSACTION_TYPE = mapper.getTypeFactory().constructParametricType(ObjectWithSignature.class, LeaseCancelTransaction.class);
    public static final JavaType PROOFED_TRANSACTION_TYPE = mapper.getTypeFactory().constructParametricType(ObjectWithProofs.class, LeaseCancelTransaction.class);

    private final PublicKeyAccount sender;
    private final byte chainId;
    private final String leaseId;
    private final long fee;
    private final long timestamp;

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
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", LEASE_CANCEL);
        data.put("id", getId());
        data.put("chainId", chainId);
        data.put("senderPublicKey", Base58.encode(sender.getPublicKey()));
        data.put("leaseId", leaseId);
        data.put("fee", fee);
        data.put("timestamp", timestamp);
        return data;
    }
}
