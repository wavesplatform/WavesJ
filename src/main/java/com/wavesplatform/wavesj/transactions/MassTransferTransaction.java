package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.*;

public class MassTransferTransaction extends TransactionWithProofs {
    public static final byte MASS_TRANSFER = 11;

    private final PublicKeyAccount senderPublicKey;
    private final String assetId;
    private final Collection<Transfer> transfers;
    private final long fee;
    private final ByteString attachment;
    private final long timestamp;

    @JsonCreator
    public MassTransferTransaction(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                                   @JsonProperty("assetId") String assetId,
                                   @JsonProperty("transfers") Collection<Transfer> transfers,
                                   @JsonProperty("fee") long fee,
                                   @JsonProperty("attachment") ByteString attachment,
                                   @JsonProperty("timestamp") long timestamp,
                                   @JsonProperty("proofs") List<ByteString> proofs) {
        super(proofs);
        this.senderPublicKey = senderPublicKey;
        this.assetId = assetId;
        this.transfers = transfers;
        this.fee = fee;
        this.attachment = attachment;
        this.timestamp = timestamp;
    }

    public MassTransferTransaction(PrivateKeyAccount senderPublicKey,
                                   String assetId,
                                   Collection<Transfer> transfers,
                                   long fee,
                                   ByteString attachment,
                                   long timestamp) {
        super(senderPublicKey);
        this.senderPublicKey = senderPublicKey;
        this.assetId = assetId;
        this.transfers = transfers;
        this.fee = fee;
        this.attachment = attachment;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    public String getAssetId() {
        return Asset.toJsonObject(assetId);
    }

    public Collection<Transfer> getTransfers() {
        return transfers;
    }

    public long getFee() {
        return fee;
    }

    public ByteString getAttachment() {
        return attachment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(5 * KBYTE);
        buf.put(senderPublicKey.getPublicKey());
        putAsset(buf, assetId);
        buf.putShort((short) transfers.size());

        List<Transfer> tr = new ArrayList<Transfer>(transfers.size());
        for (Transfer t : transfers) {
            String rc = putRecipient(buf, senderPublicKey.getChainId(), t.recipient);
            buf.putLong(t.amount);
            tr.add(new Transfer(rc, t.amount));
        }
        buf.putLong(timestamp).putLong(fee);
        putString(buf, attachment.getBase58String());

        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte getType() {
        return MASS_TRANSFER;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
