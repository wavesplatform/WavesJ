package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.TransactionStCh;
import com.wavesplatform.wavesj.json.WavesJsonMapper;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransactionDeserTest {
    ObjectMapper mapper = new WavesJsonMapper((byte) 'T');

    protected <T extends Transaction> T deserializationTest(String json, T tx, Class<T> txClass) throws IOException {
        T deser = mapper.readValue(json, txClass);

        assertTxEquals(tx, deser);

        // test deserialization for common interfaces
        assertTxEquals(tx, mapper.readValue(json, Transaction.class));
        assertTxEquals(tx, mapper.readValue(json, TransactionStCh.class));

        return deser;
    }

    protected static void assertTxEquals(Transaction expected, Transaction actual) {
        assertEquals(expected, actual);
        assertEquals(expected.getId(), actual.getId());
        assertTrue("Height must be deser and greater than 0", actual.getHeight() > 0);
    }
}
