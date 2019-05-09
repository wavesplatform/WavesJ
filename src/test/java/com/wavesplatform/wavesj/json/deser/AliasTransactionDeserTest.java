package com.wavesplatform.wavesj.json.deser;

import com.wavesplatform.wavesj.Alias;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.transactions.AliasTransactionV1;
import com.wavesplatform.wavesj.transactions.AliasTransactionV2;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class AliasTransactionDeserTest extends TransactionDeserTest {
    AliasTransactionV1 txV1 = new AliasTransactionV1(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), new Alias("myalias", (byte) 'T'), 100000, 1526910778245L, new ByteString("CC1jQ4qkuVfMvB2Kpg2Go6QKXJxUFC8UUswUxBsxwisrR8N5s3Yc8zA6dhjTwfWKfdouSTAnRXCxTXb3T6pJq3T"));
    AliasTransactionV2 txV2 = new AliasTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), new Alias("myalias", (byte) 'T'), 100000, 1526910778245L, Collections.singletonList(new ByteString("26U7rQTwpdma5GYSZb5bNygVCtSuWL6DKet1Nauf5J57v19mmfnq434YrkKYJqvYt2ydQBUT3P7Xgj5ZVDVAcc5k")));

    @Test
    public void V1DeserializeTest() throws IOException {
        deserializationTest("{\"height\":1234,\"type\":10,\"id\":\"7acjQQWJAharrgzb4Z6jo3eeAKAGPmLkHTPtvBTKaiug\",\"sender\":\"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\",\"senderPublicKey\":\"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\",\"fee\":100000,\"timestamp\":1526910778245,\"signature\":\"CC1jQ4qkuVfMvB2Kpg2Go6QKXJxUFC8UUswUxBsxwisrR8N5s3Yc8zA6dhjTwfWKfdouSTAnRXCxTXb3T6pJq3T\",\"version\":1,\"alias\":\"myalias\"}", txV1, AliasTransactionV1.class);
    }

    @Test
    public void V2DeserializeTest() throws IOException {
        deserializationTest("{\"height\":1234,\"type\":10,\"id\":\"7acjQQWJAharrgzb4Z6jo3eeAKAGPmLkHTPtvBTKaiug\",\"sender\":\"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\",\"senderPublicKey\":\"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\",\"fee\":100000,\"timestamp\":1526910778245,\"proofs\":[\"26U7rQTwpdma5GYSZb5bNygVCtSuWL6DKet1Nauf5J57v19mmfnq434YrkKYJqvYt2ydQBUT3P7Xgj5ZVDVAcc5k\"],\"version\":2,\"alias\":\"myalias\"}", txV2, AliasTransactionV2.class);
    }
}
