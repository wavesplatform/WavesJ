package com.wavesplatform.wavesj.transactions;

import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.matcher.Order;
import com.wavesplatform.wavesj.matcher.OrderV1;

public interface ExchangeTransaction extends Transaction, Signable, WithId  {
    byte EXCHANGE = 7;

    long getAmount();
    long getPrice();
    long getBuyMatcherFee();
    long getSellMatcherFee();

}
