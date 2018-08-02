package com.wavesplatform.wavesj;

import org.apache.commons.codec.Charsets;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class Alias {
    private String name;
    private byte chainId;

    public static String PREFIX = "alias:";

    public static byte AddressVersion = 2;
    public static byte MIN_LENGTH = 4;
    public static byte MAX_LENGTH = 30;

    public Alias(String name, byte networkByte) {
        this.name = name;
        this.chainId = networkByte;
    }

    public String getName() {
        return name;
    }

    public byte getChainId() {
        return chainId;
    }

    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(Alias.AddressVersion)
                .put(chainId)
                .put(name.getBytes(Charsets.UTF_8));
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Alias fromString(String str) {
        if (!str.startsWith(Alias.PREFIX)) throw new IllegalArgumentException("alias should starts from the prefix");
        if (str.charAt(7) != ':') throw new IllegalArgumentException("alias should contains separator");
        return new Alias(str.substring(8, str.length()), (byte) str.charAt(6));
    }
}
