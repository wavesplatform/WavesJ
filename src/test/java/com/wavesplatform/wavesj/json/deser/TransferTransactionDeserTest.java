package com.wavesplatform.wavesj.json.deser;

import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.transactions.TransferTransactionV1;
import com.wavesplatform.wavesj.transactions.TransferTransactionV2;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class TransferTransactionDeserTest extends TransactionDeserTest {
    TransferTransactionV1 txV1 = new TransferTransactionV1(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "3My3KZgFQ3CrVHgz6vGRt8687sH4oAA1qp8", 1900000, Asset.WAVES, 100000, Asset.WAVES, new ByteString("4t2Xazb2SX"), 1526552510868L, new ByteString("eaV1i3hEiXyYQd6DQY7EnPg9XzpAvB9VA3bnpin2qJe4G36GZXaGnYKCgSf9xiQ61DcAwcBFzjSXh6FwCgazzFz"));
    TransferTransactionV2 txV2 = new TransferTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "3My3KZgFQ3CrVHgz6vGRt8687sH4oAA1qp8", 100000000, Asset.WAVES, 100000000, Asset.WAVES, new ByteString("4t2Xazb2SX"), 1526641218066L, Collections.singletonList(new ByteString("4bfDaqBcnK3hT8ywFEFndxtS1DTSYfncUqd4s5Vyaa66PZHawtC73rDswUur6QZu5RpqM7L9NFgBHT1vhCoox4vi")));

    @Test
    public void V1DeserializeTest() throws IOException {
        deserializationTest("{\"type\": 4,\"id\": \"FLszEaqasJptohmP6zrXodBwjaEYq4jRP2BzdPPjvukk\",\"sender\": \"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\",\"senderPublicKey\": \"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\",\"fee\": 100000,\"timestamp\": 1526552510868,\"signature\": \"eaV1i3hEiXyYQd6DQY7EnPg9XzpAvB9VA3bnpin2qJe4G36GZXaGnYKCgSf9xiQ61DcAwcBFzjSXh6FwCgazzFz\",\"version\": 1,\"recipient\": \"3My3KZgFQ3CrVHgz6vGRt8687sH4oAA1qp8\",\"address\": null,\"feeAssetId\": null,\"feeAsset\": null,\"amount\": 1900000,\"attachment\": \"4t2Xazb2SX\"}", txV1, TransferTransactionV1.class);
    }

    @Test
    public void V1DeserializeTest2() throws IOException {
        TransactionWithSignature deserialized = (TransactionWithSignature) mapper.readValue("{\"type\":4,\"id\":\"2D8FxS6mrqwzaPkMkyefhZK3zp5itjJVEKeBt1xTgwJ2\",\"sender\":\"3MwKaQvkniYhY7uMt6CDzubSFZ9ke52GHTG\",\"senderPublicKey\":\"JBMg1USzm9pL3b4q1fQYPNxRJRG8Sto4WNpgwJAnV9gY\",\"fee\":100000,\"timestamp\":1534935016512,\"signature\":\"5us8hrQnvNL3fx8tHhFDMCbdwBYXzaTF1PanSTNJB3zqhPyb5SCD6YpRafq9mCMrnY1QDRJCx7buCGn66ue39YfD\",\"version\":1,\"recipient\":\"3NCC7CuD9GJpZzNENMyczFVtHTtJ972rrTg\",\"address\":null,\"feeAssetId\":null,\"feeAsset\":null,\"amount\":100000000,\"attachment\":\"3k9wwt7nZn\"}", Transaction.class);
        assert(deserialized.verifySignature());
    }

    @Test
    public void V2DeserializeTest() throws IOException {
        deserializationTest("{\"type\": 4,\"id\": \"2qMiGUpNMuRpeyTnXLa1mLuVP1cYEtxys55cQbDaXd5g\",\"sender\": \"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\",\"senderPublicKey\": \"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\",\"fee\": 100000000,\"timestamp\": 1526641218066,\"proofs\": [\"4bfDaqBcnK3hT8ywFEFndxtS1DTSYfncUqd4s5Vyaa66PZHawtC73rDswUur6QZu5RpqM7L9NFgBHT1vhCoox4vi\"],\"version\": 2,\"recipient\": \"3My3KZgFQ3CrVHgz6vGRt8687sH4oAA1qp8\",\"address\": null,\"feeAssetId\": null,\"feeAsset\": null,\"amount\": 100000000,\"attachment\": \"4t2Xazb2SX\"}", txV2, TransferTransactionV2.class);
    }
}
