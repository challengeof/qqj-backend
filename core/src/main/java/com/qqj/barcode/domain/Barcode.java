package com.qqj.barcode.domain;

import com.qqj.admin.domain.AdminUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bowen on 16/4/26.
 */
@Entity
@Getter
@Setter
public class Barcode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date createTime;

    @ManyToOne
    @JoinColumn(name = "admin_user_id")
    private AdminUser operator;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "barcode_id")
    private List<BarcodeItem> barcodeItems = new ArrayList<BarcodeItem>();

    private String boxCode;

    private String expressNo;

}
