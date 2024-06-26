package com.bin.access.provide;

import com.bin.access.service.WebSocketService;
import com.bin.interfaceapi.access.AccessServiceApi;
import com.bin.interfaceapi.access.dto.PushMessageDTO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: bin.jiang
 * @date: 2024/6/25 16:03
 **/
@Component
@DubboService
public class PushServiceProvide implements AccessServiceApi {

    @Autowired
    private WebSocketService webSocketService;

    private ThreadPoolExecutor processThread;

    @PostConstruct
    public void init(){
        processThread = new ThreadPoolExecutor(4, 10, 60, TimeUnit.SECONDS,new ArrayBlockingQueue<>(100), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                // 自身线程执行
                r.run();
            }
        });
    }

    @Override
    public boolean pushMessage(List<PushMessageDTO> data) {
        // 这里直接交给线程池去处理，rpc直接返回成功。
        processThread.execute(new Runnable() {
            @Override
            public void run() {
                webSocketService.handlerMessage(data);
            }
        });
        return true;
    }

}
