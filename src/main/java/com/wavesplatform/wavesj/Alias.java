package com.wavesplatform.wavesj;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;
import static com.wavesplatform.wavesj.ByteUtils.putString;

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
                .put(chainId);
        putString(buf, name);
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

    public static Alias fromRawString(String str, byte networkByte) {
        return new Alias(str, networkByte);
    }

    public String toRawString() {
        return PREFIX + (char) chainId + ":" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alias alias = (Alias) o;

        if (getChainId() != alias.getChainId()) return false;
        return getName() != null ? getName().equals(alias.getName()) : alias.getName() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (int) getChainId();
        return result;
    }
}
