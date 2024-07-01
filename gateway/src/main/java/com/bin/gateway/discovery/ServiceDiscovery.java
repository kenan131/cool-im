package com.bin.gateway.discovery;

/**
 * @author: bin
 * @date: 2023/12/14 16:55
 **/

public interface ServiceDiscovery {
    void discovery();

    void stop();

    void start();
}
