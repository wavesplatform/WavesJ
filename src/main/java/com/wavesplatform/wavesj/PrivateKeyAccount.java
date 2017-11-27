package com.wavesplatform.wavesj;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.whispersystems.curve25519.java.curve_sigs;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class PrivateKeyAccount extends PublicKeyAccount {
    private static final Digest SHA256 = new SHA256Digest();

    private final byte[] privateKey;

    private PrivateKeyAccount(byte[] privateKey, char scheme) {
        super(publicKey(privateKey), scheme);
        this.privateKey = privateKey;
    }

    public PrivateKeyAccount(String seed, int nonce, char scheme) {
        this(privateKey(seed, nonce), scheme);
    }

    public PrivateKeyAccount(String privateKey, char scheme) {
        this(Base58.decode(privateKey), scheme);
    }

    public final byte[] getPrivateKey() {
        return Arrays.copyOf(privateKey, privateKey.length);
    }

    private static byte[] privateKey(String seed, int nonce) {
        // account seed from seed & nonce
        ByteBuffer buf = ByteBuffer.allocate(seed.getBytes().length + 4);
        buf.putInt(nonce).put(seed.getBytes());
        byte[] accountSeed = secureHash(buf.array(), 0, buf.array().length);

        // private key from account seed & scheme
        byte[] hashedSeed = hash(accountSeed, 0, accountSeed.length, SHA256);
        byte[] privateKey = Arrays.copyOf(hashedSeed, 32);
        privateKey[0]  &= 248;
        privateKey[31] &= 127;
        privateKey[31] |= 64;

        return privateKey;
    }

    private static byte[] publicKey(byte[] privateKey) {
        byte[] publicKey = new byte[32];
        curve_sigs.curve25519_keygen(publicKey, privateKey);
        return publicKey;
    }
}
