package util;

import com.wavesplatform.transactions.common.ChainId;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.Profile;
import com.wavesplatform.wavesj.exceptions.NodeException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.wavesplatform.wavesj.util.WavesEthConverter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class WavesEthConverterTest {

    @Test
    public void convertWavesToEthAddressTest() {
        String ethAddress = wavesToEthAddress("3Mi63XiwniEj6mTC557pxdRDddtpj7fZMMw");
        assertEquals("0xc01187f4ae820a0c956c48c06ee73f85c373cc03", ethAddress);
    }

    @Test
    public void convertEthToWavesAddressTest() {
        String wavesAddress = ethToWavesAddress("0xc01187f4ae820a0c956c48c06ee73f85c373cc03", ChainId.STAGENET);
        assertEquals("3Mi63XiwniEj6mTC557pxdRDddtpj7fZMMw", wavesAddress);
    }

    @Test
    public void convertWavesToEthAssetTest() {
        String wavesToEthAsset = wavesToEthAsset("9DNEvLFSSnSSaNCb5WEYMz64hsadDjx1THZw3z2hiyJe");
        assertEquals("0x7a087b3384447a48393eda243e630b07db443597", wavesToEthAsset);
    }

    @Test
    public void convertEthToWavesAssetTest() throws NodeException, IOException {
        Node node = new Node(Profile.STAGENET);
        String wavesToEthAsset = ethToWavesAsset(node, "0x7a087b3384447a48393eda243e630b07db443597");
        assertEquals("9DNEvLFSSnSSaNCb5WEYMz64hsadDjx1THZw3z2hiyJe", wavesToEthAsset);
    }
}
