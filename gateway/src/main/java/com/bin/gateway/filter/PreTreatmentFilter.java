package com.bin.gateway.filter;

import com.bin.gateway.common.CoolGateWayException;
import com.bin.gateway.common.ResponseCode;
import com.bin.gateway.model.Context;
import com.bin.gateway.model.CoolGatewayProperties;
import com.bin.gateway.model.RouterDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author: bin
 * @date: 2023/12/21 17:02
 **/

@Component
public class PreTreatmentFilter implements Filter{
    private List<RouterDefinition> routes;
    private List<String> whites;
    //路径匹配工具类
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    private CoolGatewayProperties coolGatewayProperties;

    //处理路径映射
    @PostConstruct
    public void init(){
        //先默认从本地配置拿，如果本地为空再从配置中心拿。TODO 配置中心拉配置待写
        routes = coolGatewayProperties.getRoutes();
        whites = coolGatewayProperties.getWhites();
    }
    @Override
    public void doFiler(Context context) {
        String path = context.getGatewayRequest().getPath();
        //获取请求白名单，如认证服务则不需要验证权限。
        for(String patter : whites ){
            if(antPathMatcher.match(patter,path)){
                context.getGatewayRule().setIsAuthority(false);
                break;
            }
        }
        int routerIndex = -1;
        for(int i=0;i<routes.size();i++){
            RouterDefinition route = routes.get(i);
            for(String patter : route.getPredicates()){
                if(antPathMatcher.match(patter,path)){
                    routerIndex = i;
                    break;
                }
            }
            if(routerIndex != -1)
                break;
        }
        if(routerIndex == -1 || routes.get(routerIndex) == null ){
            throw new CoolGateWayException("请求路径："+path ,ResponseCode.PATH_MATCH_NOT_FOUND);
        }else{
            RouterDefinition routerDefinition = routes.get(routerIndex);
            context.setServiceName(routerDefinition.getInstanceName());//实例名
            context.getGatewayRule().setLoadBalanceType(routerDefinition.getLoadType());//负载类型
        }
    }


    @Override
    public int getOrder() {
        return 5;
    }
}
