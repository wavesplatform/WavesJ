package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wavesplatform.wavesj.matcher.Order;

import java.io.IOException;

public class OrderTypeDeser extends JsonDeserializer<Order.Type> {
    @Override
    public Order.Type deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return Order.Type.valueOf(jsonParser.getValueAsString());
    }
}
