package com.sankuai.meituan.waimai.autoconfigure.mtthrift;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liuzhenyuan
 * @version Last modified 15/7/21
 */
@ConfigurationProperties(prefix = "meituan.mtthrift")
public class ThriftProperties {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
