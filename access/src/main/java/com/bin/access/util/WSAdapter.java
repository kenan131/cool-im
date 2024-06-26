package com.bin.access.util;

import com.bin.interfaceapi.access.dto.WSBaseResp;
import com.bin.interfaceapi.access.dto.WSRespTypeEnum;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Description: ws消息适配器
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Component
public class WSAdapter {
    public static WSBaseResp buildInvalidateTokenResp() {
        WSBaseResp wsBaseResp = new WSBaseResp();
        wsBaseResp.setType(WSRespTypeEnum.INVALIDATE_TOKEN.getType());
        return wsBaseResp;
    }
}
