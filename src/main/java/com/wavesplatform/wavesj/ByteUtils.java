package com.wavesplatform.wavesj;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.MessageFormat;

import static com.wavesplatform.wavesj.Asset.isWaves;

public class ByteUtils {
    public static final int KBYTE = 1024;
    public static final byte OPTIONAL_EXIST = 1;
    public static final byte OPTIONAL_EMPTY = 0;

    public final static Charset UTF8 = Charset.forName("UTF-8");

    private static final String LENGTH_GT_SHORT = "Attempting to put array with size={0} greater than MaxShort({1})";

    public static byte[] toBytes(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.position()];
        buffer.position(0);
        buffer.get(bytes);
        return bytes;
    }

    public static void putOptionalFlag(ByteBuffer buffer, Serializable obj) {
        if (obj != null) {
            buffer.put(ByteUtils.OPTIONAL_EXIST);
        } else {
            buffer.put(ByteUtils.OPTIONAL_EMPTY);
        }
    }

    public static void putAsset(ByteBuffer buffer, String assetId) {
        if (isWaves(assetId)) {
            buffer.put(OPTIONAL_EMPTY);
        } else {
            buffer.put(OPTIONAL_EXIST).put(Base58.decode(assetId));
        }
    }

    public static void putString(ByteBuffer buffer, String s) {
        if (s == null) s = "";
        putBytes(buffer, s.getBytes(UTF8));
    }

    public static void putString(ByteBuffer buffer, String s, BytesFormatter bytesFormatter) {
        if (s == null) s = "";
        putBytes(buffer, s.getBytes(UTF8), bytesFormatter);
    }

    public static void putScript(ByteBuffer buffer, String script) {
        byte[] bytes = script == null ? new byte[0] : Base64.decode(script);
        buffer.put((byte) (bytes.length > 0 ? 1 : 0));
        if (bytes.length > 0) {
            putBytes(buffer, bytes);
        }
    }

    public static void putBytes(ByteBuffer buffer, byte[] bytes) {
        buffer.putShort(toShort(bytes.length)).put(bytes);
    }

    public static short toShort(long val) {
        checkShort(val);
        return (short) val;
    }

    private static void checkShort(long length) {
        if (length > Short.MAX_VALUE) {
            throw new IllegalStateException(MessageFormat.format(LENGTH_GT_SHORT, length, Short.MAX_VALUE));
        }
    }

    public static void putBytes(ByteBuffer buffer, byte[] bytes, BytesFormatter bytesFormatter) {
        bytesFormatter.put(buffer, bytes);
    }

    public static String putRecipient(ByteBuffer buffer, byte chainId, String recipient) {
        if (recipient.length() <= 30) {
            // assume an alias
            buffer.put((byte) 0x02).put(chainId).putShort((short) recipient.length()).put(recipient.getBytes(UTF8));
            return String.format("alias:%c:%s", chainId, recipient);
        } else {
            buffer.put(Base58.decode(recipient));
            return recipient;
        }
    }

    public static String hash(byte[] bytes) {
        return Base58.encode(Hash.hash(bytes, 0, bytes.length, Hash.BLAKE2B256));
    }

    public static String sign(PrivateKeyAccount account, ByteBuffer buffer) {
        return account.sign(toBytes(buffer));
    }

    public enum BytesFormatter {
        LENGTH_AS_SHORT() {
            @Override
            public void put(ByteBuffer buffer, byte[] bytes) {
                putBytes(buffer, bytes);
            }
        },

        LENGTH_AS_INT() {
            @Override
            public void put(ByteBuffer buffer, byte[] bytes) {
                buffer.putInt(bytes.length).put(bytes);
            }
        };

        public abstract void put(ByteBuffer buffer, byte[] bytes);
    }
}
