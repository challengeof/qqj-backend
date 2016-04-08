package com.mishu.cgwy.admin.domain;

import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.common.domain.Zone;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.redis.RedisCache;
import com.mishu.cgwy.stock.domain.Depot;
import lombok.Data;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.HashSet;
import java.util.Set;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:30 PM
 */
@Entity
@Data
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
    @JoinTable(name = "admin_user_organization_xref", joinColumns = @JoinColumn(name = "admin_user_id", referencedColumnName =
            "id"), inverseJoinColumns = @JoinColumn(name = "organization_id", referencedColumnName = "id"))
    private Set<Organization> organizations = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "admin_user_block_xref", joinColumns = @JoinColumn(name = "admin_user_id", referencedColumnName =
            "id"), inverseJoinColumns = @JoinColumn(name = "block_id", referencedColumnName = "id"))
    private Set<Block> blocks = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "admin_user_warehouse_xref", joinColumns = @JoinColumn(name = "admin_user_id", referencedColumnName =
            "id"), inverseJoinColumns = @JoinColumn(name = "warehouse_id", referencedColumnName = "id"))
    @BatchSize(size = 50)
    private Set<Warehouse> warehouses = new HashSet<Warehouse>();


    @ManyToMany
    @JoinTable(name = "admin_user_city_xref", joinColumns = @JoinColumn(name = "admin_user_id", referencedColumnName =
            "id"), inverseJoinColumns = @JoinColumn(name = "city_id", referencedColumnName = "id"))
    private Set<City> cities = new HashSet<>();


    @ManyToMany
    @JoinTable(name = "admin_user_depot_city_xref", joinColumns = @JoinColumn(name = "admin_user_id", referencedColumnName =
            "id"), inverseJoinColumns = @JoinColumn(name = "city_id", referencedColumnName = "id"))
    private Set<City> depotCities = new HashSet<>();


    @ManyToMany
    @JoinTable(name = "admin_user_depot_xref", joinColumns = @JoinColumn(name = "admin_user_id", referencedColumnName =
            "id"),inverseJoinColumns = @JoinColumn(name = "depot_id", referencedColumnName = "id"))
    private Set<Depot> depots = new HashSet<>();



    @Transient
    public boolean hasRole(String roleName) {
        for (AdminRole role : adminRoles) {
            if (role.getName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "AdminUser{" +
                "telephone='" + telephone + '\'' +
                ", enabled=" + enabled +
                ", password='" + password + '\'' +
                ", realname='" + realname + '\'' +
                ", username='" + username + '\'' +
                ", id=" + id +
                '}';
    }
}
