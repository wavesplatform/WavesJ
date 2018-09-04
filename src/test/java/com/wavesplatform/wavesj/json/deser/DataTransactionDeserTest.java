package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.Base64;
import com.wavesplatform.wavesj.ByteString;
import com.wavesplatform.wavesj.DataEntry;
import com.wavesplatform.wavesj.PublicKeyAccount;
import com.wavesplatform.wavesj.json.WavesJsonMapper;
import com.wavesplatform.wavesj.transactions.DataTransaction;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DataTransactionDeserTest extends TransactionDeserTest {
    ObjectMapper mapper = new WavesJsonMapper((byte) 'T');

    List<DataEntry<?>> data = new LinkedList<DataEntry<?>>();

    {
        data.add(new DataEntry.LongEntry("int", 24L));
        data.add(new DataEntry.BooleanEntry("bool", true));
        data.add(new DataEntry.BinaryEntry("blob", new ByteString(Base64.decode("YWxpY2U="))));
    }

    DataTransaction tx = new DataTransaction(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), data, 100000, 1526911531530L, Collections.singletonList(new ByteString("32mNYSefBTrkVngG5REkmmGAVv69ZvNhpbegmnqDReMTmXNyYqbECPgHgXrX2UwyKGLFS45j7xDFyPXjF8jcfw94")));

    @Test
    public void V1DeserializeTest() throws IOException {
        deserializationTest("{\"type\":12,\"id\":\"87SfuGJXH1cki2RGDH7WMTGnTXeunkc5mEjNKmmMdRzM\",\"sender\":\"3N5GRqzDBhjVXnCn44baHcz2GoZy5qLxtTh\",\"senderPublicKey\":\"FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z\",\"fee\":100000,\"timestamp\":1526911531530,\"proofs\":[\"32mNYSefBTrkVngG5REkmmGAVv69ZvNhpbegmnqDReMTmXNyYqbECPgHgXrX2UwyKGLFS45j7xDFyPXjF8jcfw94\"],\"version\":1,\"data\":[{\"key\":\"int\",\"type\":\"integer\",\"value\":24},{\"key\":\"bool\",\"type\":\"boolean\",\"value\":true},{\"key\":\"blob\",\"type\":\"binary\",\"value\":\"base64:YWxpY2U=\"}]}", tx, DataTransaction.class);
    }

}
