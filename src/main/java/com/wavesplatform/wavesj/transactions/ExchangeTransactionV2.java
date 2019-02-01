package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.matcher.Order;
import com.wavesplatform.wavesj.matcher.OrderV2;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class ExchangeTransactionV2 extends TransactionWithProofs<ExchangeTransactionV2> implements ExchangeTransaction {

    private final long amount;
    private final long price;
    private final long buyMatcherFee;
    private final long sellMatcherFee;
    private final long fee;
    private final OrderV2 buyOrder;
    private final OrderV2 sellOrder;
    private final PublicKeyAccount senderPublicKey;
    private final long timestamp;

    public ExchangeTransactionV2(PrivateKeyAccount senderPublicKey,
                                 long amount,
                                 long price,
                                 OrderV2 buyOrder,
                                 OrderV2 sellOrder,
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
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(senderPublicKey.sign(getBytes()))));
    }

    public ExchangeTransactionV2(long amount,
                                 long price,
                                 OrderV2 buyOrder,
                                 OrderV2 sellOrder,
                                 long buyMatcherFee,
                                 long sellMatcherFee,
                                 long fee,
                                 long timestamp,
                                 List<ByteString> proofs) {
        setProofs(proofs);
        this.amount = amount;
        this.price = price;
        this.buyMatcherFee = buyMatcherFee;
        this.sellMatcherFee = sellMatcherFee;
        this.fee = fee;
        this.buyOrder = buyOrder;
        this.sellOrder = sellOrder;
        this.senderPublicKey = buyOrder.getMatcherPublicKey();
        this.timestamp = timestamp;
    }

    @JsonCreator
    public ExchangeTransactionV2(@JsonProperty("senderPublicKey") PublicKeyAccount senderPublicKey,
                                 @JsonProperty("amount") long amount,
                                 @JsonProperty("price") long price,
                                 @JsonProperty("order1") OrderV2 buyOrder,
                                 @JsonProperty("order2") OrderV2 sellOrder,
                                 @JsonProperty("buyMatcherFee") long buyMatcherFee,
                                 @JsonProperty("sellMatcherFee") long sellMatcherFee,
                                 @JsonProperty("fee") long fee,
                                 @JsonProperty("timestamp") long timestamp,
                                 @JsonProperty("proofs") List<ByteString> proofs) {
        setProofs(proofs);
        this.amount = amount;
        this.price = price;
        this.buyMatcherFee = buyMatcherFee;
        this.sellMatcherFee = sellMatcherFee;
        this.fee = fee;
        this.buyOrder = buyOrder;
        this.sellOrder = sellOrder;
        this.senderPublicKey = senderPublicKey;
        this.timestamp = timestamp;
    }

    public long getAmount() {
        return amount;
    }

    public long getPrice() {
        return price;
    }

    public long getBuyMatcherFee() {
        return buyMatcherFee;
    }

    public long getSellMatcherFee() {
        return sellMatcherFee;
    }

    @Override
    public long getFee() {
        return fee;
    }

    public OrderV2 getOrder1() {
        return buyOrder;
    }

    public OrderV2 getOrder2() {
        return sellOrder;
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put((byte) 0)
                .putInt(ExchangeTransactionV2.EXCHANGE)
                .put(Transaction.V2)
                .putInt(buyOrder.getBytes().length)
                .put(buyOrder.getBytes())
                .putInt(1) //proofs version
                .putInt(buyOrder.getProofs().size());
        buyOrder.getProofs().forEach(p -> buf.put(p.getBytes()));
        buf.putInt(sellOrder.getBytes().length)
                .put(sellOrder.getBytes())
                .putInt(1) //proofs version
                .putInt(sellOrder.getProofs().size());
        sellOrder.getProofs().forEach(p -> buf.put(p.getBytes()));
        buf.putLong(price)
                .putLong(amount)
                .putLong(buyMatcherFee)
                .putLong(sellMatcherFee)
                .putLong(fee)
                .putLong(timestamp);
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
        return 1;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExchangeTransactionV2 that = (ExchangeTransactionV2) o;

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

    @Override
    public ExchangeTransactionV2 withProof(int index, ByteString proof) {
        List<ByteString> newProofs = updateProofs(index, proof);
        return new ExchangeTransactionV2(senderPublicKey, amount, price, buyOrder, sellOrder, buyMatcherFee, sellMatcherFee, fee, timestamp, newProofs);
    }
}
