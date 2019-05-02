package com.wavesplatform.wavesj;

import org.apache.http.annotation.NotThreadSafe;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.runners.MethodSorters.NAME_ASCENDING;

@FixMethodOrder(NAME_ASCENDING)
@NotThreadSafe
public abstract class BaseITest {

    public static Predicate<Long, Long> EQUALS = new Equals<Long>();
    public static long DEFAULT_TIMEOUT = 120 * 1000;

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseITest.class);

    protected static boolean cleanUp = false;

    private static List<PrivateKeyAccount> allocatedAccounts = Collections.synchronizedList(new ArrayList<PrivateKeyAccount>(100));

    protected byte chainId = Config.getChainId();
    protected Node node = Config.getNode();
    protected PrivateKeyAccount benzAcc = Config.getBenzAcc();

    @Before
    public void setUp() {
        cleanUp = false;
    }

    @After
    public void cleanUp() throws Exception {
        if (cleanUp) {
            Thread.sleep(10 * 1000);
            // always use 0.005 fee because - can be improved in future
            for (PrivateKeyAccount acc : allocatedAccounts) {
                returnToBenzAcc(acc, Asset.toWavelets(0.005));
            }
        }
    }

    /**
     * Because of inheritance this methods will be always last
     */
    @Test
    public void enableCleanUp() {
        cleanUp = true;
    }

    protected PrivateKeyAccount generateAcc(String name, boolean cleanUpAfterTests) {
        String seed = PrivateKeyAccount.generateSeed();
        PrivateKeyAccount acc = PrivateKeyAccount.fromSeed(seed, 0, chainId);
        LOGGER.info("{} generated: address={} public={} private={} seed={}",
                name, acc.getAddress(), Base58.encode(acc.getPublicKey()), Base58.encode(acc.getPrivateKey()), seed);
        if (cleanUpAfterTests) {
            allocatedAccounts.add(acc);
        }
        return acc;
    }

    protected long waitOnBalance(String addressToCheck, long initialBalance, long amountToWait, Predicate<Long, Long> predicate, long timeToWait)
            throws IOException {
        long remainingTime = timeToWait;
        long pause = 1000;
        int pauseCounter = 0;
        long expectedBalance = initialBalance + amountToWait;
        long actualBalance;

        try {
            do {
                long start = System.currentTimeMillis();
                if (remainingTime != timeToWait) {
                    if (pauseCounter % 10 == 0) {
                        LOGGER.info("Waiting balance CONTINUED: address={} initialBalance={} amountToWait={} time_to_wait={}",
                                addressToCheck, initialBalance, amountToWait, formatInterval(remainingTime));
                    }
                    Thread.sleep(pause);
                    pauseCounter++;
                }

                actualBalance = node.getBalance(addressToCheck);
                remainingTime -= (System.currentTimeMillis() - start);
            } while (!predicate.test(actualBalance, expectedBalance) && remainingTime > 0);
        } catch (InterruptedException ex) {
            LOGGER.warn("Application shutdown has been initiated: waitMoneyOnBalance was interrupted: address={} initialBalance={} amountToWait={}",
                    addressToCheck, initialBalance, amountToWait);
            throw new IOException("Application shutdown has been initiated: waitMoneyOnBalance was interrupted");
        }

        if (remainingTime < 0) {
            LOGGER.error("Waited time is greater than specified timeout - waitMoneyOnBalance was interrupted: address={} initialBalance={} amountToWait={} timeToWait={}",
                    addressToCheck, initialBalance, amountToWait, timeToWait);
            throw new RuntimeException("Waited time is greater than specified timeout " + timeToWait + " seconds");
        }

        return actualBalance;
    }

    protected void returnToBenzAcc(PrivateKeyAccount fromAcc, long fee) {
        long returnAmt = -1;
        try {
            if (fromAcc != null) {
                long availableAmt = node.getBalance(fromAcc.getAddress());
                if (availableAmt > fee) {
                    returnAmt = availableAmt - fee;
                    LOGGER.info("Return to benz START: amount={} from_address={} benz_address={}",
                            Asset.fromWavelets(returnAmt), fromAcc.getAddress(), benzAcc.getAddress());
                    node.transfer(fromAcc, benzAcc.getAddress(), returnAmt, fee, "");
                    LOGGER.info("Return to benz SUCCESS: amount={} from_address={} benz_address={}",
                            Asset.fromWavelets(returnAmt), fromAcc.getAddress(), benzAcc.getAddress());
                } else {
                    LOGGER.warn("Return to benz SKIPPED - fee is greater than available funds: available={} fee={} from_address={} benz_address={}",
                            Asset.fromWavelets(availableAmt), Asset.fromWavelets(fee), fromAcc.getAddress(), benzAcc.getAddress());
                }
            }
        } catch (Throwable ex) {
            LOGGER.warn("Return to benz ERROR: amount={} from_address={} benz_address={} msg={}",
                    Asset.fromWavelets(returnAmt), fromAcc.getAddress(), benzAcc.getAddress(), ex.getMessage());
        }
    }

    public static String formatInterval(long millis) {
        long sec = millis / 1000;
        long min = sec / 60;
        sec = sec - min * 60;
        long hr = min / 60;
        min = min - hr * 60;
        return String.format("%02d:%02d:%02d", hr, min, sec);
    }

    // used to prevent multiple initializations
    public static class Config {
        private static byte chainId = Account.TESTNET;
        private static Node node = new Node();
        private static PrivateKeyAccount benzAcc;

        public static byte getChainId() {
            return chainId;
        }

        public static Node getNode() {
            return node;
        }

        public static PrivateKeyAccount getBenzAcc() {
            if (benzAcc == null) {
                String benzPrivateKey = System.getProperty("benzPrivateKey");
                if (benzPrivateKey == null) {
                    throw new IllegalStateException("Please pass -DbenzPrivateKey=yourTestnetPrivateKey as a build option to run tests");
                }

                benzAcc = PrivateKeyAccount.fromPrivateKey(benzPrivateKey, chainId);
                LOGGER.info("Benz Account: address={} public={} private={}",
                        benzAcc.getAddress(), Base58.encode(benzAcc.getPublicKey()), Base58.encode(benzAcc.getPrivateKey()));
            }
            return benzAcc;
        }
    }

    public interface Predicate<T, U> {
        boolean test(T t, U u);
    }

    public static class Equals<T extends Comparable<T>> implements Predicate<T, T> {
        @Override
        public boolean test(T b1, T b2) {
            return b1 != null && b2 != null && b1.compareTo(b2) == 0;
        }
    }

    public static class Greater<T extends Comparable<T>> implements Predicate<T, T> {
        @Override
        public boolean test(T b1, T b2) {
            return b1 != null && b2 != null && b1.compareTo(b2) > 0;
        }
    }

    public static class Less<T extends Comparable<T>> implements Predicate<T, T> {
        @Override
        public boolean test(T b1, T b2) {
            return b1 != null && b2 != null && b1.compareTo(b2) < 0;
        }
    }
}
