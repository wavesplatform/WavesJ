package com.wavesplatform.wavesj.json.ser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wavesplatform.wavesj.ObjectWithProofs;

import java.io.IOException;
import java.io.StringWriter;

public class ObjectWithProofsSer extends JsonSerializer<ObjectWithProofs> {
    private ObjectMapper objectMapper;

    public ObjectWithProofsSer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void serialize(ObjectWithProofs objectWithSignature, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("proofs", objectWithSignature.getProofs());
        JsonFactory factory = new JsonFactory();
        StringWriter innerJsonWriter = new StringWriter();
        JsonGenerator g = factory.createGenerator(innerJsonWriter);
        objectMapper.writeValue(g, objectWithSignature.getObject());
        g.close();
        innerJsonWriter.close();
        String innerJsonString = innerJsonWriter.toString();
        jsonGenerator.writeRaw("," + innerJsonString.substring(1, innerJsonString.length() - 1));
        jsonGenerator.writeEndObject();
    }
}
