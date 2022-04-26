package com.wavesplatform.wavesj.util;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.common.ChainId;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.utils.Numeric;

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

    public static String ethToWavesAddress(String address) {
        byte[] pkHash = copyOfRange(decode(address.substring(2)), 0, 20);
        byte[] prefixBytes = new byte[]{0x01, ChainId.STAGENET};
        byte[] checkSumBytes = addAll(prefixBytes, pkHash);
        byte[] checkSum = keccak(blake(checkSumBytes));
        byte[] wavesBytes = addAll(
                addAll(prefixBytes, pkHash),
                copyOfRange(checkSum, 0, 4)
        );
        return encode(wavesBytes);
    }

    public static String wavesToEthAsset(String address) {
        byte[] decode = copyOfRange(Base58.decode(address), 0, 20);
        return "0x" + Hex.toHexString(decode);
    }

    public static String ethToWavesAsset(String address) {
        //todo convert asset from eth to waves format
        //https://nodes-stagenet.wavesnodes.com/eth/assets?id=0x7a087b3384447a48393eda243e630b07db443597
        // read attribute from response. example "assetId": "9DNEvLFSSnSSaNCb5WEYMz64hsadDjx1THZw3z2hiyJe"
        throw new UnsupportedOperationException("ethToWavesAsset method is not supported yet");
    }

}
