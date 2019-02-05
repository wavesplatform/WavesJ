package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.matcher.Order;
import com.wavesplatform.wavesj.matcher.OrderV1;

import java.nio.ByteBuffer;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class ExchangeTransactionV1 extends TransactionWithSignature implements ExchangeTransaction {

    private final long amount;
    private final long price;
    private final long buyMatcherFee;
    private final long sellMatcherFee;
    private final long fee;
    private final Order buyOrder;
    private final Order sellOrder;
    private final PublicKeyAccount senderPublicKey;
    private final long timestamp;

    public ExchangeTransactionV1(PrivateKeyAccount senderPublicKey,
                                 Order buyOrder,
                                 Order sellOrder,
                                 long amount,
                                 long price,
                                 long buyMatcherFee,
                                 long sellMatcherFee,
                                 long fee,
                                 long timestamp) {
        this.amount = amount;
        this.price = price;
        this.buyMatcherFee = buyMatcherFee;
        this.sellMatcherFee = sellMatcherFee;
        this.fee = fee;
        this.buyOrder = buyOrder;
        this.sellOrder = sellOrder;
        this.senderPublicKey = new PublicKeyAccount(senderPublicKey.getPublicKey(), senderPublicKey.getChainId());
        this.timestamp = timestamp;
        this.signature = new ByteString(senderPublicKey.sign(getBodyBytes()));
    }

    public ExchangeTransactionV1(Order buyOrder,
                                 Order sellOrder,
                                 long amount,
                                 long price,
                                 long buyMatcherFee,
                                 long sellMatcherFee,
                                 long fee,
                                 long timestamp,
                                 ByteString signature) {
        this.amount = amount;
        this.price = price;
        this.buyMatcherFee = buyMatcherFee;
        this.sellMatcherFee = sellMatcherFee;
        this.fee = fee;
        this.buyOrder = buyOrder;
        this.sellOrder = sellOrder;
        this.senderPublicKey = buyOrder.getMatcherPublicKey();
        this.timestamp = timestamp;
        this.signature = signature;
    }

    @JsonCreator
    public ExchangeTransactionV1(@JsonProperty("order1") Order buyOrder,
                                 @JsonProperty("order2") Order sellOrder,
                                 @JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                                 @JsonProperty("amount") long amount,
                                 @JsonProperty("price") long price,
                                 @JsonProperty("buyMatcherFee") long buyMatcherFee,
                                 @JsonProperty("sellMatcherFee") long sellMatcherFee,
                                 @JsonProperty("fee") long fee,
                                 @JsonProperty("timestamp") long timestamp,
                                 @JsonProperty("signature") ByteString signature) {
        this.amount = amount;
        this.price = price;
        this.buyMatcherFee = buyMatcherFee;
        this.sellMatcherFee = sellMatcherFee;
        this.fee = fee;
        this.buyOrder = buyOrder;
        this.sellOrder = sellOrder;
        this.senderPublicKey = senderPublicKey;
        this.timestamp = timestamp;
        this.signature = signature;
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @Override
    public long getPrice() {
        return price;
    }

    @Override
    public long getBuyMatcherFee() {
        return buyMatcherFee;
    }

    @Override
    public long getSellMatcherFee() {
        return sellMatcherFee;
    }

    @Override
    public long getFee() {
        return fee;
    }

    @Override
    public Order getOrder1() {
        return buyOrder;
    }

    @Override
    public Order getOrder2() {
        return sellOrder;
    }

    @Override
    public byte[] getBodyBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(ExchangeTransactionV1.EXCHANGE)
                .putInt(buyOrder.getBodyBytes().length + 64)
                .putInt(sellOrder.getBodyBytes().length + 64)
                .put(buyOrder.getBodyBytes())
                .put(((OrderV1)buyOrder).getSignature().getBytes())
                .put(sellOrder.getBodyBytes())
                .put(((OrderV1)sellOrder).getSignature().getBytes())
                .putLong(price)
                .putLong(amount)
                .putLong(buyMatcherFee)
                .putLong(sellMatcherFee)
                .putLong(fee)
                .putLong(timestamp);
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(getBodyBytes())
                .put(signature.getBytes());
        return ByteArraysUtils.getOnlyUsed(buf);
    }

    @Override
    public PublicKeyAccount getSenderPublicKey() {
        return senderPublicKey;
    }

    @Override
    public byte getType() {
        return EXCHANGE;
    }

    @Override
    public byte getVersion() {
        return Transaction.V1;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExchangeTransactionV1 that = (ExchangeTransactionV1) o;

        if (getAmount() != that.getAmount()) return false;
        if (getPrice() != that.getPrice()) return false;
        if (getBuyMatcherFee() != that.getBuyMatcherFee()) return false;
        if (getSellMatcherFee() != that.getSellMatcherFee()) return false;
        if (getFee() != that.getFee()) return false;
        if (getTimestamp() != that.getTimestamp()) return false;
        if (getOrder1() != null ? !getOrder1().equals(that.getOrder1()) : that.getOrder1() != null) return false;
        if (getOrder2() != null ? !getOrder2().equals(that.getOrder2()) : that.getOrder2() != null) return false;
        return getSenderPublicKey() != null ? getSenderPublicKey().equals(that.getSenderPublicKey()) : that.getSenderPublicKey() == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (getAmount() ^ (getAmount() >>> 32));
        result = 31 * result + (int) (getPrice() ^ (getPrice() >>> 32));
        result = 31 * result + (int) (getBuyMatcherFee() ^ (getBuyMatcherFee() >>> 32));
        result = 31 * result + (int) (getSellMatcherFee() ^ (getSellMatcherFee() >>> 32));
        result = 31 * result + (int) (getFee() ^ (getFee() >>> 32));
        result = 31 * result + (getOrder1() != null ? getOrder1().hashCode() : 0);
        result = 31 * result + (getOrder2() != null ? getOrder2().hashCode() : 0);
        result = 31 * result + (getSenderPublicKey() != null ? getSenderPublicKey().hashCode() : 0);
        result = 31 * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        return result;
    }
}
