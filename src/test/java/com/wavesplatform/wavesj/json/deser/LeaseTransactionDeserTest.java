package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.json.WavesJsonMapper;
import com.wavesplatform.wavesj.transactions.LeaseTransactionV1;
import com.wavesplatform.wavesj.transactions.LeaseTransactionV2;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class LeaseTransactionDeserTest extends TransactionDeserTest {
    ObjectMapper mapper = new WavesJsonMapper((byte) 'T');

    LeaseTransactionV1 txV1 = new LeaseTransactionV1(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "3NCBMxgdghg4tUhEEffSXy11L6hUi6fcBpd", 10000000, 1000000, 1526646300260L, new ByteString("iy3TmfbFds7pc9cDDqfjEJhfhVyNtm3GcxoVz8L3kJFvgRPUmiqqKLMeJGYyN12AhaQ6HvE7aF1tFgaAoCCgNJJ"));
    LeaseTransactionV2 txV2 = new LeaseTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "3NCBMxgdghg4tUhEEffSXy11L6hUi6fcBpd", 10000000, 1000000, 1526646497465L, Collections.singletonList(new ByteString("5Fr3yLwvfKGDsFLi8A8JbHqToHDojrPbdEGx9mrwbeVWWoiDY5pRqS3rcX1rXC9ud52vuxVdBmGyGk5krcgwFu9q")));

    @Test
    public void V1DeserializeTest() throws IOException {
        deserializationTest("{\"height\":1234,\"type\":8,\"id\":\"EXhjYjy8a1dURbttrGzfcft7cddDnPnoa3vqaBLCTFVY\",\"sender\":\"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\",\"senderPublicKey\":\"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\",\"fee\":1000000,\"timestamp\":1526646300260,\"signature\":\"iy3TmfbFds7pc9cDDqfjEJhfhVyNtm3GcxoVz8L3kJFvgRPUmiqqKLMeJGYyN12AhaQ6HvE7aF1tFgaAoCCgNJJ\",\"version\":1,\"amount\":10000000,\"recipient\":\"3NCBMxgdghg4tUhEEffSXy11L6hUi6fcBpd\"}", txV1, LeaseTransactionV1.class);
    }

    @Test
    public void V2DeserializeTest() throws IOException {
        deserializationTest("{\"height\":1234,\"type\":8,\"id\":\"UL85wuJDXXe6BtQUob4KNb72kTaf8RN9Gp1NajvGMeU\",\"sender\":\"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\",\"senderPublicKey\":\"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\",\"fee\":1000000,\"timestamp\":1526646497465,\"proofs\":[\"5Fr3yLwvfKGDsFLi8A8JbHqToHDojrPbdEGx9mrwbeVWWoiDY5pRqS3rcX1rXC9ud52vuxVdBmGyGk5krcgwFu9q\"],\"version\":2,\"amount\":10000000,\"recipient\":\"3NCBMxgdghg4tUhEEffSXy11L6hUi6fcBpd\"}", txV2, LeaseTransactionV2.class);
    }
}
