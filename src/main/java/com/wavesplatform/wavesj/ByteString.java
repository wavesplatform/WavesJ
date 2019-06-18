package com.wavesplatform.wavesj;

import java.io.Serializable;
import java.util.Arrays;

public class ByteString implements Serializable {
    private byte[] bytes;
    private String base58String;

    public ByteString(String base58String) throws IllegalArgumentException {
        // to check valid base58 string
        if (base58String != null) {
            this.bytes = Base58.decode(base58String);
            this.base58String = base58String;
        } else {
            this.base58String = EMPTY.base58String;
        }
    }

    public ByteString(byte[] bytes) {
        this.bytes = bytes;
        if (bytes == null) this.base58String = EMPTY.base58String;
    }

    public static ByteString EMPTY = new ByteString(new byte[0]);

    public String getBase58String() {
        String base58String = this.base58String;
        if (base58String == null) {
            base58String = Base58.encode(this.bytes);
            this.base58String = base58String;
        }
        return base58String;
    }

    public byte[] getBytes() {
        byte[] bytes = this.bytes;
        if (bytes == null) {
            bytes = Base58.decode(this.base58String);
            this.bytes = bytes;
        }
        return bytes;
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
