package com.wavesplatform.wavesj.json.ser;

import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.transactions.BurnTransactionV1;
import com.wavesplatform.wavesj.transactions.BurnTransactionV2;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class BurnTransactionSerTest extends TransactionSerTest {
    BurnTransactionV1 txV1 = new BurnTransactionV1(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), "9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz", 10000000000L, 100000000L, 1526287561757L, new ByteString("uapJcAJQryBhWThU43rYgMNmvdT7kY747vx5BBgxr2KvaeTRx8Vsuh4yu1JxBymU9LnAoo1zjQcPrWSuhi6dVPE"));
    BurnTransactionV2 txV2 = new BurnTransactionV2(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), (byte) 'T', "9ekQuYn92natMnMq8KqeGK3Nn7cpKd3BvPEGgD6fFyyz", 10000000000L, 100000000L, 1526287561757L, Collections.singletonList(new ByteString("3NcEv6tcVMuXkTJwiqW4J3GMCTe8iSLY7neEfNZonp59eTQEZXYPQWs565CRUctDrvcbtmsRgWvnN7BnFZ1AVZ1H")));

    @Test
    public void V1SerializeTest() throws IOException {
        serializationRoadtripTest(txV1, BurnTransactionV1.class);
    }

    @Test
    public void V2SerializeTest() throws IOException {
        serializationRoadtripTest(txV2, BurnTransactionV2.class);
    }
}
