package com.mishu.cgwy.accounting.facade;

import com.mishu.cgwy.accounting.domain.VendorAccountHistory;
import com.mishu.cgwy.accounting.dto.VendorAccountHistoryListRequest;
import com.mishu.cgwy.accounting.service.VendorAccountHistoryService;
import com.mishu.cgwy.accounting.vo.VendorAccountHistoryVo;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class VendorAccountHistoryFacade {

    @Autowired
    private VendorAccountHistoryService vendorAccountHistoryService;

    @Transactional
    public void saveVendorAccountHistory(VendorAccountHistory vendorAccountHistory) {
        vendorAccountHistoryService.save(vendorAccountHistory);
    }

    @Transactional
    public void deleteVendorAccountHistory(Long id) {
        vendorAccountHistoryService.delete(id);
    }

    public List<VendorAccountHistory> findByPaymentId(Long id) {
        return vendorAccountHistoryService.findByPaymentId(id);
    }

    public List<VendorAccountHistory> findByAccountPayableWriteoffId(Long id) {
        return vendorAccountHistoryService.findByAccountPayableWriteoffId(id);
    }

    public QueryResponse<VendorAccountHistoryVo> getVendorAccountHistories(VendorAccountHistoryListRequest request) {
        return vendorAccountHistoryService.getVendorAccountHistories(request);
    }

    public HttpEntity<byte[]> exportVendorAccountHistoryList(VendorAccountHistoryListRequest request) throws Exception {
        List<VendorAccountHistoryVo> list = vendorAccountHistoryService.getAllVendorAccountHistories(request);

        final String fileName = "vendor-account-histories.xls";
        final HashMap<String, Object> beanParams = new HashMap<>();
        beanParams.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        List<List<VendorAccountHistoryVo>> beans = new ArrayList<>();
        beans.add(list);

        List<String> sheetNames = new ArrayList<>();
        sheetNames.add("供应商往来明细");

        return ExportExcelUtils.generateExcelBytes(beans, "vendorAccountHistories", sheetNames, beanParams, fileName, ExportExcelUtils.VENDOR_ACCOUNT_HISTORIES_TEMPLATE);
    }
}

