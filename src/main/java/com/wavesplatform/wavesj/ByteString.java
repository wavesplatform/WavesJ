package com.wavesplatform.wavesj;

public class ByteString {
    private String base58String;

    public ByteString(String base58String) throws IllegalArgumentException {
        // to check valid base58 string
        Base58.decode(base58String);
        this.base58String = base58String;
    }

    public ByteString(byte[] bytes) {
        this.base58String = Base58.encode(bytes);
    }

    public static ByteString EMPTY = new ByteString(new byte[0]);

    public String getBase58String() {
        return base58String;
    }

    public byte[] getBytes() {
        return Base58.decode(base58String);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ByteString that = (ByteString) o;

        return getBase58String() != null ? getBase58String().equals(that.getBase58String()) : that.getBase58String() == null;
    }

    @Override
    public int hashCode() {
        return getBase58String() != null ? getBase58String().hashCode() : 0;
    }
}