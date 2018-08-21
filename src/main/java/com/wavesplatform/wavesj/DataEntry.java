package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonTypeIdResolver(DataEntryTypeResolver.class)
public abstract class DataEntry<T> {
    private final static Charset UTF8 = Charset.forName("UTF-8");
    private final static byte INTEGER = 0;
    private final static byte BOOLEAN = 1;
    private final static byte BINARY = 2;
    private final static byte STRING = 3;

    private final String key;
    private final T value;

    private final String type;

    private DataEntry(String key, String type, T value) {
        this.key = key;
        this.type = type;
        this.value = value;
    }

    public int size() {
        return key.getBytes(UTF8).length + 2;
    }

    public void write(ByteBuffer buf) {
        byte[] bytes = key.getBytes(UTF8);
        buf.putShort((short) bytes.length).put(bytes);
    }


    public static class LongEntry extends DataEntry<Long> {
        @JsonCreator
        public LongEntry(@JsonProperty("key") String key, @JsonProperty("value") long value) {
            super(key, "integer", value);
        }

        public int size() {
            return super.size() + 1 + 8;
        }

        public void write(ByteBuffer buf) {
            super.write(buf);
            buf.put(INTEGER).putLong(getValue());
        }
    }

    public static class BooleanEntry extends DataEntry<Boolean> {
        @JsonCreator
        public BooleanEntry(@JsonProperty("key") String key, @JsonProperty("value") boolean value) {
            super(key, "boolean", value);
        }

        public int size() {
            return super.size() + 1 + 1;
        }

        public void write(ByteBuffer buf) {
            super.write(buf);
            buf.put(BOOLEAN).put((byte) (getValue() ? 1 : 0));
        }
    }

    @JsonSerialize(using = BinaryEntry.Serializer.class)
    public static class BinaryEntry extends DataEntry<ByteString> {
        @JsonCreator
        public BinaryEntry(@JsonProperty("key") String key, @JsonProperty("value") ByteString value) {
            super(key, "binary", value);
        }

        public int size() {
            return super.size() + 1 + 2 + getValue().getBytes().length;
        }

        public void write(ByteBuffer buf) {
            super.write(buf);
            buf.put(BINARY).putShort((short) getValue().getBytes().length).put(getValue().getBytes());
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
            public void serialize(BinaryEntry value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
                gen.writeStartObject();
                gen.writeStringField("key", value.getKey());
                gen.writeStringField("value", Base64.encode(value.getValue().getBytes()));
                gen.writeStringField("type", value.getType());
                gen.writeEndObject();
            }

            @Override
            public void serializeWithType(BinaryEntry value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
                serialize(value, gen, serializers);
            }
        }
    }

    public static class StringEntry extends DataEntry<String> {
        private final byte[] bytes = getValue().getBytes(UTF8);

        @JsonCreator
        public StringEntry(@JsonProperty("key") String key, @JsonProperty("value") String value) {
            super(key, "string", value);
        }

        public int size() {
            return super.size() + 1 + 2 + bytes.length;
        }

        public void write(ByteBuffer buf) {
            super.write(buf);
            buf.put(STRING).putShort((short) bytes.length).put(bytes);
        }
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataEntry<?> dataEntry = (DataEntry<?>) o;

        if (getKey() != null ? !getKey().equals(dataEntry.getKey()) : dataEntry.getKey() != null) return false;
        if (getValue() != null ? !getValue().equals(dataEntry.getValue()) : dataEntry.getValue() != null) return false;
        return getType() != null ? getType().equals(dataEntry.getType()) : dataEntry.getType() == null;
    }

    @Override
    public int hashCode() {
        int result = getKey() != null ? getKey().hashCode() : 0;
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        return result;
    }
}
