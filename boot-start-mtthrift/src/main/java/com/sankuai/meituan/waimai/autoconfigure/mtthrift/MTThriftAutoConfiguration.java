package com.sankuai.meituan.waimai.autoconfigure.mtthrift;

import com.meituan.service.mobile.mtthrift.proxy.ThriftClientProxy;
import com.meituan.service.mobile.mtthrift.proxy.ThriftServerPublisher;
import com.sankuai.meituan.waimai.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 提供mtthrift 自动配置
 *
 * @author liuzhenyuan
 * @version Last modified 15/7/21
 */
@Configuration
@ConditionalOnClass(value = {ThriftClientProxy.class, ThriftServerPublisher.class})
@EnableConfigurationProperties
public class MTThriftAutoConfiguration {

    @Bean(name = "com.sankuai.meituan.waimai.autoconfigure.thrift.ThriftProperties")
    @ConditionalOnMissingBean
    public MTThriftProperties redisProperties() {
        return new MTThriftProperties();
    }

    /**
     * client 自动配置
     */
    @Configuration
    @ConditionalOnExpression("'${meituan.mtthrift.client}' == 'true'")
    protected static class BaseMTThriftClientConfiguration {

        @Autowired
        private MTThriftProperties properties;

    }

    /**
     * server 自动配置
     */
    @Configuration
    @ConditionalOnExpression("'${meituan.mtthrift.client}' == 'false'")
    protected static class BaseMTThriftServerConfiguration {

        @Autowired
        private MTThriftProperties properties;


    }
}
