package com.qqj.org.domain;

import com.qqj.admin.domain.AdminUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private boolean enabled = true;

    @ManyToOne
    private AdminUser founder;

}
