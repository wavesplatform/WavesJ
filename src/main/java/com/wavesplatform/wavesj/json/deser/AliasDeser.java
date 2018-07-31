package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wavesplatform.wavesj.Alias;

import java.io.IOException;

public class AliasDeser extends JsonDeserializer<Alias> {
    @Override
    public Alias deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return Alias.fromString(jsonParser.getValueAsString());
    }
}
