package com.wavesplatform.wavesj.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.transactions.Transaction;
import com.wavesplatform.transactions.common.AssetId;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class WavesJMapperTest {

    private static URL getJsonResourceURL(String fileName) {
        return WavesJMapperTest.class.getResource("/json/"+fileName);
    }

    @Test
    void testDeserializeTransactionData1() throws IOException {
        // setup
        ObjectMapper sut = new WavesJMapper();

        // sut
        Transaction transaction = sut.readValue(getJsonResourceURL("transaction-data-1.json"), Transaction.class);

        // verify transaction with json content of /json/transaction-data-1.json
        assertNotNull(transaction);
        assertEquals("3PHh9Mxg9wrHNWCg5jyyRpFQFLdnpRy5paK",transaction.sender().address().encoded());
        assertEquals("2cTXygSmpSP46VZSAmCcMh1tEHbaQTskRBJQmMvNNW7W",transaction.id().encoded());
        assertEquals(1,transaction.proofs().size());
        assertEquals("3EtQYsPwZyVMjFNzMr6zmNvwAzstNf2fD9XsFAr65enHthw4Js7K8TDLM7QcQRzxgkFCUBww9j5tU1JWL7q5jmgu",transaction.proofs().get(0).encoded());
        assertEquals(100000l,transaction.fee().value());
        // assetId not set in transaction json; defaults to Waves
        assertEquals(AssetId.WAVES,transaction.fee().assetId());
        assertEquals(12,transaction.type());
        assertEquals(1,transaction.version());
        assertEquals(1635245671683l,transaction.timestamp());
    }
}