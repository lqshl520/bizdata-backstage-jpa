package com.bizdata.framework.shiro.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Shiro相关配置(读取application.yml中配置项)
 * <p>
 * Created by sdevil507 on 2017/8/18.
 */
@Component
@ConfigurationProperties(prefix = "shiro")
@Data
public class ShiroConfigProperties {

    /**
     * 控制同一账号登陆人数相关配置
     */
    private KickoutProperties kickout;

    /**
     * 控制密码相关配置
     */
    private PasswordProperties password;

    /**
     * 控制cookie相关配置
     */
    private CookieProperties cookie;
}
