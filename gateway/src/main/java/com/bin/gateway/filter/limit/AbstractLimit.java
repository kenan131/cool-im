package com.bin.gateway.filter.limit;

/**
 * @author: bin
 * @date: 2023/12/27 11:20
 **/
public abstract class AbstractLimit {
    protected int qps =10;
    protected int timeWindow = 5000;

    public void setQps(int qps) {
        this.qps = qps;
    }

    public void setTimeWindow(int timeWindow) {
        this.timeWindow = timeWindow;
    }

    abstract boolean doLimit(String ip);
}
