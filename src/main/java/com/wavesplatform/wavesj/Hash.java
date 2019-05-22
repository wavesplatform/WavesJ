package com.wavesplatform.wavesj;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.digests.SHA256Digest;

public class Hash {
    private static final ThreadLocal<Digest> BLAKE2B256 = new ThreadLocal<Digest>();
    private static final ThreadLocal<Digest> KECCAK256 = new ThreadLocal<Digest>();
    private static final ThreadLocal<Digest> SHA256 = new ThreadLocal<Digest>();

    public static byte[] secureHash(byte[] message, int ofs, int len) {
        final byte[] blake2b = hash(message, ofs, len, Hash.BLAKE2B256);
        return hash(blake2b, 0, blake2b.length, Hash.KECCAK256);
    }

    public static byte[] blake2b(byte[] message, int ofs, int len) {
        return hash(message, ofs, len, Hash.BLAKE2B256);
    }

    public static byte[] sha256(byte[] message, int ofs, int len) {
        return hash(message, ofs, len, Hash.SHA256);
    }

    private static Digest digest(ThreadLocal<Digest> cache) {
        Digest digest = cache.get();
        if (digest == null) {
            if (cache == BLAKE2B256) {
                digest = new Blake2bDigest(256);
            } else if (cache == KECCAK256) {
                digest = new KeccakDigest(256);
            } else if (cache == SHA256) {
                digest = new SHA256Digest();
            }
            cache.set(digest);
        }
        return digest;
    }

    private static byte[] hash(byte[] message, int ofs, int len, ThreadLocal<Digest> alg) {
        final Digest digest = digest(alg);
        final byte[] result = new byte[digest.getDigestSize()];
        digest.update(message, ofs, len);
        digest.doFinal(result, 0);
        return result;
    }
}
