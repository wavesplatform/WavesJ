package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.json.WavesJsonMapper;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransactionDeserTest {
    ObjectMapper mapper = new WavesJsonMapper((byte) 'T');

    protected <T extends Transaction> T deserializationTest(String json, T tx, Class<T> txClass) throws IOException {
        T deserialized = mapper.readValue(json, txClass);
        assertEquals(deserialized, tx);
        assertEquals(deserialized.getId(), tx.getId());
        assertTrue("Height must be deserialized and greater than 0", deserialized.getHeight() > 0);
        return deserialized;
    }
}
