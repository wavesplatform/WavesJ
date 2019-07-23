package com.wavesplatform.wavesj.json.dataservices;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.json.WavesJsonMapper;
import com.wavesplatform.wavesj.json.deser.TransactionDeserializer;

import java.io.IOException;

public class DataSrvTransactionDeser extends TransactionDeserializer {
    public DataSrvTransactionDeser(WavesJsonMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public Transaction deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        TreeNode rootNode = jsonParser.getCodec().readTree(jsonParser);
        TreeNode treeNode = rootNode.path("data");

        return deserialize(treeNode);
    }
}
