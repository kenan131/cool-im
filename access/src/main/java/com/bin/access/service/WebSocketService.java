package com.bin.access.service;

import cn.hutool.json.JSONUtil;
import com.bin.model.user.vo.request.user.LoginReqDto;
import com.bin.api.access.dto.WSAdapter;
import com.bin.api.router.RouterServiceApi;
import com.bin.api.user.UserServiceApi;
import com.bin.model.common.exception.WSBaseResp;
import com.bin.model.user.dto.PushMessageDTO;
import com.bin.model.common.exception.WSPushTypeEnum;
import com.bin.model.user.vo.response.user.LoginResp;
import com.bin.model.user.vo.response.ws.WSLoginFail;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.NetUtils;
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

    static String localIp = NetUtils.getLocalHost();

    public void login(LoginReqDto dto,Channel channel){
        try{
            LoginResp resp = userServiceApi.login(dto);
            if(resp.getType()){
                sendMsg(channel, resp.getWsBaseResp());
                onLine(resp.getUid(),channel);
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

    public void onLine(Long userId, Channel channel) {
        userIdChannelMap.put(userId, channel);
        channelUserIdMap.put(channel, userId);
        // 绑定
        System.out.println("绑定========"+userId+" : "+localIp);
        routerServiceApi.bind(userId,localIp);
    }

    public Channel getChannel(Integer userId) {
        Channel channel = userIdChannelMap.get(userId);
        return channel;
    }

    public void connect(String token, ChannelHandlerContext ctx) {
        System.out.println("获取token" + token);
        LoginResp loginResp = userServiceApi.getUserId(token);
        if (loginResp == null) {
            System.err.println("用户token过期");
            offLine(ctx);
            // 前端token过期，发送消息给前端清理。
            sendMsg(ctx.channel(), WSAdapter.buildInvalidateTokenResp());
        }else{
            onLine(loginResp.getUid(), ctx.channel());
            sendMsg(ctx.channel(), loginResp.getWsBaseResp());
        }
    }

    public void handlerMessage(List<PushMessageDTO> data) {
        System.out.println("消息推送！");
        System.out.println(data);
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
        System.err.println("消息推送到前端" + wsBaseResp + " 用户id ：" + uid);
        sendMsg(channel, wsBaseResp);
    }

    public void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid) {
        System.out.println("推送全员！");
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
