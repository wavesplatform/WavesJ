package com.wavesplatform.wavesj;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static com.wavesplatform.wavesj.Asset.isWaves;

public class ByteUtils {
    public static final int KBYTE = 1024;
    public final static Charset UTF8 = Charset.forName("UTF-8");

    public static byte[] toBytes(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.position()];
        buffer.position(0);
        buffer.get(bytes);
        return bytes;
    }

    public static void putAsset(ByteBuffer buffer, String assetId) {
        if (isWaves(assetId)) {
            buffer.put((byte) 0);
        } else {
            buffer.put((byte) 1).put(Base58.decode(assetId));
        }
    }

    public static void putString(ByteBuffer buffer, String s) {
        if (s == null) s = "";
        putBytes(buffer, s.getBytes(UTF8));
    }

    public static void putScript(ByteBuffer buffer, String script) {
        byte[] bytes = script == null ? new byte[0] : Base64.decode(script);
        buffer.put((byte) (bytes.length > 0 ? 1 : 0));
        putBytes(buffer, bytes);
    }

    public static void putBytes(ByteBuffer buffer, byte[] bytes) {
        buffer.putShort((short) bytes.length).put(bytes);
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
}
