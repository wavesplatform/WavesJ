package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public abstract class DataEntry<T> {
    private final static byte INTEGER = 0;
    private final static byte BOOLEAN = 1;
    private final static byte BINARY  = 2;

    public final String key;
    public final T value;

    @JsonProperty
    final String type;

    private DataEntry(String key, String type, T value) {
        this.key = key;
        this.type = type;
        this.value = value;
    }

    int size() {
        return key.getBytes(StandardCharsets.UTF_8).length + 2;
    }

    void write(ByteBuffer buf) {
        byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
        buf.putShort((short) bytes.length).put(bytes);
    }


    public static class LongEntry extends DataEntry<Long> {
        public LongEntry(String key, long value) {
            super(key, "integer", value);
        }

        int size() {
            return super.size() + 1 + 8;
        }

        void write(ByteBuffer buf) {
            super.write(buf);
            buf.put(INTEGER).putLong(value);
        }
    }

    public static class BooleanEntry extends DataEntry<Boolean> {
        public BooleanEntry(String key, boolean value) {
            super(key, "boolean", value);
        }

        int size() {
            return super.size() + 1 + 1;
        }

        void write(ByteBuffer buf) {
            super.write(buf);
            buf.put(BOOLEAN).put((byte) (value ? 1 : 0));
        }
    }

    @JsonSerialize(using = BinaryEntry.Serializer.class)
    public static class BinaryEntry extends DataEntry<byte[]> {
        public BinaryEntry(String key, byte[] value) {
            super(key, "binary", value);
        }

        int size() {
            return super.size() + 1 + 2 + value.length;
        }

        void write(ByteBuffer buf) {
            super.write(buf);
            buf.put(BINARY).putShort((short) value.length).put(value);
        }

        static class Serializer extends StdSerializer<BinaryEntry> {

            public Serializer() {
                this(null);
            }

            public Serializer(Class<BinaryEntry> t) {
                super(t);
            }

            @Override
            public void serialize(BinaryEntry value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                gen.writeStartObject();
                gen.writeStringField("key", value.key);
                gen.writeStringField("value", Base58.encode(value.value));
                gen.writeStringField("type", value.type);
                gen.writeEndObject();
            }
        }
    }
}
