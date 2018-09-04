package com.wavesplatform.wavesj.json.ser;

import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.transactions.LeaseTransactionV1;
import com.wavesplatform.wavesj.transactions.LeaseTransactionV2;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class LeaseTransactionSerTest extends TransactionSerTest {
    LeaseTransactionV1 txV1 = new LeaseTransactionV1(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "3NCBMxgdghg4tUhEEffSXy11L6hUi6fcBpd", 10000000, 1000000, 1526646300260L, new ByteString("iy3TmfbFds7pc9cDDqfjEJhfhVyNtm3GcxoVz8L3kJFvgRPUmiqqKLMeJGYyN12AhaQ6HvE7aF1tFgaAoCCgNJJ"));
    LeaseTransactionV2 txV2 = new LeaseTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "3NCBMxgdghg4tUhEEffSXy11L6hUi6fcBpd", 10000000, 1000000, 1526646497465L, Collections.singletonList(new ByteString("5Fr3yLwvfKGDsFLi8A8JbHqToHDojrPbdEGx9mrwbeVWWoiDY5pRqS3rcX1rXC9ud52vuxVdBmGyGk5krcgwFu9q")));

    @Test
    public void V1SerializeTest() throws IOException {
        serializationRoadtripTest(txV1, LeaseTransactionV1.class);
    }

    @Test
    public void V2SerializeTest() throws IOException {
        serializationRoadtripTest(txV2, LeaseTransactionV2.class);
    }
}
