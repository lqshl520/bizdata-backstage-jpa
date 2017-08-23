package com.bizdata.framework.shiro.config;

import lombok.Data;

/**
 * Shiro框架控制同一账号同时登陆人数配置
 * <p>
 * Created by sdevil507 on 2017/8/18.
 */
@Data
public class KickoutProperties {

    /**
     * 同一账号同时登陆人数(默认1)
     */
    private int maxSession = 1;

    /**
     * 超过同一账号同时登陆人数,踢出人员方式(默认踢出第一个)
     * <p>
     * true:最后一个用户无法登陆
     * false:踢出第一个登陆的用户
     */
    private boolean kickoutAfter = false;
}
