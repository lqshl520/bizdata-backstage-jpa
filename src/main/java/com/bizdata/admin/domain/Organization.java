package com.bizdata.admin.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.sdevil507.base.JpaUUIDBaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 组织机构表
 *
 * @author sdevil507
 * @version 1.0
 */
@Entity
@Table(name = "admin_organization")
@Data
@EqualsAndHashCode(callSuper = true)
public class Organization extends JpaUUIDBaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(updatable = false, nullable = false)
    private String parent;

    @Column(nullable = false)
    private Boolean expanded = true;

    @Column(nullable = false)
    private Boolean loaded = true;

    @Column(updatable = false, nullable = false)
    private int level;

    @Column(nullable = false)
    private Boolean isleaf = true;
}
