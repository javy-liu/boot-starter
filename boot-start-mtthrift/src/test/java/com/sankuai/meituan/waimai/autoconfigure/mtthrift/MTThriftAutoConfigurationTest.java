package com.sankuai.meituan.waimai.autoconfigure.mtthrift;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

/**
 * @author liuzhenyuan
 * @version Last modified 15/7/22
 */
public class MTThriftAutoConfigurationTest {

    private AnnotationConfigApplicationContext context;

    @Before
    public void setUp() throws Exception {
        context = new AnnotationConfigApplicationContext();
    }

    @Test
    public void testServerAutoConfiguration() throws Exception {
        EnvironmentTestUtils.addEnvironment(this.context, "meituan.mtthrift.name:mtthrift");
        EnvironmentTestUtils.addEnvironment(this.context, "meituan.mtthrift.client:false");
        EnvironmentTestUtils.addEnvironment(this.context, "meituan.mtthrift.ports:1");
        EnvironmentTestUtils.addEnvironment(this.context, "meituan.mtthrift.ports:2");
        EnvironmentTestUtils.addEnvironment(this.context, "meituan.mtthrift.ports:3, 4");
        this.context.register(MTThriftAutoConfiguration.class);
        this.context.refresh();
        System.out.println();
    }
}