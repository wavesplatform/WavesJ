package com.wavesplatform.wavesj.json.ser;

import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.transactions.LeaseCancelTransactionV1;
import com.wavesplatform.wavesj.transactions.LeaseCancelTransactionV2;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class LeaseCancelTransactionSerTest extends TransactionSerTest {
    LeaseCancelTransactionV1 txV1 = new LeaseCancelTransactionV1(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "EXhjYjy8a1dURbttrGzfcft7cddDnPnoa3vqaBLCTFVY", 1000000, 1526646300260L, new ByteString("4T76AXcksn2ixhyMNu4m9UyY54M3HDTw5E2HqUsGV4phogs2vpgBcN5oncu4sbW4U3KU197yfHMxrc3kZ7e6zHG3"));
    LeaseCancelTransactionV2 txV2 = new LeaseCancelTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), (byte) 'T', "DJWkQxRyJNqWhq9qSQpK2D4tsrct6eZbjSv3AH4PSha6", 1000000, 1526646300260L, Collections.singletonList(new ByteString("3h5SQLbCzaLoTHUeoCjXUHB6qhNUfHZjQQVsWTRAgTGMEdK5aeULMVUfDq63J56kkHJiviYTDT92bLGc8ELrUgvi")));

    @Test
    public void V1SerializeTest() throws IOException {
        serializationRoadtripTest(txV1, LeaseCancelTransactionV1.class);
    }

    @Test
    public void V2SerializeTest() throws IOException {
        serializationRoadtripTest(txV2, LeaseCancelTransactionV2.class);
    }
}
