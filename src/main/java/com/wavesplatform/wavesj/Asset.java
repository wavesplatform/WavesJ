package com.wavesplatform.wavesj;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public abstract class Asset {
    /**
     * Constant used to represent WAVES token in asset transactions.
     */
    public static final String WAVES = "WAVES";

    public static final long TOKEN = 100*1000*1000L;

    public static final BigDecimal WAVELETS_MULT = new BigDecimal(TOKEN);

    public static final long MILLI = 100*1000L;

    public static final int DEFAULT_SCALE = 12;

    public static long toWavelets(double amount) {
        return (WAVELETS_MULT.multiply(new BigDecimal(amount, MathContext.DECIMAL64))).longValue();
    }

    public static long toWavelets(long amount) {
        return amount * TOKEN;
    }

    public static long toWavelets(BigDecimal amount) {
        return amount.multiply(WAVELETS_MULT).longValue();
    }

    public static BigDecimal fromWavelets(long waveletsAmt) {
        BigDecimal amountBd = new BigDecimal(waveletsAmt);
        return amountBd.divide(WAVELETS_MULT, DEFAULT_SCALE, RoundingMode.HALF_DOWN);
    }

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
