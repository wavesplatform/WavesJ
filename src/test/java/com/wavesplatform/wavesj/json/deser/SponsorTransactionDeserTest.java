package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.json.WavesJsonMapper;
import com.wavesplatform.wavesj.transactions.SponsorTransaction;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class SponsorTransactionDeserTest extends TransactionDeserTest {
    ObjectMapper mapper = new WavesJsonMapper((byte) 'T');

    SponsorTransaction tx = new SponsorTransaction(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz", 100000, 100000000, 1520945679531L, Collections.singletonList(new ByteString("3QrF81WkwGhbNvKcwpAVyBPL1MLuAG5qmR6fmtK9PTYQoFKGsFg1Rtd2kbMBuX2ZfiFX58nR1XwC19LUXZUmkXE7")));

    @Test
    public void V1DeserializeTest() throws IOException {
        deserializationTest("{\"type\":14,\"id\":\"Gobt7AiyQAfduRkW8Mk3naWbzH67Zsv9rdmgRNmon1Mb\",\"sender\":\"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\",\"senderPublicKey\":\"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\",\"fee\":100000000,\"timestamp\":1520945679531,\"proofs\":[\"3QrF81WkwGhbNvKcwpAVyBPL1MLuAG5qmR6fmtK9PTYQoFKGsFg1Rtd2kbMBuX2ZfiFX58nR1XwC19LUXZUmkXE7\"],\"version\":1,\"assetId\":\"9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz\",\"minSponsoredAssetFee\":100000}", tx, SponsorTransaction.class);
    }
}
