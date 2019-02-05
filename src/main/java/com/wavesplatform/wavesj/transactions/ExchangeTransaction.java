package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.Signable;
import com.wavesplatform.wavesj.Transaction;
import com.wavesplatform.wavesj.WithId;
import com.wavesplatform.wavesj.matcher.Order;

public interface ExchangeTransaction extends Transaction, Signable, WithId  {
    byte EXCHANGE = 7;

    long getAmount();
    long getPrice();
    long getBuyMatcherFee();
    long getSellMatcherFee();
    Order getOrder1();
    Order getOrder2();

}
