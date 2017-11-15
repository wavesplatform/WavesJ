package com.wavesplatform.core;

import org.bitcoinj.core.Base58;
import static org.junit.Assert.*;
import org.junit.Test;

public class AccountTest {

    @Test
    public void smokeTest() {
        String pk = "8LbAU5BSrGkpk5wbjLMNjrbc9VzN9KBBYv9X8wGpmAJT";
        String sk = "CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t";
        String addr = "3MzZCGFyuxgC4ZmtKRS7vpJTs75ZXdkbp1K";

        PrivateKeyAccount acc = new PrivateKeyAccount(sk, 'T');
        assertArrayEquals(Base58.decode(sk), acc.getPrivateKey());
        assertArrayEquals(Base58.decode(pk), acc.getPublicKey());
        assertEquals(addr, acc.getAddress().toString());
    }
}
