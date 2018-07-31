package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wavesplatform.wavesj.Asset;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.Transaction;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.wavesplatform.wavesj.ByteUtils.*;

//@JsonDeserialize(using = TransferTransaction.Deserializer.class)
public class TransferTransaction extends Transaction {
    public static final byte TRANSFER = 4;

    public static final TypeReference<TransferTransaction> TRANSACTION_TYPE = new TypeReference<TransferTransaction>() {};
    public static final JavaType SIGNED_TRANSACTION_TYPE = mapper.getTypeFactory().constructParametricType(ObjectWithSignature.class, TransferTransaction.class);
    public static final JavaType PROOFED_TRANSACTION_TYPE = mapper.getTypeFactory().constructParametricType(ObjectWithProofs.class, TransferTransaction.class);


    private final PublicKeyAccount sender;
    private final String recipient;
    private final long amount;
    private final String assetId;
    private final long fee;
    private final String feeAssetId;
    private final String attachment;
    private final long timestamp;

    public TransferTransaction(PublicKeyAccount sender, String recipient, long amount, String assetId, long fee, String feeAssetId, String attachment, long timestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.assetId = assetId;
        this.fee = fee;
        this.feeAssetId = feeAssetId;
        this.attachment = attachment;
        this.timestamp = timestamp;
    }

    public PublicKeyAccount getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public long getAmount() {
        return amount;
    }

    public String getAssetId() {
        return assetId;
    }

    public long getFee() {
        return fee;
    }

    public String getFeeAssetId() {
        return feeAssetId;
    }

    public String getAttachment() {
        return attachment;
    }

    public long getTimestamp() {
        return timestamp;
    }


    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(sender.getPublicKey());
        putAsset(buf, assetId);
        putAsset(buf, feeAssetId);
        buf.putLong(timestamp).putLong(amount).putLong(fee);
        putRecipient(buf, sender.getChainId(), recipient);
        putString(buf, attachment);
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", TRANSFER);
        data.put("id", getId());
        data.put("senderPublicKey", Base58.encode(sender.getPublicKey()));
        data.put("recipient", recipient);
        data.put("amount", amount);
        data.put("assetId", Asset.toJsonObject(assetId));
        data.put("fee", fee);
        data.put("feeAssetId", Asset.toJsonObject(feeAssetId));
        data.put("timestamp", timestamp);
        data.put("attachment", attachment);
        return data;
    }
}
