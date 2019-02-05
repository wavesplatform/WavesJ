package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.matcher.Order;
import com.wavesplatform.wavesj.matcher.OrderV1;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExchangeTransactionV1Test {

    private Order buy = new OrderV1(
            new PublicKeyAccount("D8zy3ozYSncN7CA95N8xd8JAGsCSRTUo1wvHs79YEMk6", (byte) 'T'),
            new PublicKeyAccount("EhU7fcqsxYut5AM9SMEwb2hrvrj94wJMCSdZ2uAcRNvc", (byte) 'T'),
            Order.Type.BUY,
            new AssetPair("8dzLYRNtYR6ASG2W4h3FqeeY49paRxNheQwRW6CpP1HT", "4zjSCagDvgPkTCwFjvE4KMFtXz1WX4dNaNutyBw8XnrG"),
            1000000000L,
            120000L,
            1489774238394L,
            1492366238394L,
            1000000,
            new ByteString("Rn5q3nktRQuvBmNLsfAnD1U4QWGvVro9haxbWseLmCG4EYt3eHLP1bF52ykFR5k65468REcuFPPhUw98w9YvJkg"));

    private Order sell = new OrderV1(
            new PublicKeyAccount("2DfRWy9FexuTPpLmGRmEwtPBF2F23TVpGwb1uEjhkju5", (byte) 'T'),
            new PublicKeyAccount("EhU7fcqsxYut5AM9SMEwb2hrvrj94wJMCSdZ2uAcRNvc", (byte) 'T'),
            Order.Type.SELL,
            new AssetPair("8dzLYRNtYR6ASG2W4h3FqeeY49paRxNheQwRW6CpP1HT", "4zjSCagDvgPkTCwFjvE4KMFtXz1WX4dNaNutyBw8XnrG"),
            1000000000,
            115000,
            1489774287568L,
            1492366287568L,
            1000000,
            new ByteString("WX2YDD2gwY4eDjEKK25RFwrgDBMCNu98sTfXAde9AVBVz7hMHJNrQ1wUZPCR7g23xiEQqPY4UdftsU1bmZhHV3b"));


    private ExchangeTransactionV1 tx = new ExchangeTransactionV1(
            buy, sell, 1000000000,
            120000,
            1000000,
            1000000,
            100000,
            1489774288591L,
            new ByteString("2ohJgxWV1VKqJZsgJwGC4xedABBwxD11uDyJjjSZq2Z42jKT65fxMUsbtJVm9iQWcvfATKj6NFQUhcsie9JqbmLX")
    );

    @Test
    public void bytesBytesTest() {
        assertEquals("3RKzgMpnJVw1kiGUkzGUEgpN6NWoScNUg2asMeyuRizqaf6tMWTKmLsFkWJTNW9KeJZoTRidu3Y2REH3oUEDsdzfevwsLUiQjgs46VFxf4gAeNvMs9ByP59SywEPpbJEKUgFfiP1LriFBTfduCsCUSP4aJvvfmcnETvivL7VcL1hvvfpp1fMhr6imzqFqT8D2Aq2ELFfKu3soBmwx1bxqF4ZMoQyTak9LDH97EHfjsR9RJwNhfi3bDoUws7JsbodBCQtKJLDc6g2pkHuaqYhjyHPK4icMqRTdWPJ2fiKHcdcBdVXqp9K2NgmhDuTg7Ufpoy3AnA6bq2uiwh4gasq2kM9HUb85Co8SD5kAq6fEnvPycKVa8TL7HTpYqGuRg7T9Kp9H3hmvN41tK9DAy9qKexPoQJm5S1MzvVtRPu6o3VBkiHFtgxGsAntLJxtRrffxgRkQpE7e3m4fitEtx2ets6oz7pujSmK7BCbk8qBbD5DjPGEw1GcHhgR1Duwr9BwoFELwMFxKZpca2XdMKepNQ1muJXwPEBJzaSFFuC21XFaqjAgopzCyVsMUFsWRYCjJ7Vd45tEDk1foNYLwJXTzRonrFfZNXXv5F8snP5czdu8kHMPiKzysYEoMPVV53T2FRRJAbPLm7oWoq6nhbjKaaPUBnS5NYnA7N9DA8pVFsuEnLdhCyBjwUbzDA1Xi82Xw3mLmU7rrAatNXSt6VMEwDvXhahcDYFgtG6PSKfYFWgmT6wCxK5zNsrSZ7wqnPHDcEUapteXpdVL4f51aP4QtoFQhwjLP39EVgJkRP9",
                Base58.encode(tx.getBytes()));
    }

    @Test
    public void bodyBytesTest(){
        assertEquals("URBthu3xguLXQU6ERAxFqWShUpah5TZdcqviZ4ETvCjPFArhDhYQBVSftoiHXGp5pe72et4ny7JczgYqCxnqPLPeZHsP9q2YSXHRCaZgwSxuVnbHmkDttdMbSyotGWvd5juTdmHgBEv9VnmPRHW63mwY9VP9bYX5yWqcuHtvNVzUafAqfMXonjyhPv3VUjmQPV9NvcFoG8P8jUD498g331Wk3LEJumk4Et47VnThPuKEoaqEpZ7EMpeDJ1j38HFJpVUg6uLuS65mURuat3XMwmFD9f7PfVm9iBXCWaxYcCsMawzwWfHDVKVob2TZa3xu2zoFKjfYJtqkXJp6PruT4tjETjT9DNzfzfHHXuN71KvLwpfrh5WijxKzvU2PNDbi1Eygzmfh2AdD68cY1Y2TFPaD2vrY1mDGVLWf2LzWGps7n7ucMzm13HyzKdpZRzw9m97j5uqg8EZaQtJxD9htyDiANPCqdKAAMEC7eo3EYKDoxc9sNJbvrWPeNkhgrQh8BjDEQd3P1Azfze3xBhkMyo6byzCcwCS8BKFCqCBXFbmTA1dbHMrJXUyqdoKPdpDfoxByPwSvmzbmxzjPzku6msvxSX1X8niPsb5qYyyZ8FcgPxp9JKNMUs12fEvf8aJbp2HU9a4bWSMRpt4E77eEBvMcHHTY2YDibjxqPuUzFrki68JPMEYqMuZRg7UnQ9NXcR1LaMkXF2BKXJn",
                Base58.encode(tx.getBodyBytes()));
    }


    @Test
    public void bytesIdTest() {
        assertEquals("CPf7EWE4hPrBNKJpzBtBm9os4UsyZ8Eebhzwq4EqLWqG", tx.getId().getBase58String());
    }


    @Test
    public void signatureTest() {
        assertTrue(tx.verifySignature());
    }
}
