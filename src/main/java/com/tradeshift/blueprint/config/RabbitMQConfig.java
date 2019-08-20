package com.tradeshift.blueprint.config;

import java.io.IOException;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.rabbitmq.client.DefaultSaslConfig;
import com.tradeshift.commons.tls.TLSContextUtil;

@Configuration
public class RabbitMQConfig {

    @Value("${tls.keystore.location}")
    private Resource tlsKeystore;

    @Value("${tls.keystore.password:}")
    private String tlsKeystorePassword;

    @Value("${rabbitmq.host: localhost}")
    private String host;

    @Value("${rabbitmq.virtual-host: tradeshift}")
    private String virtualHost;


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setChannelTransacted(true);
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(new ExponentialBackOffPolicy());
        template.setRetryTemplate(retryTemplate);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory containerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    public CachingConnectionFactory getConnectionFactory() throws IOException {
        com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
        factory.setSaslConfig(DefaultSaslConfig.EXTERNAL);
        factory.useSslProtocol(TLSContextUtil.tls12ContextFromPKCS12(tlsKeystore.getInputStream(),
                tlsKeystorePassword.toCharArray()));
        factory.setHost(host);
        factory.setPort(5671);
        factory.setAutomaticRecoveryEnabled(false);
        factory.setVirtualHost(virtualHost);
        return new CachingConnectionFactory(factory);
    }
}
