package com.wavesplatform.wavesj.json.deser;

import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.transactions.TransferTransactionV1;
import com.wavesplatform.wavesj.transactions.TransferTransactionV2;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class TransferTransactionDeserTest extends TransactionDeserTest {
    TransferTransactionV1 txV1 = new TransferTransactionV1(
            new PublicKeyAccount("CRxqEuxhdZBEHX42MU4FfyJxuHmbDBTaHMhM3Uki7pLw", (byte) 'T'),
            "3NCBMxgdghg4tUhEEffSXy11L6hUi6fcBpd",
            211279, Asset.WAVES,
            127765, Asset.WAVES,
            new ByteString("6o3ErT152o"),
            1533380600952L,
            new ByteString("CQMjst5nhabw8bQMMei4RfPPVR4YxGk2SPsyKpTj2qqatqmSj8WewNG4foV7TRn3QDFLwH7ftxR4yKURoycNp5B"));
    TransferTransactionV2 txV2 = new TransferTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "3My3KZgFQ3CrVHgz6vGRt8687sH4oAA1qp8", 100000000, Asset.WAVES, 100000000, Asset.WAVES, new ByteString("4t2Xazb2SX"), 1526641218066L, Collections.singletonList(new ByteString("4bfDaqBcnK3hT8ywFEFndxtS1DTSYfncUqd4s5Vyaa66PZHawtC73rDswUur6QZu5RpqM7L9NFgBHT1vhCoox4vi")));

    @Test
    public void V1DeserializeTest() throws IOException {
        deserializationTest("{" +
                "\"type\": 4," +
                "\"id\": \"GNpRVfYPfqg5AQBVgXH2nEedGWZqktnb7vCzrEs4S6qR\"," +
                "\"sender\": \"3NBVqYXrapgJP9atQccdBPAgJPwHDKkh6A8\"," +
                "\"senderPublicKey\": \"CRxqEuxhdZBEHX42MU4FfyJxuHmbDBTaHMhM3Uki7pLw\"," +
                "\"fee\": 127765," +
                "\"timestamp\": 1533380600952," +
                "\"signature\": \"CQMjst5nhabw8bQMMei4RfPPVR4YxGk2SPsyKpTj2qqatqmSj8WewNG4foV7TRn3QDFLwH7ftxR4yKURoycNp5B\"," +
                "\"version\": 1," +
                "\"recipient\": \"3NCBMxgdghg4tUhEEffSXy11L6hUi6fcBpd\"," +
                "\"address\": null," +
                "\"feeAssetId\": null," +
                "\"feeAsset\": null," +
                "\"amount\": 211279," +
                "\"attachment\": \"6o3ErT152o\"}", txV1, TransferTransactionV1.class);
    }

    @Test
    public void V1DeserializeTest2() throws IOException {
        TransactionWithSignature deserialized = (TransactionWithSignature) mapper.readValue("{" +
                        "\"type\": 4," +
                        "\"id\": \"GNpRVfYPfqg5AQBVgXH2nEedGWZqktnb7vCzrEs4S6qR\"," +
                        "\"sender\": \"3NBVqYXrapgJP9atQccdBPAgJPwHDKkh6A8\"," +
                        "\"senderPublicKey\": \"CRxqEuxhdZBEHX42MU4FfyJxuHmbDBTaHMhM3Uki7pLw\"," +
                        "\"fee\": 127765," +
                        "\"timestamp\": 1533380600952," +
                        "\"signature\": \"CQMjst5nhabw8bQMMei4RfPPVR4YxGk2SPsyKpTj2qqatqmSj8WewNG4foV7TRn3QDFLwH7ftxR4yKURoycNp5B\"," +
                        "\"version\": 1," +
                        "\"recipient\": \"3NCBMxgdghg4tUhEEffSXy11L6hUi6fcBpd\"," +
                        "\"address\": null," +
                        "\"feeAssetId\": null," +
                        "\"feeAsset\": null," +
                        "\"amount\": 211279," +
                        "\"attachment\": \"6o3ErT152o\"}"
                , Transaction.class);
        assert(deserialized.verifySignature());
    }

    @Test
    public void V2DeserializeTest() throws IOException {
        deserializationTest("{\"type\": 4,\"id\": \"2qMiGUpNMuRpeyTnXLa1mLuVP1cYEtxys55cQbDaXd5g\",\"sender\": \"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\",\"senderPublicKey\": \"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\",\"fee\": 100000000,\"timestamp\": 1526641218066,\"proofs\": [\"4bfDaqBcnK3hT8ywFEFndxtS1DTSYfncUqd4s5Vyaa66PZHawtC73rDswUur6QZu5RpqM7L9NFgBHT1vhCoox4vi\"],\"version\": 2,\"recipient\": \"3My3KZgFQ3CrVHgz6vGRt8687sH4oAA1qp8\",\"address\": null,\"feeAssetId\": null,\"feeAsset\": null,\"amount\": 100000000,\"attachment\": \"4t2Xazb2SX\"}", txV2, TransferTransactionV2.class);
    }
}
