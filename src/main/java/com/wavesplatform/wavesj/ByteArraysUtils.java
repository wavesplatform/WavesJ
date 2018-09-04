package com.wavesplatform.wavesj;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteArraysUtils {
    public static byte[] addAll(final byte[] array1, byte[] array2) {
        byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static byte[] getOnlyUsed(ByteBuffer buf) {
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
    }
}
