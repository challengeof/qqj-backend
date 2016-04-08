package com.mishu.cgwy.inventory.domain;

import com.mishu.cgwy.accounting.domain.VendorAccount;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.organization.domain.Organization;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:35 PM
 */
@Entity
@Getter
@Setter
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String contact;

    private String telephone;

    private String email;

    private String address;

    private String brand;

//     预付费
//    private boolean prepaid = false;

//    @Deprecated
//    private boolean selfSupport;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "account_id")
    private VendorAccount account;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "payment_vendor_id")
    private Vendor paymentVendor;

    private boolean defaultVendor;

    @Column(unique = true)
    private String username;

    private String password;
}

