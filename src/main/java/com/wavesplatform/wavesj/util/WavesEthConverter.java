package com.wavesplatform.wavesj.util;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.common.ChainId;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.exceptions.NodeException;
import org.apache.http.client.HttpClient;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.utils.Numeric;

import java.io.IOException;

import static com.wavesplatform.crypto.Hash.blake;
import static com.wavesplatform.crypto.Hash.keccak;
import static com.wavesplatform.crypto.base.Base58.encode;
import static java.util.Arrays.copyOfRange;
import static org.apache.commons.lang3.ArrayUtils.addAll;
import static org.bouncycastle.util.encoders.Hex.decode;

public class WavesEthConverter {

    public static String wavesToEthAddress(String address) {
        byte[] wavesAddress = Base58.decode(address);
        byte[] ethAddress = copyOfRange(wavesAddress, 2, 22);
        return Numeric.toHexString(ethAddress);
    }

    public static String ethToWavesAddress(String address, byte chainId) {
        byte[] pkHash = copyOfRange(decode(address.substring(2)), 0, 20);
        byte[] prefixBytes = new byte[]{0x01, chainId};
        byte[] checkSumBytes = addAll(prefixBytes, pkHash);
        byte[] checkSum = keccak(blake(checkSumBytes));
        byte[] wavesBytes = addAll(
                addAll(prefixBytes, pkHash),
                copyOfRange(checkSum, 0, 4)
        );
        return encode(wavesBytes);
    }

    public static String wavesToEthAsset(String asset) {
        byte[] decode = copyOfRange(Base58.decode(asset), 0, 20);
        return "0x" + Hex.toHexString(decode);
    }

    public static String ethToWavesAsset(Node node, String asset) throws NodeException, IOException {
        return node.ethToWavesAsset(asset);
    }

}
