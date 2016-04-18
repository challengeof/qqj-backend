package com.qqj.admin.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class AdminUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    private String realname;

    private String password;

    private String telephone;

    private boolean enabled = true;

    @ManyToMany
    @JoinTable(
        name = "admin_user_role_xref",
        joinColumns = @JoinColumn(name = "admin_user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "admin_role_id", referencedColumnName = "id")
    )
    @BatchSize(size = 50)
    private Set<AdminRole> adminRoles = new HashSet<AdminRole>();

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
