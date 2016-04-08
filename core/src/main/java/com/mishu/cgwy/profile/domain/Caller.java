
package com.mishu.cgwy.profile.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.*;

/**
 * Created by king-ck on 2015/9/29.
 */
@Data
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames={"phone"})},
        indexes = {@Index(name = "CALLER_CREATEDATE_INDEX", columnList = "createDate", unique = false),
                   @Index(name = "CALLER_MODIFYDATE_INDEX", columnList = "modifyDate", unique = false)}
)
public class Caller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phone;

    private String name;

//    @Column(length = 2000)
    private String detail;

    private Date createDate;

    private Date modifyDate;


    public Caller(){}
    public Caller( Long	id, String phone, String name, String detail, Date	createDate, Date	modifyDate){

        this.id=id;
        this.phone=phone;
        this.name=name;
        this.detail=detail;
        this.createDate=createDate;
        this.modifyDate=modifyDate;
    }

}
