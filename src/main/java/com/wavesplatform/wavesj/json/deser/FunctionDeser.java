package com.wavesplatform.wavesj.json.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.wavesplatform.transactions.invocation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FunctionDeser extends JsonDeserializer<Function> {

    @Override
    public Function deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = p.getCodec();
        JsonNode json = codec.readTree(p);

        if (json.isNull())
            return Function.asDefault();

        String name = json.get("function").asText();
        List<Arg> args = parseArgsList(json.get("args"));

        return Function.as(name, args);
    }

    public static List<Arg> parseArgsList(JsonNode list) {
        List<Arg> args = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            JsonNode arg = list.get(i);
            if ("String".equals(arg.get("type").asText()))
                args.add(StringArg.as(arg.get("value").asText()));
            else if ("Int".equals(arg.get("type").asText()))
                args.add(IntegerArg.as(arg.get("value").asLong()));
            else if ("Boolean".equals(arg.get("type").asText()))
                args.add(BooleanArg.as(arg.get("value").asBoolean()));
            else if ("ByteVector".equals(arg.get("type").asText()))
                args.add(BinaryArg.as(arg.get("value").asText()));
            else if ("List".equals(arg.get("type").asText()))
                args.add(ListArg.as(parseArgsList(arg.get("value"))));
        }
        return args;
    }
}
