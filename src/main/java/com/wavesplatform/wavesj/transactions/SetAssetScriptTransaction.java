package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.*;

public class SetAssetScriptTransaction extends TransactionWithProofs<SetAssetScriptTransaction>{
    public static final byte SET_ASSET_SCRIPT = 15;

    private final PublicKeyAccount senderPublicKey;
    private final byte chainId;
    private final String script;
    private final String assetId;
    private final long fee;
    private final long timestamp;
    private static final int MAX_TX_SIZE = KBYTE;

    public SetAssetScriptTransaction(PrivateKeyAccount senderPublicKey,
                                     byte chainId,
                                     String assetId,
                                     String script,
                                     long fee,
                                     long timestamp) {
        this.senderPublicKey = senderPublicKey;
        this.chainId = chainId;
        this.assetId = assetId;
        this.script = script;
        this.fee = fee;
        this.timestamp = timestamp;
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(senderPublicKey.sign(getBodyBytes()))));
    }

    @JsonCreator
    public SetAssetScriptTransaction(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                                     @JsonProperty("chainId") byte chainId,
                                     @JsonProperty("assetId") String assetId,
                                     @JsonProperty("script") String script,
                                     @JsonProperty("fee") long fee,
                                     @JsonProperty("timestamp") long timestamp,
                                     @JsonProperty("proofs") List<ByteString> proofs) {
        setProofs(proofs);
        this.senderPublicKey = senderPublicKey;
        this.chainId = chainId;
        this.assetId = assetId;
        this.script = script;
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
        return Asset.toJsonObject(assetId);
    }

    public String getScript() {
        return script;
    }

    public long getFee() {
        return fee;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int getTransactionMaxSize(){
        byte[] rawScript = script == null ? new byte[0] : Base64.decode(script);
        return MAX_TX_SIZE+rawScript.length;
    }

    public byte[] getBodyBytes() {

        ByteBuffer buf = ByteBuffer.allocate(getTransactionMaxSize());
        buf.put(SetAssetScriptTransaction.SET_ASSET_SCRIPT);
        buf.put(Transaction.V1);
        buf.put(chainId);
        buf.put(senderPublicKey.getPublicKey());
        buf.put(Base58.decode(assetId));
        buf.putLong(fee)
                .putLong(timestamp);
        putScript(buf, script);
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte getType() {
        return SET_ASSET_SCRIPT;
    }

    @Override
    public byte getVersion() {
        return Transaction.V1;
    }

    public SetAssetScriptTransaction withProof(int index, ByteString proof) {
        List<ByteString> newProofs = updateProofs(index, proof);
        return new SetAssetScriptTransaction(senderPublicKey, chainId, assetId, script, fee, timestamp, newProofs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SetAssetScriptTransaction that = (SetAssetScriptTransaction) o;

        if (getChainId() != that.getChainId()) return false;
        if (getFee() != that.getFee()) return false;
        if (getTimestamp() != that.getTimestamp()) return false;
        if (getAssetId() != null ? !getAssetId().equals(that.getAssetId()) : that.getAssetId() != null) return false;
        if (getSenderPublicKey() != null ? !getSenderPublicKey().equals(that.getSenderPublicKey()) : that.getSenderPublicKey() != null)
            return false;
        return getScript() != null ? getScript().equals(that.getScript()) : that.getScript() == null;
    }

    @Override
    public int hashCode() {
        int result = getSenderPublicKey() != null ? getSenderPublicKey().hashCode() : 0;
        result = 31 * result + (int) getChainId();
        result = 31 * result + (getAssetId() != null ? getAssetId().hashCode() : 0);
        result = 31 * result + (getScript() != null ? getScript().hashCode() : 0);
        result = 31 * result + (int) (getFee() ^ (getFee() >>> 32));
        result = 31 * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        return result;
    }
}
