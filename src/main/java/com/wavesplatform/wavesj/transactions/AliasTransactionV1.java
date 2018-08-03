package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.*;

public class AliasTransactionV1 extends TransactionWithSignature implements AliasTransaction {
    private final PublicKeyAccount senderPublicKey;
    private final Alias alias;
    private final long fee;
    private final long timestamp;

    @Override
    public ByteString getId() {
        return new ByteString(hash(ByteArraysUtils.addAll(new byte[]{ALIAS}, alias.getBytes())));
    }

    public AliasTransactionV1(PrivateKeyAccount senderPublicKey,
                              Alias alias,
                              long fee,
                              long timestamp) {
        super(senderPublicKey);
        this.senderPublicKey = senderPublicKey;
        this.alias = alias;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    @JsonCreator
    public AliasTransactionV1(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                              @JsonProperty("alias") Alias alias,
                              @JsonProperty("fee") long fee,
                              @JsonProperty("timestamp") long timestamp,
                              @JsonProperty("signature") ByteString signature
    ) {
        super(signature);
        this.senderPublicKey = senderPublicKey;
        this.alias = alias;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    @Override
    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    @Override
    public Alias getAlias() {
        return alias;
    }

    @Override
    public long getFee() {
        return fee;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(ALIAS).put(senderPublicKey.getPublicKey());
        putBytes(buf, alias.getBytes());
        buf.putLong(fee).putLong(timestamp);
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte getVersion() {
        return Transaction.V1;
    }

    @Override
    public byte getType() {
        return ALIAS;
    }
}
