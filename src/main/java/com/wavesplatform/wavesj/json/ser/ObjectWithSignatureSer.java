package com.wavesplatform.wavesj.json.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wavesplatform.wavesj.ObjectWithSignature;

import java.io.IOException;

public class ObjectWithSignatureSer extends JsonSerializer<ObjectWithSignature> {
    private ObjectMapper objectMapper;

    public ObjectWithSignatureSer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void serialize(ObjectWithSignature objectWithSignature, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("signature", objectWithSignature.getSignature());
        objectMapper.writeValue(jsonGenerator, objectWithSignature.getObject());
        jsonGenerator.writeEndObject();
    }
}
