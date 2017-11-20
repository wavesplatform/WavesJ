package com.wavesplatform.wavesj;

import org.bitcoinj.core.Base58;
import static org.junit.Assert.*;
import org.junit.Test;

public class AccountTest {

    @Test
    public void testAccountProperties() {
        String pk = "8LbAU5BSrGkpk5wbjLMNjrbc9VzN9KBBYv9X8wGpmAJT";
        String sk = "CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t";
        String addr = "3MzZCGFyuxgC4ZmtKRS7vpJTs75ZXdkbp1K";

        PrivateKeyAccount acc = new PrivateKeyAccount(sk, 'T');
        assertArrayEquals(Base58.decode(sk), acc.getPrivateKey());
        assertArrayEquals(Base58.decode(pk), acc.getPublicKey());
        assertEquals(addr, acc.getAddress());
    }

    @Test
    public void testAccountCreation() {
        byte[] seed = PrivateKeyAccount.generateSeed();
        assertEquals(64, seed.length);

        PrivateKeyAccount account = PrivateKeyAccount.create(seed, 0, 'T');
        assertEquals(32, account.getPrivateKey().length);
        assertEquals(32, account.getPublicKey().length);
        assertEquals(26, Base58.decode(account.getAddress()).length);
    }
}
