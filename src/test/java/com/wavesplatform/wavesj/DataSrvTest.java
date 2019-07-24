package com.wavesplatform.wavesj;

import com.wavesplatform.wavesj.transactions.ExchangeTransaction;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.util.List;

public class DataSrvTest {

    @Test
    public void simpleTest() throws Exception {
        CloseableHttpClient httpClient = createDefaultClient();
        DataSrv dataSrv = new DataSrv(DataSrv.TESTNET_URL, (byte)'T', httpClient);

        List<ExchangeTransaction> txs = dataSrv.getExchangeTransactions("B6UzpMEdaQSzfhJh3DwYoaTjsCVE36RNTm4o2qJxEoDr", null, 100);
        for (ExchangeTransaction tx: txs) {
            System.out.println(tx.toString());
        }
    }

    private static CloseableHttpClient createDefaultClient() {
        return HttpClients.custom().setDefaultRequestConfig(
                RequestConfig.custom()
                        .setSocketTimeout(5000)
                        .setConnectTimeout(5000)
                        .setConnectionRequestTimeout(5000)
                        .setCookieSpec(CookieSpecs.STANDARD)
                        .build())
                .build();
    }
}
