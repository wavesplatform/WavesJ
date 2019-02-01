package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wavesplatform.wavesj.matcher.OrderV1;

import java.io.IOException;

public class OrderStatusDeser extends JsonDeserializer<OrderV1.Status> {
    @Override
    public OrderV1.Status deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return OrderV1.Status.fromString(jsonParser.getValueAsString());
    }
}
