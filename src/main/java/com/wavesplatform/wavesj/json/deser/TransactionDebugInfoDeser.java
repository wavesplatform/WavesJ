package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.wavesplatform.wavesj.ApplicationStatus;
import com.wavesplatform.wavesj.TransactionDebugInfo;
import com.wavesplatform.wavesj.StateChanges;
import im.mak.waves.transactions.Transaction;

import java.io.IOException;

public class TransactionDebugInfoDeser extends JsonDeserializer<TransactionDebugInfo> {

    @Override
    public TransactionDebugInfo deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = p.getCodec();
        JsonNode json = codec.readTree(p);

        //transaction fields and info fields are on the same level
        Transaction tx = Transaction.fromJson(json.toString());
        ApplicationStatus status = codec.treeToValue(json.get("applicationStatus"), ApplicationStatus.class);
        //transactions in block don't have height, so their height get filled later in the Block constructor
        int height = json.hasNonNull("height") ? json.get("height").asInt() : 0;
        StateChanges stateChanges = json.hasNonNull("stateChanges")
                ? codec.treeToValue(json.get("stateChanges"), StateChanges.class)
                : null;

        return new TransactionDebugInfo(tx, status, height, stateChanges);
    }
}
