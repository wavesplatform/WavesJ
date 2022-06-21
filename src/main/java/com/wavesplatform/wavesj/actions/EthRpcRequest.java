package com.wavesplatform.wavesj.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class EthRpcRequest {

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private String jsonrpc;
    private String method;
    private List<String> params;
    private long id;


    public EthRpcRequest(String jsonrpc, String method, List<String> params, long id) {
        this.jsonrpc = jsonrpc;
        this.method = method;
        this.params = params;
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String toJsonString() throws JsonProcessingException {
        return JSON_MAPPER.writeValueAsString(this);
    }
}
