package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.json.WavesJsonMapper;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TransactionDeserTest {
    ObjectMapper mapper = new WavesJsonMapper((byte) 'T');

    protected void deserializationTest(String json, Transaction tx, Class<?> txClass) throws IOException {
        Transaction deserialized = (Transaction) mapper.readValue(json, txClass);
        assertEquals(deserialized, tx);
        assertEquals(deserialized.getId(), tx.getId());
    }
}
