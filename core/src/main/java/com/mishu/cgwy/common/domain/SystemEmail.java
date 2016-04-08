package com.mishu.cgwy.common.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(uniqueConstraints = {@UniqueConstraint(columnNames={"city_id","type"})})
public class SystemEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int type;

    private String name;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(columnDefinition = "text")
    private String sendTo;

    @Column(columnDefinition = "text")
    private String sendCc;
}
