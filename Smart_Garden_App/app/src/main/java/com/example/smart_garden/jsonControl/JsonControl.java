package com.example.smart_garden.jsonControl;

public class JsonControl {
    private String method;
    private Params params;
    private long timeout;

    public JsonControl(String method, Params params, long timeout) {
        this.method = method;
        this.params = params;
        this.timeout = timeout;
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
