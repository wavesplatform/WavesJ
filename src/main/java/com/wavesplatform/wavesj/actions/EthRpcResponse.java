package com.wavesplatform.wavesj.actions;

public class EthRpcResponse {

    private long id;
    private String jsonrpc;
    private String result;

    public EthRpcResponse() {
    }

    public EthRpcResponse(long id, String jsonrpc, String result) {
        this.id = id;
        this.jsonrpc = jsonrpc;
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "EthRpcResponse{" +
                "id=" + id +
                ", jsonrpc='" + jsonrpc + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}