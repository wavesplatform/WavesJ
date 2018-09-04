package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wavesplatform.wavesj.Base64;
import com.wavesplatform.wavesj.ByteString;

import java.io.IOException;

public class ByteStringDeser extends JsonDeserializer<ByteString> {
    @Override
    public ByteString deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String str = jsonParser.getValueAsString();
        if (str.startsWith("base58:")) {
            return new ByteString(str.substring(7));
        } else if (str.startsWith("base64:")) {
            return new ByteString(Base64.decode(str.substring(7)));
        } else return new ByteString(str);
    }
}
