package com.wavesplatform.wavesj.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.matcher.Order;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import static com.wavesplatform.wavesj.ByteUtils.KBYTE;

public class ExchangeTransactionV2 extends TransactionWithProofs<ExchangeTransactionV2> implements ExchangeTransaction {

    public static final byte EXCHANGE = 7;
    private final long amount;
    private final long price;
    private final long buyMatcherFee;
    private final long sellMatcherFee;
    private final long fee;
    private final Order buyOrder;
    private final Order sellOrder;
    private final PublicKeyAccount senderPublicKey;
    private final long timestamp;
    private static final int MAX_TX_SIZE = KBYTE;

    public ExchangeTransactionV2(PrivateKeyAccount senderPublicKey,
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
        this.proofs = Collections.unmodifiableList(Collections.singletonList(new ByteString(senderPublicKey.sign(getBodyBytes()))));
    }

    public ExchangeTransactionV2(Order buyOrder,
                                 Order sellOrder,
                                 long amount,
                                 long price,
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
                                 @JsonProperty("order1") Order buyOrder,
                                 @JsonProperty("order2") Order sellOrder,
                                 @JsonProperty("amount") long amount,
                                 @JsonProperty("price") long price,
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

    public Order getOrder1() {
        return buyOrder;
    }

    public Order getOrder2() {
        return sellOrder;
    }


    @Override
    public int getTransactionMaxSize(){
        return MAX_TX_SIZE;
    }

    @Override
    public byte[] getBodyBytes() {
        ByteBuffer buf = ByteBuffer.allocate(getTransactionMaxSize());
        buf.put((byte) 0)
                .put(ExchangeTransactionV2.EXCHANGE)
                .put(Transaction.V2);
        buf.putInt(buyOrder.getBytes().length);
        if (buyOrder.getVersion() == Order.V1)
            buf.put((byte) 1);
        buf.put(buyOrder.getBytes());
        buf.putInt(sellOrder.getBytes().length);
        if (sellOrder.getVersion() == Order.V1)
            buf.put((byte) 1);
        buf.put(sellOrder.getBytes());
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
        return Transaction.V2;
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
        return new ExchangeTransactionV2(senderPublicKey, buyOrder, sellOrder, amount, price, buyMatcherFee, sellMatcherFee, fee, timestamp, newProofs);
    }
}
