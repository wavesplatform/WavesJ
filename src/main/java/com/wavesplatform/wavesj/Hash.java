package com.wavesplatform.wavesj;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.digests.SHA256Digest;

public class Hash {
    public static final ThreadLocal<Digest> BLAKE2B256 = new ThreadLocal<Digest>();
    public static final ThreadLocal<Digest> KECCAK256 = new ThreadLocal<Digest>();
    public static final ThreadLocal<Digest> SHA256 = new ThreadLocal<Digest>();

    private static Digest digest(ThreadLocal<Digest> cache) {
        Digest d = cache.get();
        if (d == null) {
            if (cache == BLAKE2B256) {
                d = new Blake2bDigest(256);
            } else if (cache == KECCAK256) {
                d = new KeccakDigest(256);
            } else if (cache == SHA256) {
                d = new SHA256Digest();
            }
            cache.set(d);
        }
        return d;
    }

    protected static byte[] hash(byte[] message, int ofs, int len, ThreadLocal<Digest> alg) {
        Digest d = digest(alg);
        byte[] res = new byte[d.getDigestSize()];
        d.update(message, ofs, len);
        d.doFinal(res, 0);
        return res;
    }

    public static byte[] secureHash(byte[] message, int ofs, int len) {
        byte[] blake2b = hash(message, ofs, len, Hash.BLAKE2B256);
        return hash(blake2b, 0, blake2b.length, Hash.KECCAK256);
    }
}
