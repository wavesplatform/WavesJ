package com.wavesplatform.wavesj.json.deser;

import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.transactions.BurnTransactionV1;
import com.wavesplatform.wavesj.transactions.BurnTransactionV2;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class BurnTransactionDeserTest extends TransactionDeserTest {
    private BurnTransactionV1 txV1 = new BurnTransactionV1(
            new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'),
            "9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz",
            10000000000L,
            100000000L,
            1526287561757L,
            new ByteString("uapJcAJQryBhWThU43rYgMNmvdT7kY747vx5BBgxr2KvaeTRx8Vsuh4yu1JxBymU9LnAoo1zjQcPrWSuhi6dVPE"));
    private BurnTransactionV2 txV2 = new BurnTransactionV2(
            new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'),
            (byte) 'T', "9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz",
            10000000000L,
            100000000L,
            1526287561757L,
            Collections.singletonList(new ByteString("3NcEv6tcVMuXkTJwiqW4J3GMCTe8iSLY7neEfNZonp59eTQEZXYPQWs565CRUctDrvcbtmsRgWvnN7BnFZ1AVZ1H")));

    @Test
    public void V1DeserializeTest() throws IOException {
        deserializationTest("{\"height\":1234," +
                "\"type\":6," +
                "\"id\":\"Ci1q7y7Qq2C2GDH7YVXsQ8w5vRRKYeoYTp9J76AXw8TZ\"," +
                "\"sender\":\"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\"," +
                "\"senderPublicKey\":\"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\"," +
                "\"assetId\":\"9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz\"," +
                "\"fee\":100000000,\"timestamp\":1526287561757," +
                "\"signature\":\"uapJcAJQryBhWThU43rYgMNmvdT7kY747vx5BBgxr2KvaeTRx8Vsuh4yu1JxBymU9LnAoo1zjQcPrWSuhi6dVPE\"," +
                "\"chainId\":null," +
                "\"version\":1," +
                "\"address\":\"9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz\"," +
                "\"amount\":10000000000}", txV1, BurnTransactionV1.class);
    }

    @Test
    public void V2DeserializeTest() throws IOException {
        deserializationTest("{\"height\":1234," +
                "\"type\":6," +
                "\"id\":\"6QA1sLV53euVCX5fFemNuEyRVdQ5JYo5dWDsCmtKADRc\"," +
                "\"sender\":\"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\"," +
                "\"senderPublicKey\":\"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\"," +
                "\"assetId\":\"9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz\"," +
                "\"fee\":100000000,\"timestamp\":1526287561757," +
                "\"proofs\":[\"3NcEv6tcVMuXkTJwiqW4J3GMCTe8iSLY7neEfNZonp59eTQEZXYPQWs565CRUctDrvcbtmsRgWvnN7BnFZ1AVZ1H\"]," +
                "\"chainId\":84,\"version\":2," +
                "\"address\":\"9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz\"," +
                "\"amount\":10000000000}", txV2, BurnTransactionV2.class);
    }
}
