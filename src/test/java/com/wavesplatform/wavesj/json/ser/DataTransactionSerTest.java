package com.wavesplatform.wavesj.json.ser;

import com.wavesplatform.wavesj.Base64;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.DataEntry;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.transactions.DataTransaction;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DataTransactionSerTest extends TransactionSerTest {
    List<DataEntry<?>> data = new LinkedList<DataEntry<?>>();

    {
        data.add(new DataEntry.LongEntry("int", 24L));
        data.add(new DataEntry.BooleanEntry("bool", true));
        data.add(new DataEntry.BinaryEntry("blob", new ByteString(Base64.decode("YWxpY2U="))));
    }

    DataTransaction tx = new DataTransaction(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), data, 100000, 1526911531530L, Collections.singletonList(new ByteString("32mNYSefBTrkVngG5REkmmGAVv69ZvNhpbegmnqDReMTmXNyYqbECPgHgXrX2UwyKGLFS45j7xDFyPXjF8jcfw94")));

    @Test
    public void V1SerializeTest() throws IOException {
        serializationRoadtripTest(tx, DataTransaction.class);
    }

}
