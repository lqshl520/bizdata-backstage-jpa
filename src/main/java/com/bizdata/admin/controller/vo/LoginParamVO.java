package com.bizdata.admin.controller.vo;

import lombok.Data;

/**
 * 登录实体类
 * <p>
 * Created by sdevil507 on 2017/8/17.
 */
@Data
public class LoginParamVO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否记住我
     */
    private boolean rememberMe;
}
