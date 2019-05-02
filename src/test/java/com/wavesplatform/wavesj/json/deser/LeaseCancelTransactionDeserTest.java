package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.json.WavesJsonMapper;
import com.wavesplatform.wavesj.transactions.LeaseCancelTransactionV1;
import com.wavesplatform.wavesj.transactions.LeaseCancelTransactionV2;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class LeaseCancelTransactionDeserTest extends TransactionDeserTest {
    ObjectMapper mapper = new WavesJsonMapper((byte) 'T');

    LeaseCancelTransactionV1 txV1 = new LeaseCancelTransactionV1(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "EXhjYjy8a1dURbttrGzfcft7cddDnPnoa3vqaBLCTFVY", 1000000, 1526646300260L, new ByteString("4T76AXcksn2ixhyMNu4m9UyY54M3HDTw5E2HqUsGV4phogs2vpgBcN5oncu4sbW4U3KU197yfHMxrc3kZ7e6zHG3"));
    LeaseCancelTransactionV2 txV2 = new LeaseCancelTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), (byte) 'T', "DJWkQxRyJNqWhq9qSQpK2D4tsrct6eZbjSv3AH4PSha6", 1000000, 1526646300260L, Collections.singletonList(new ByteString("3h5SQLbCzaLoTHUeoCjXUHB6qhNUfHZjQQVsWTRAgTGMEdK5aeULMVUfDq63J56kkHJiviYTDT92bLGc8ELrUgvi")));

    @Test
    public void V1DeserializeTest() throws IOException {
        deserializationTest("{\"height\":1234,\"type\":9,\"id\":\"7hmabbFS8a2z79a29pzZH1s8LHxrsEAnnLjJxNdZ1gGw\",\"sender\":\"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\",\"senderPublicKey\":\"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\",\"fee\":1000000,\"timestamp\":1526646300260,\"signature\":\"4T76AXcksn2ixhyMNu4m9UyY54M3HDTw5E2HqUsGV4phogs2vpgBcN5oncu4sbW4U3KU197yfHMxrc3kZ7e6zHG3\",\"version\":1,\"leaseId\":\"EXhjYjy8a1dURbttrGzfcft7cddDnPnoa3vqaBLCTFVY\",\"chainId\":null}", txV1, LeaseCancelTransactionV1.class);
    }

    @Test
    public void V2DeserializeTest() throws IOException {
        deserializationTest("{\"height\":1234,\"type\":9,\"id\":\"4nvUUiQjTH7D2LFyzaxs8JwaZYZHDggJgq1iP99TvVDM\",\"sender\":\"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\",\"senderPublicKey\":\"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\",\"fee\":1000000,\"timestamp\":1526646300260,\"proofs\":[\"3h5SQLbCzaLoTHUeoCjXUHB6qhNUfHZjQQVsWTRAgTGMEdK5aeULMVUfDq63J56kkHJiviYTDT92bLGc8ELrUgvi\"],\"version\":2,\"leaseId\":\"DJWkQxRyJNqWhq9qSQpK2D4tsrct6eZbjSv3AH4PSha6\",\"chainId\":84}", txV2, LeaseCancelTransactionV2.class);
    }
}
