package com.sankuai.meituan.waimai.boot.starter.mtthrift.annotation;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * 标识MTThrift Server
 * @author liuzhenyuan
 * @version Last modified 15/8/31
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
@ComponentScan
public @interface MTThriftHandler {
    /**
     * 注册bean的名称
     *
     * @return
     */
    String[] value() default {};

    /**
     * zk 配置
     * @return
     */
    ZooKeeperConfig zkConfig();

    int selectorThreads() default 4;
    int minWorkerThreads() default 50;
    int shutdownWaitTime() default 10;
    int maxWorkerThreads() default 256;
    int port();
    boolean daemon() default true;
    String appKey();

    ClusterManagerType clusterManager() default ClusterManagerType.ZK;

    /**
     * 配置zk
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({})
    @interface ZooKeeperConfig {
        String zkServers() default "";
        String zkPath() default "";
    }

    enum ClusterManagerType {
        ZK,MIX
    }
}
