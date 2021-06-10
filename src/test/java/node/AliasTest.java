package node;

import base.BaseTestWithNodeInDocker;
import com.wavesplatform.transactions.CreateAliasTransaction;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.Alias;
import com.wavesplatform.wavesj.exceptions.NodeException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class AliasTest extends BaseTestWithNodeInDocker {

    @Test
    void aliases() throws IOException, NodeException {
        PrivateKey alice = createAccountWithBalance(CreateAliasTransaction.MIN_FEE);
        Alias alias = Alias.as("alias-" + System.currentTimeMillis());

        node.waitForTransaction(node.broadcast(
                CreateAliasTransaction.builder(alias.toString()).getSignedWith(alice)).id());

        assertThat(node.getAliasesByAddress(alice.address())).containsExactly(alias);
        assertThat(node.getAddressByAlias(alias)).isEqualTo(alice.address());
    }

}
