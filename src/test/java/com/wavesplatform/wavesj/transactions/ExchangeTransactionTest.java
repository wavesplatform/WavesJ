package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.matcher.Order;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Sell Account:
 *      address=3MtEyGGB3XQ6zWB71cCN98fotHvjxxwNMu4
 *      public=4QZkF9ejEsao1M8pNDAjoNqGsLsT3E6koXbNtCFxscce
 *      private=3C8rjfZJnh2EHhKccvggPsoMXLw53DriT8EnjyRPJdhh
 *      seed=sunset noodle trap mule mango can spring garment slot august photo champion paper host more
 *
 * Buy Account:
 *      address=3Mvqinkpz45gprXcpgcMb9yqUv4jpBGMQMw
 *      public=H9S6sPxueb6z1PB46VZJD6FbaTxsNfT8GHv5PPHbvDHx
 *      private=HAWqLZA98pJPvwGZuomKUkNpgfzKt4HhfMdFWPXwjxuX
 *      seed=creek extend car eight fat hole farm they behave element bag allow absurd clinic harbor
 *
 * Matcher Account:
 *      address=3Mu5FBXL16bHA5EuDrFAx6Ej2zjydqFR8bt
 *      public=9EpwUuxutBzZHTttQZWWSDnkQpgFVdwbpzpHen6bbmJT
 *      private=HhDixyYKsfDYbV6XnMJgrpT2mHPGvSfGKS5RfX3y9W4s
 *      seed=save code exhibit ramp scare donate net enjoy glimpse like absorb assume skin robust grass
 */
public class ExchangeTransactionTest {

    Order sell = new Order(Order.Type.SELL,
            new AssetPair(Asset.WAVES, "9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy"),
            3, 5000000000L, 1526992336241L, 1529584336241L, 2,
            new PublicKeyAccount("4QZkF9ejEsao1M8pNDAjoNqGsLsT3E6koXbNtCFxscce", (byte)'T'),
            new PublicKeyAccount("9EpwUuxutBzZHTttQZWWSDnkQpgFVdwbpzpHen6bbmJT", (byte)'T'),
            new ByteString("5H88PP3MKbjp33MB1FTjvPzwse6RRFTLSw6DvStqJT1Daj3n4Z9cjJaLJVD9XRWnQURZQNN76uZFMKcVy5ZRXhQ9"));

    Order buy = new Order(Order.Type.BUY,
            new AssetPair(Asset.WAVES, "9ZDWzK53XT5bixkmMwTJi2YzgxCqn5dUajXFcT2HcFDy"),
            2, 6000000000L, 1526992336241L, 1529584336241L, 1,
            new PublicKeyAccount("H9S6sPxueb6z1PB46VZJD6FbaTxsNfT8GHv5PPHbvDHx", (byte)'T'),
            new PublicKeyAccount("9EpwUuxutBzZHTttQZWWSDnkQpgFVdwbpzpHen6bbmJT", (byte)'T'),
            new ByteString("5zcZi59ExGw34aN9jWTeUubh1k8V6PfDp7fBPVLa1xfDdNX8nwn7TtddVhGgQjDApqcYaHSLhdmVZQw8zjGQTSfA"));

    ExchangeTransaction tx = new ExchangeTransaction(
            2, 5000000000L, buy, sell, 1, 1, 1, 1526992336241L,
            new ByteString("46vN6tLmbgLZ8reFwURBRnifoKntP7rBxpr1CPvrQwEPERBMQpWGpdcn559yCQTQrZ3XkXQseVN1CbR4L97fzhDf")
    );

    @Test
    public void bytesBytesTest() {
        assertEquals("6MgyGQMgz1Qr5HaMc6pmcVVUxhDL3T8EmnJfWuQCpVKF3LZDdEgudqzuFfNGadmkWFD1H6neUxPE1xhsP2aC3FuPAPJUaapjznv4J2KBUUQmjrMk9kTaEMt7bbzHkHbdC9eqsuDrYgYFyQhjq5TSBRyaGERGyuw5sy3SSkDkkApfPL4cDadzCWg6feA2c7MUJiAifSRAUAW9J5FNEZWSgJxAWU2WfFL6PfHS3maTNRvDfCQziDwVsdToMqYfiA5DqtDXMX6wsTnbYtYuryKo9GQQPmHWUt32f1cBjfKPBC8goapDKWFjJKNPgEwW7ri47L8Tyt2sHBhCmC3bULaJYihy6qjpbYVGfJdaA34PUaMJZSyTD2SHTsbKaApobvDXkoqrSdfJUoGmwbPrQnDWSBzNPfTTrrykNNkpXDS8Eb7Wmkf9AEnm9ar3ASuewbY5ZAUvobV9qzCwALurPmEQ7w6ejco7ubfoTe7Ru8US3iw5zBfie42H3crp3h3pCmgYDduAQ3Tmayw9qGSXARaiwJAVkUoMHBzDFxoTvq6x5oFe7T9umrw9mMEqTgFWs8cJAWyr1ptAAg1PPfw7iFf7LDAijz7e5fiiY9WjXqGe3G6e9jtYFBeM7dGk", Base58.encode(tx.getBytes()));
    }

    @Test
    public void bytesIdTest() {
        assertEquals("CkoSA84PqhAp7b68PSQBKYAybaRAaHZLd64jzBpWDzQ3", tx.getId().getBase58String());
    }

    @Test
    public void signatureTest() {
        assertTrue(tx.verifySignature());
    }
}
