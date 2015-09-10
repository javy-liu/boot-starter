package com.sankuai.meituan.waimai.boot.starter.mtthrift.autoconfigure;

import com.sankuai.meituan.waimai.boot.starter.mtthrift.annotation.MTThriftHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.embedded.RegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author liuzhenyuan
 * @version Last modified 15/8/31
 */
@Configuration
@ConditionalOnClass(MTThriftHandler.class)
public class MTThriftAutoConfiguration {

    /**
     * 抽象化配置接口
     */
    public interface ThriftConfigurer {
        void configureProxyFactory(ProxyFactory proxyFactory);
    }

    @Bean
    @ConditionalOnMissingBean(ThriftConfigurer.class)
    ThriftConfigurer thriftConfigurer() {
        return new DefaultThriftConfigurer();
    }

    @Bean
    @ConditionalOnMissingBean(TProtocolFactory.class)
    TProtocolFactory thriftProtocolFactory() {
        return new TBinaryProtocol.Factory();
    }


    public static class DefaultThriftConfigurer implements ThriftConfigurer {
        @Autowired(required = false)
        GaugeService gaugeService;

        public void configureProxyFactory(ProxyFactory proxyFactory) {
            proxyFactory.setOptimize(true);

//            if(gaugeService != null) {
//                proxyFactory.addAdvice(new MetricsThriftMethodInterceptor(gaugeService));
//            }
//
//            proxyFactory.addAdvice(new ExceptionsThriftMethodInterceptor());
        }
    }


    /**
     * 动态注册MTThrift服务
     */
    @Configuration
    public static class Registrar implements ApplicationContextAware, BeanFactoryPostProcessor {
        @Getter
        @Setter
        ApplicationContext applicationContext;

        @Autowired
        TProtocolFactory protocolFactory;

        @Autowired
        ThriftConfigurer thriftConfigurer;


        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

            String[] beanNames = applicationContext.getBeanNamesForAnnotation(MTThriftHandler.class);

            for (String beanName : beanNames) {
                MTThriftHandler annotation = configurableListableBeanFactory.findAnnotationOnBean(beanName,
                        MTThriftHandler.class);
                Object bean = configurableListableBeanFactory.getBean(beanName);
                // 注册
                registerHandler(configurableListableBeanFactory, annotation, bean);
            }
        }

        /**
         * 动态注册MTThrift服务
         *
         * @param configurableListableBeanFactory
         * @param annotation
         * @throws BeansException
         */
        protected void registerHandler(ConfigurableListableBeanFactory configurableListableBeanFactory,
                                       MTThriftHandler annotation, Object handler) throws BeansException {
            Class<?>[] handlerInterfaces = handler.getClass().getInterfaces();

            //
            Class ifaceClass = null;
            Class serviceClass = null;

            // 获取接口和实现类
            for (Class<?> handlerInterfaceClass : handlerInterfaces) {
                if (!handlerInterfaceClass.getName().endsWith("$Iface")) {
                    continue;
                }

                serviceClass = handlerInterfaceClass.getDeclaringClass();

                if (serviceClass == null) {
                    continue;
                }
                ifaceClass = handlerInterfaceClass;
                break;
            }


            System.out.println();

        }
    }

}
