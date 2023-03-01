package com.wavesplatform.wavesj.util;

import com.wavesplatform.lang.CompileAndParseResult;
import com.wavesplatform.lang.Lang;
import com.wavesplatform.transactions.common.Base64String;
import com.wavesplatform.wavesj.ScriptInfo;
import com.wavesplatform.wavesj.exceptions.CompilationException;
import scala.jdk.CollectionConverters;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class CompilationUtil {

    public static ScriptInfo compile(String source) throws CompilationException {
        return compile(source, false, false);
    }
    public static ScriptInfo compile(String source, boolean needCompaction, boolean removeUnusedCode) throws CompilationException {
        CompileAndParseResult result;

        try {
            result = Lang.parseAndCompile(source, needCompaction, removeUnusedCode);
        } catch (IllegalArgumentException e) {
            throw new CompilationException(e.getMessage());
        }

        if (result instanceof CompileAndParseResult.Contract) {
            return scriptInfoFromContract((CompileAndParseResult.Contract) result);
        } else if (result instanceof CompileAndParseResult.Library) {
            return scriptInfoFromLibrary((CompileAndParseResult.Library) result);
        } else if (result instanceof CompileAndParseResult.Expression) {
            return scriptInfoFromExpression((CompileAndParseResult.Expression) result);
        }
        throw new IllegalArgumentException("Unknown compilation result");
    }

    private static ScriptInfo scriptInfoFromContract(CompileAndParseResult.Contract contract) {
        Map<String, Integer> callableComplexity = CollectionConverters
                .MapHasAsJava(contract.callableComplexities())
                .asJava()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> ((Long) e.getValue()).intValue()));

        int complexity = callableComplexity.values().stream().mapToInt(v -> v).max().orElse(0);

        return new ScriptInfo(
                new Base64String(contract.bytes()),
                complexity,
                ((Long) contract.verifierComplexity()).intValue(),
                callableComplexity,
                0L
        );
    }

    private static ScriptInfo scriptInfoFromLibrary(CompileAndParseResult.Library library) {
        return new ScriptInfo(
                new Base64String(library.bytes()),
                (int) library.complexity(),
                (int) library.complexity(),
                Collections.emptyMap(),
                0L
        );
    }

    private static ScriptInfo scriptInfoFromExpression(CompileAndParseResult.Expression expression) {
        return new ScriptInfo(
                new Base64String(expression.bytes()),
                (int) expression.complexity(),
                (int) expression.complexity(),
                Collections.emptyMap(),
                0L
        );
    }

}
