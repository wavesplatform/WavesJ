package com.wavesplatform.core;

import org.bitcoinj.core.Base58;
import org.bouncycastle.crypto.Digest;

import java.nio.ByteBuffer;

public class PublicKeyAccount {
    private static final Digest BLAKE2B256 = new org.bouncycastle.crypto.digests.Blake2bDigest(256);
    private static final Digest KECCAK256 = new org.bouncycastle.crypto.digests.KeccakDigest(256);

    private final char scheme;
    private final byte[] publicKey;
    private final Address address;

    public PublicKeyAccount(byte[] publicKey, char scheme) {
        this.scheme = scheme;
        this.publicKey = publicKey;
        this.address = new Address(address(publicKey, scheme));
    }

    public PublicKeyAccount(String publicKey, char scheme) {
        this(Base58.decode(publicKey), scheme);
    }

    public final byte[] getPublicKey() {
        return publicKey;
    }

    public final Address getAddress() {
        return address;
    }

    public final char getScheme() {
        return scheme;
    }

    private static byte[] hash(byte[] message, int ofs, int len, Digest alg) {
        byte[] res = new byte[alg.getDigestSize()];
        alg.update(message, ofs, len);
        alg.doFinal(res, 0);
        return res;
    }

    static byte[] secureHash(byte[] message, int ofs, int len) {
        byte[] blake2b = hash(message, ofs, len, BLAKE2B256);
        return hash(blake2b, 0, blake2b.length, KECCAK256);
    }

    private static byte[] address(byte[] publicKey, char scheme) { ///need this?
        ByteBuffer buf = ByteBuffer.allocate(26);
        byte[] hash = secureHash(publicKey, 0, publicKey.length);
        buf.put((byte) 1).put((byte) scheme).put(hash, 0, 20);
        byte[] checksum = secureHash(buf.array(), 0, 22);
        buf.put(checksum, 0, 4);
        return buf.array();
    }
}
