package com.bin.gateway.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author: bin
 * @date: 2023/12/15 11:38
 **/
@Data
public class CoolServerInstance {

    /*
        服务名
     */
    protected String serviceName;

    protected String instanceId;

    protected String ip;

    protected int port;

    protected Map<String, String> metadata = new HashMap();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoolServerInstance that = (CoolServerInstance) o;
        return Objects.equals(instanceId, that.instanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId);
    }
}
