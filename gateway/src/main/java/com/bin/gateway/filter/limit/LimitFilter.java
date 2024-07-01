package com.bin.gateway.filter.limit;

import com.bin.gateway.common.CoolGateWayException;
import com.bin.gateway.common.ResponseCode;
import com.bin.gateway.filter.Filter;
import com.bin.gateway.model.Context;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * @author: bin
 * @date: 2023/12/26 11:55
 * 根据ip地址限流访问
 **/
@Slf4j
@Component
public class LimitFilter implements Filter {

    @Value("${cool.gateway.limit.open:true}")
    private boolean open;
    @Value("${cool.gateway.limit.limitType:count}")
    private String limitType = "count";
    @Value("${cool.gateway.limit.qps:10}")
    private int qps;
    @Value("${cool.gateway.limit.timeWindow:5000}")
    private int timeWindow;
    private static HashMap<String,Class<? extends AbstractLimit>> map = new HashMap();
    private AbstractLimit limitImp;

    static {
        map.put("count",CountLimit.class);
        map.put("slide",SlideLimit.class);
    }
    public LimitFilter() {

    }
    @PostConstruct
    public void init(){
        try {
            Class<? extends AbstractLimit> implClass = map.get(limitType);
            limitImp = implClass.newInstance();
        } catch (Exception e) {
            log.error("limitType {} relation Class not found." + limitType);
            throw new RuntimeException("limitType relation Class not found");
        }
        limitImp.setQps(qps);
        limitImp.setTimeWindow(timeWindow);
    }

    @Override
    public void doFiler(Context context) {
        if(!open){
            return ;
        }
        InetSocketAddress socketAddress = (InetSocketAddress) context.getCtx().channel().remoteAddress();
        String ip =socketAddress.getAddress().getHostAddress();
        boolean ac = limitImp.doLimit(ip);
        if(!ac){
            throw new CoolGateWayException(ResponseCode.REQUEST_LIMIT);
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
