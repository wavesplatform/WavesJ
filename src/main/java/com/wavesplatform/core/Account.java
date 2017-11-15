package com.wavesplatform.core;

import org.bitcoinj.core.Base58;
import org.bouncycastle.crypto.Digest;
import org.whispersystems.curve25519.Curve25519;
import org.whispersystems.curve25519.java.curve_sigs;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Account {
    private static final Digest BLAKE2B256 = new org.bouncycastle.crypto.digests.Blake2bDigest(256);
    private static final Digest KECCAK256 = new org.bouncycastle.crypto.digests.KeccakDigest(256);
    private static final Curve25519 cipher = Curve25519.getInstance(Curve25519.BEST);///

    private final byte[] privateKey;

    public Account(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    public Account(String privateKey) {
        this(Base58.decode(privateKey));
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public byte[] getPublicKey() {
        byte[] publicKey = new byte[32];
        curve_sigs.curve25519_keygen(publicKey, privateKey);
        return publicKey;
    }

    public Address getAddress(char scheme) {
        return new Address(address(getPublicKey(), scheme));
    }

    private static byte[] hash(byte[] message, int ofs, int len, Digest alg) {
        byte[] res = new byte[alg.getDigestSize()];
        alg.update(message, ofs, len);
        alg.doFinal(res, 0);
        return res;
    }

    private static byte[] secureHash(byte[] message, int ofs, int len) {
        byte[] blake2b = hash(message, ofs, len, BLAKE2B256);
        return hash(blake2b, 0, blake2b.length, KECCAK256);
    }

    private byte[] accountSeed(byte[] seed, int nonce) {
        ByteBuffer buf = ByteBuffer.allocate(seed.length + 4);
        buf.putInt(nonce).put(seed);
        return secureHash(buf.array(), 0, buf.array().length);
    }

    private static byte[] address(byte[] publicKey, char scheme) { ///need this?
        ByteBuffer buf = ByteBuffer.allocate(26);
        byte[] hash = secureHash(publicKey, 0, publicKey.length);
        buf.put((byte) 1).put((byte) scheme).put(hash, 0, 20);
        byte[] checksum = secureHash(buf.array(), 0, 22);
        buf.put(checksum, 0, 4);
        return buf.array();
    }

    public static void main(String[] args) {///
        Account acc = new Account("CMLwxbMZJMztyTJ6Zkos66cgU7DybfFJfyJtTVpme54t");

        byte[] goldenPk = Base58.decode("8LbAU5BSrGkpk5wbjLMNjrbc9VzN9KBBYv9X8wGpmAJT");
        byte[] pk = acc.getPublicKey();
        if (! Arrays.equals(pk, goldenPk)) System.err.println("PK ERROR");

        String goldenAddr = "3MzZCGFyuxgC4ZmtKRS7vpJTs75ZXdkbp1K";
        String addr = acc.getAddress('T').toString();
        if (! addr.equals(goldenAddr)) System.err.println("Addr ERROR");
    }
}
