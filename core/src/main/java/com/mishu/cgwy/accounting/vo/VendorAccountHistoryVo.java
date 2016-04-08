package com.mishu.cgwy.accounting.vo;

import com.mishu.cgwy.accounting.domain.AccountPayable;
import com.mishu.cgwy.accounting.domain.AccountPayableWriteoff;
import com.mishu.cgwy.accounting.domain.VendorAccountHistory;
import com.mishu.cgwy.accounting.enumeration.VendorAccountOperationType;
import com.mishu.cgwy.inventory.vo.VendorVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class VendorAccountHistoryVo {

    private Date accountDate;

    private VendorVo vendor;

    private VendorAccountOperationType type;

    private Long vendorAccountOperationId;

    private BigDecimal payable = BigDecimal.ZERO;

    private BigDecimal writeOff = BigDecimal.ZERO;
}
