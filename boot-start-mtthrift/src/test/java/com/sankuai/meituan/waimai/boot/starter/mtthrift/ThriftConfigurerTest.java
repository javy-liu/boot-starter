package com.sankuai.meituan.waimai.boot.starter.mtthrift;

import com.sankuai.meituan.waimai.boot.starter.mtthrift.autoconfigure.MTThriftAutoConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * @author liuzhenyuan
 * @version Last modified 15/8/31
 */
public class ThriftConfigurerTest {

    private AnnotationConfigApplicationContext context;


    private void load(Class<?> config, String... environment) {
        Collection<Class<?>> configs = new ArrayList<Class<?>>();
        configs.add(config);
        doLoad(configs, environment);
    }

    private void doLoad(Collection<Class<?>> configs,
                        String... environment) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        EnvironmentTestUtils.addEnvironment(applicationContext, environment);
        for (Class<?> config : configs) {
            applicationContext.register(config);
        }
        applicationContext.register(MTThriftAutoConfiguration.class);
        applicationContext.refresh();
        this.context = applicationContext;
    }


    @Configuration
    @ComponentScan
    static class EmptyConfiguration {

    }

    @Test
    public void testConfig() throws Exception {
        load(EmptyConfiguration.class);

        System.out.println();
    }
}