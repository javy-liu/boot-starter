package com.sankuai.meituan.waimai;

import com.sankuai.meituan.waimai.autoconfigure.mtthrift.ThriftAutoConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuzhenyuan
 * @version Last modified 15/7/21
 */
public class ThriftAutoConfigurationTest {

    private AnnotationConfigApplicationContext context;

    @After
    public void close() {
        this.context.close();
    }

    @Before
    public void setUp() throws Exception {
        context = new AnnotationConfigApplicationContext();
    }

    @Test
    public void test01() throws Exception {
        EnvironmentTestUtils.addEnvironment(this.context, "meituan.mtthrift.name:123");
        this.context.register(ThriftAutoConfiguration.class);
        this.context.refresh();
        System.out.println();

    }

    @Configuration
    static class TestConfiguration {

        @Bean
        public A aa() {
            return new A();
        }
    }
}