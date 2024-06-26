package com.bin.im.config;

import com.bin.im.mq.MQProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executor;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-06
 */
@Configuration
@EnableScheduling
//@MapperScan(basePackageClasses = SecureInvokeRecordMapper.class)
//@Import({SecureInvokeAspect.class, SecureInvokeRecordDao.class})
public class TransactionAutoConfiguration {

    @Nullable
    protected Executor executor;

    /**
     * Collect any {@link AsyncConfigurer} beans through autowiring.
     */
//    @Autowired
//    void setConfigurers(ObjectProvider<SecureInvokeConfigurer> configurers) {
//        Supplier<SecureInvokeConfigurer> configurer = SingletonSupplier.of(() -> {
//            List<SecureInvokeConfigurer> candidates = configurers.stream().collect(Collectors.toList());
//            if (CollectionUtils.isEmpty(candidates)) {
//                return null;
//            }
//            if (candidates.size() > 1) {
//                throw new IllegalStateException("Only one SecureInvokeConfigurer may exist");
//            }
//            return candidates.get(0);
//        });
//        executor = Optional.ofNullable(configurer.get()).map(SecureInvokeConfigurer::getSecureInvokeExecutor).orElse(ForkJoinPool.commonPool());
//    }

//    @Bean
//    public SecureInvokeService getSecureInvokeService(SecureInvokeRecordDao dao) {
//        return new SecureInvokeService(dao, executor);
//    }

    @Bean
    public MQProducer getMQProducer() {
        return new MQProducer();
    }
}
