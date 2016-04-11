package com.mishu.cgwy.admin.domain;

import com.mishu.cgwy.common.domain.City;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:30 PM
 */
@Entity
@Getter
@Setter
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class AdminUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    private String realname;

    private String password;

    private boolean enabled = true;

    private String telephone;

    private boolean globalAdmin = false;


    @ManyToMany
    @JoinTable(name = "admin_user_role_xref", joinColumns = @JoinColumn(name = "admin_user_id", referencedColumnName =
            "id"), inverseJoinColumns = @JoinColumn(name = "admin_role_id", referencedColumnName =
            "id"))
    @BatchSize(size = 50)
    private Set<AdminRole> adminRoles = new HashSet<AdminRole>();

    @ManyToMany
    @JoinTable(name = "admin_user_city_xref", joinColumns = @JoinColumn(name = "admin_user_id", referencedColumnName =
            "id"), inverseJoinColumns = @JoinColumn(name = "city_id", referencedColumnName = "id"))
    private Set<City> cities = new HashSet<>();

    @Transient
    public boolean hasRole(String roleName) {
        for (AdminRole role : adminRoles) {
            if (role.getName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }
}
