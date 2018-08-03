package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class BurnTransactionV1 extends TransactionWithSignature implements BurnTransaction {
    private final PublicKeyAccount senderPublicKey;
    private final byte chainId;
    private final String assetId;
    private final long amount;
    private final long fee;
    private final long timestamp;

    public BurnTransactionV1(PrivateKeyAccount senderPublicKey,
                             byte chainId,
                             String assetId,
                             long amount,
                             long fee,
                             long timestamp) {
        super(senderPublicKey);
        this.senderPublicKey = senderPublicKey;
        this.chainId = chainId;
        this.assetId = assetId;
        this.amount = amount;
        this.fee = fee;
        this.timestamp = timestamp;
    }
    @JsonCreator
    public BurnTransactionV1(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                             @JsonProperty("chainId") byte chainId,
                             @JsonProperty("assetId") String assetId,
                             @JsonProperty("amount") long amount,
                             @JsonProperty("fee") long fee,
                             @JsonProperty("timestamp") long timestamp,
                             @JsonProperty("signature") ByteString signature) {
        super(signature);
        this.senderPublicKey = senderPublicKey;
        this.chainId = chainId;
        this.assetId = assetId;
        this.amount = amount;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    public byte getChainId() {
        return chainId;
    }

    public String getAssetId() {
        return assetId;
    }

    public long getAmount() {
        return amount;
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
        buf.put(senderPublicKey.getPublicKey()).put(Base58.decode(assetId))
                .putLong(amount).putLong(fee).putLong(timestamp);
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte getType() {
        return BURN;
    }

    @Override
    public byte getVersion() {
        return Transaction.V1;
    }
}
