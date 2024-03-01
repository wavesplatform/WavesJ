package util;

import com.wavesplatform.transactions.common.Base64String;
import com.wavesplatform.wavesj.ScriptInfo;
import com.wavesplatform.wavesj.exceptions.CompilationException;
import com.wavesplatform.wavesj.util.CompilationUtil;
import org.junit.Assert;
import org.junit.Test;

public class CompilerTest {

    @Test
    public void compileContract() throws CompilationException {
        ScriptInfo scriptInfo = CompilationUtil.compile(contract);
        Assert.assertEquals(new Base64String(compiledContract), scriptInfo.script());
        Assert.assertEquals(207, scriptInfo.complexity());
        Assert.assertEquals(207, scriptInfo.verifierComplexity());
        Assert.assertEquals(Integer.valueOf(21), scriptInfo.callableComplexities().get("issueNft"));
        Assert.assertEquals(Integer.valueOf(4), scriptInfo.callableComplexities().get("setPrice"));
        Assert.assertEquals(Integer.valueOf(207), scriptInfo.callableComplexities().get("pullTheLever"));
        Assert.assertEquals(0L, scriptInfo.extraFee());
    }

    @Test
    public void compileExpression() throws CompilationException {
        ScriptInfo scriptInfo = CompilationUtil.compile(expression);
        Assert.assertEquals(new Base64String(compiledExpression), scriptInfo.script());
        Assert.assertEquals(3, scriptInfo.complexity());
        Assert.assertEquals(3, scriptInfo.verifierComplexity());
        Assert.assertTrue(scriptInfo.callableComplexities().isEmpty());
        Assert.assertEquals(0L, scriptInfo.extraFee());
    }

    @Test
    public void compileLibrary() throws CompilationException {
        ScriptInfo scriptInfo = CompilationUtil.compile(library);
        Assert.assertEquals(new Base64String(compiledLibrary), scriptInfo.script());
        Assert.assertEquals(0, scriptInfo.complexity());
        Assert.assertEquals(0, scriptInfo.verifierComplexity());
        Assert.assertTrue(scriptInfo.callableComplexities().isEmpty());
        Assert.assertEquals(0L, scriptInfo.extraFee());
    }

    @Test
    public void compileV8() throws CompilationException {
        String script = "{-# STDLIB_VERSION 8 #-}\n" +
                "{-# CONTENT_TYPE EXPRESSION #-}\n" +
                "{-# SCRIPT_TYPE ACCOUNT #-}\n" +
                "\n" +
                "let a = calculateDelay(Address(base58''), 0)\n" +
                "let b = [1, 2, 3].replaceByIndex(1, 0)\n" +
                "a == b[0]";
        ScriptInfo scriptInfo = CompilationUtil.compile(script);
        Assert.assertEquals(12, scriptInfo.complexity());
        Assert.assertEquals(12, scriptInfo.verifierComplexity());
        Assert.assertTrue(scriptInfo.callableComplexities().isEmpty());
        Assert.assertEquals(0, scriptInfo.extraFee());
    }

    @Test(expected = CompilationException.class)
    public void compilationError() throws CompilationException {
        CompilationUtil.compile("{-# STDLIB_V 1 #-}");
    }

    private static final String contract = "{-# STDLIB_VERSION 6 #-}\n" +
            "{-# CONTENT_TYPE DAPP #-}\n" +
            "{-# SCRIPT_TYPE ACCOUNT #-}\n" +
            "\n" +
            "let kCommon = \"common\"\n" +
            "let kRare = \"rare\"\n" +
            "let kLegendary = \"legendary\"\n" +
            "let kPrice = \"priceLever\"\n" +
            "let separator = \"|\"\n" +
            "let countRare =  getInteger(this, kRare).valueOrElse(0)\n" +
            "let countCommon =  getInteger(this, kCommon).valueOrElse(0)\n" +
            "let countLegendary = getInteger(this, kLegendary).valueOrElse(0)\n" +
            "let correctPrice = getIntegerValue(this, kPrice)\n" +
            "\n" +
            "\n" +
            "func randomize(bytes: ByteVector, range: Int) = {\n" +
            "    let hash = sha256(bytes)\n" +
            "    let index = toInt(hash)\n" +
            "    index % range\n" +
            "}\n" +
            "\n" +
            "func incrementCounter(rare: String) = {\n" +
            "    if (rare==kCommon) then\n" +
            "        IntegerEntry(kCommon, countCommon + 1)\n" +
            "    else if (rare==kRare) then\n" +
            "        IntegerEntry(kRare, countCommon + 1)\n" +
            "    else\n" +
            "        IntegerEntry(kLegendary, countLegendary + 1)\n" +
            "}\n" +
            "\n" +
            "func calculateRarity(transactionId: ByteVector) = {\n" +
            "    let tokenRare = randomize(transactionId, 100)\n" +
            "    if(tokenRare<=5) then\n" +
            "        kLegendary\n" +
            "    else if (tokenRare>=80) then\n" +
            "        kRare\n" +
            "    else\n" +
            "        kCommon\n" +
            "}\n" +
            "\n" +
            "@Callable(i)\n" +
            "func issueNft(name: String, desc: String, rarity: String, prefix: String) = {\n" +
            "    if (i.caller != this) then\n" +
            "        throw(\"Only owner\")\n" +
            "    else {\n" +
            "        let newToken = Issue(name, desc, 1, 0, false)\n" +
            "        let issueId = calculateAssetId(newToken)\n" +
            "        [\n" +
            "            newToken,\n" +
            "            StringEntry(rarity + separator + prefix, issueId.toBase58String())\n" +
            "        ]\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "@Callable(i)\n" +
            "func setPrice(price: Int) = {\n" +
            "    if (i.caller != this) then\n" +
            "        throw(\"Only owner\")\n" +
            "    else\n" +
            "        [\n" +
            "            IntegerEntry(kPrice, price)\n" +
            "        ]\n" +
            "}\n" +
            "\n" +
            "@Callable(i)\n" +
            "func pullTheLever() = {\n" +
            "    let amount = i.payments[0].amount.value()\n" +
            "    if (i.payments[0].assetId != unit) then\n" +
            "        throw(\"Pay in waves\")\n" +
            "    else {\n" +
            "        if (amount<correctPrice) then\n" +
            "            throw(\"Payment amount < \" + correctPrice.toString())\n" +
            "        else {\n" +
            "            let rarity = calculateRarity(i.transactionId)\n" +
            "            let incrementStruct = incrementCounter(rarity)\n" +
            "            let tokenKey = rarity + separator + toString(getInteger(this, rarity).valueOrElse(0))\n" +
            "            let assetKey = getString(this, tokenKey).valueOrErrorMessage(tokenKey)\n" +
            "            [\n" +
            "                incrementStruct,\n" +
            "                ScriptTransfer(i.caller, 1, assetKey.fromBase58String()),\n" +
            "                StringEntry(i.caller.toString() + tokenKey, assetKey)\n" +
            "            ]\n" +
            "        }\n" +
            "    }\n" +
            "}";

    private static final String compiledContract = "BgIRCAISBgoECAgICBIDCgEBEgAMAAdrQ29tbW9uAgZjb21tb24ABWtSYXJlAgRyYXJlAAprTGVnZW5kYXJ5AglsZWdlbmRhcnkABmtQcmljZQIKcHJpY2VMZXZlcgAJc2VwYXJhdG9yAgF8AAljb3VudFJhcmUJAQt2YWx1ZU9yRWxzZQIJAJoIAgUEdGhpcwUFa1JhcmUAAAALY291bnRDb21tb24JAQt2YWx1ZU9yRWxzZQIJAJoIAgUEdGhpcwUHa0NvbW1vbgAAAA5jb3VudExlZ2VuZGFyeQkBC3ZhbHVlT3JFbHNlAgkAmggCBQR0aGlzBQprTGVnZW5kYXJ5AAAADGNvcnJlY3RQcmljZQkBEUBleHRyTmF0aXZlKDEwNTApAgUEdGhpcwUGa1ByaWNlAQlyYW5kb21pemUCBWJ5dGVzBXJhbmdlBARoYXNoCQD3AwEFBWJ5dGVzBAVpbmRleAkAsQkBBQRoYXNoCQBqAgUFaW5kZXgFBXJhbmdlARBpbmNyZW1lbnRDb3VudGVyAQRyYXJlAwkAAAIFBHJhcmUFB2tDb21tb24JAQxJbnRlZ2VyRW50cnkCBQdrQ29tbW9uCQBkAgULY291bnRDb21tb24AAQMJAAACBQRyYXJlBQVrUmFyZQkBDEludGVnZXJFbnRyeQIFBWtSYXJlCQBkAgULY291bnRDb21tb24AAQkBDEludGVnZXJFbnRyeQIFCmtMZWdlbmRhcnkJAGQCBQ5jb3VudExlZ2VuZGFyeQABAQ9jYWxjdWxhdGVSYXJpdHkBDXRyYW5zYWN0aW9uSWQECXRva2VuUmFyZQkBCXJhbmRvbWl6ZQIFDXRyYW5zYWN0aW9uSWQAZAMJAGcCAAUFCXRva2VuUmFyZQUKa0xlZ2VuZGFyeQMJAGcCBQl0b2tlblJhcmUAUAUFa1JhcmUFB2tDb21tb24DAWkBCGlzc3VlTmZ0BARuYW1lBGRlc2MGcmFyaXR5BnByZWZpeAMJAQIhPQIIBQFpBmNhbGxlcgUEdGhpcwkAAgECCk9ubHkgb3duZXIECG5ld1Rva2VuCQDCCAUFBG5hbWUFBGRlc2MAAQAABwQHaXNzdWVJZAkAuAgBBQhuZXdUb2tlbgkAzAgCBQhuZXdUb2tlbgkAzAgCCQELU3RyaW5nRW50cnkCCQCsAgIJAKwCAgUGcmFyaXR5BQlzZXBhcmF0b3IFBnByZWZpeAkA2AQBBQdpc3N1ZUlkBQNuaWwBaQEIc2V0UHJpY2UBBXByaWNlAwkBAiE9AggFAWkGY2FsbGVyBQR0aGlzCQACAQIKT25seSBvd25lcgkAzAgCCQEMSW50ZWdlckVudHJ5AgUGa1ByaWNlBQVwcmljZQUDbmlsAWkBDHB1bGxUaGVMZXZlcgAEBmFtb3VudAkBBXZhbHVlAQgJAJEDAggFAWkIcGF5bWVudHMAAAZhbW91bnQDCQECIT0CCAkAkQMCCAUBaQhwYXltZW50cwAAB2Fzc2V0SWQFBHVuaXQJAAIBAgxQYXkgaW4gd2F2ZXMDCQBmAgUMY29ycmVjdFByaWNlBQZhbW91bnQJAAIBCQCsAgICEVBheW1lbnQgYW1vdW50IDwgCQCkAwEFDGNvcnJlY3RQcmljZQQGcmFyaXR5CQEPY2FsY3VsYXRlUmFyaXR5AQgFAWkNdHJhbnNhY3Rpb25JZAQPaW5jcmVtZW50U3RydWN0CQEQaW5jcmVtZW50Q291bnRlcgEFBnJhcml0eQQIdG9rZW5LZXkJAKwCAgkArAICBQZyYXJpdHkFCXNlcGFyYXRvcgkApAMBCQELdmFsdWVPckVsc2UCCQCaCAIFBHRoaXMFBnJhcml0eQAABAhhc3NldEtleQkBE3ZhbHVlT3JFcnJvck1lc3NhZ2UCCQCdCAIFBHRoaXMFCHRva2VuS2V5BQh0b2tlbktleQkAzAgCBQ9pbmNyZW1lbnRTdHJ1Y3QJAMwIAgkBDlNjcmlwdFRyYW5zZmVyAwgFAWkGY2FsbGVyAAEJANkEAQUIYXNzZXRLZXkJAMwIAgkBC1N0cmluZ0VudHJ5AgkArAICCQClCAEIBQFpBmNhbGxlcgUIdG9rZW5LZXkFCGFzc2V0S2V5BQNuaWwA/uxjpA==";

    private static final String expression = "{-# STDLIB_VERSION 5 #-}\n" +
            "{-# CONTENT_TYPE EXPRESSION #-}\n" +
            "{-# SCRIPT_TYPE ASSET #-}\n" +
            "\n" +
            "match tx {\n" +
            "    case t : SetAssetScriptTransaction | BurnTransaction => throw(\"You can`t set new script or burn token\")\n" +
            "    case _ => true\n" +
            "}";
    private static final String compiledExpression = "BQQAAAAHJG1hdGNoMAUAAAACdHgDAwkAAAEAAAACBQAAAAckbWF0Y2gwAgAAAA9CdXJuVHJhbnNhY3Rpb24GCQAAAQAAAAIFAAAAByRtYXRjaDACAAAAGVNldEFzc2V0U2NyaXB0VHJhbnNhY3Rpb24EAAAAAXQFAAAAByRtYXRjaDAJAAACAAAAAQIAAAAmWW91IGNhbmB0IHNldCBuZXcgc2NyaXB0IG9yIGJ1cm4gdG9rZW4GIjS9lg==";

    private static final String library = "{-# STDLIB_VERSION 5 #-}\n" +
            "{-# CONTENT_TYPE LIBRARY #-}\n" +
            "\n" +
            "func getOwnerAddr() = {\n" +
            "    let owner = getString(this, \"owner\")\n" +
            "    if (!isDefined(owner)) then {\n" +
            "        throw(\"init first\")\n" +
            "    } else {\n" +
            "        owner.value().addressFromStringValue()\n" +
            "    }\n" +
            "}";
    private static final String compiledLibrary = "BQoBAAAADGdldE93bmVyQWRkcgAAAAAEAAAABW93bmVyCQAEHQAAAAIFAAAABHRoaXMCAAAABW93bmVyAwkBAAAAASEAAAABCQEAAAAJaXNEZWZpbmVkAAAAAQUAAAAFb3duZXIJAAACAAAAAQIAAAAKaW5pdCBmaXJzdAkBAAAAEUBleHRyTmF0aXZlKDEwNjIpAAAAAQkBAAAABXZhbHVlAAAAAQUAAAAFb3duZXIGlBiNxg==";
}

