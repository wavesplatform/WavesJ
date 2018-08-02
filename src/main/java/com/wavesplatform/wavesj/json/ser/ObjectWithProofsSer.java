package com.wavesplatform.wavesj.json.ser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wavesplatform.wavesj.ObjectWithProofs;
import com.wavesplatform.wavesj.ObjectWithSignature;
import com.wavesplatform.wavesj.transactions.DataTransaction;
import com.wavesplatform.wavesj.transactions.MassTransferTransaction;
import com.wavesplatform.wavesj.transactions.SetScriptTransaction;
import com.wavesplatform.wavesj.transactions.SponsorTransaction;

import java.io.IOException;
import java.io.StringWriter;

public class ObjectWithProofsSer extends JsonSerializer<ObjectWithProofs> {
    private ObjectMapper objectMapper;

    public ObjectWithProofsSer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void serialize(ObjectWithProofs objectWithProofs, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("proofs", objectWithProofs.getProofs());
        byte version = ObjectWithProofs.V2;
        if (objectWithProofs.getObject() instanceof SponsorTransaction ||
                objectWithProofs.getObject() instanceof SetScriptTransaction ||
                objectWithProofs.getObject() instanceof MassTransferTransaction ||
                objectWithProofs.getObject() instanceof DataTransaction) {
            version = ObjectWithSignature.V1;
        }
        jsonGenerator.writeNumberField("version", version);
        JsonFactory factory = new JsonFactory();
        StringWriter innerJsonWriter = new StringWriter();
        JsonGenerator g = factory.createGenerator(innerJsonWriter);
        objectMapper.writeValue(g, objectWithProofs.getObject());
        g.close();
        innerJsonWriter.close();
        String innerJsonString = innerJsonWriter.toString();
        jsonGenerator.writeRaw("," + innerJsonString.substring(1, innerJsonString.length() - 1));
        jsonGenerator.writeEndObject();
    }
}
