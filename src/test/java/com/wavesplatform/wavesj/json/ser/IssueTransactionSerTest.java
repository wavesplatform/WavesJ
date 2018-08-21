package com.wavesplatform.wavesj.json.ser;

import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.transactions.IssueTransactionV1;
import com.wavesplatform.wavesj.transactions.IssueTransactionV2;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class IssueTransactionSerTest extends TransactionSerTest {
    IssueTransactionV1 txV1 = new IssueTransactionV1(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "Gigacoin", "Gigacoin", 10000000000L, (byte) 8, true, 100000000, 1526287561757L, new ByteString("28kE1uN1pX2bwhzr9UHw5UuB9meTFEDFgeunNgy6nZWpHX4pzkGYotu8DhQ88AdqUG6Yy5wcXgHseKPBUygSgRMJ"));
    IssueTransactionV2 txV2 = new IssueTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), (byte) 'T', "Gigacoin", "Gigacoin", 10000000000L, (byte) 8, true, null, 100000000, 1526287561757L, Collections.singletonList(new ByteString("28kE1uN1pX2bwhzr9UHw5UuB9meTFEDFgeunNgy6nZWpHX4pzkGYotu8DhQ88AdqUG6Yy5wcXgHseKPBUygSgRMJ")));

    @Test
    public void V1SerializeTest() throws IOException {
        serializationRoadtripTest(txV1, IssueTransactionV1.class);
    }

    @Test
    public void V2SerializeTest() throws IOException {
        serializationRoadtripTest(txV2, IssueTransactionV2.class);
    }
}
