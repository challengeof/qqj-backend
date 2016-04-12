package com.qqj.admin.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * User: xudong
 * Date: 3/3/15
 * Time: 10:58 AM
 */
@Entity
@Data
@Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class AdminPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String displayName;
}
