package com.wavesplatform.wavesj;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.Blake2bDigest;

class Hash {
    static final Digest BLAKE2B256 = new Blake2bDigest(256);

    static byte[] hash(byte[] message, int ofs, int len, Digest alg) {
        byte[] res = new byte[alg.getDigestSize()];
        alg.update(message, ofs, len);
        alg.doFinal(res, 0);
        return res;
    }
}
