package com.bizdata.admin.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.sdevil507.base.JpaUUIDBaseEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * 用户信息实体类
 *
 * @author sdevil507
 * @version 1.0
 */
@Entity
@Table(name = "admin_user")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"roleList"})
public class User extends JpaUUIDBaseEntity {
    /**
     * 所属机构
     */
    private String organizationId;

    /**
     * 所属机构名称
     */
    @Transient
    private String organizationName;

    /**
     * 用户名
     */
    @Column(nullable = false, unique = true, length = 40)
    private String username;

    /**
     * email
     */
    @Column(nullable = false)
    private String email;

    /**
     * 密码
     */
    @Column(nullable = false)
    private String password;

    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private Date createTime = new Date();

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 密码修改时间
     */
    private Date passwordUpdateTime = new Date();

    /**
     * 拥有的角色列表
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "admin_user_role", joinColumns = {
            @JoinColumn(name = "userid", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "roleid", referencedColumnName = "id")})
    private Set<Role> roleList;

    /**
     * 是否被锁定
     */
    @Column(nullable = false)
    private boolean locked = true;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
