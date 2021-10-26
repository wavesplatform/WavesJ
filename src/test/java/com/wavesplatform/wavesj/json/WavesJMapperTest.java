package com.wavesplatform.wavesj.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.crypto.Crypto;
import com.wavesplatform.transactions.DataTransaction;
import com.wavesplatform.transactions.Transaction;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.data.DataEntry;
import com.wavesplatform.transactions.data.StringEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 10,50,100 })
    void testDeserializeDataTransactionFromBuilder(int dataCount) throws JsonProcessingException {
        // setup
        PrivateKey privateKey = PrivateKey.fromSeed(Crypto.getRandomSeedBytes());
        ObjectMapper sut = new WavesJMapper();

        List<DataEntry> data = new ArrayList<>();
        for(int i=0;i<dataCount;i++) {
            StringEntry.as("test-"+i, UUID.randomUUID().toString());
        }
        String json = DataTransaction.builder(data)
                .fee(Amount.of(100000l))
                .getSignedWith(privateKey)
                .toJson();

        // sut
        Transaction transaction = sut.readValue(json, Transaction.class);

        // verify
        assertNotNull(transaction);
        assertEquals(privateKey.address(),transaction.sender().address());
    }
}