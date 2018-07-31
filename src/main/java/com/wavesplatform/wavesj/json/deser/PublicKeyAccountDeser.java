package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.PublicKeyAccount;

import java.io.IOException;

public class PublicKeyAccountDeser extends JsonDeserializer<PublicKeyAccount> {
    private byte chainId;
    private ObjectMapper mapper = new ObjectMapper();

    public PublicKeyAccountDeser(byte chainId) {
        this.chainId = chainId;
    }

    @Override
    public PublicKeyAccount deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return new PublicKeyAccount(jsonParser.currentToken().asString(), chainId);
    }
}
