package com.wavesplatform.wavesj;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Supplier;

public class ByteString implements Serializable {
    private byte[] bytes;
    private Supplier<String> base58;

    public ByteString(final String base58String) throws IllegalArgumentException {
        // to check valid base58 string
        if (base58String != null) {
            this.bytes = Base58.decode(base58String);
            this.base58 = () -> base58String;
        } else {
            this.base58 = () -> "";
            this.bytes = EMPTY.bytes;
        }
    }

    public ByteString(final byte[] bytes) {
        this.bytes = bytes;
        this.base58 = () -> (bytes == null) ? "" : Base58.encode(bytes);
    }

    public static ByteString EMPTY = new ByteString(new byte[0]);

    public String getBase58String() {
        return base58.get();
    }

    public byte[] getBytes() {
        return this.bytes;
    }


    @Override
    public String toString(){
        return this.getBase58String();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ByteString that = (ByteString) o;
        final byte[] leftBytes = getBytes();
        final byte[] rightBytes = that.getBytes();
        return leftBytes != null ? Arrays.equals(leftBytes, rightBytes) : rightBytes == null;
    }

    @Override
    public int hashCode() {
        final byte[] bytes = getBytes();
        return bytes != null ? Arrays.hashCode(bytes) : 0;
    }
}