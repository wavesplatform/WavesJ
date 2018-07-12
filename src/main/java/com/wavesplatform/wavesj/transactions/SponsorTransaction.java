package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class SponsorTransaction extends Transaction {
    static final byte SPONSOR       = 14;
    private PublicKeyAccount sender;
    private String assetId;
    private long minAssetFee;
    private long fee;
    private long timestamp;

    public SponsorTransaction(PublicKeyAccount sender, String assetId, long minAssetFee, long fee, long timestamp) {
        this.sender = sender;
        this.assetId = assetId;
        this.minAssetFee = minAssetFee;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSender() {
        return sender;
    }

    public String getAssetId() {
        return assetId;
    }

    public long getMinAssetFee() {
        return minAssetFee;
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
        buf.put(sender.getPublicKey()).put(Base58.decode(assetId))
                .putLong(minAssetFee).putLong(fee).putLong(timestamp);
        return buf.array();
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", SPONSOR);
        data.put("senderPublicKey", Base58.encode(sender.getPublicKey()));
        data.put("assetId", assetId);
        data.put("minSponsoredAssetFee", minAssetFee == 0L ? null : minAssetFee);
        data.put("fee", fee);
        data.put("timestamp", timestamp);
        return data;
    }
}
