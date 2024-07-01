package com.bin.gateway.filter;

import com.bin.gateway.common.GatewayConst;
import com.bin.gateway.model.Context;
import com.bin.gateway.model.net.GatewayRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * @author: bin.jiang
 * @date: 2024/4/30 15:06
 **/
@Component
public class GrayFilter implements Filter{

    @Override
    public void doFiler(Context context) {
        GatewayRequest gatewayRequest = context.getGatewayRequest();
        HttpHeaders headers = gatewayRequest.getHeaders();
        String gray = headers.get(GatewayConst.GRAY);
        if("true".equals(gray)){
            context.getGatewayRule().setIsGray(true);
        }
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
