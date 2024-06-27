package com.bin.access.service;

import cn.hutool.json.JSONUtil;
import com.bin.model.user.vo.request.user.LoginReqDto;
import com.bin.api.access.dto.WSAdapter;
import com.bin.api.router.RouterServiceApi;
import com.bin.api.user.UserServiceApi;
import com.bin.model.user.enums.WSBaseResp;
import com.bin.model.user.dto.PushMessageDTO;
import com.bin.model.user.enums.WSPushTypeEnum;
import com.bin.model.user.vo.response.user.LoginResp;
import com.bin.model.user.vo.response.ws.WSLoginFail;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author: bin.jiang
 * @date: 2024/6/5 15:49
 **/
@Slf4j
@Component
public class WebSocketService {

    private Map<Long, Channel> userIdChannelMap = new HashMap<>();
    private Map<Channel, Long> channelUserIdMap = new HashMap<>();

    @DubboReference(check = false)
    private UserServiceApi userServiceApi;

    @DubboReference(check = false)
    private RouterServiceApi routerServiceApi;

    private String localIp;

    @PostConstruct
    void init() throws UnknownHostException {
        // 注册服务
        InetAddress localHost = InetAddress.getLocalHost();
        //获取本机ip地址
        localIp = localHost.getHostAddress();
    }

    public void login(LoginReqDto dto,Channel channel){
        try{
            LoginResp resp = userServiceApi.login(dto);
            if(resp.getType()){
                sendMsg(channel, resp.getWsBaseResp());
            }else{
                System.out.println("登录失败" + resp.getErrorMsg());
                sendMsg(channel, WSAdapter.buildLoginFailMsg(new WSLoginFail(resp.getErrorMsg())));
            }
        }catch (Exception e){
            log.error("登录异常{" + dto.getUserName() + ":" + dto.getPassWord() + "}");
            e.printStackTrace();
        }
    }

    public void offLine(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        Long userId = channelUserIdMap.get(channel);
        channelUserIdMap.remove(channel);
        userIdChannelMap.remove(userId);
        // 解绑
        if(userId != null){
            routerServiceApi.unbind(userId,localIp);
        }
    }

    public void onLine(Long userId, ChannelHandlerContext ctx) {
        userIdChannelMap.put(userId, ctx.channel());
        channelUserIdMap.put(ctx.channel(), userId);
        // 绑定
        routerServiceApi.bind(userId,localIp);
    }

    public Channel getChannel(Integer userId) {
        Channel channel = userIdChannelMap.get(userId);
        return channel;
    }

    public void connect(String token, ChannelHandlerContext ctx) {
        Long userId = userServiceApi.getUserId(token);
        if (userId == null) {
            offLine(ctx);
            // 前端token过期，发送消息给前端清理。
            sendMsg(ctx.channel(), WSAdapter.buildInvalidateTokenResp());
        }
        onLine(userId, ctx);
    }

    public void handlerMessage(List<PushMessageDTO> data) {
        data.forEach(dto -> {
            WSPushTypeEnum wsPushTypeEnum = WSPushTypeEnum.of(dto.getPushType());
            switch (wsPushTypeEnum) {
                case USER:
                    dto.getUidList().forEach(uid -> {
                        sendToUid(dto.getWsBaseMsg(), uid);
                    });
                    break;
                case ALL:
                    sendToAllOnline(dto.getWsBaseMsg(), null);
                    break;
            }
        });
    }


    public void sendToUid(WSBaseResp wsBaseResp, Long uid) {
        Channel channel = userIdChannelMap.get(uid);
        if (channel == null) {
            log.info("用户：{}不在线", uid);
            return;
        }
        sendMsg(channel, wsBaseResp);
    }

    public void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid) {
        userIdChannelMap.forEach((userId, channel) -> {
            if (Objects.nonNull(skipUid) && Objects.equals(userId, skipUid)) {
                return;
            }
            sendMsg(channel, wsBaseResp);
        });
    }


    /**
     * 给本地channel发送消息
     */
    public void sendMsg(Channel channel, WSBaseResp wsBaseResp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
    }


}
