package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public abstract class DataEntry<T> {
    private final static Charset UTF8 = Charset.forName("UTF-8");
    private final static byte INTEGER = 0;
    private final static byte BOOLEAN = 1;
    private final static byte BINARY  = 2;
    private final static byte STRING  = 3;

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
        return key.getBytes(UTF8).length + 2;
    }

    void write(ByteBuffer buf) {
        byte[] bytes = key.getBytes(UTF8);
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

        @SuppressWarnings("serial")
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
                gen.writeStringField("value", Base64.encode(value.value));
                gen.writeStringField("type", value.type);
                gen.writeEndObject();
            }
        }
    }

    public static class StringEntry extends DataEntry<String> {
        private final byte[] bytes = value.getBytes(UTF8);

        public StringEntry(String key, String value) {
            super(key, "string", value);
        }

        int size() {
            return super.size() + 1 + 2 + bytes.length;
        }

        void write(ByteBuffer buf) {
            super.write(buf);
            buf.put(STRING).putShort((short) bytes.length).put(bytes);
        }
    }
}
