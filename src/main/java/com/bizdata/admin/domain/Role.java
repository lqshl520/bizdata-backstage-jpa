package com.bizdata.admin.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.sdevil507.base.JpaUUIDBaseEntity;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "admin_role")
@Data
@EqualsAndHashCode(exclude = {"resourceList", "userList"}, callSuper = true)
public class Role extends JpaUUIDBaseEntity {
    private String role; // 角色标识 程序中判断使用,如"admin"
    private String description; // 角色描述,UI界面显示使用

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "admin_role_resource", joinColumns = {
            @JoinColumn(name = "roleid", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "resourceid", referencedColumnName = "id")})
    private List<Resource> resourceList; // 拥有的资源

    /**
     * 对应该角色的用户列表
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "admin_user_role", joinColumns = {
            @JoinColumn(name = "roleid", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "userid", referencedColumnName = "id")})
    private Set<User> userList;

    public Role() {
    }

    public Role(String role, String description) {
        this.role = role;
        this.description = description;
    }
}
