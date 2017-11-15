package com.wavesplatform.core;

import org.bitcoinj.core.Base58;
import org.whispersystems.curve25519.java.curve_sigs;

import java.nio.ByteBuffer;

public class PrivateKeyAccount extends PublicKeyAccount {
    private final byte[] privateKey;

    public PrivateKeyAccount(byte[] privateKey, char scheme) {
        super(publicKey(privateKey), scheme);
        this.privateKey = privateKey;
    }

    public PrivateKeyAccount(String privateKey, char scheme) {
        this(Base58.decode(privateKey), scheme);
    }

    public final byte[] getPrivateKey() {
        return privateKey;
    }

    private static byte[] publicKey(byte[] privateKey) {
        byte[] publicKey = new byte[32];
        curve_sigs.curve25519_keygen(publicKey, privateKey);
        return publicKey;
    }

    private byte[] accountSeed(byte[] seed, int nonce) {
        ByteBuffer buf = ByteBuffer.allocate(seed.length + 4);
        buf.putInt(nonce).put(seed);
        return secureHash(buf.array(), 0, buf.array().length);
    }
}
