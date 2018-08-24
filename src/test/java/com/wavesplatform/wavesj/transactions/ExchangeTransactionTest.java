package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.matcher.Order;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExchangeTransactionTest {
    Order sell = new Order(Order.Type.SELL, new AssetPair(Asset.WAVES, "9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy"), 3, 5000000000L, 1526992336241L, 1529584336241L, 2, new PublicKeyAccount("7E9Za8v8aT6EyU1sX91CVK7tWUeAetnNYDxzKZsyjyKV", (byte)'T'), new PublicKeyAccount("Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP", (byte)'T'), new ByteString("2R6JfmNjEnbXAA6nt8YuCzSf1effDS4Wkz8owpCD9BdCNn864SnambTuwgLRYzzeP5CAsKHEviYKAJ2157vdr5Zq"));
    Order buy = new Order(Order.Type.BUY, new AssetPair(Asset.WAVES, "9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy"), 2, 6000000000L, 1526992336241L, 1529584336241L, 1, new PublicKeyAccount("BqeJY8CP3PeUDaByz57iRekVUGtLxoow4XxPvXfHynaZ", (byte)'T'), new PublicKeyAccount("Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP", (byte)'T'), new ByteString("2bkuGwECMFGyFqgoHV4q7GRRWBqYmBFWpYRkzgYANR4nN2twgrNaouRiZBqiK2RJzuo9NooB9iRiuZ4hypBbUQs"));

    ExchangeTransaction tx = new ExchangeTransaction(
            2, 5000000000L, buy, sell, 1, 1, 1, 1526992336241L,
            new ByteString("5NxNhjMrrH5EWjSFnVnPbanpThic6fnNL48APVAkwq19y2FpQp4tNSqoAZgboC2ykUfqQs9suwBQj6wERmsWWNqa")
    );

    @Test
    public void bytesBytesTest() {
        assertEquals("6MgyGQMgz1QqkmbgKRKuAzeaNu3ZwmonHawr7k6fbqaAUJSPuvJyVGrE8U9CnVafwmuFcifDfePnnjizWbTqmkFAaejNegi1C3mgfwHMATNy4JyFkwnCjtfzt22Y7LaVHWft81nuQEhgNT4w9w7fATVyYuMM7ZsMfaBBaJN9LPaARDK5dR7qPWvh6TKuWBCSx1QmN6hwWcHJwKb8fNvTLZiX9Dk3bmqCK9idw1pjZDMLoeRPRzivDCTvLkAYtqyHactxtgN9WSAtjB1yQpgMmYkEfBBamStdxo8Ljw4fqKGetotsUyLRfz8FFexAnmoKxwvmpkxGuspEtpMnJAq9LC1FBhAkNDuGHWRrpQksLgXzyLLtnY31q9TBTWjbCsr44ByVqBYt2bFszNo77a8AWJJziKeuvGiWYoK56FYScQx5z27R1HZV4dCXc3ypkberMJ49YprawWJKfoiu2GsjybCCaQnbFcF1Ffs2Nm83wc45btkCHYM64FCWGFsNY9M2MLVADpzWwauNNW6VDeSkirn2jS5ZSui48j6NEQjXEVzH3Z3CsF66hgGrt62YwREKccRnKUjvTWuG8qimf6woQ7uKgTsqHw3FdzybHDrwuLDc6JBDPJoP7Kxc", Base58.encode(tx.getBytes()));
    }

    @Test
    public void bytesIdTest() {
        assertEquals("FaDrdKax2KBZY6Mh7K3tWmanEdzZx6MhYUmpjV3LBJRp", tx.getId().getBase58String());
    }


    @Test
    public void signatureTest() {
        assertTrue(tx.verifySignature());
    }
}
