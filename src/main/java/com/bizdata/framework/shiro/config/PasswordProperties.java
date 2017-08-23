package com.bizdata.framework.shiro.config;

import lombok.Data;

/**
 * Shiro框架控制密码相关的配置项
 * <p>
 * Created by sdevil507 on 2017/8/18.
 */
@Data
public class PasswordProperties {

    /**
     * 密码重试次数(默认密码重试5次,则锁定账号)
     */
    private int retryCount = 5;
}
