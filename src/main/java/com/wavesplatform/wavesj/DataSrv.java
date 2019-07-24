package com.wavesplatform.wavesj;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wavesplatform.wavesj.json.dataservices.DataSrvJsonMapper;
import com.wavesplatform.wavesj.json.dataservices.DataSrvTxWrapper;
import com.wavesplatform.wavesj.transactions.ExchangeTransaction;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

public class DataSrv {

    public static final String TESTNET_URL = "https://api-test.wavesplatform.com";

    public enum Sort {
        ASC("asc"), DESC("desc");

        private String sort;

        Sort(String sort) {
            this.sort = sort;
        }
    }

    private final URI uri;
    private final DataSrvJsonMapper dsMapper;
    private final CloseableHttpClient client;

    private ResponseMapper<List<ExchangeTransaction>> wrappedExchangesListMapper;

    public DataSrv(String uri, byte chainId, CloseableHttpClient httpClient) throws URISyntaxException {
        this.uri = new URI(uri);
        this.dsMapper = new DataSrvJsonMapper(chainId);
        this.client = httpClient;

        wrappedExchangesListMapper = new ListResponseMapper<ExchangeTransaction>(
                new TypeReference<List<DataSrvTxWrapper<ExchangeTransaction>>>() {});
    }

    public List<ExchangeTransaction> getExchangeTransactions(String amountAsset, Sort sort, int limit) throws IOException {
        String requestUrl = "/v0/transactions/exchange";
        limit = limit > 0 ? limit : 100;
        sort = sort != null ? sort : Sort.DESC;

        String params = "?limit=" + limit + "&sort=" + sort.sort;

        if (amountAsset != null) {
            params = params + "&amountAsset=" + amountAsset;
        }

        HttpGet req = new HttpGet(uri.resolve(requestUrl + params));

        return exec(req, wrappedExchangesListMapper);
    }

    private <T> T exec(HttpUriRequest request, ResponseMapper<T> responseMapper) throws IOException {
        CloseableHttpResponse resp = null;
        try {
            resp = client.execute(request);
            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new IOException(EntityUtils.toString(resp.getEntity()));
            }
            return responseMapper.map(resp);
        } finally {
            if (resp != null) {
                resp.close();
            }
        }
    }

    private interface ResponseMapper<T> {
        T map(CloseableHttpResponse r) throws IOException;
    }

    private class ListResponseMapper<T extends Transaction> implements ResponseMapper<List<T>> {

        private TypeReference<List<DataSrvTxWrapper<T>>> type;

        private ListResponseMapper(TypeReference<List<DataSrvTxWrapper<T>>> type) {
            this.type = type;
        }

        @Override
        public List<T> map(CloseableHttpResponse r) throws IOException {
            TreeNode rootNode = dsMapper.readTree(r.getEntity().getContent());
            TreeNode txTreeNode = rootNode.path("data");

            List<DataSrvTxWrapper<T>> wrapped = dsMapper.convertValue(txTreeNode, type);
            return unwrap(wrapped);
        }

        private List<T> unwrap(List<DataSrvTxWrapper<T>> wrappers) {
            List<T> transactions = new LinkedList<T>();
            for (DataSrvTxWrapper<T> w: wrappers) {
                transactions.add(w.getTx());
            }
            return transactions;
        }
    }

    private class SingTxResponseMapper<T extends Transaction> implements ResponseMapper<T> {

        private TypeReference<DataSrvTxWrapper<T>> type;

        private SingTxResponseMapper(TypeReference<DataSrvTxWrapper<T>> type) {
            this.type = type;
        }

        @Override
        public T map(CloseableHttpResponse r) throws IOException {
            TreeNode rootNode = dsMapper.readTree(r.getEntity().getContent());
            TreeNode txTreeNode = rootNode.path("data");

            DataSrvTxWrapper<T> wrapper = dsMapper.convertValue(txTreeNode, type);
            return unwrap(wrapper);
        }

        private T unwrap(DataSrvTxWrapper<T> wrapper) {
            return wrapper.getTx();
        }
    }
}
