package com.wavesplatform.wavesj;

public class Base64 {
    private static org.apache.commons.codec.binary.Base64 codec = new org.apache.commons.codec.binary.Base64();

    public static String encode(byte[] input) {
        return "base64:" + new String(codec.encode(input));
    }

    public static byte[] decode(String input) {
        if (input.startsWith("base64:")) input = input.substring(7);
        return codec.decode(input);
    }
}
