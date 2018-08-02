package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wavesplatform.wavesj.matcher.Order;

import java.io.IOException;

public class OrderStatusDeser extends JsonDeserializer<Order.Status> {
    @Override
    public Order.Status deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return Order.Status.fromString(jsonParser.getValueAsString());
    }
}
