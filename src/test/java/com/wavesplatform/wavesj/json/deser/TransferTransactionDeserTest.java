package com.wavesplatform.wavesj.json.deser;

import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.transactions.TransferTransactionV1;
import com.wavesplatform.wavesj.transactions.TransferTransactionV2;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class TransferTransactionDeserTest extends TransactionDeserTest {

    /**
     * address=3MtEyGGB3XQ6zWB71cCN98fotHvjxxwNMu4
     * public=4QZkF9ejEsao1M8pNDAjoNqGsLsT3E6koXbNtCFxscce
     * private=3C8rjfZJnh2EHhKccvggPsoMXLw53DriT8EnjyRPJdhh
     * seed=sunset noodle trap mule mango can spring garment slot august photo champion paper host more
     */
    TransferTransactionV1 txV1 = new TransferTransactionV1(
            new PublicKeyAccount("4QZkF9ejEsao1M8pNDAjoNqGsLsT3E6koXbNtCFxscce", (byte) 'T'),
            "3My3KZgFQ3CrVHgz6vGRt8687sH4oAA1qp8",
            1900000,
            Asset.WAVES,
            100000,
            Asset.WAVES,
            new ByteString("4t2Xazb2SX"),
            1526552510868L,
            new ByteString("7iqHtiDN7YPUHEGvAUUWAecyZvEinjLFcPUvnmGgZVhA8JVNvWgVALhqvZaE2D3qAhTxJhKoWKHEpKg5iEXinA6"));

    TransferTransactionV2 txV2 = new TransferTransactionV2(
            new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'),
            "3My3KZgFQ3CrVHgz6vGRt8687sH4oAA1qp8",
            100000000,
            Asset.WAVES,
            100000000,
            Asset.WAVES,
            new ByteString("4t2Xazb2SX"),
            1526641218066L,
            Collections.singletonList(new ByteString("4bfDaqBcnK3hT8ywFEFndxtS1DTSYfncUqd4s5Vyaa66PZHawtC73rDswUur6QZu5RpqM7L9NFgBHT1vhCoox4vi")));

    @Test
    public void V1DeserializeTest() throws IOException {
        deserializationTest("{\"type\": 4," +
                "\"id\": \"H8f4V15H4nBbvJTPe8nLZMKng6SeRuKqpz4vu8EAg9eQ\"," +
                "\"sender\": \"3MtEyGGB3XQ6zWB71cCN98fotHvjxxwNMu4\"," +
                "\"senderPublicKey\": \"4QZkF9ejEsao1M8pNDAjoNqGsLsT3E6koXbNtCFxscce\"," +
                "\"fee\": 100000," +
                "\"timestamp\": 1526552510868," +
                "\"signature\": \"rVyovtmWFAAsQTH3kPzRg1i2Bt6S3bNRiQGRid5S7m53xpgGiLGRKMVsyuqDKdXjCpd8oEnk2WCeThiwBxEozEp\"," +
                "\"version\": 1," +
                "\"recipient\": \"3My3KZgFQ3CrVHgz6vGRt8687sH4oAA1qp8\"," +
                "\"assetId\": null," +
                "\"feeAssetId\": null," +
                "\"feeAsset\": null," +
                "\"amount\": 1900000," +
                "\"height\":1234," +
                "\"attachment\": \"4t2Xazb2SX\"}", txV1, TransferTransactionV1.class);
    }

    /**
     * Account Info:
     * address=3Mvqinkpz45gprXcpgcMb9yqUv4jpBGMQMw
     * public=H9S6sPxueb6z1PB46VZJD6FbaTxsNfT8GHv5PPHbvDHx
     * private=HAWqLZA98pJPvwGZuomKUkNpgfzKt4HhfMdFWPXwjxuX
     * seed=creek extend car eight fat hole farm they behave element bag allow absurd clinic harbor
     */
    @Test
    public void V1DeserializeTest2() throws IOException {
        TransactionWithSignature deserialized = (TransactionWithSignature) mapper.readValue("{\"type\":4," +
                "\"id\":\"3amMDaJJiLhvyxrqs8JpFT5s42gfbZKMGuPasAEEbu1a\"," +
                "\"sender\":\"3Mvqinkpz45gprXcpgcMb9yqUv4jpBGMQMw\"," +
                "\"senderPublicKey\":\"H9S6sPxueb6z1PB46VZJD6FbaTxsNfT8GHv5PPHbvDHx\"," +
                "\"fee\":100000," +
                "\"timestamp\":1534935016512," +
                "\"signature\":\"5e1VdakF93ZgnAYCxRPFpApPHeZqQTUi4Gc9SxYGnW5bCAAmv3GPFPdP6Rfajw1bWQNTMnNneySyRYrsHg8dPQm9\"," +
                "\"version\":1," +
                "\"recipient\":\"3NCC7CuD9GJpZzNENMyczFVtHTtJ972rrTg\"," +
                "\"assetId\":null," +
                "\"feeAssetId\":null," +
                "\"feeAsset\":null," +
                "\"amount\":100000000," +
                "\"height\":1234," +
                "\"attachment\":\"3k9wwt7nZn\"}", Transaction.class);
        assert(deserialized.verifySignature());
    }

    @Test
    public void V2DeserializeTest() throws IOException {
        deserializationTest("{\"type\": 4," +
                "\"id\": \"2qMiGUpNMuRpeyTnXLa1mLuVP1cYEtxys55cQbDaXd5g\"," +
                "\"sender\": \"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\"," +
                "\"senderPublicKey\": \"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\"," +
                "\"fee\": 100000000," +
                "\"timestamp\": 1526641218066," +
                "\"proofs\": [\"4bfDaqBcnK3hT8ywFEFndxtS1DTSYfncUqd4s5Vyaa66PZHawtC73rDswUur6QZu5RpqM7L9NFgBHT1vhCoox4vi\"]," +
                "\"version\": 2," +
                "\"recipient\": \"3My3KZgFQ3CrVHgz6vGRt8687sH4oAA1qp8\"," +
                "\"assetId\": null," +
                "\"feeAssetId\": null," +
                "\"feeAsset\": null," +
                "\"amount\": 100000000," +
                "\"height\":1234," +
                "\"attachment\": \"4t2Xazb2SX\"}", txV2, TransferTransactionV2.class);
    }
}
