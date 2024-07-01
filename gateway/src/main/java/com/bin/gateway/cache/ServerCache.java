package com.bin.gateway.cache;

import com.bin.gateway.model.CoolServerInstance;

import java.util.List;
import java.util.Set;

/**
 * @author: bin
 * @date: 2023/12/15 11:35
 **/

public interface ServerCache {
    void setInstance(String serviceName, CoolServerInstance instance);

    void setInstances(String serviceName, List<CoolServerInstance> instances);

    void removeInstances(String serviceName);

    Set<CoolServerInstance> getInstanceSetByServiceName(String serviceName);

    List<CoolServerInstance> getInstanceListByServiceName(String serviceName);


}
