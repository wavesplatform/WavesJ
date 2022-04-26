package util;

import com.wavesplatform.wavesj.util.HashUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HashUtilTest {

    @Test
    void fastHashTest() {
        assertEquals("E4tJDXwEuaSB64YvaqLwuFn77XUcBBYMPryGCghSWEjS", HashUtil.fastHash("string"));
    }

    @Test
    void secureHashTest() {
        assertEquals("8Ljq3LQZyA99SSx9JD5hA3AjrqhAeJf7WhiKE3hioVXS", HashUtil.secureHash("string"));
    }
}
