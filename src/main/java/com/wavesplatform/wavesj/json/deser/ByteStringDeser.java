package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wavesplatform.wavesj.ByteString;

import java.io.IOException;

public class ByteStringDeser extends JsonDeserializer<ByteString> {
    @Override
    public ByteString deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return new ByteString(jsonParser.getValueAsString());
    }
}
