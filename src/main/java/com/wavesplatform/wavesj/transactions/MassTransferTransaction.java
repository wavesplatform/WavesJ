package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wavesplatform.wavesj.*;

import java.nio.ByteBuffer;
import java.util.*;

import static com.wavesplatform.wavesj.ByteUtils.*;

//@JsonDeserialize(using = MassTransferTransaction.Deserializer.class)
public class MassTransferTransaction extends Transaction {
    public static final byte MASS_TRANSFER = 11;

//    public static final TypeReference<MassTransferTransaction> TRANSACTION_TYPE = new TypeReference<MassTransferTransaction>() {};
//    public static final JavaType SIGNED_TRANSACTION_TYPE = mapper.getTypeFactory().constructParametricType(ObjectWithSignature.class, MassTransferTransaction.class);
//    public static final JavaType PROOFED_TRANSACTION_TYPE = mapper.getTypeFactory().constructParametricType(ObjectWithProofs.class, MassTransferTransaction.class);

    private final PublicKeyAccount sender;
    private final String assetId;
    private final Collection<Transfer> transfers;
    private final long fee;
    private final String attachment;
    private final long timestamp;

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

        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", MASS_TRANSFER);
        data.put("id", getId());
        data.put("senderPublicKey", Base58.encode(sender.getPublicKey()));
        data.put("assetId", Asset.toJsonObject(assetId));
        data.put("transfers", transfers);
        data.put("fee", fee);
        data.put("timestamp", timestamp);
        byte[] attachmentBytes = (attachment == null ? "" : attachment).getBytes();

        data.put("attachment", Base58.encode(attachmentBytes));

        return data;
    }

    @Override
    public byte getType() {
        return MASS_TRANSFER;
    }
}
