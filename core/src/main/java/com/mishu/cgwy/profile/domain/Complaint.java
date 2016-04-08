package com.mishu.cgwy.profile.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 8:01 PM
 */
@Entity
@Data
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long customerId;
    private Long adminId;
    private Date createTime;
    private int complaintNumber;


}
