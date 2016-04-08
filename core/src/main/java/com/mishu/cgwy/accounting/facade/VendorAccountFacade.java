package com.mishu.cgwy.accounting.facade;

import com.mishu.cgwy.accounting.domain.VendorAccount;
import com.mishu.cgwy.accounting.dto.VendorAccountListRequest;
import com.mishu.cgwy.accounting.service.AccountPayableService;
import com.mishu.cgwy.accounting.service.VendorAccountHistoryService;
import com.mishu.cgwy.accounting.service.VendorAccountService;
import com.mishu.cgwy.accounting.wrapper.VendorAccountWrapper;
import com.mishu.cgwy.admin.service.VendorService;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class VendorAccountFacade {

    @Autowired
    private VendorService vendorService;

    @Autowired
    private VendorAccountService vendorAccountService;

    @Autowired
    private VendorAccountHistoryService vendorAccountHistoryService;

    @Autowired
    private AccountPayableService accountPayableService;

    @Transactional
    public void updateVendorAccount(Vendor vendor, BigDecimal payable, BigDecimal balance) {
        VendorAccount vendorAccount = vendor.getAccount();
        if (vendorAccount == null) {
            vendorAccount = new VendorAccount();
            vendorAccount.setVendor(vendor);
            vendorAccount.setBalance(balance);
            vendorAccount.setPayable(payable);
            vendor.setAccount(vendorAccount);
        } else {
            BigDecimal newPayable = vendorAccount.getPayable().add(payable);
            BigDecimal newBalance = vendorAccount.getBalance().add(balance);
            vendorAccount.setPayable(newPayable);
            vendorAccount.setBalance(newBalance);
        }

        vendorService.save(vendor);
    }

    public QueryResponse<VendorAccountWrapper> getVendorAccountList(VendorAccountListRequest request) {

        List<VendorAccountWrapper> list = new ArrayList<>();
        Page<VendorAccount> vendorAccountPage = vendorAccountService.find(request);

        for (VendorAccount vendorAccount : vendorAccountPage.getContent()) {
            VendorAccountWrapper wrapper = vendorAccountHistoryService.getVendorAccount(vendorAccount.getVendor(), request.getStatisticalDate());
            list.add(wrapper);
        }

        QueryResponse<VendorAccountWrapper> res = new QueryResponse<VendorAccountWrapper>();
        res.setContent(list);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(vendorAccountPage.getTotalElements());

        return res;
    }

    public HttpEntity<byte[]> exportVendorAccounts(VendorAccountListRequest request) throws Exception {

        List<VendorAccount> vendorAccounts = vendorAccountService.getAllVendorAccounts(request);

        List<VendorAccountWrapper> list = new ArrayList<>();
        for (VendorAccount vendorAccount : vendorAccounts) {
            VendorAccountWrapper wrapper = vendorAccountHistoryService.getVendorAccount(vendorAccount.getVendor(), request.getStatisticalDate());
            list.add(wrapper);
        }

        final String fileName = "vendor-accounts.xls";
        final HashMap<String, Object> beanParams = new HashMap<>();
        beanParams.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        List<List<VendorAccountWrapper>> beans = new ArrayList<>();
        beans.add(list);

        List<String> sheetNames = new ArrayList<>();
        sheetNames.add("供应商对账-汇总");

        return ExportExcelUtils.generateExcelBytes(beans, "vendorAccounts", sheetNames, beanParams, fileName, ExportExcelUtils.VENDOR_ACCOUNTS_TEMPLATE);
    }
}

