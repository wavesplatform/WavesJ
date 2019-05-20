package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.json.WavesJsonMapper;
import com.wavesplatform.wavesj.transactions.IssueTransactionV1;
import com.wavesplatform.wavesj.transactions.IssueTransactionV2;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class IssueTransactionDeserTest extends TransactionDeserTest {
    ObjectMapper mapper = new WavesJsonMapper((byte) 'T');

    IssueTransactionV1 txV1 = new IssueTransactionV1(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "Gigacoin", "Gigacoin", 10000000000L, (byte) 8, true, 100000000, 1526287561757L, new ByteString("28kE1uN1pX2bwhzr9UHw5UuB9meTFEDFgeunNgy6nZWpHX4pzkGYotu8DhQ88AdqUG6Yy5wcXgHseKPBUygSgRMJ"));
    IssueTransactionV2 txV2 = new IssueTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), (byte) 'T', "Gigacoin", "Gigacoin", 10000000000L, (byte) 8, true, null, 100000000, 1526287561757L, Collections.singletonList(new ByteString("28kE1uN1pX2bwhzr9UHw5UuB9meTFEDFgeunNgy6nZWpHX4pzkGYotu8DhQ88AdqUG6Yy5wcXgHseKPBUygSgRMJ")));

    @Test
    public void V1DeserializeTest() throws IOException {
        deserializationTest("{\"height\":1234,\"type\":3,\"id\":\"9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz\",\"sender\":\"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\",\"senderPublicKey\":\"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\",\"fee\":100000000,\"timestamp\":1526287561757,\"version\":1,\"signature\":\"28kE1uN1pX2bwhzr9UHw5UuB9meTFEDFgeunNgy6nZWpHX4pzkGYotu8DhQ88AdqUG6Yy5wcXgHseKPBUygSgRMJ\",\"assetId\":\"9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz\",\"name\":\"Gigacoin\",\"quantity\":10000000000,\"reissuable\":true,\"decimals\":8,\"description\":\"Gigacoin\"}", txV1, IssueTransactionV1.class);
    }

    @Test
    public void V2DeserializeTest() throws IOException {
        deserializationTest("{\"height\":1234,\"type\":3,\"id\":\"2ykNAo5JrvNCcL8PtCmc9pTcNtKUy2PjJkrFdRvTfUf4\",\"sender\":\"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\",\"senderPublicKey\":\"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\",\"fee\":100000000,\"timestamp\":1526287561757,\"proofs\":[\"43TCfWBa6t2o2ggsD4bU9FpvH3kmDbSBWKE1Z6B5i5Ax5wJaGT2zAvBihSbnSS3AikZLcicVWhUk1bQAMWVzTG5g\"],\"version\":2,\"assetId\":\"2ykNAo5JrvNCcL8PtCmc9pTcNtKUy2PjJkrFdRvTfUf4\",\"name\":\"Gigacoin\",\"quantity\":10000000000,\"reissuable\":true,\"decimals\":8,\"description\":\"Gigacoin\",\"chainId\":84}", txV2, IssueTransactionV2.class);
    }
}
