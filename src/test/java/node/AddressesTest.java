package node;

import base.BaseTestWithNodeInDocker;
import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.transactions.DataTransaction;
import com.wavesplatform.transactions.LeaseTransaction;
import com.wavesplatform.transactions.SetScriptTransaction;
import com.wavesplatform.transactions.TransferTransaction;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.Base64String;
import com.wavesplatform.transactions.data.*;
import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.exceptions.NodeException;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AddressesTest extends BaseTestWithNodeInDocker {

    @Test
    void addresses() throws IOException, NodeException {
        assertThat(node.getAddresses()).containsOnly(faucet.address());
        assertThat(node.getAddresses(0, 10)).containsOnly(faucet.address());
    }

    @Test
    void wavesBalance() throws IOException, NodeException {
        long initBalance = 1000000;
        long balanceAfterLeaseOutFee = initBalance - LeaseTransaction.MIN_FEE;
        long leasedIn = 30000;
        long leasedOut = 60000;

        PrivateKey alicePK = createAccountWithBalance(initBalance);
        Address alice = alicePK.address();
        Address bob = createAccountWithBalance(initBalance).address();

        node.waitForTransaction(node.broadcast(
                LeaseTransaction.builder(alice, leasedIn).getSignedWith(faucet)).id());

        int height = node.waitForTransaction(node.broadcast(
                LeaseTransaction.builder(bob, leasedOut).getSignedWith(alicePK)).id()).height();

        assertThat(node.getBalance(alice)).isEqualTo(balanceAfterLeaseOutFee);
        assertThat(node.getBalanceDetails(alice))
                        .isEqualTo(new BalanceDetails(
                                alice,
                                balanceAfterLeaseOutFee - leasedOut,
                                balanceAfterLeaseOutFee,
                                0,
                                balanceAfterLeaseOutFee - leasedOut + leasedIn));
        assertThat(node.getBalanceDetails(faucet.address()).generating()).isPositive();
        assertThat(node.getEffectiveBalance(alice)).isEqualTo(balanceAfterLeaseOutFee - leasedOut + leasedIn);

        assertThat(node.getBalances(asList(alice, bob)))
                        .containsExactlyInAnyOrder(
                                new Balance(alice, balanceAfterLeaseOutFee),
                                new Balance(bob, initBalance));
        assertThat(node.getBalances(asList(alice, bob), height))
                .containsExactlyInAnyOrder(
                        new Balance(alice, balanceAfterLeaseOutFee),
                        new Balance(bob, initBalance));

        node.waitForHeight(height + 1);
        assertThat(node.getBalance(alice, 1)).isEqualTo(balanceAfterLeaseOutFee);
        assertThat(node.getEffectiveBalance(alice, 1))
                .isEqualTo(balanceAfterLeaseOutFee - leasedOut + leasedIn);
    }

    @Test
    void wavesBalanceHistory() throws IOException, NodeException {
        long initBalance = 1000000;
        long transferAmount = 50000;

        Address alice = createAccountWithBalance().address();
        int initHeight = node.waitForTransaction(node.broadcast(TransferTransaction
                .builder(alice, Amount.of(initBalance)).getSignedWith(faucet)).id()).height();
        node.waitForHeight(initHeight + 1);

        int transferHeight = node.waitForTransaction(node.broadcast(TransferTransaction
                .builder(alice, Amount.of(transferAmount)).getSignedWith(faucet)).id()).height();
        node.waitForHeight(transferHeight + 1);

        assertThat(node.getBalanceHistory(alice))
                .containsExactlyInAnyOrder(
                        new HistoryBalance(initHeight, initBalance),
                        new HistoryBalance(transferHeight, initBalance + transferAmount));
    }

    @Test
    void data() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        Base64String binaryValue = new Base64String(new byte[32767]);
        String stringWithMaxLength = StringUtils.repeat("Ы", 32766 / 2) + "?";

        DataEntry[] expectedEntries = {
                BinaryEntry.as("bin-empty", Bytes.empty()),
                BinaryEntry.as("bin-max", binaryValue),
                BooleanEntry.as("bool-false", false),
                BooleanEntry.as("bool-true", true),
                IntegerEntry.as("int-min", Long.MIN_VALUE),
                IntegerEntry.as("int-zero", 0),
                IntegerEntry.as("int-max", Long.MAX_VALUE),
                StringEntry.as("str-empty", ""),
                StringEntry.as("str-max", stringWithMaxLength)};

        node.waitForTransaction(node.broadcast(
                DataTransaction.builder(expectedEntries).getSignedWith(alice)).id());

        assertThat(node.getData(alice.address())).containsExactlyInAnyOrder(expectedEntries);
        assertThat(node.getData(alice.address(), "bin-max")).isEqualTo(new BinaryEntry("bin-max", binaryValue));
        assertThat(node.getData(alice.address(), asList("bin-max", "int-max")))
                .containsExactlyInAnyOrder(
                        new BinaryEntry("bin-max", binaryValue),
                        new IntegerEntry("int-max", Long.MAX_VALUE));
        assertThat(node.getData(alice.address(), Pattern.compile("int.+")))
                .containsExactlyInAnyOrder(
                        new IntegerEntry("int-min", Long.MIN_VALUE),
                        new IntegerEntry("int-max", Long.MAX_VALUE),
                        new IntegerEntry("int-zero", 0));
    }

    @Test
    void expressionCompileAndScriptInfo() throws IOException, NodeException {
        ScriptInfo scriptInfo = node.compileScript(
                "{-# STDLIB_VERSION 5 #-}\n" +
                        "{-# CONTENT_TYPE EXPRESSION #-}\n" +
                        "{-# SCRIPT_TYPE ACCOUNT #-}\n" +
                        "sigVerify(tx.bodyBytes, tx.proofs[0], tx.senderPublicKey)");
        Base64String expectedScript = new Base64String(
                "BQkAAfQAAAADCAUAAAACdHgAAAAJYm9keUJ5dGVzCQABkQAAAAIIBQAAAAJ0eAAAAAZwcm9vZnMAAAAAAAAAAAAIBQAAAAJ0eAAAAA9zZW5kZXJQdWJsaWNLZXlzTh3b");

        PrivateKey alice = createAccountWithBalance(SetScriptTransaction.MIN_FEE);

        node.waitForTransaction(node.broadcast(
                SetScriptTransaction.builder(scriptInfo.script()).getSignedWith(alice)).id());

        assertThat(node.getScriptInfo(alice.address())).isEqualTo(scriptInfo);
        assertThat(scriptInfo).isEqualTo(
                new ScriptInfo(expectedScript, 209, 209, new HashMap<>(), 400000));
        assertThat(assertThrows(NodeException.class, () ->
                node.getScriptMeta(alice.address())))
                .hasMessage("ScriptParseError(Expected DApp)"); //TODO waiting fix in Node. The scenario should work
    }

    @Test
    void dAppCompileAndScriptInfo() throws IOException, NodeException {
        ScriptInfo compileScriptInfo = node.compileScript(
                "{-# STDLIB_VERSION 5 #-}\n" +
                        "{-# CONTENT_TYPE DAPP #-}\n" +
                        "{-# SCRIPT_TYPE ACCOUNT #-}\n" +
                        "\n" +
                        "@Callable(inv)\n" +
                        "func foo() = nil\n" +
                        "\n" +
                        "@Callable(inv)\n" +
                        "func bar(bin: ByteVector, bool: Boolean, int: Int, str: String, list: List[Int]) = nil");
        Base64String expectedScript = new Base64String(
                "AAIFAAAAAAAAAA0IAhIAEgcKBQIEAQgRAAAAAAAAAAIAAAADaW52AQAAAANmb28AAAAABQAAAANuaWwAAAADaW52AQAAAANiYXIAAAAFAAAAA2JpbgAAAARib29sAAAAA2ludAAAAANzdHIAAAAEbGlzdAUAAAADbmlsAAAAAHZW33I=");

        HashMap<String, Integer> expectedComplexities = new HashMap<>();
        expectedComplexities.put("foo", 1);
        expectedComplexities.put("bar", 6);

        HashMap<String, List<ArgMeta>> expectedFunctions = new HashMap<>();
        expectedFunctions.put("foo", new ArrayList<>());
        expectedFunctions.put("bar", asList(
                new ArgMeta("bin", "ByteVector"),
                new ArgMeta("bool", "Boolean"),
                new ArgMeta("int", "Int"),
                new ArgMeta("str", "String"),
                new ArgMeta("list", "List[Int]")));

        PrivateKey alice = createAccountWithBalance(SetScriptTransaction.MIN_FEE);

        node.waitForTransaction(node.broadcast(
                SetScriptTransaction.builder(compileScriptInfo.script()).getSignedWith(alice)).id());

        ScriptInfo actualScriptInfo = node.getScriptInfo(alice.address());
        assertThat(actualScriptInfo).isEqualTo(
                new ScriptInfo(expectedScript, 6, 0, expectedComplexities, 0));
        assertThat(node.getScriptMeta(alice.address())).isEqualTo(
                new ScriptMeta(2, expectedFunctions));

        // TODO waiting fix in Node. Field `extraFee` should be 0. Uncomment and remove all the assertions below
        // assertThat(node.getScriptInfo(alice.address())).isEqualTo(scriptInfo);
        assertThat(compileScriptInfo.extraFee()).isEqualTo(400000);
        assertThat(actualScriptInfo.script()).isEqualTo(compileScriptInfo.script());
        assertThat(actualScriptInfo.callableComplexities()).isEqualTo(compileScriptInfo.callableComplexities());
        assertThat(actualScriptInfo.complexity()).isEqualTo(compileScriptInfo.complexity());
        assertThat(actualScriptInfo.verifierComplexity()).isEqualTo(compileScriptInfo.verifierComplexity());
    }

}
