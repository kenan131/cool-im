package com.bin.gateway.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: bin
 * @date: 2023/12/25 13:41
 **/
@ConfigurationProperties(prefix ="cool.gateway")
@Component
public class CoolGatewayProperties {

    private String port ="9000";
    private String cacheType = "local";
    private List<RouterDefinition> routes = new ArrayList<>();
    private List<String> whites = new ArrayList<>();
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getCacheType() {
        return cacheType;
    }

    public void setCacheType(String cacheType) {
        this.cacheType = cacheType;
    }

    public List<RouterDefinition> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouterDefinition> routes) {
        this.routes = routes;
    }

    public List<String> getWhites() {
        return whites;
    }

    public void setWhites(List<String> whites) {
        this.whites = whites;
    }
}

