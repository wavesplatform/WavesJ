package com.wavesplatform.wavesj;

public abstract class Asset {
    /**
     * Constant used to represent WAVES token in asset transactions.
     */
    public static final String WAVES = "WAVES";

    public static final long TOKEN = 100000000L;

    public static final long MILLI = 100000L;

    static public String normalize(String assetId) {
        return assetId == null || assetId.isEmpty() ? Asset.WAVES : assetId;
    }

    static public boolean isWaves(String assetId) {
        return WAVES.equals(normalize(assetId));
    }

    static public String toJsonObject(String assetId) {
        return isWaves(assetId) ? null : assetId;
    }
}
