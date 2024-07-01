package com.bin.gateway.filter;

import com.bin.gateway.common.CoolGateWayException;
import com.bin.gateway.common.GatewayConst;
import com.bin.gateway.common.ResponseCode;
import com.bin.gateway.model.Context;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.netty.handler.codec.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author: bin
 * @date: 2023/12/26 11:53
 **/
@Component
@ConditionalOnProperty(name = "cool.gateway.authority.enable",havingValue = "true")
public class AuthFilter implements Filter{

    @Value("${cool.gateway.authority.accessKey:coolGateway}")
    private String accessKey;

    @Override
    public void doFiler(Context context) {
        if(context.getGatewayRule().getIsAuthority()){
            HttpHeaders headers = context.getGatewayRequest().getHeaders();
            //暂且默认客户端的cookie 只存储jwt登录信息
            String jwt = headers.get(GatewayConst.TOKEN);
            try{
                Claims claims = Jwts.parser()
                        .setSigningKey(accessKey)
                        .parseClaimsJws(jwt)
                        .getBody();
                //内容默认存入 subject中。
                String userId = claims.getSubject();
                //看是否不为空  并且能转为整数。 如果转换失败 走catch。
                Long.valueOf(userId);
                //将 userId 传入 请求头中，供下游服务使用。
                headers.add("userId",userId);
            }catch (Exception e){
                throw new CoolGateWayException(ResponseCode.AUTHORITY_FAIL);
            }
        }
    }


    @Override
    public int getOrder() {
        return 15;
    }
}
