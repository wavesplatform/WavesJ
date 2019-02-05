package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.matcher.Order;
import com.wavesplatform.wavesj.matcher.OrderV1;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExchangeTransactionV1Test {
    private Order sell = new OrderV1(
            new PublicKeyAccount("7E9Za8v8aT6EyU1sX91CVK7tWUeAetnNYDxzKZsyjyKV", (byte) 'T'),
            new PublicKeyAccount("Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP", (byte) 'T'),
            Order.Type.SELL,
            new AssetPair("WAVES", "9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy"),
            3,
            5000000000L,
            1526992336241L,
            1529584336241L,
            2,
            new ByteString("2R6JfmNjEnbXAA6nt8YuCzSf1effDS4Wkz8owpCD9BdCNn864SnambTuwgLRYzzeP5CAsKHEviYKAJ2157vdr5Zq"));
    private Order buy = new OrderV1(
            new PublicKeyAccount("BqeJY8CP3PeUDaByz57iRekVUGtLxoow4XxPvXfHynaZ", (byte) 'T'),
            new PublicKeyAccount("Fvk5DXmfyWVZqQVBowUBMwYtRAHDtdyZNNeRrwSjt6KP", (byte) 'T'),
            Order.Type.BUY,
            new AssetPair("WAVES", "9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy"),
            2,
            6000000000L,
            1526992336241L,
            1529584336241L,
            1,
            new ByteString("2bkuGwECMFGyFqgoHV4q7GRRWBqYmBFWpYRkzgYANR4nN2twgrNaouRiZBqiK2RJzuo9NooB9iRiuZ4hypBbUQs"));

    ExchangeTransactionV1 tx = new ExchangeTransactionV1(
            buy, sell,2, 5000000000L,  1, 1, 1, 1526992336241L,
            new ByteString("5NxNhjMrrH5EWjSFnVnPbanpThic6fnNL48APVAkwq19y2FpQp4tNSqoAZgboC2ykUfqQs9suwBQj6wERmsWWNqa")
    );

    @Test
    public void bytesBytesTest() {
        assertEquals("URBthZu3g5j9K4jQJ2mpPibVjekrzr5QXFhojqQfvEb746pNdc7Xh5S6e9G95XaQ4KNmXCyvg6RFPNTiDY1y5o1A22gD3tegV8uu1CHAcg8viRDoGGRDgyr2aSC2pvkhccg3A5hnMeb8gg1tpeYdM6hfsCQ6bRvKskGFjSfMqE1mCVsiFVNt5uNPPW3AcNgWAdvwZDYjRyTTbhKSU62iXiKbmkKjdT1bQZxtxfmiLijTwCTLMCxMRneMQDy6pQ3Wuu8ieHQScHbfqQjR4zbMaJMmvZxD8VYR8pM2Ea9UTvQ3ddDKtxLbNcQu4EeHLUFHs4jRwtugKKzjnLRyvE3JmnZzX7JWrqDKCqWeqNQZmKtEDL9efZzStdmZ9HjjcT3SuDCdPn9YM5QX1NmubwkiiMfSG7U8kp67nkjaGTXD3dLGUExu7Aj7sSe3JCFt2cAokLFBm6U6AL6BzWq2GfoQpcYjMdbr3xqrnuZgqV3h4vT1huwfumapK2CDYvSvy2TKm1nSbtepVTbqKCG3E52Sg6PGhUiYBx75nnBAPeJtJdLFNz7kyvT9o71jXjyPM7QaMkXw8Crt2HM6n9P8NeAuCcVBLwrKrhtMKtY987tYQkSzfjDUQ7tBaKvJmkTaMyhxPqBoVt5sLdDuUQ9GVXNAbcyoNnNs1Uqut9wEQ21KtL4nKYzh9aL1gVYGHJkt87DDTJARfBqBadSMtc4",
                Base58.encode(tx.getBytes()));
    }

    @Test
    public void bodyBytesTest(){
        assertEquals("6MgyGQMgz1QqkmbgKRKuAzeaNu3ZwmonHawr7k6fbqaAUJSPuvJyVGrE8U9CnVafwmuFcifDfePnnjizWbTqmkFAaejNegi1C3mgfwHMATNy4JyFkwnCjtfzt22Y7LaVHWft81nuQEhgNT4w9w7fATVyYuMM7ZsMfaBBaJN9LPaARDK5dR7qPWvh6TKuWBCSx1QmN6hwWcHJwKb8fNvTLZiX9Dk3bmqCK9idw1pjZDMLoeRPRzivDCTvLkAYtqyHactxtgN9WSAtjB1yQpgMmYkEfBBamStdxo8Ljw4fqKGetotsUyLRfz8FFexAnmoKxwvmpkxGuspEtpMnJAq9LC1FBhAkNDuGHWRrpQksLgXzyLLtnY31q9TBTWjbCsr44ByVqBYt2bFszNo77a8AWJJziKeuvGiWYoK56FYScQx5z27R1HZV4dCXc3ypkberMJ49YprawWJKfoiu2GsjybCCaQnbFcF1Ffs2Nm83wc45btkCHYM64FCWGFsNY9M2MLVADpzWwauNNW6VDeSkirn2jS5ZSui48j6NEQjXEVzH3Z3CsF66hgGrt62YwREKccRnKUjvTWuG8qimf6woQ7uKgTsqHw3FdzybHDrwuLDc6JBDPJoP7Kxc",
                Base58.encode(tx.getBodyBytes()));
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
