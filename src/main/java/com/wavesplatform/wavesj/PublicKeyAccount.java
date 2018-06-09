package com.wavesplatform.wavesj;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.wavesplatform.wavesj.Hash.secureHash;

public class PublicKeyAccount implements Account {

    private final byte chainId;
    private final byte[] publicKey;
    private final String address;

    public PublicKeyAccount(byte[] publicKey, byte chainId) {
        this.chainId = chainId;
        this.publicKey = publicKey;
        this.address = Base58.encode(address(publicKey, chainId));
    }

    public PublicKeyAccount(String publicKey, byte chainId) {
        this(Base58.decode(publicKey), chainId);
    }

    public final byte[] getPublicKey() {
        return Arrays.copyOf(publicKey, publicKey.length);
    }

    public final String getAddress() {
        return address;
    }

    public final byte getChainId() {
        return chainId;
    }

    private static byte[] address(byte[] publicKey, byte chainId) {
        ByteBuffer buf = ByteBuffer.allocate(26);
        byte[] hash = secureHash(publicKey, 0, publicKey.length);
        buf.put((byte) 1).put((byte) chainId).put(hash, 0, 20);
        byte[] checksum = secureHash(buf.array(), 0, 22);
        buf.put(checksum, 0, 4);
        return buf.array();
    }
}
