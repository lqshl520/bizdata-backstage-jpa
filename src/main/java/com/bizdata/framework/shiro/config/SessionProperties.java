package com.bizdata.framework.shiro.config;

import lombok.Data;

/**
 * Shiro框架Session配置相关
 * <p>
 * Created by sdevil507 on 2017/8/26.
 */
@Data
public class SessionProperties {
    /**
     * 默认session超时时间(60分钟,单位:s)
     */
    private long timeOut = 60 * 60;
}
