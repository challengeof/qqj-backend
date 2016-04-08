package com.mishu.cgwy.accounting.wrapper;

import com.mishu.cgwy.accounting.domain.AccountPayable;
import com.mishu.cgwy.accounting.domain.VendorAccount;
import com.mishu.cgwy.accounting.enumeration.AccountPayableStatus;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.inventory.vo.VendorVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class VendorAccountWrapper {

    private VendorVo vendor;

    private BigDecimal balance;

    private BigDecimal payable;

    private BigDecimal totalHistoryPayable;

    private BigDecimal totalHistoryPayment;

    private BigDecimal totalWriteOffAmount;

    @Override
    public String toString() {
        return "VendorAccountWrapper{" +
                ", balance=" + balance +
                ", payable=" + payable +
                ", totalHistoryPayable=" + totalHistoryPayable +
                ", totalHistoryPayment=" + totalHistoryPayment +
                ", totalWriteOffAmount=" + totalWriteOffAmount +
                '}';
    }
}
