package node.transactions;

import base.BaseTestWithNodeInDocker;
import com.wavesplatform.transactions.*;
import com.wavesplatform.transactions.EthereumTransaction.Invocation;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Base64String;
import com.wavesplatform.transactions.invocation.*;
import com.wavesplatform.wavesj.actions.EthRpcResponse;
import com.wavesplatform.wavesj.exceptions.NodeException;
import com.wavesplatform.wavesj.info.EthereumTransactionInfo;
import com.wavesplatform.wavesj.info.IssueTransactionInfo;
import com.wavesplatform.wavesj.util.WavesEthConverter;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

import static com.wavesplatform.transactions.EthereumTransaction.DEFAULT_GAS_PRICE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EthereumTransactionIntegrationTest extends BaseTestWithNodeInDocker {

    private final String mnemonic = "shrug target screen enemy endorse chef term october blast rate fog runway";

    @Test
    void transferTest() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        Credentials bob = MetamaskHelper.generateCredentials(mnemonic);
        String bobAddress = WavesEthConverter.ethToWavesAddress(bob.getAddress(), WavesConfig.chainId());

        transferBalance(alice, new Address(bobAddress), Amount.of(1_00_000_000));

        EthereumTransaction transferTx = EthereumTransaction.createAndSign(
                new EthereumTransaction.Transfer(
                        new Address(alice.address().encoded()),
                        new Amount(1000, AssetId.WAVES)
                ),
                DEFAULT_GAS_PRICE,
                WavesConfig.chainId(),
                100000L,
                Instant.now().toEpochMilli(),
                bob.getEcKeyPair()
        );

        EthRpcResponse rs = node.broadcastEthTransaction(transferTx);
        node.waitForTransaction(transferTx.id());
        EthereumTransactionInfo ethTxInfo = node.getTransactionInfo(transferTx.id(), EthereumTransactionInfo.class);

        assertEquals(transferTx.id().encoded(), ethTxInfo.tx().id().encoded());
        assertEquals(transferTx.sender(), ethTxInfo.tx().sender());
        assertEquals(transferTx.timestamp(), ethTxInfo.tx().timestamp());
        assertEquals(transferTx.fee(), ethTxInfo.tx().fee());

        EthereumTransaction.Transfer transferTxPayload = (EthereumTransaction.Transfer) transferTx.payload();
        EthereumTransaction.Transfer ethTxInfoPayload = (EthereumTransaction.Transfer) ethTxInfo.tx().payload();

        assertEquals(transferTxPayload.recipient(), ethTxInfoPayload.recipient());
        assertEquals(transferTxPayload.amount(), ethTxInfoPayload.amount());
    }

    @Test
    void invocationTest() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        Credentials bob = MetamaskHelper.generateCredentials(mnemonic);
        String bobAddress = WavesEthConverter.ethToWavesAddress(bob.getAddress(), WavesConfig.chainId());

        AssetId assetId = createAsset(alice);
        transferBalance(alice, new Address(bobAddress), Amount.of(100, assetId));
        broadcastScript(alice);
        transferBalance(alice, new Address(bobAddress), Amount.of(3_00_000_000));

        ArrayList<Amount> payments = new ArrayList<>();
        payments.add(Amount.of(1, assetId));
        payments.add(Amount.of(2, assetId));
        payments.add(Amount.of(3, assetId));
        payments.add(Amount.of(4, assetId));
        payments.add(Amount.of(5, assetId));
        payments.add(Amount.of(6, assetId));
        payments.add(Amount.of(7, assetId));
        payments.add(Amount.of(8, assetId));
        payments.add(Amount.of(9, assetId));
        payments.add(Amount.of(10, assetId));

        EthereumTransaction ethInvokeTx = EthereumTransaction.createAndSign(
                new Invocation(
                        alice.address(),
                        Function.as("call",
                                BinaryArg.as(new Address(bobAddress).bytes()),
                                BooleanArg.as(true),
                                IntegerArg.as(100500),
                                StringArg.as(alice.address().toString()),
                                ListArg.as(IntegerArg.as(100500))
                        ),
                        payments
                ),
                DEFAULT_GAS_PRICE,
                WavesConfig.chainId(),
                100500000L,
                Instant.now().toEpochMilli(),
                bob.getEcKeyPair()
        );

        EthRpcResponse rs = node.broadcastEthTransaction(ethInvokeTx);
        node.waitForTransaction(ethInvokeTx.id());

        EthereumTransactionInfo ethInvokeTxInfo = node.getTransactionInfo(ethInvokeTx.id(), EthereumTransactionInfo.class);

        assertEquals(ethInvokeTx.id().encoded(), ethInvokeTxInfo.tx().id().encoded());
        assertEquals(ethInvokeTx.sender(), ethInvokeTxInfo.tx().sender());
        assertEquals(ethInvokeTx.timestamp(), ethInvokeTxInfo.tx().timestamp());
        assertEquals(ethInvokeTx.fee(), ethInvokeTxInfo.tx().fee());

        Invocation ethInvokeTxPayload = (Invocation) ethInvokeTx.payload();
        Invocation ethInvokeTxInfoPayload = (Invocation) ethInvokeTxInfo.tx().payload();

        assertEquals(ethInvokeTxPayload.function(), ethInvokeTxInfoPayload.function());
        assertEquals(ethInvokeTxPayload.function().args(), ethInvokeTxInfoPayload.function().args());
        assertEquals(ethInvokeTxPayload.dApp(), ethInvokeTxInfoPayload.dApp());
        assertEquals(ethInvokeTxPayload.payments().size(), ethInvokeTxInfoPayload.payments().size());
    }

    private AssetId createAsset(PrivateKey signer) throws NodeException, IOException {
        return node.waitForTransaction(node.broadcast(
                        IssueTransaction.builder("Asset", 1000, 2).getSignedWith(signer)).id(),
                IssueTransactionInfo.class).tx().assetId();
    }

    private void broadcastScript(PrivateKey signer) throws NodeException, IOException {
        Base64String script = node.compileScript(
                "{-# STDLIB_VERSION 5 #-}\n" +
                        "{-# CONTENT_TYPE DAPP #-}\n" +
                        "{-# SCRIPT_TYPE ACCOUNT #-}\n" +
                        "@Callable(inv)\n" +
                        "func call(bv: ByteVector, b: Boolean, int: Int, str: String, list: List[Int]) = {\n" +
                        "  let asset = Issue(\"Asset\", \"\", 1, 0, true)\n" +
                        "  let assetId = asset.calculateAssetId()\n" +
                        "  let lease = Lease(inv.caller, 7)\n" +
                        "  let leaseId = lease.calculateLeaseId()\n" +
                        "  [\n" +
                        "    BinaryEntry(\"bin\", assetId),\n" +
                        "    BooleanEntry(\"bool\", true),\n" +
                        "    IntegerEntry(\"int\", 100500),\n" +
                        "    StringEntry(\"assetId\", assetId.toBase58String()),\n" +
                        "    StringEntry(\"leaseId\", leaseId.toBase58String()),\n" +
                        "    StringEntry(\"del\", \"\"),\n" +
                        "    DeleteEntry(\"del\"),\n" +
                        "    asset,\n" +
                        "    SponsorFee(assetId, 1),\n" +
                        "    Reissue(assetId, 4, false),\n" +
                        "    Burn(assetId, 3),\n" +
                        "    ScriptTransfer(inv.caller, 2, assetId),\n" +
                        "    lease,\n" +
                        "    LeaseCancel(lease.calculateLeaseId())\n" +
                        "  ]\n" +
                        "}").script();

        node.waitForTransaction(node.broadcast(
                SetScriptTransaction.builder(script).fee(3400000).getSignedWith(signer)).id());
    }

    private void transferBalance(PrivateKey from, Address to, Amount amount) throws NodeException, IOException {
        TransferTransaction tx = TransferTransaction.builder(to, amount).getSignedWith(from);
        node.waitForTransaction(node.broadcast(tx).id());
    }
}
