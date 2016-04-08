package com.mishu.cgwy.accounting.facade;

import com.mishu.cgwy.accounting.dto.AccountPayableItemListRequest;
import com.mishu.cgwy.accounting.service.AccountPayableItemService;
import com.mishu.cgwy.accounting.wrapper.AccountPayableItemWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AccountPayableItemFacade {
    @Autowired
    private AccountPayableItemService accountPayableItemService;

    public QueryResponse<AccountPayableItemWrapper> getAccountPayableItems(AccountPayableItemListRequest request) {
        return accountPayableItemService.getAccountPayableItems(request);
    }

    public HttpEntity<byte[]> exportPayableItems(AccountPayableItemListRequest request) throws Exception {
        List<AccountPayableItemWrapper> list = accountPayableItemService.getAllAccountPayableItems(request);

        final String fileName = "account-payable-items.xls";
        final HashMap<String, Object> beans = new HashMap<>();
        beans.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        beans.put("accountPayableItems", list);

        BigDecimal total = new BigDecimal(0);
        for (AccountPayableItemWrapper wrapper : list) {
            total = total.add(new BigDecimal(wrapper.getQuantity()).multiply(wrapper.getPrice()));
        }

        total = total.setScale(2, RoundingMode.HALF_UP);
        beans.put("total", total);

        return ExportExcelUtils.generateExcelBytes(beans, fileName, ExportExcelUtils.ACCOUNT_PAYABLE_ITEMS_TEMPLATE);
    }
}

