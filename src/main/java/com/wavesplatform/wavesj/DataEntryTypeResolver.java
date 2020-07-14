package com.wavesplatform.wavesj;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

import java.io.IOException;

public class DataEntryTypeResolver extends TypeIdResolverBase {
    @Override
    public String idFromValue(Object o) {
        return String.valueOf(((DataEntry) o).getType());
    }

    @Override
    public String idFromValueAndType(Object o, Class<?> aClass) {
        return idFromValue(o);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        Class t = null;

        if (id == null || id.equals("delete")) {
            t = DataEntry.DeleteEntry.class;
        } else if (id.equals("integer")) {
            t = DataEntry.LongEntry.class;
        } else if (id.equals("boolean")) {
            t = DataEntry.BooleanEntry.class;
        } else if (id.equals("binary")) {
            t = DataEntry.BinaryEntry.class;
        } else if (id.equals("string")) {
            t = DataEntry.StringEntry.class;
        }
        return context.constructType(t);
    }
}
