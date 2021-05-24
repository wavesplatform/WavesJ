package node;

import base.BaseTestWithNodeInDocker;
import com.wavesplatform.transactions.InvokeScriptTransaction;
import com.wavesplatform.transactions.LeaseCancelTransaction;
import com.wavesplatform.transactions.LeaseTransaction;
import com.wavesplatform.transactions.SetScriptTransaction;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.Base64String;
import com.wavesplatform.transactions.invocation.Function;
import com.wavesplatform.transactions.invocation.IntegerArg;
import com.wavesplatform.transactions.invocation.StringArg;
import com.wavesplatform.wavesj.ApplicationStatus;
import com.wavesplatform.wavesj.LeaseStatus;
import com.wavesplatform.wavesj.LeaseInfo;
import com.wavesplatform.wavesj.info.LeaseCancelTransactionInfo;
import com.wavesplatform.wavesj.info.LeaseTransactionInfo;
import com.wavesplatform.wavesj.info.TransactionInfo;
import com.wavesplatform.wavesj.exceptions.NodeException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LeasingTest extends BaseTestWithNodeInDocker {

    @Test
    void leaseInfo() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(10000000);
        PrivateKey bob = createAccountWithBalance(10000000);

        Base64String dAppScript = node.compileScript(
                "{-# STDLIB_VERSION 5 #-}\n{-# CONTENT_TYPE DAPP #-}\n{-# SCRIPT_TYPE ACCOUNT #-}\n" +
                        "@Callable(inv)\nfunc lease(amount: Int) = [Lease(inv.caller, amount)]\n" +
                        "@Callable(inv)\nfunc cancel(leaseId: String) = [LeaseCancel(leaseId.fromBase58String())]").script();
        node.waitForTransaction(node.broadcast(
                SetScriptTransaction.builder(dAppScript).getSignedWith(bob)).id());

        // 1. send leasing

        long leaseAmount = 10000;
        long invokeLeaseAmount = 20000;

        TransactionInfo leaseTx = node.waitForTransaction(node.broadcast(
                LeaseTransaction.builder(bob.address(), leaseAmount).getSignedWith(alice)).id());

        TransactionInfo invokeTx = node.waitForTransaction(node.broadcast(
                InvokeScriptTransaction
                        .builder(bob.address(), Function.as("lease",
                                IntegerArg.as(invokeLeaseAmount)))
                        .getSignedWith(alice)).id());
        LeaseInfo stateChangesLease = node.getStateChanges(invokeTx.tx().id()).stateChanges().leases().get(0);

        // get info

        LeaseInfo leasing = node.getLeaseInfo(leaseTx.tx().id());
        LeaseInfo invokeLeasing = node.getLeaseInfo(stateChangesLease.id());
        List<LeaseInfo> leasingList = node.getLeasesInfo(leaseTx.tx().id(), stateChangesLease.id());
        List<LeaseInfo> activeLeases = node.getActiveLeases(alice.address());

        // assert active leasing

        assertThat(leasing).isEqualTo(new LeaseInfo(
                leaseTx.tx().id(), leaseTx.tx().id(), alice.address(), bob.address(),
                leaseAmount, leaseTx.height(), LeaseStatus.ACTIVE, 0, null));

        assertThat(leasing.cancelHeight()).isNotPresent();
        assertThat(leasing.cancelTransactionId()).isNotPresent();

        assertThat(invokeLeasing).isEqualTo(new LeaseInfo(
                stateChangesLease.id(), invokeTx.tx().id(), bob.address(), alice.address(),
                invokeLeaseAmount, invokeTx.height(), LeaseStatus.ACTIVE, 0, null));
        assertThat(invokeLeasing).isEqualTo(stateChangesLease);

        assertThat(leasingList).containsExactlyInAnyOrder(leasing, invokeLeasing);
        assertThat(activeLeases).containsExactlyInAnyOrder(leasingList.toArray(new LeaseInfo[0]));

        // 2. cancel leasing

        TransactionInfo leaseCancelTx = node.waitForTransaction(node.broadcast(
                LeaseCancelTransaction.builder(leasing.id()).getSignedWith(alice)).id());

        TransactionInfo invokeCancelTx = node.waitForTransaction(node.broadcast(
                InvokeScriptTransaction
                        .builder(bob.address(), Function.as("cancel",
                                StringArg.as(invokeLeasing.id().toString())))
                        .getSignedWith(alice)).id());
        LeaseInfo stateChangesCancel = node.getStateChanges(invokeCancelTx.tx().id()).stateChanges().leaseCancels().get(0);

        // get info

        LeaseInfo leasingCancel = node.getLeaseInfo(leaseTx.tx().id());
        LeaseInfo invokeLeasingCancel = node.getLeaseInfo(stateChangesLease.id());
        List<LeaseInfo> leasingListCancel = node.getLeasesInfo(leaseTx.tx().id(), stateChangesLease.id());
        List<LeaseInfo> activeLeasesCancel = node.getActiveLeases(alice.address());

        // assert canceled leasing

        assertThat(leasingCancel).isEqualTo(new LeaseInfo(
                leaseTx.tx().id(), leaseTx.tx().id(), alice.address(), bob.address(),
                leaseAmount, leaseTx.height(), LeaseStatus.CANCELED,
                leaseCancelTx.height(), leaseCancelTx.tx().id()));

        assertThat(leasingCancel.cancelHeight()).isPresent();
        assertThat(leasingCancel.cancelTransactionId()).isPresent();

        assertThat(invokeLeasingCancel).isEqualTo(new LeaseInfo(
                stateChangesLease.id(), invokeTx.tx().id(), bob.address(), alice.address(),
                invokeLeaseAmount, invokeTx.height(), LeaseStatus.CANCELED,
                stateChangesCancel.height(), invokeCancelTx.tx().id()));
        // TODO bug, should be fixed in Node. Uncomment at Node 1.3.5 release
        //assertThat(invokeLeasingCancel).isEqualTo(stateChangesCancel);

        //assertThat(leasingList).containsExactlyInAnyOrder(leasingCancel, invokeLeasingCancel);
        assertThat(activeLeasesCancel).isEmpty();
    }

    //TODO move to BlocksTest
    @Test
    void block() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(1000000);
        PrivateKey bob = createAccountWithBalance(1000000);

        // 1. lease

        LeaseTransaction leaseTx = LeaseTransaction.builder(bob.address(), 10000).getSignedWith(alice);
        TransactionInfo leaseTxInfo = node.waitForTransaction(node.broadcast(leaseTx).id());

        TransactionInfo leaseTxInBlock = node.getBlock(leaseTxInfo.height())
                .transactions().stream()
                .filter(t -> t.tx().id().equals(leaseTx.id()))
                .findFirst().orElseThrow(AssertionError::new);
        TransactionInfo leaseTxInBlocksSeq = node.getBlocks(leaseTxInfo.height(), leaseTxInfo.height())
                .get(0).transactions().stream()
                .filter(t -> t.tx().id().equals(leaseTx.id()))
                .findFirst().orElseThrow(AssertionError::new);

        assertThat(leaseTxInfo).isEqualTo(leaseTxInBlock);
        assertThat(leaseTxInfo).isEqualTo(leaseTxInBlocksSeq);
        assertThat(leaseTxInBlock).isEqualTo(
                new TransactionInfo(leaseTx, ApplicationStatus.SUCCEEDED, leaseTxInfo.height()));
        assertThat(leaseTxInBlocksSeq).isEqualTo(
                new TransactionInfo(leaseTx, ApplicationStatus.SUCCEEDED, leaseTxInfo.height()));

        // 2. cancel

        LeaseCancelTransaction cancelTx = LeaseCancelTransaction.builder(leaseTx.id()).getSignedWith(alice);
        TransactionInfo cancelTxInfo = node.waitForTransaction(node.broadcast(cancelTx).id());

        TransactionInfo cancelTxInBlock = node.getBlock(cancelTxInfo.height())
                .transactions().stream()
                .filter(t -> t.tx().id().equals(cancelTx.id()))
                .findFirst().orElseThrow(AssertionError::new);
        TransactionInfo cancelTxInBlocksSeq = node.getBlocks(cancelTxInfo.height(), cancelTxInfo.height())
                .get(0).transactions().stream()
                .filter(t -> t.tx().id().equals(cancelTx.id()))
                .findFirst().orElseThrow(AssertionError::new);

        assertThat(cancelTxInfo).isEqualTo(cancelTxInBlock);
        assertThat(cancelTxInfo).isEqualTo(cancelTxInBlocksSeq);
        assertThat(cancelTxInBlock).isEqualTo(
                new TransactionInfo(cancelTx, ApplicationStatus.SUCCEEDED, cancelTxInfo.height()));
        assertThat(cancelTxInBlocksSeq).isEqualTo(
                new TransactionInfo(cancelTx, ApplicationStatus.SUCCEEDED, cancelTxInfo.height()));
    }

}
