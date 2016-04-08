package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.organization.domain.Organization;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class VendorAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(precision = 16, scale = 2)
    private BigDecimal balance;

    @Column(precision = 16, scale = 2)
    private BigDecimal payable;

    @Override
    public String toString() {
        return "VendorAccount{" +
                "id=" + id +
                ", balance=" + balance +
                ", payable=" + payable +
                '}';
    }
}
