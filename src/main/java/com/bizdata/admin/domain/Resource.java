package com.bizdata.admin.domain;

import com.bizdata.commons.constant.ResourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.sdevil507.base.JpaUUIDBaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admin_resource")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"menus"})
public class Resource extends JpaUUIDBaseEntity {
    /**
     * 资源名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 资源类型
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType type;

    /**
     * 资源路径
     */
    private String url;

    /**
     * 权限字符串
     */
    @Column(nullable = false)
    private String permission;

    /**
     * 资源图标
     */
    private String icon;

    /**
     * 是否是初始化的
     */
    @Column(nullable = false)
    private Boolean isInitialized = false;

    /**
     * 排序号
     */
    @Column(nullable = false)
    private Integer sortNum;

    /**
     * 父id
     */
    @Column(updatable = false)
    private String parent = "";

    /**
     * 是否展开
     */
    @Column(nullable = false)
    private Boolean expanded = true;

    /**
     * 加载
     */
    @Column(nullable = false)
    private Boolean loaded = true;

    /**
     * 层级
     */
    @Column(updatable = false, nullable = false)
    private int level;

    /**
     * 是否叶子节点
     */
    private Boolean isleaf = true;

    /**
     * 是否是根节点
     */
    private Boolean root = false;

    /**
     * 如果是column 栏目类型，则有包含的menu 菜单
     */
    @Transient
    private List<Resource> menus = new ArrayList<>();
}
