package com.sankuai.meituan.waimai.autoconfigure.mtthrift;

import com.meituan.service.mobile.mtthrift.proxy.ThriftServerPublisher;
import com.sankuai.meituan.waimai.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 提供thrift server实例化配置信息
 *
 * @author liuzhenyuan
 * @version Last modified 15/7/21
 */
@Configuration
@ConditionalOnClass(value = {ThriftServerPublisher.class})
@EnableConfigurationProperties
public class ThriftServerAutoConfiguration {

    @Bean(name = "com.sankuai.meituan.waimai.autoconfigure.thrift.ThriftProperties")
    @ConditionalOnMissingBean
    public ThriftProperties redisProperties() {
        return new ThriftProperties();
    }

    @Configuration
    protected static class BaseThriftConfiguration {

        @Autowired
        private ThriftProperties properties;


        @Bean
        @ConditionalOnProperty(prefix = "meituan.mtthrift", name = {"name"})
        public A A() {
            A a = new A();

            a.setName(properties.getName());
            return a;
        }
    }
}
