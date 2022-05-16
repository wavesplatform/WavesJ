package node;

import base.BaseTestWithNodeInDocker;
import com.wavesplatform.wavesj.BlockchainRewards;
import com.wavesplatform.wavesj.exceptions.NodeException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class BlockchainTest extends BaseTestWithNodeInDocker {

    @Test
    void rewards() throws IOException, NodeException {
        int height = node.getHeight();
        BlockchainRewards rewards = node.getBlockchainRewards();

        assertThat(node.getBlockchainRewards(rewards.height())).isEqualTo(rewards);
        assertThat(node.getBlockchainRewards(height - 1)).isNotEqualTo(rewards);

        assertThat(rewards.height()).isBetween(height, height + 10);
        assertThat(rewards.totalWavesAmount()).isGreaterThan(100000000_00000000L);
        assertThat(rewards.currentReward()).isBetween(5_00000000L, 7_00000000L);
        assertThat(rewards.minIncrement()).isEqualTo(50000000);
        assertThat(rewards.term()).isEqualTo(6);
        assertThat(rewards.nextCheck()).isGreaterThan(rewards.votingIntervalStart());
        assertThat(rewards.votingInterval()).isEqualTo(3);
        assertThat(rewards.votingThreshold()).isEqualTo(2);
        assertThat(asList(rewards.votes().increase(), rewards.votes().decrease())).contains(0);
    }

}
