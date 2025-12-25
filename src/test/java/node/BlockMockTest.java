package node;

import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.exceptions.NodeException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static node.mock.util.MockHttpRsUtil.mockGBlockHeadersRs;
import static node.mock.util.MockHttpRsUtil.mockHttpClient;
import static org.junit.jupiter.api.Assertions.*;

public class BlockMockTest {
    private final Node node = new Node(Profile.LOCAL, mockHttpClient("src/test/resources/stub/addressesD.json"));

    private static final byte CHAIN_ID = 'D';

    public BlockMockTest() throws NodeException, IOException {
    }

    @Test
    void readBlocksHeadersWithFinalizationVoting() throws NodeException, IOException {
        mockGBlockHeadersRs(node,
                16,
                "src/test/resources/stub/blocks/block_with_voting.json"
        );

        BlockHeaders bh = node.getBlockHeaders(16);

        // ===== base fields =====
        assertEquals(5, bh.version());
        assertEquals(1766555613090L, bh.timestamp());
        assertEquals("6mb8A1dnf8zdvkuCHbqPi8qukALDm97in9wfiNFcbHNq", bh.reference().encoded());
        assertEquals("AdHHXpTvCPxQw6Ge57BaPpkLKHrVmAy4VBv2UPR3rzBu", bh.transactionsRoot().encoded());
        assertEquals("9xhR5xULkazyC5Po7bPfTHV8APbrH72N2KHfkV4nYDXi", bh.id().encoded());

        assertEquals(-1, bh.desiredReward());
        assertEquals("3FZs7eqZSmG56AqfTdWj2wJ1NWTrUZNgS6Q", bh.generator().toString());

        assertEquals(
                "67HpHJnrEwJuqvn44JjgHJy5TuRLXfu4kAi1fujVHq8y356JFTmLs1LXJciBpJAbTRV9jkxZYGsUNfzS7G9i5LVN",
                bh.signature().encoded()
        );

        assertEquals(1068, bh.size());
        assertEquals(3, bh.transactionsCount());
        assertEquals(16, bh.height());
        assertEquals(300_000L, bh.totalFee());
        assertEquals(6_000_000_000L, bh.reward());

        assertEquals(
                "5VjPUXb4NU9QQFoTsEET2xM9ncJfVzjQYZwpUQENStMH",
                bh.vrf().encoded()
        );

        assertEquals(0, bh.features().size());
        assertEquals(20120L, bh.baseTarget());
        assertEquals(
                "jUqHiYP7E7LZMS8facqNrjp9m6jLB31TjmCxX3aqubyApK9q6qEBUbqapXi6X38oYjYsW1qBA7rTmAo4GvTC8vSDVRWeLrwfxwsQCgUV7MNyqcMt3efJD6bHuuyUh7yfVie",
                bh.generationSignature().encoded()
        );

        FinalizationVoting fv = bh.finalizationVoting();
        assertEquals(14, fv.getFinalizedHeight());
        assertEquals(List.of(1, 3), fv.getEndorserIndexes());
        assertEquals(
                "xgHXeGVftVtfxEZhRrfC5Q3pb8DajoGnFed7PxDBC9JegLuW7e1jYjh9QXzU9AeDSB4dShS8id9oThaKwgG4wddevXbiBwenuVFdRXUM3hyqbkcgMCnDLexhZtWhxfrs7AU",
                fv.getAggregatedEndorsementSignature().encoded()
        );

        assertEquals(1, fv.getConflictEndorsements().size());
        ConflictEndorsement ce = fv.getConflictEndorsements().get(0);
        assertEquals(2, ce.getEndorserIndex());
        assertEquals(
                "3LKirJGRvMcdaachGNEuWVSyGbpzWKcagrjsQQuaYXwq",
                ce.getFinalizedBlockId().encoded()
        );
        assertEquals(13, ce.getFinalizedHeight());
        assertEquals(
                "omjkPiJX473jCxnScb5XXwi7MRf8Sb4Wom18X68eFodkMuSv3ztNK8fW3886cSqEDJ9PwWV9goJ4H6EPMSkoZmFNSkfnbZzRwrbrCdYFrjDgWzxw91tyWLtX7F2xKAcHsQq",
                ce.getSignature().encoded()
        );

        assertEquals("BCuRAvrPqngYBKvJ3HKERz3nZZQRKEUMY81FJCEQPfNV",
                bh.generatorPublicKey().encoded());
        assertEquals("HwLttQ7xwStDavkXbBtfaeUy3QoKJMbCbTkNEezivEL6",
                bh.stateHash().encoded());
        assertEquals(3, bh.rewardShares().size());
    }

    @Test
    public void readChallengedBlocksHeaders() throws IOException, NodeException {
        mockGBlockHeadersRs(node,
                42,
                "src/test/resources/stub/blocks/challenged_block.json"
        );

        BlockHeaders bh = node.getBlockHeaders(42);
        assertEquals(5, bh.version());
        assertEquals(42, bh.height());
        assertNotNull(bh.generatorPublicKey());
        assertNotNull(bh.stateHash());
        assertNotNull(bh.rewardShares());
        assertEquals(2, bh.rewardShares().size());

        ChallengedHeader ch = bh.challengedHeader();
        assertNotNull(ch);
        assertEquals(
                "52SXMJphW45QdshxfYUxH7w6FhXmGXU2zTmW6mDLvcaWVigKjtufQT7gTJ3gimKFx8mwKyGuTtzoMDf6CYWymjLE",
                ch.getHeaderSignature().encoded()
        );

        assertNotNull(ch.getFeatures());
        assertTrue(ch.getFeatures().isEmpty());
        assertEquals(
                "3Fgtiv5L5q4CXFfwuAfbkjy9ppehkNzjbEG",
                ch.getGenerator().encoded()
        );
        assertEquals(
                "5FUsFwB6b8mE61VMbSB6X7m23NTy4yN8f1fApXo8tZs7",
                ch.getGeneratorPublicKey().encoded()
        );
        assertEquals(-1L, ch.getDesiredReward());
        assertEquals(
                "11111111111111111111111111111111",
                ch.getStateHash().encoded()
        );
        FinalizationVoting fv = ch.getFinalizationVoting();
        assertNotNull(fv);
        assertEquals(List.of(2, 1), fv.getEndorserIndexes());
        assertEquals(
                "yEuHinUSJCG8LdNtvtstKp7edVGrwMmGQQ8qf6ghWc8BB9oiJryCMVmsBQrAJQZX7i8cC1d5mqH2jYLJT2QeJ7QSbMGBAWueVcYhUzpoPtwvtG5NxZUcvTsRUjiZEWPT7uD",
                fv.getAggregatedEndorsementSignature().encoded()
        );
        assertEquals(40, fv.getFinalizedHeight());
    }
}
