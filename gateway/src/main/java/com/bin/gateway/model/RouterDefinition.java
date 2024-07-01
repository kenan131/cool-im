package com.bin.gateway.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: bin
 * @date: 2023/12/25 14:02
 **/

public class RouterDefinition {
    private String id;
    private String instanceName;
    private String loadType;
    private List<String> predicates = new ArrayList<>();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getLoadType() {
        return loadType;
    }

    public void setLoadType(String loadType) {
        this.loadType = loadType;
    }

    public List<String> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<String> predicates) {
        this.predicates = predicates;
    }
}
