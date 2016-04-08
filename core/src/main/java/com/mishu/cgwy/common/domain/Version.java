package com.mishu.cgwy.common.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by kaicheng on 4/16/15.
 */
@Entity
@Data
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer versionCode;

    private String versionName;

    private String comment;

    @OneToOne
    @JoinColumn(name = "file_id")
    private MediaFile file;

    private Integer type;

    private Integer forceUpdate;
}
