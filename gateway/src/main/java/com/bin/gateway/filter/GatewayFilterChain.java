package com.bin.gateway.filter;

import com.bin.gateway.model.Context;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author: bin
 * @date: 2023/12/21 13:51
 **/
@Component
public class GatewayFilterChain implements ApplicationContextAware {
    private List<Filter> filters = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Filter> beansOfType = applicationContext.getBeansOfType(Filter.class);
        for(Map.Entry<String,Filter> filterEntry : beansOfType.entrySet()){
            Filter filter = filterEntry.getValue();
            filters.add(filter);
        }
        filters.sort(Comparator.comparingInt(Filter::getOrder));
    }

    public boolean addFilter(Filter filter){
        boolean res = filters.add(filter);
        filters.sort(Comparator.comparingInt(Filter::getOrder));
        return res;
    }

    public void doFilters(Context context){
        for (Filter filter : filters){
            filter.doFiler(context);
        }
    }

}