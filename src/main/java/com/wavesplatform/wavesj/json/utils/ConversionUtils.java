package com.wavesplatform.wavesj.json.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.wavesplatform.wavesj.Asset;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

public class ConversionUtils {

    public static final FastDateFormat DATE_FORMAT =
            FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", TimeZone.getTimeZone("UTC"));

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionUtils.class);

    public static long toDefaultAmtFormat(JsonNode node) {
        if (node != null && node.isNumber()) {
            if (node.isLong() || node.isInt() || node.isShort()) {
                return node.asLong();
            } else if (node.isBigDecimal()) {
                return Asset.toWavelets(node.decimalValue());
            }
        }
        LOGGER.warn("Invalid type was passed into toDefaultAmtFormat method. Default 0 value is used. node={}", node != null ? node.toString() : "empty");
        return 0L;
    }

    public static long toTimestamp(JsonNode node) {
        if (node != null) {
            if (node.isLong() || node.isInt() || node.isShort()) {
                return node.asLong();
            } else if (node.isTextual()) {
                try {
                    Date date = DATE_FORMAT.parse(node.textValue());
                    return date.getTime();
                } catch (ParseException ex) {
                    LOGGER.warn("Couldn't convert textual date into timestamp: text={}. Default 0 value is used", node.textValue(), ex);
                }
            }
        }
        LOGGER.warn("Invalid type was passed into toDefaultAmtFormat method. Default 0 value is used. node={}", node != null ? node.toString() : "empty");
        return 0L;
    }
}