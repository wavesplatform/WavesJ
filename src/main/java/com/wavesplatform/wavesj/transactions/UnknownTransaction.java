package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.TransactionWithSignature;

public class UnknownTransaction extends TransactionWithSignature {

    private PublicKeyAccount senderPublicKey;
    private long timestamp;
    private long fee;
    private ByteString id;
    private byte version;
    private byte type;

    @JsonCreator
    public UnknownTransaction(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                              @JsonProperty("timestamp") long timestamp,
                              @JsonProperty("fee") long fee,
                              @JsonProperty("signature") ByteString signature,
                              @JsonProperty("id") ByteString id,
                              @JsonProperty("version") byte version,
                              @JsonProperty("type") byte type
    ) {
        this.senderPublicKey = senderPublicKey;
        this.timestamp = timestamp;
        this.fee = fee;
        this.signature = signature;
        this.id = id;
        this.version = version;
        this.type = type;
    }

    @Override
    public ByteString getId() {
        return id;
    }

    @Override
    public byte[] getBodyBytes() {
        throw new IllegalStateException("Can't build the bytes for unknown transaction");
    }

    @Override
    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public long getFee() {
        return fee;
    }

    @Override
    public byte getVersion() {
        return version;
    }

    @Override
    public byte getType() {
        return type;
    }
}
