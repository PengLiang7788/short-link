package com.example.shortlink.link.config;

import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 彭亮
 * @create 2023-01-06 18:38
 */
@Configuration
@Data
public class RabbitMQConfig {

    /**
     * 交换机名称
     */
    private String shortLinkEventExchange="short_link.event.exchange";

    /**
     * 创建交换机 Topic类型
     * 一般一个微服务一个交换机
     * @return
     */
    @Bean
    public Exchange shortLinkEventExchange(){
        // 参数说明：交换机名称，开启持久化，不自动删除
        return new TopicExchange(shortLinkEventExchange,true,false);
    }


    /**
     * 新增短链 队列 C端解析
     */
    private String shortLinkAddLinkQueue="short_link.add.link.queue";

    /**
     * 新增短链映射 队列  B端商家
     */
    private String shortLinkAddMappingQueue="short_link.add.mapping.queue";

    /**
     * 新增短链具体的routingKey,【发送消息使用】
     */
    private String shortLinkAddRoutingKey="short_link.add.link.mapping.routing.key";

    /**
     * topic类型的binding key，用于绑定队列和交换机，是用于 link 消费者
     */
    private String shortLinkAddLinkBindingKey="short_link.add.link.*.routing.key";

    /**
     * topic类型的binding key，用于绑定队列和交换机，是用于 mapping 消费者
     */
    private String shortLinkAddMappingBindingKey="short_link.add.*.mapping.routing.key";


    /**
     * 新增短链api队列和交换机的绑定关系建立
     */
    @Bean
    public Binding shortLinkAddApiBinding(){
        return new Binding(shortLinkAddLinkQueue,Binding.DestinationType.QUEUE, shortLinkEventExchange,shortLinkAddLinkBindingKey,null);
    }


    /**
     * 新增短链mapping队列和交换机的绑定关系建立
     */
    @Bean
    public Binding shortLinkAddMappingBinding(){
        return new Binding(shortLinkAddMappingQueue,Binding.DestinationType.QUEUE, shortLinkEventExchange,shortLinkAddMappingBindingKey,null);
    }


    /**
     * 新增短链api 普通队列，用于被监听
     */
    @Bean
    public Queue shortLinkAddLinkQueue(){

        return new Queue(shortLinkAddLinkQueue,true,false,false);

    }

    /**
     * 新增短链mapping 普通队列，用于被监听
     */
    @Bean
    public Queue shortLinkAddMappingQueue(){

        return new Queue(shortLinkAddMappingQueue,true,false,false);

    }

}
