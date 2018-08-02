package com.wavesplatform.wavesj.json.ser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wavesplatform.wavesj.ObjectWithSignature;

import java.io.IOException;
import java.io.StringWriter;

public class ObjectWithSignatureSer extends JsonSerializer<ObjectWithSignature> {
    private ObjectMapper objectMapper;

    public ObjectWithSignatureSer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void serialize(ObjectWithSignature objectWithSignature, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("signature", objectWithSignature.getSignature());
        jsonGenerator.writeNumberField("version", objectWithSignature.getVersion());
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
