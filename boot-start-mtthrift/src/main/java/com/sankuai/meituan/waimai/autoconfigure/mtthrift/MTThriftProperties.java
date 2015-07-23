package com.sankuai.meituan.waimai.autoconfigure.mtthrift;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Set;

/**
 * @author liuzhenyuan
 * @version Last modified 15/7/21
 */
@ConfigurationProperties(prefix = "meituan.mtthrift")
@Getter
@Setter
public class MTThriftProperties {

    /**
     * 服务类型 分为客户端，服务端
     */
    private boolean client = true;

    private Set<Server> servers;

    private Set<Client> clients;

    /**
     * 如果server中没有配置 那么使用该配置
     */
    private String appKey;

    @Getter
    @Setter
    public static class Server {
        private String serviceInterface;
        private String serviceImpl;
        private Set<String> zkServers;
        private String zkPath;
        private int selectorThreads = 4;
        private int minWorkerThreads = 50;
        private int shutdownWaitTime = 15;
        private int maxWorkerThreads = 2048;
        private int port;
        private boolean daemon = true;
        private String appKey;
        private String clusterManager = "mix";
    }

    @Getter
    @Setter
    public static class Client {

    }
}
