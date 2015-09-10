package com.sankuai.meituan.waimai.boot.starter.mtthrift.handler;

import com.meituan.service.mobile.mtthrift.server.MTIface;
import com.sankuai.meituan.waimai.boot.starter.mtthrift.annotation.MTThriftHandler;
import com.sankuai.meituan.waimai.boot.starter.mtthrift.service.UserService;
import com.sankuai.meituan.waimai.service.order.query.service.UserThriftService;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author liuzhenyuan
 * @version Last modified 15/8/31
 */
@MTThriftHandler(
        value = "testHandler",
        zkConfig =
        @MTThriftHandler.ZooKeeperConfig(
                zkServers = "192.168.2.95:9331,192.168.2.245:9331,192.168.2.209:9331",
                zkPath = "/sankuai/${app.type}/${app.key}/config/server_wmordertrans"),
        port = 9940,
        appKey = "com.sankuai.waimai.order.trans",
        clusterManager = MTThriftHandler.ClusterManagerType.MIX
)
@Slf4j
public class UserServiceHandler extends MTIface implements UserThriftService.Iface {

    @Autowired
    private UserService userService;

    @Override
    public String findUsernameById(long id) throws TException {
        log.info("===========进入handler==============");
        return userService.findUsernameById(id);
    }
}
