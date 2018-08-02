package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import com.wavesplatform.wavesj.*;

import java.io.IOException;
import java.util.List;

public class ObjectContainerDeser extends JsonDeserializer<ProofedObject> implements ContextualDeserializer {
    private JavaType valueType;
    private ObjectMapper objectMapper;

    public ObjectContainerDeser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectContainerDeser createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        ObjectContainerDeser deserializer = new ObjectContainerDeser(objectMapper);
        deserializer.valueType = ctxt.getContextualType().getBindings().getBoundType(0);
        return deserializer;
    }

    @Override
    public ProofedObject deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        TreeNode treeNode = jsonParser.getCodec().readTree(jsonParser);
        TreeNode signatureNode = treeNode.get("signature");
        TreeNode proofsNode = treeNode.get("proofs");
        Object tx = objectMapper.treeToValue(treeNode, valueType.getRawClass());
        if (signatureNode != null) {
            String signature = ((TextNode) signatureNode).textValue();
            return new ObjectWithSignature<Signable>((Signable) tx, new ByteString(signature));
        } else if (proofsNode != null) {
            List<ByteString> proofs = objectMapper.readValue(proofsNode.traverse(), new TypeReference<List<ByteString>>() {});
            return new ObjectWithProofs<Transaction>((Transaction) tx, proofs);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
