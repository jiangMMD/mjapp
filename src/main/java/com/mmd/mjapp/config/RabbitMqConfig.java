package com.mmd.mjapp.config;

import com.mmd.mjapp.constant.FileConstant;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitMQ 相关配置
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 配置一个队列
     * @return
     */
    @Bean
    public Queue queue() {
        return new Queue(FileConstant.RABBIT_MQ);
    }
}
