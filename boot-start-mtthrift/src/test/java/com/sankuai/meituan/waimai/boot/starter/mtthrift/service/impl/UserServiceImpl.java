package com.sankuai.meituan.waimai.boot.starter.mtthrift.service.impl;

import com.sankuai.meituan.waimai.boot.starter.mtthrift.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author liuzhenyuan
 * @version Last modified 15/8/31
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Override
    public String findUsernameById(long id) {
        log.info("===========载入数据==============");
        return "jerry liu";
    }
}
