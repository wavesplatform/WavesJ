package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.*;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DataTransactionTest {
    List<DataEntry<?>> data = new LinkedList<DataEntry<?>>();

    {
        data.add(new DataEntry.LongEntry("int", 24L));
        data.add(new DataEntry.BooleanEntry("bool", true));
        data.add(new DataEntry.BinaryEntry("blob", new ByteString(Base64.decode("YWxpY2U="))));
    }

    DataTransaction tx = new DataTransaction(new PublicKeyAccount("FM5ojNqW7e9cZ9zhPYGkpSP1Pcd8Z3e3MNKYVS5pGJ8Z", (byte) 'T'), data, 100000, 1526911531530L, Collections.singletonList(new ByteString("32mNYSefBTrkVngG5REkmmGAVv69ZvNhpbegmnqDReMTmXNyYqbECPgHgXrX2UwyKGLFS45j7xDFyPXjF8jcfw94")));

    @Test
    public void bytesBytesTest() {
        assertEquals("6bwzPpC7aKm9wBBmFcLFaz2dJ2Cnnf25RdF4FeNQjZJ7fS1qazDnaoN6edP5f2BGaJHcfScsQRVPT2Le9KTirT9L9XrRmrVMSeqc8B3MbjauqA3CEgnpbNY7", Base58.encode(tx.getBodyBytes()));
    }

    @Test
    public void bytesIdTest() {
        assertEquals("87SfuGJXH1cki2RGDH7WMTGnTXeunkc5mEjNKmmMdRzM", tx.getId().getBase58String());
    }

}
