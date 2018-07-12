package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.*;

import static com.wavesplatform.wavesj.ByteUtils.*;

public class MassTransferTransaction extends Transaction {
    static final byte MASS_TRANSFER = 11;

    private PublicKeyAccount sender;
    private String assetId;
    private Collection<Transfer> transfers;
    private long fee;
    private String attachment;
    private long timestamp;

    public MassTransferTransaction(PublicKeyAccount sender, String assetId, Collection<Transfer> transfers, long fee, String attachment, long timestamp) {
        this.sender = sender;
        this.assetId = assetId;
        this.transfers = transfers;
        this.fee = fee;
        this.attachment = attachment;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSender() {
        return sender;
    }

    public String getAssetId() {
        return assetId;
    }

    public Collection<Transfer> getTransfers() {
        return transfers;
    }

    public long getFee() {
        return fee;
    }

    public String getAttachment() {
        return attachment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(5 * KBYTE);
        buf.put(sender.getPublicKey());
        putAsset(buf, assetId);
        buf.putShort((short) transfers.size());

        List<Transfer> tr = new ArrayList<Transfer>(transfers.size());
        for (Transfer t : transfers) {
            String rc = putRecipient(buf, sender.getChainId(), t.recipient);
            buf.putLong(t.amount);
            tr.add(new Transfer(rc, t.amount));
        }
        buf.putLong(timestamp).putLong(fee);
        putString(buf, attachment);

        return buf.array();
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", MASS_TRANSFER);
        data.put("senderPublicKey", Base58.encode(sender.getPublicKey()));
        data.put("assetId", Asset.toJsonObject(assetId));
        data.put("transfers", transfers);
        data.put("fee", fee);
        data.put("timestamp", timestamp);
        byte[] attachmentBytes = (attachment == null ? "" : attachment).getBytes();

        data.put("attachment", Base58.encode(attachmentBytes));

        return data;
    }
}
