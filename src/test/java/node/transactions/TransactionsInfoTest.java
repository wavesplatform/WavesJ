package node.transactions;

import base.BaseTestWithNodeInDocker;
import com.wavesplatform.transactions.*;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.*;
import com.wavesplatform.transactions.data.*;
import com.wavesplatform.transactions.exchange.Order;
import com.wavesplatform.transactions.exchange.OrderType;
import com.wavesplatform.transactions.invocation.*;
import com.wavesplatform.transactions.mass.Transfer;
import com.wavesplatform.wavesj.LeaseStatus;
import com.wavesplatform.wavesj.exceptions.NodeException;
import com.wavesplatform.wavesj.info.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionsInfoTest extends BaseTestWithNodeInDocker {

    @Test
    void txs() throws IOException, NodeException {
//TODO
//        node.broadcast();
//        node.getTransactionInfo();
//        node.getTransactionsByAddress();
//        node.calculateTransactionFee();
//        node.getTransactionStatus();
//        node.getTransactionsStatus();
//        node.getUnconfirmedTransaction();
//        node.getUnconfirmedTransactions();
//        node.validateTransaction();
    }

    @Test
    void issueTransactionInfo() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        IssueTransaction tx = IssueTransaction.builder("Asset", 1000, 2).getSignedWith(alice);
        node.waitForTransaction(node.broadcast(tx).id());

        TransactionInfo commonInfo = node.getTransactionInfo(tx.id());
        IssueTransactionInfo txInfo = node.getTransactionInfo(tx.id(), IssueTransactionInfo.class);

        assertThat(commonInfo).isInstanceOf(IssueTransactionInfo.class);
        assertThat(commonInfo.tx()).isInstanceOf(IssueTransaction.class);
        assertThat(txInfo).isEqualTo(commonInfo);
        assertThat(txInfo.height()).isPositive();
        assertThat(txInfo.tx()).isEqualTo(tx);
    }

    @Test
    void transferTransactionInfo() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);
        Address bob = createAccountWithBalance().address();

        TransferTransaction tx = TransferTransaction.builder(bob, Amount.of(1000)).getSignedWith(alice);
        node.waitForTransaction(node.broadcast(tx).id());

        TransactionInfo commonInfo = node.getTransactionInfo(tx.id());
        TransferTransactionInfo txInfo = node.getTransactionInfo(tx.id(), TransferTransactionInfo.class);

        assertThat(commonInfo).isInstanceOf(TransferTransactionInfo.class);
        assertThat(commonInfo.tx()).isInstanceOf(TransferTransaction.class);
        assertThat(txInfo).isEqualTo(commonInfo);
        assertThat(txInfo.height()).isPositive();
        assertThat(txInfo.tx()).isEqualTo(tx);
    }

    @Test
    void reissueTransactionInfo() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        AssetId assetId = node.waitForTransaction(node.broadcast(
                IssueTransaction.builder("Asset", 1000, 2).getSignedWith(alice)).id(),
                IssueTransactionInfo.class).tx().assetId();
        ReissueTransaction tx = ReissueTransaction.builder(Amount.of(1000, assetId)).getSignedWith(alice);
        node.waitForTransaction(node.broadcast(tx).id());

        TransactionInfo commonInfo = node.getTransactionInfo(tx.id());
        ReissueTransactionInfo txInfo = node.getTransactionInfo(tx.id(), ReissueTransactionInfo.class);

        assertThat(commonInfo).isInstanceOf(ReissueTransactionInfo.class);
        assertThat(commonInfo.tx()).isInstanceOf(ReissueTransaction.class);
        assertThat(txInfo).isEqualTo(commonInfo);
        assertThat(txInfo.height()).isPositive();
        assertThat(txInfo.tx()).isEqualTo(tx);
    }

    @Test
    void burnTransactionInfo() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        AssetId assetId = node.waitForTransaction(node.broadcast(
                IssueTransaction.builder("Asset", 1000, 2).getSignedWith(alice)).id(),
                IssueTransactionInfo.class).tx().assetId();
        BurnTransaction tx = BurnTransaction.builder(Amount.of(100, assetId)).getSignedWith(alice);
        node.waitForTransaction(node.broadcast(tx).id());

        TransactionInfo commonInfo = node.getTransactionInfo(tx.id());
        BurnTransactionInfo txInfo = node.getTransactionInfo(tx.id(), BurnTransactionInfo.class);

        assertThat(commonInfo).isInstanceOf(BurnTransactionInfo.class);
        assertThat(commonInfo.tx()).isInstanceOf(BurnTransaction.class);
        assertThat(txInfo).isEqualTo(commonInfo);
        assertThat(txInfo.height()).isPositive();
        assertThat(txInfo.tx()).isEqualTo(tx);
    }

    @Test
    void exchangeTransactionInfo() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);
        PrivateKey bob = createAccountWithBalance(10_00000000);

        AssetId assetId = node.waitForTransaction(node.broadcast(
                IssueTransaction.builder("Asset", 1000, 2).getSignedWith(alice)).id(),
                IssueTransactionInfo.class).tx().assetId();

        Amount amount = Amount.of(1);
        Amount price = Amount.of(100, assetId);
        long matcherFee = 300000;
        Order buy = Order.builder(OrderType.BUY, amount, price, alice.publicKey()).getSignedWith(alice);
        Order sell = Order.builder(OrderType.SELL, amount, price, alice.publicKey()).getSignedWith(bob);

        ExchangeTransaction tx = ExchangeTransaction
                .builder(buy, sell, amount.value(), price.value(), matcherFee, matcherFee).getSignedWith(alice);
        node.waitForTransaction(node.broadcast(tx).id());

        TransactionInfo commonInfo = node.getTransactionInfo(tx.id());
        ExchangeTransactionInfo txInfo = node.getTransactionInfo(tx.id(), ExchangeTransactionInfo.class);

        assertThat(commonInfo).isInstanceOf(ExchangeTransactionInfo.class);
        assertThat(commonInfo.tx()).isInstanceOf(ExchangeTransaction.class);
        assertThat(txInfo).isEqualTo(commonInfo);
        assertThat(txInfo.height()).isPositive();
        assertThat(txInfo.tx()).isEqualTo(tx);
    }

    @Test
    void leaseTransactionInfo() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);
        Address bob = createAccountWithBalance().address();

        LeaseTransaction tx = LeaseTransaction.builder(bob, 1000).getSignedWith(alice);
        node.waitForTransaction(node.broadcast(tx).id());

        TransactionInfo commonInfo = node.getTransactionInfo(tx.id());
        LeaseTransactionInfo txInfo = node.getTransactionInfo(tx.id(), LeaseTransactionInfo.class);

        assertThat(commonInfo).isInstanceOf(LeaseTransactionInfo.class);
        assertThat(commonInfo.tx()).isInstanceOf(LeaseTransaction.class);
        assertThat(txInfo).isEqualTo(commonInfo);
        assertThat(txInfo.height()).isPositive();
        assertThat(txInfo.tx()).isEqualTo(tx);
        assertThat(txInfo.status()).isEqualTo(LeaseStatus.ACTIVE);
    }

    @Test
    void leaseCancelTransactionInfo() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);
        Address bob = createAccountWithBalance().address();

        long amount = 1000;

        LeaseTransaction leaseTx = LeaseTransaction.builder(bob, amount).getSignedWith(alice);
        int leaseHeight = node.waitForTransaction(node.broadcast(leaseTx).id()).height();

        LeaseCancelTransaction tx = LeaseCancelTransaction.builder(leaseTx.id()).getSignedWith(alice);
        node.waitForTransaction(node.broadcast(tx).id());

        TransactionInfo commonInfo = node.getTransactionInfo(tx.id());
        LeaseCancelTransactionInfo txInfo = node.getTransactionInfo(tx.id(), LeaseCancelTransactionInfo.class);

        assertThat(commonInfo).isInstanceOf(LeaseCancelTransactionInfo.class);
        assertThat(commonInfo.tx()).isInstanceOf(LeaseCancelTransaction.class);
        assertThat(txInfo).isEqualTo(commonInfo);
        assertThat(txInfo.height()).isPositive();
        assertThat(txInfo.tx()).isEqualTo(tx);
        //TODO wait fix in Node 1.3.5
//        assertThat(txInfo.leaseInfo()).isEqualTo(new LeaseInfo(
//                leaseTx.id(), leaseTx.id(), alice.address(), bob, amount, txInfo.height(), CANCELED, leaseHeight, tx.id()));
    }

    @Test
    void createAliasTransactionInfo() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        Alias alias = Alias.as("alice_" + System.currentTimeMillis());

        CreateAliasTransaction tx = CreateAliasTransaction.builder(alias.toString()).getSignedWith(alice);
        node.waitForTransaction(node.broadcast(tx).id());

        TransactionInfo commonInfo = node.getTransactionInfo(tx.id());
        CreateAliasTransactionInfo txInfo = node.getTransactionInfo(tx.id(), CreateAliasTransactionInfo.class);

        assertThat(commonInfo).isInstanceOf(CreateAliasTransactionInfo.class);
        assertThat(commonInfo.tx()).isInstanceOf(CreateAliasTransaction.class);
        assertThat(txInfo).isEqualTo(commonInfo);
        assertThat(txInfo.height()).isPositive();
        assertThat(txInfo.tx()).isEqualTo(tx);
    }

    @Test
    void massTransferTransactionInfo() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);
        Address bob = createAccountWithBalance().address();

        MassTransferTransaction tx = MassTransferTransaction.builder(Transfer.to(bob, 1000)).getSignedWith(alice);
        node.waitForTransaction(node.broadcast(tx).id());

        TransactionInfo commonInfo = node.getTransactionInfo(tx.id());
        MassTransferTransactionInfo txInfo = node.getTransactionInfo(tx.id(), MassTransferTransactionInfo.class);

        assertThat(commonInfo).isInstanceOf(MassTransferTransactionInfo.class);
        assertThat(commonInfo.tx()).isInstanceOf(MassTransferTransaction.class);
        assertThat(txInfo).isEqualTo(commonInfo);
        assertThat(txInfo.height()).isPositive();
        assertThat(txInfo.tx()).isEqualTo(tx);
    }

    @Test
    void dataTransactionInfo() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        DataTransaction tx = DataTransaction.builder(StringEntry.as("str", alice.address().toString())).getSignedWith(alice);
        node.waitForTransaction(node.broadcast(tx).id());

        TransactionInfo commonInfo = node.getTransactionInfo(tx.id());
        DataTransactionInfo txInfo = node.getTransactionInfo(tx.id(), DataTransactionInfo.class);

        assertThat(commonInfo).isInstanceOf(DataTransactionInfo.class);
        assertThat(commonInfo.tx()).isInstanceOf(DataTransaction.class);
        assertThat(txInfo).isEqualTo(commonInfo);
        assertThat(txInfo.height()).isPositive();
        assertThat(txInfo.tx()).isEqualTo(tx);
    }

    @Test
    void setScriptTransactionInfo() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        Base64String script = node.compileScript("{-# SCRIPT_TYPE ACCOUNT #-} true").script();
        SetScriptTransaction tx = SetScriptTransaction.builder(script).getSignedWith(alice);
        node.waitForTransaction(node.broadcast(tx).id());

        TransactionInfo commonInfo = node.getTransactionInfo(tx.id());
        SetScriptTransactionInfo txInfo = node.getTransactionInfo(tx.id(), SetScriptTransactionInfo.class);

        assertThat(commonInfo).isInstanceOf(SetScriptTransactionInfo.class);
        assertThat(commonInfo.tx()).isInstanceOf(SetScriptTransaction.class);
        assertThat(txInfo).isEqualTo(commonInfo);
        assertThat(txInfo.height()).isPositive();
        assertThat(txInfo.tx()).isEqualTo(tx);
    }

    @Test
    void sponsorFeeTransactionInfo() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        AssetId assetId = node.waitForTransaction(node.broadcast(
                IssueTransaction.builder("Asset", 1000, 2).getSignedWith(alice)).id(),
                IssueTransactionInfo.class).tx().assetId();

        SponsorFeeTransaction tx = SponsorFeeTransaction.builder(assetId, 5).getSignedWith(alice);
        node.waitForTransaction(node.broadcast(tx).id());

        TransactionInfo commonInfo = node.getTransactionInfo(tx.id());
        SponsorFeeTransactionInfo txInfo = node.getTransactionInfo(tx.id(), SponsorFeeTransactionInfo.class);

        assertThat(commonInfo).isInstanceOf(SponsorFeeTransactionInfo.class);
        assertThat(commonInfo.tx()).isInstanceOf(SponsorFeeTransaction.class);
        assertThat(txInfo).isEqualTo(commonInfo);
        assertThat(txInfo.height()).isPositive();
        assertThat(txInfo.tx()).isEqualTo(tx);
    }

    @Test
    void setAssetScriptTransactionInfo() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        Base64String script = node.compileScript("{-# SCRIPT_TYPE ASSET #-} true").script();
        AssetId assetId = node.waitForTransaction(node.broadcast(
                IssueTransaction.builder("Asset", 1000, 2)
                        .script(script).getSignedWith(alice)).id(),
                IssueTransactionInfo.class).tx().assetId();

        SetAssetScriptTransaction tx = SetAssetScriptTransaction.builder(assetId, script).getSignedWith(alice);
        node.waitForTransaction(node.broadcast(tx).id());

        TransactionInfo commonInfo = node.getTransactionInfo(tx.id());
        SetAssetScriptTransactionInfo txInfo = node.getTransactionInfo(tx.id(), SetAssetScriptTransactionInfo.class);

        assertThat(commonInfo).isInstanceOf(SetAssetScriptTransactionInfo.class);
        assertThat(commonInfo.tx()).isInstanceOf(SetAssetScriptTransaction.class);
        assertThat(txInfo).isEqualTo(commonInfo);
        assertThat(txInfo.height()).isPositive();
        assertThat(txInfo.tx()).isEqualTo(tx);
    }

    @Test
    void invokeScriptTransactionInfo() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);
        PrivateKey bob = createAccountWithBalance(10_00000000);

        AssetId assetId = node.waitForTransaction(node.broadcast(
                IssueTransaction.builder("Asset", 1000, 2).getSignedWith(alice)).id(),
                IssueTransactionInfo.class).tx().assetId();

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
                SetScriptTransaction.builder(script).getSignedWith(bob)).id());

        InvokeScriptTransaction tx = InvokeScriptTransaction
                .builder(bob.address(), Function.as("call",
                        BinaryArg.as(alice.address().bytes()),
                        BooleanArg.as(true),
                        IntegerArg.as(100500),
                        StringArg.as(alice.address().toString()),
                        ListArg.as(IntegerArg.as(100500))
                )).payments(
                        Amount.of(1, assetId),
                        Amount.of(2, assetId),
                        Amount.of(3, assetId),
                        Amount.of(4, assetId),
                        Amount.of(5, assetId),
                        Amount.of(6, assetId),
                        Amount.of(7, assetId),
                        Amount.of(8, assetId),
                        Amount.of(9, assetId),
                        Amount.of(10, assetId)
                ).extraFee(1_00000000)
                .getSignedWith(alice);
        node.waitForTransaction(node.broadcast(tx).id());

        TransactionInfo commonInfo = node.getTransactionInfo(tx.id());
        InvokeScriptTransactionInfo txInfo = node.getTransactionInfo(tx.id(), InvokeScriptTransactionInfo.class);

        assertThat(commonInfo).isInstanceOf(InvokeScriptTransactionInfo.class);
        assertThat(commonInfo.tx()).isInstanceOf(InvokeScriptTransaction.class);
        assertThat(txInfo).isEqualTo(commonInfo);
        assertThat(txInfo.height()).isPositive();
        assertThat(txInfo.tx()).isEqualTo(tx);
    }

    @Test
    void updateAssetInfoTransactionInfo() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        AssetId assetId = node.waitForTransaction(node.broadcast(
                IssueTransaction.builder("Asset", 1000, 2).getSignedWith(alice)).id(),
                IssueTransactionInfo.class).tx().assetId();

        node.waitBlocks(2);

        UpdateAssetInfoTransaction tx = UpdateAssetInfoTransaction
                .builder(assetId, "New Asset", "New description").getSignedWith(alice);
        node.waitForTransaction(node.broadcast(tx).id());

        TransactionInfo commonInfo = node.getTransactionInfo(tx.id());
        UpdateAssetInfoTransactionInfo txInfo = node.getTransactionInfo(tx.id(), UpdateAssetInfoTransactionInfo.class);

        assertThat(commonInfo).isInstanceOf(UpdateAssetInfoTransactionInfo.class);
        assertThat(commonInfo.tx()).isInstanceOf(UpdateAssetInfoTransaction.class);
        assertThat(txInfo).isEqualTo(commonInfo);
        assertThat(txInfo.height()).isPositive();
        assertThat(txInfo.tx()).isEqualTo(tx);
    }

    @Test
    void multipleTransactionsInfo() throws NodeException, IOException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        Id issueTxId = node.waitForTransaction(
                node.broadcast(IssueTransaction.builder("Asset", 1000, 2).getSignedWith(alice))
        ).tx().id();

        Id aliasTxId = node.waitForTransaction(
                node.broadcast(CreateAliasTransaction.builder("alice").getSignedWith(alice))
        ).tx().id();

        List<TransactionInfo> txsInfo = node.getTransactionsInfo(asList(issueTxId, aliasTxId));

        assertThat(txsInfo).hasSize(2);
        assertThat(txsInfo).hasAtLeastOneElementOfType(IssueTransactionInfo.class);
        assertThat(txsInfo).hasAtLeastOneElementOfType(CreateAliasTransactionInfo.class);
    }

    @Test
    void multipleTransactionsInfoWithSpecifiedType() throws NodeException, IOException {
        PrivateKey alice = createAccountWithBalance(10_00000000);

        Id aliasTxId1 = node.waitForTransaction(
                node.broadcast(CreateAliasTransaction.builder("alice1").getSignedWith(alice))
        ).tx().id();

        Id aliasTxId2 = node.waitForTransaction(
                node.broadcast(CreateAliasTransaction.builder("alice2").getSignedWith(alice))
        ).tx().id();

        Id issueTxId = node.waitForTransaction(
                node.broadcast(IssueTransaction.builder("Asset", 1000, 2).getSignedWith(alice))
        ).tx().id();

        List<CreateAliasTransactionInfo> txsInfo = node.getTransactionsInfo(asList(aliasTxId1, aliasTxId2), CreateAliasTransactionInfo.class);

        assertThat(txsInfo).hasSize(2);
        assertThat(txsInfo).hasOnlyElementsOfType(CreateAliasTransactionInfo.class);

        assertThrows(ClassCastException.class, () ->
                node.getTransactionsInfo(asList(aliasTxId1, issueTxId), CreateAliasTransactionInfo.class),
                "Cannot cast com.wavesplatform.wavesj.info.IssueTransactionInfo to com.wavesplatform.wavesj.info.CreateAliasTransactionInfo");
    }

}
