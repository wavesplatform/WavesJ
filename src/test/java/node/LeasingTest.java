package node;

import base.BaseTestWithNodeInDocker;
import com.wavesplatform.transactions.InvokeScriptTransaction;
import com.wavesplatform.transactions.LeaseCancelTransaction;
import com.wavesplatform.transactions.LeaseTransaction;
import com.wavesplatform.transactions.SetScriptTransaction;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.Base64String;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.transactions.invocation.Function;
import com.wavesplatform.transactions.invocation.IntegerArg;
import com.wavesplatform.transactions.invocation.StringArg;
import com.wavesplatform.wavesj.LeaseStatus;
import com.wavesplatform.wavesj.StateChanges;
import com.wavesplatform.wavesj.actions.LeaseCancelInfo;
import com.wavesplatform.wavesj.actions.LeaseInfo;
import com.wavesplatform.wavesj.info.TransactionInfo;
import com.wavesplatform.wavesj.exceptions.NodeException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LeasingTest extends BaseTestWithNodeInDocker {

    //TODO getLeaseInfo(id)
    //TODO getLeaseInfo(ids)
    //TODO getActiveLeases()

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
        printTxInfo("INVOKE LEASE", invokeTx.tx().id());
        LeaseInfo stateChangesLease = node.getStateChanges(invokeTx.tx().id()).stateChanges().leases().get(0);

        // get info

        LeaseInfo leasing = node.getLeaseInfo(leaseTx.tx().id());
        LeaseInfo invokeLeasing = node.getLeaseInfo(stateChangesLease.id());
        List<LeaseInfo> leasingList = node.getLeasesInfo(leaseTx.tx().id(), stateChangesLease.id());
        List<LeaseInfo> activeLeases = node.getActiveLeases(alice.address());

        // assert active leasing

        assertThat(leasing).isEqualTo(new LeaseInfo(
                leaseTx.tx().id(), leaseTx.tx().id(), alice.address(), bob.address(),
                leaseAmount, leaseTx.height(), LeaseStatus.ACTIVE));

        assertThat(invokeLeasing).isEqualTo(new LeaseInfo(
                stateChangesLease.id(), invokeTx.tx().id(), bob.address(), alice.address(),
                invokeLeaseAmount, invokeTx.height(), LeaseStatus.ACTIVE));
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
        printTxInfo("INVOKE CANCEL", invokeCancelTx.tx().id());
        LeaseInfo stateChangesCancel = node.getStateChanges(invokeCancelTx.tx().id()).stateChanges().leaseCancels().get(0);

        // get info

        LeaseInfo leasingCancel = node.getLeaseInfo(leaseTx.tx().id());
        LeaseInfo invokeLeasingCancel = node.getLeaseInfo(stateChangesLease.id());
        List<LeaseInfo> leasingListCancel = node.getLeasesInfo(leaseTx.tx().id(), stateChangesLease.id());
        List<LeaseInfo> activeLeasesCancel = node.getActiveLeases(alice.address());

        // assert canceled leasing

        assertThat(leasingCancel).isEqualTo(new LeaseCancelInfo(
                leaseTx.tx().id(), leaseTx.tx().id(), alice.address(), bob.address(),
                leaseAmount, leaseTx.height(), LeaseStatus.CANCELED,
                leaseCancelTx.height(), leaseCancelTx.tx().id()));

        assertThat(invokeLeasingCancel).isEqualTo(new LeaseCancelInfo(
                stateChangesLease.id(), invokeTx.tx().id(), bob.address(), alice.address(),
                invokeLeaseAmount, invokeTx.height(), LeaseStatus.CANCELED,
                stateChangesCancel.height(), invokeCancelTx.tx().id()));
        assertThat(invokeLeasingCancel).isEqualTo(stateChangesCancel);

        assertThat(leasingList).containsExactlyInAnyOrder(leasingCancel, invokeLeasingCancel);
        assertThat(activeLeases).isEmpty();
    }

    @Test
    void canceledLeaseInfo() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(1000000);
        PrivateKey bob = createAccountWithBalance(1000000);

        Base64String dAppScript = node.compileScript(
                "{-# STDLIB_VERSION 5 #-}\n{-# CONTENT_TYPE DAPP #-}\n{-# SCRIPT_TYPE ACCOUNT #-}\n" +
                        "@Callable(inv)\nfunc lease(amount: Int) = [Lease(inv.caller, amount)]").script();
        node.waitForTransaction(node.broadcast(
                SetScriptTransaction.builder(dAppScript).getSignedWith(bob)).id());

        LeaseTransaction leaseTx = LeaseTransaction.builder(bob.address(), 10000).getSignedWith(alice);
        node.waitForTransaction(node.broadcast(leaseTx).id()).tx().id();

        LeaseInfo leasing = node.getLeaseInfo(leaseTx.id());

        TransactionInfo txInBlock = node.getBlock(leasing.height())
                .transactions().stream()
                .filter(t -> t.tx().id().equals(leaseTx.id()))
                .findFirst().orElseThrow(AssertionError::new);
        TransactionInfo txInBlocksSeq = node.getBlocks(leasing.height(), leasing.height())
                .get(0).transactions().stream()
                .filter(t -> t.tx().id().equals(leaseTx.id()))
                .findFirst().orElseThrow(AssertionError::new);
    }

    //TODO lease from invoke + inner

    //TODO check everywhere: in blocks, addresses, assets, transactions

}
