package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.wavesplatform.wavesj.*;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.wavesplatform.wavesj.ByteArraysUtils.getOnlyUsed;
import static com.wavesplatform.wavesj.ByteUtils.BytesFormatter.LENGTH_AS_SHORT;
import static com.wavesplatform.wavesj.ByteUtils.*;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

public class InvokeScriptTransaction extends TransactionWithProofs<InvokeScriptTransaction> {
    public static final byte CONTRACT_INVOKE = 16;


    private static final int MAX_TX_SIZE = 5 * KBYTE;
    private byte chainId;
    private PublicKeyAccount senderPublicKey;
    private @JsonProperty("dApp")
    String dApp;
    private FunctionCall call;
    private @JsonProperty("payment")
    List<Payment> payments = new ArrayList<Payment>();
    private long fee;
    private String feeAssetId;
    private long timestamp;

    @JsonCreator
    public InvokeScriptTransaction(@JsonProperty("chainId") byte chainId,
                                   @JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                                   @JsonProperty("dApp") String dApp,
                                   @JsonProperty("call") FunctionCall call,
                                   @JsonProperty("payment") List<Payment> payments,
                                   @JsonProperty("fee") long fee,
                                   @JsonProperty("feeAssetId") String feeAssetId,
                                   @JsonProperty("timestamp") long timestamp,
                                   @JsonProperty("proofs") List<ByteString> proofs) {
        setProofs(proofs);
        this.chainId = chainId;
        this.senderPublicKey = senderPublicKey;
        this.dApp = dApp;
        this.call = call;
        this.payments = payments != null ? payments : new ArrayList<Payment>();
        this.fee = fee;
        this.feeAssetId = feeAssetId;
        this.timestamp = timestamp;
    }

    public InvokeScriptTransaction(byte chainId,
                                   PrivateKeyAccount senderPrivateKey,
                                   String dApp,
                                   FunctionCall call,
                                   List<Payment> payments,
                                   long fee,
                                   String feeAssetId,
                                   long timestamp) {
        this.chainId = chainId;
        this.senderPublicKey = new PublicKeyAccount(senderPrivateKey.getPublicKey(), senderPrivateKey.getChainId());
        this.dApp = dApp;
        this.call = call;
        this.payments = payments;
        this.fee = fee;
        this.feeAssetId = feeAssetId;
        this.timestamp = timestamp;
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(senderPrivateKey.sign(getBodyBytes()))));
    }

    public InvokeScriptTransaction(byte chainId, PrivateKeyAccount senderPrivateKey, String dApp, FunctionCall call,
                                   long fee, String feeAssetId, long timestamp) {
        this.chainId = chainId;
        this.senderPublicKey = new PublicKeyAccount(senderPrivateKey.getPublicKey(), senderPrivateKey.getChainId());
        this.dApp = dApp;
        this.call = call;
        this.fee = fee;
        this.feeAssetId = feeAssetId;
        this.timestamp = timestamp;
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(senderPrivateKey.sign(getBodyBytes()))));
    }

    public InvokeScriptTransaction(byte chainId, PrivateKeyAccount senderPrivateKey, String dApp, String functionName,
                                   long fee, String feeAssetId, long timestamp) {
        this.chainId = chainId;
        this.senderPublicKey = new PublicKeyAccount(senderPrivateKey.getPublicKey(), senderPrivateKey.getChainId());
        this.dApp = dApp;
        this.call = functionName == null ? null : new FunctionCall(functionName);
        this.fee = fee;
        this.feeAssetId = feeAssetId;
        this.timestamp = timestamp;
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(senderPrivateKey.sign(getBodyBytes()))));
    }

    public InvokeScriptTransaction withArg(long val) {
        call.addArg(val);
        return this;
    }

    public InvokeScriptTransaction withArg(String val) {
        call.addArg(val);
        return this;
    }

    public InvokeScriptTransaction withArg(boolean val) {
        call.addArg(val);
        return this;
    }

    public InvokeScriptTransaction withArg(ByteString val) {
        call.addArg(val);
        return this;
    }

    public InvokeScriptTransaction withPayment(long amount, String assetId) {
        payments.add(new Payment(amount, assetId));
        return this;
    }


    public static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }

    public InvokeScriptTransaction sign(PrivateKeyAccount senderPrivateKey) {
        this.proofs = unmodifiableList(singletonList(new ByteString(senderPrivateKey.sign(getBodyBytes()))));
        return this;
    }

    public byte getChainId() {
        return chainId;
    }

    public String getdApp() {
        return dApp;
    }

    public String getFeeAssetId() {
        return Asset.toJsonObject(feeAssetId);
    }

    @Override
    public long getFee() {
        return fee;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    @Override
    public byte getType() {
        return CONTRACT_INVOKE;
    }

    @Override
    public byte getVersion() {
        return Transaction.V1;
    }

    public FunctionCall getCall() {
        return call;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    @Override
    public InvokeScriptTransaction withProof(int index, ByteString proof) {
        List<ByteString> newProofs = updateProofs(index, proof);
        return new InvokeScriptTransaction(chainId, senderPublicKey, dApp, call, payments, fee, feeAssetId, timestamp, newProofs);
    }


    @Override
    public int getTransactionMaxSize() {
        return MAX_TX_SIZE;
    }

    @Override
    public byte[] getBodyBytes() {
        ByteBuffer buf = ByteBuffer.allocate(getTransactionMaxSize());
        buf.put(CONTRACT_INVOKE).put(Transaction.V1).put(chainId);
        buf.put(senderPublicKey.getPublicKey());
        ByteUtils.putRecipient(buf, chainId, dApp);

        if (call == null) {
            buf.put((byte) 0);
        } else {
            buf.put((byte) 1);
            call.write(buf);
        }

        buf.putShort(toShort(payments.size()));
        for (Payment payment : payments) {
            payment.write(buf);
        }

        buf.putLong(fee);
        putAsset(buf, feeAssetId);
        buf.putLong(timestamp);

        return getOnlyUsed(buf);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvokeScriptTransaction that = (InvokeScriptTransaction) o;

        if (getChainId() != that.getChainId()) return false;
        if (getFee() != that.getFee()) return false;
        if (getTimestamp() != that.getTimestamp()) return false;
        if (!getSenderPublicKey().equals(that.getSenderPublicKey())) return false;
        if (!getdApp().equals(that.getdApp())) return false;
        if (getCall() != null ? !getCall().equals(that.getCall()) : that.getCall() != null) return false;
        if (!getPayments().equals(that.getPayments())) return false;
        return getFeeAssetId() != null ? getFeeAssetId().equals(that.getFeeAssetId()) : that.getFeeAssetId() == null;
    }

    @Override
    public int hashCode() {
        int result = (int) getChainId();
        result = 31 * result + getSenderPublicKey().hashCode();
        result = 31 * result + getdApp().hashCode();
        result = 31 * result + (getCall() != null ? getCall().hashCode() : 0);
        result = 31 * result + getPayments().hashCode();
        result = 31 * result + (int) (getFee() ^ (getFee() >>> 32));
        result = 31 * result + (getFeeAssetId() != null ? getFeeAssetId().hashCode() : 0);
        result = 31 * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        return result;
    }

    @JsonPropertyOrder({"name", "args"})
    public static class FunctionCall implements Serializable {
        private static final byte E_FUNCALL = (byte) 9;

        /**
         * WARNING! Unsupported in current implementation. Function header for native function
         */
        private static final byte FH_NATIVE = (byte) 0;

        /**
         * Function header for custom (user's) function
         */
        private static final byte FH_USER = (byte) 1;

        @JsonProperty("function")
        private String name;
        private LinkedList<FunctionalArg<?>> args = new LinkedList<FunctionalArg<?>>();

        @JsonCreator
        FunctionCall(@JsonProperty("function") String name,
                     @JsonProperty("args") LinkedList<FunctionalArg<?>> args) {
            this.name = name;
            this.args = args;
        }

        public FunctionCall(String name) {
//            if (name == null || name.isEmpty()) {
//                throw new IllegalArgumentException("Function name couldn't be null or empty");
//            }
            this.name = name;
        }

        public FunctionCall addArg(long val) {
            args.add(new LongArg(val));
            return this;
        }

        public FunctionCall addArg(String val) {
            args.add(new StringArg(val));
            return this;
        }

        public FunctionCall addArg(boolean val) {
            args.add(new BooleanArg(val));
            return this;
        }

        public FunctionCall addArg(ByteString val) {
            args.add(new BinaryArg(val));
            return this;
        }

        public void write(ByteBuffer buf) {
            // special bytes to indicate function call. Used in Serde serializer
            buf.put(E_FUNCALL);
            buf.put(FH_USER);

            // write function name
            ByteUtils.putString(buf, name, ByteUtils.BytesFormatter.LENGTH_AS_INT);

            // write function's arguments
            buf.putInt(args.size());
            for (FunctionalArg<?> arg : args) {
                arg.write(buf);
            }
        }

        public ByteBuffer toBytes() {
            final ByteBuffer buf;
            try {
                final int nameLength = name.getBytes("UTF-8").length;
                int argsLength = 4;
                for (FunctionalArg<?> arg : args) argsLength += arg.bytesSize();
                buf = ByteBuffer.allocate(2 + 4 + nameLength + 4 + argsLength);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            // special bytes to indicate function call. Used in Serde serializer
            buf.put(E_FUNCALL);
            buf.put(FH_USER);

            // write function name
            ByteUtils.putString(buf, name, ByteUtils.BytesFormatter.LENGTH_AS_INT);

            // write function's arguments
            buf.putInt(args.size());
            for (FunctionalArg<?> arg : args) {
                arg.write(buf);
            }
            buf.flip();
            return buf.compact().asReadOnlyBuffer();
        }

        public static FunctionCall fromBytes(final ByteBuffer buf) {
            buf.position(buf.position() + 2);
            final byte[] nameBytes = new byte[buf.getInt()];
            buf.get(nameBytes);

            final int argsSize = buf.getInt();
            assert argsSize >= 0 && argsSize <= 22;
            final LinkedList<FunctionalArg<?>> args = new LinkedList<FunctionalArg<?>>();
            for (int i = 0; i < argsSize; i++) args.add(FunctionalArg.fromBytes(buf));

            try {
                return new FunctionCall(new String(nameBytes, "UTF-8"), args);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<FunctionalArg<?>> getArgs() {
            return args;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FunctionCall that = (FunctionCall) o;

            if (!getName().equals(that.getName())) return false;
            return getArgs() != null ? getArgs().equals(that.getArgs()) : that.getArgs() == null;
        }

        @Override
        public int hashCode() {
            int result = getName().hashCode();
            result = 31 * result + (getArgs() != null ? getArgs().hashCode() : 0);
            return result;
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "type")
    @JsonSubTypes({
            @Type(value = LongArg.class, name = LongArg.T_LONG),
            @Type(value = BinaryArg.class, name = BinaryArg.T_BINARY),
            @Type(value = StringArg.class, name = StringArg.T_STRING),
            @Type(value = BooleanArg.class, name = BooleanArg.T_BOOLEAN)
    })
    public abstract static class FunctionalArg<T extends Serializable> implements Serializable {
        protected T value;

        FunctionalArg(T value) {
            if (value == null) {
                throw new IllegalArgumentException("Value could not be null");
            }
            this.value = value;
        }

        public abstract void write(ByteBuffer buf);

        public static FunctionalArg<?> fromBytes(ByteBuffer buf) {
            switch (buf.get()) {
                case StringArg.E_STRING:
                    final byte[] string = new byte[buf.getInt()];
                    buf.get(string);
                    try {
                        return new StringArg(new String(string, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }

                case BinaryArg.E_BYTES:
                    final byte[] bytes = new byte[buf.getInt()];
                    buf.get(bytes);
                    return new BinaryArg(new ByteString(bytes));

                case LongArg.E_LONG:
                    return new LongArg(buf.getLong());

                case BooleanArg.E_TRUE:
                    return new BooleanArg(true);

                case BooleanArg.E_FALSE:
                    return new BooleanArg(false);

                default:
                    throw new IllegalArgumentException("Data type not supported");
            }
        }

        public abstract int bytesSize();

        public T getValue() {
            return value;
        }

        public abstract String getType();

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FunctionalArg<?> that = (FunctionalArg<?>) o;

            if (!getValue().equals(that.getValue())) return false;
            return getType().equals(that.getType());
        }

        @Override
        public int hashCode() {
            int result = getValue().hashCode();
            result = 31 * result + getType().hashCode();
            return result;
        }
    }

    public static class LongArg extends FunctionalArg<Long> {
        private static final byte E_LONG = (byte) 0;
        private static final String T_LONG = "integer";

        @JsonCreator
        public LongArg(@JsonProperty("value") Long value) {
            super(value);
        }

        @Override
        public String getType() {
            return T_LONG;
        }

        @Override
        public void write(ByteBuffer buf) {
            buf.put(E_LONG);
            buf.putLong(value);
        }

        @Override
        public int bytesSize() {
            return 1 + 8;
        }
    }

    public static class BinaryArg extends FunctionalArg<ByteString> {
        private static final byte E_BYTES = (byte) 1;
        private static final String T_BINARY = "binary";

        @JsonCreator
        public BinaryArg(@JsonProperty("value") ByteString value) {
            super(value);
        }

        @Override
        public String getType() {
            return T_BINARY;
        }

        @Override
        public void write(ByteBuffer buf) {
            byte[] binary = value.getBytes();
            buf.put(E_BYTES);
            ByteUtils.putBytes(buf, binary, ByteUtils.BytesFormatter.LENGTH_AS_INT);
        }

        @Override
        public int bytesSize() {
            return 1 + 4 + value.getBytes().length;
        }
    }

    public static class StringArg extends FunctionalArg<String> {
        private static final byte E_STRING = (byte) 2;
        private static final String T_STRING = "string";

        @JsonCreator
        public StringArg(@JsonProperty("value") String value) {
            super(value);
        }

        @Override
        public String getType() {
            return T_STRING;
        }

        @Override
        public void write(ByteBuffer buf) {
            buf.put(E_STRING);
            ByteUtils.putString(buf, value, ByteUtils.BytesFormatter.LENGTH_AS_INT);
        }

        @Override
        public int bytesSize() {
            try {
                return 1 + 4 + value.getBytes("UTF-8").length;
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class BooleanArg extends FunctionalArg<Boolean> {
        private static final byte E_TRUE = (byte) 6;
        private static final byte E_FALSE = (byte) 7;
        private static final String T_BOOLEAN = "boolean";

        @JsonCreator
        public BooleanArg(@JsonProperty("value") Boolean value) {
            super(value);
        }

        @Override
        public String getType() {
            return T_BOOLEAN;
        }

        @Override
        public void write(ByteBuffer buf) {
            if (Boolean.TRUE.equals(value)) {
                buf.put(E_TRUE);
            } else {
                buf.put(E_FALSE);
            }
        }

        @Override
        public int bytesSize() {
            return 1;
        }
    }

    public static class Payment implements Serializable {
        private long amount;
        private String assetId;

        @JsonCreator
        public Payment(@JsonProperty("amount") long amount,
                       @JsonProperty("assetId") String assetId) {
            this.amount = amount;
            this.assetId = assetId;
        }

        public void write(ByteBuffer buf) {
            ByteBuffer tmpBuf = ByteBuffer.allocate(256);
            tmpBuf.putLong(amount);
            putAsset(tmpBuf, assetId);
            ByteUtils.putBytes(buf, getOnlyUsed(tmpBuf), LENGTH_AS_SHORT);
        }

        public long getAmount() {
            return amount;
        }

        public String getAssetId() {
            return Asset.toJsonObject(assetId);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Payment payment = (Payment) o;

            if (getAmount() != payment.getAmount()) return false;
            return getAssetId() != null ? getAssetId().equals(payment.getAssetId()) : payment.getAssetId() == null;
        }

        @Override
        public int hashCode() {
            int result = (int) (getAmount() ^ (getAmount() >>> 32));
            result = 31 * result + (getAssetId() != null ? getAssetId().hashCode() : 0);
            return result;
        }
    }
}
