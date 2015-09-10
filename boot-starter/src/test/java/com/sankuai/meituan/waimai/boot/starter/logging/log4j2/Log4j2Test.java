package com.sankuai.meituan.waimai.boot.starter.logging.log4j2;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author liuzhenyuan
 * @version Last modified 15/9/8
 */
@Slf4j
public class Log4j2Test {


    @Test
    public void testLog() throws Exception {


        for (int i = 0; ; i++) {
            log.info("=====================:{}", i);
            Thread.sleep(1000);
        }


    }
}
