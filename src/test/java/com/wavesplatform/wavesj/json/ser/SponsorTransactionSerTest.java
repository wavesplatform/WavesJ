package com.wavesplatform.wavesj.json.ser;

import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.transactions.SponsorTransaction;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class SponsorTransactionSerTest extends TransactionSerTest {
    SponsorTransaction tx = new SponsorTransaction(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz", 100000, 100000000, 1520945679531L, Collections.singletonList(new ByteString("3QrF81WkwGhbNvKcwpAVyBPL1MLuAG5qmR6fmtK9PTYQoFKGsFg1Rtd2kbMBuX2ZfiFX58nR1XwC19LUXZUmkXE7")));

    @Test
    public void V1SerializeTest() throws IOException {
        serializationRoadtripTest(tx, SponsorTransaction.class);
    }
}
