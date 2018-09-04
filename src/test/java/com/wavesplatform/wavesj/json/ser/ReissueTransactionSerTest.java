package com.wavesplatform.wavesj.json.ser;

import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.transactions.ReissueTransactionV1;
import com.wavesplatform.wavesj.transactions.ReissueTransactionV2;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class ReissueTransactionSerTest extends TransactionSerTest {
    ReissueTransactionV1 txV1 = new ReissueTransactionV1(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz", 100000000L, true, 100000000L, 1526287561757L, new ByteString("3LnRMrjkk7RoV35PTwcdB4yW2rqUqXaKAh8DnPk5tNWABvhVQ9oqdTk3zM8b9AbGtry7WEcQZtevfK92DCFaa6hA"));
    ReissueTransactionV2 txV2 = new ReissueTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), (byte) 'T', "9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz", 100000000L, true, 100000000L, 1526287561757L, Collections.singletonList(new ByteString("4DFEtUwJ9gjMQMuEXipv2qK7rnhhWEBqzpC3ZQesW1Kh8D822t62e3cRGWNU3N21r7huWnaty95wj2tZxYSvCfro")));

    @Test
    public void V1SerializeTest() throws IOException {
        serializationRoadtripTest(txV1, ReissueTransactionV1.class);
    }

    @Test
    public void V2SerializeTest() throws IOException {
        serializationRoadtripTest(txV2, ReissueTransactionV2.class);
    }
}
