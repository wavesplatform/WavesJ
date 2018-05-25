package com.wavesplatform.wavesj;

//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;
//
//import java.io.IOException;

public class Base64 {
//    private static BASE64Encoder encoder = new BASE64Encoder();
//    private static BASE64Decoder decoder = new BASE64Decoder();
    private static org.apache.commons.codec.binary.Base64 codec = new org.apache.commons.codec.binary.Base64();

    public static String encode(byte[] input) {
//        return "base64:" + encoder.encode(input);
        return "base64:" + new String(codec.encode(input));
    }

    public static byte[] decode(String input) {
        if (!input.startsWith("base64:")) {
            throw new IllegalArgumentException("String of the form base64:chars expected");
        } else {
            return codec.decode(input.substring(7));
//            try {
//                return decoder.decodeBuffer(input.substring(7));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
        }
    }
}
