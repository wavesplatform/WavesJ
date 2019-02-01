package com.wavesplatform.wavesj.json.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wavesplatform.wavesj.matcher.OrderV1;

import java.io.IOException;

public class OrderTypeSer extends JsonSerializer<OrderV1.Type> {
    @Override
    public void serialize(OrderV1.Type type, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(type.toJson());
    }
}
