package com.bizdata.admin.domain;

import com.bizdata.commons.constant.LoginLogoutType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.sdevil507.base.JpaUUIDBaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * 登录登出日志实体类
 *
 * @author sdevil507
 * @version 1.0
 */
@Entity
@Table(name = "admin_login_logout")
@Data
@EqualsAndHashCode(callSuper = true)
public class Login_Logout extends JpaUUIDBaseEntity {
    /**
     * 用户名
     */
    @Column(nullable = false)
    private String username;

    /**
     * ip地址
     */
    @Column(nullable = false)
    private String ip;

    /**
     * 日志内容
     */
    @Column(nullable = false)
    private String content;

    /**
     * 登录登出类型
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginLogoutType type;

    /**
     * 时间
     */
    @Column(nullable = false)
    private Date date;
}
