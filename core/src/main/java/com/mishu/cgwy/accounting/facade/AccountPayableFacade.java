package com.mishu.cgwy.accounting.facade;

import com.mishu.cgwy.accounting.domain.*;
import com.mishu.cgwy.accounting.dto.AccountPayableData;
import com.mishu.cgwy.accounting.dto.AccountPayableListRequest;
import com.mishu.cgwy.accounting.dto.AccountPaymentWriteOffListRequest;
import com.mishu.cgwy.accounting.enumeration.AccountPayableStatus;
import com.mishu.cgwy.accounting.enumeration.AccountPayableType;
import com.mishu.cgwy.accounting.enumeration.AccountPayableWriteOffStatus;
import com.mishu.cgwy.accounting.enumeration.VendorAccountOperationType;
import com.mishu.cgwy.accounting.service.AccountPayableService;
import com.mishu.cgwy.accounting.service.AccountPayableWriteOffService;
import com.mishu.cgwy.accounting.service.VendorAccountService;
import com.mishu.cgwy.accounting.wrapper.AccountPayableWrapper;
import com.mishu.cgwy.accounting.wrapper.AccountPayableWriteOffWrapper;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.admin.service.VendorService;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.*;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class AccountPayableFacade {

    @Autowired
    private AccountPayableService accountPayableService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private VendorAccountFacade vendorAccountFacade;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private AccountPayableWriteOffService accountPayableWriteOffService;

    @Autowired
    private VendorAccountService vendorAccountService;

    @Autowired
    private VendorAccountHistoryFacade vendorAccountHistoryFacade;

    @Transactional
    public void saveAccountPayable(StockIn stockIn) {

        Vendor vendor = stockIn.getPurchaseOrder().getVendor().getPaymentVendor();
        AccountPayable accountPayable = new AccountPayable();
        accountPayable.setVendor(vendor);
        accountPayable.setCreateDate(stockIn.getReceiveDate());
        accountPayable.setWriteOffAmount(BigDecimal.ZERO);
        accountPayable.setAmount(stockIn.getAmount());
        if (accountPayable.getAmount().compareTo(BigDecimal.ZERO) != 0) {
            accountPayable.setStatus(AccountPayableStatus.UNWRITEOFF.getValue());
        } else {
            accountPayable.setStatus(AccountPayableStatus.WRITEOFF.getValue());
            accountPayable.setWriteOffer(stockIn.getReceiver());
            accountPayable.setWriteOffDate(accountPayable.getCreateDate());
        }
        accountPayable.setType(AccountPayableType.PURCHASE.getValue());
        accountPayable.setStockIn(stockIn);

        List<AccountPayableItem> accountPayableItems = new ArrayList<>();

        for (StockInItem stockInItem : stockIn.getStockInItems()) {
            if (stockInItem.getRealQuantity() > 0) {
                AccountPayableItem accountPayableItem = new AccountPayableItem();
                accountPayableItem.setSku(stockInItem.getSku());
                accountPayableItem.setAccountPayable(accountPayable);
                accountPayableItem.setPrice(stockInItem.getPrice());
                accountPayableItem.setQuantity(stockInItem.getRealQuantity());
                accountPayableItem.setTaxRate(stockInItem.getTaxRate());
                accountPayableItems.add(accountPayableItem);
            }
        }

        accountPayable.setAccountPayableItems(accountPayableItems);
        accountPayable = accountPayableService.save(accountPayable);
        vendorAccountFacade.updateVendorAccount(vendor, accountPayable.getAmount(), BigDecimal.ZERO);

        if (accountPayable.getAmount().compareTo(BigDecimal.ZERO) != 0) {
            VendorAccountHistory vendorAccountHistory = new VendorAccountHistory();
            vendorAccountHistory.setAccountDate(accountPayable.getCreateDate());
            vendorAccountHistory.setAccountPayable(accountPayable);
            vendorAccountHistory.setUnWriteoffAmount(accountPayable.getAmount());
            vendorAccountHistory.setAmount(BigDecimal.ZERO);
            vendorAccountHistory.setVendor(vendor);
            vendorAccountHistory.setCreateDate(new Date());
            vendorAccountHistory.setType(VendorAccountOperationType.PAYABLE.getValue());
            vendorAccountHistoryFacade.saveVendorAccountHistory(vendorAccountHistory);
        }
    }

    public QueryResponse<AccountPayableWrapper> getAccountPayables(AccountPayableListRequest request) {
        return accountPayableService.getAccountPayables(request);
    }

    @Transactional
    public void writeOff(AdminUser adminUser, List<AccountPayableData> accountPayableDataList) {
        AdminUser writeOffer = adminUserService.getOne(adminUser.getId());

        for (AccountPayableData accountPayableData : accountPayableDataList) {

            Date writeOffDate = accountPayableData.getWriteOffDate();
            BigDecimal toWriteOff = accountPayableData.getCurrentWriteOffAmount();

            AccountPayable accountPayable = accountPayableService.getOne(accountPayableData.getId());
            accountPayable.setWriteOffDate(writeOffDate);
            accountPayable.setWriteOffer(writeOffer);

            BigDecimal writeOffAmount = accountPayable.getWriteOffAmount() == null ? BigDecimal.ZERO : accountPayable.getWriteOffAmount();
            accountPayable.setWriteOffAmount(writeOffAmount.add(toWriteOff));

            int writeOff = accountPayable.getWriteOffAmount().abs().compareTo(accountPayable.getAmount().abs());
            if (writeOff == 0) {
                accountPayable.setStatus(AccountPayableStatus.WRITEOFF.getValue());
            } else if (accountPayable.getWriteOffAmount().compareTo(new BigDecimal(0)) == 0) {
                accountPayable.setStatus(AccountPayableStatus.UNWRITEOFF.getValue());
            } else if (writeOff < 0) {
                accountPayable.setStatus(AccountPayableStatus.PARTWRITEOFF.getValue());
            }

            accountPayable = accountPayableService.save(accountPayable);

            AccountPayableWriteoff accountPayableWriteoff = new AccountPayableWriteoff();
            accountPayableWriteoff.setWriteOffAmount(toWriteOff);
            accountPayableWriteoff.setWriteOffer(writeOffer);
            accountPayableWriteoff.setWriteOffDate(writeOffDate);
            accountPayableWriteoff.setAccountPayable(accountPayable);
            accountPayableWriteoff.setCreateDate(new Date());
            accountPayableWriteoff.setStatus(AccountPayableWriteOffStatus.NORMAL.getValue());

            accountPayableWriteoff = accountPayableWriteOffService.save(accountPayableWriteoff);

            VendorAccount vendorAccount = accountPayable.getVendor().getAccount();
            vendorAccount.setBalance(vendorAccount.getBalance().subtract(toWriteOff));
            vendorAccount.setPayable(vendorAccount.getPayable().subtract(toWriteOff));
            vendorAccountService.save(vendorAccount);

            VendorAccountHistory vendorAccountHistory = new VendorAccountHistory();
            vendorAccountHistory.setAccountPayable(accountPayable);
            vendorAccountHistory.setAccountDate(accountPayableWriteoff.getWriteOffDate());
            vendorAccountHistory.setAccountPayableWriteoff(accountPayableWriteoff);
            BigDecimal historyAmount = new BigDecimal(0).subtract(toWriteOff);
            vendorAccountHistory.setUnWriteoffAmount(historyAmount);
            vendorAccountHistory.setAmount(historyAmount);
            vendorAccountHistory.setVendor(accountPayable.getVendor());
            vendorAccountHistory.setCreateDate(new Date());
            vendorAccountHistory.setType(VendorAccountOperationType.WRITEOFF.getValue());

            vendorAccountHistoryFacade.saveVendorAccountHistory(vendorAccountHistory);
        }
    }

    public QueryResponse<AccountPayableWriteOffWrapper> getAccountPayableWriteOffs(AccountPaymentWriteOffListRequest request) {
        return accountPayableService.getAccountPayableWriteOffs(request);
    }

    @Transactional
    public void writeOffCancel(AdminUser adminUser, Long id, Date cancelDate) {
        AccountPayableWriteoff accountPayableWriteoff = accountPayableWriteOffService.getOne(id);
        accountPayableWriteoff.setStatus(AccountPayableWriteOffStatus.CANCELED.getValue());
        accountPayableWriteoff.setCanceler(adminUserService.getOne(adminUser.getId()));
        accountPayableWriteoff.setCancelDate(cancelDate);
        accountPayableWriteoff.setRealCancelDate(new Date());
        accountPayableWriteOffService.save(accountPayableWriteoff);

        AccountPayable accountPayable = accountPayableService.getOne(accountPayableWriteoff.getAccountPayable().getId());
        accountPayable.setWriteOffAmount(accountPayable.getWriteOffAmount().subtract(accountPayableWriteoff.getWriteOffAmount()));

        int writeOff = accountPayable.getWriteOffAmount().abs().compareTo(accountPayable.getAmount().abs());
        if (writeOff == 0) {
            accountPayable.setStatus(AccountPayableStatus.WRITEOFF.getValue());
        } else if (accountPayable.getWriteOffAmount().compareTo(new BigDecimal(0)) == 0) {
            accountPayable.setStatus(AccountPayableStatus.UNWRITEOFF.getValue());
        } else if (writeOff < 0) {
            accountPayable.setStatus(AccountPayableStatus.PARTWRITEOFF.getValue());
        }

        accountPayableService.save(accountPayable);

        List<VendorAccountHistory> vendorAccountHistories = vendorAccountHistoryFacade.findByAccountPayableWriteoffId(accountPayableWriteoff.getId());
        if (CollectionUtils.isNotEmpty(vendorAccountHistories)) {
            VendorAccountHistory vendorAccountHistory = vendorAccountHistories.get(0);
            vendorAccountHistoryFacade.deleteVendorAccountHistory(vendorAccountHistory.getId());
            VendorAccount vendorAccount = accountPayable.getVendor().getAccount();
            vendorAccount.setBalance(vendorAccount.getBalance().subtract(vendorAccountHistory.getAmount()));
            vendorAccount.setPayable(vendorAccount.getPayable().subtract(vendorAccountHistory.getAmount()));
            vendorAccountService.save(vendorAccount);
        }
    }

    @Transactional
    public void saveAccountPayable(StockOut stockOut) {

        Vendor vendor = stockOut.getReturnNote().getPurchaseOrder().getVendor().getPaymentVendor();
        AccountPayable accountPayable = new AccountPayable();
        accountPayable.setVendor(vendor);
        accountPayable.setCreateDate(stockOut.getFinishDate());
        accountPayable.setType(AccountPayableType.RETURN.getValue());
        accountPayable.setWriteOffAmount(BigDecimal.ZERO);
        accountPayable.setAmount(stockOut.getAmount().multiply(new BigDecimal(-1)));
        if (accountPayable.getAmount().compareTo(BigDecimal.ZERO) != 0) {
            accountPayable.setStatus(AccountPayableStatus.UNWRITEOFF.getValue());
        } else {
            accountPayable.setStatus(AccountPayableStatus.WRITEOFF.getValue());
            accountPayable.setWriteOffer(stockOut.getReceiver());
            accountPayable.setWriteOffDate(accountPayable.getCreateDate());
        }
        accountPayable.setStockOut(stockOut);

        List<AccountPayableItem> accountPayableItems = new ArrayList<>();

        for (StockOutItem stockOutItem : stockOut.getStockOutItems()) {
            if (StockOutItemStatus.DISTRIBUTED.getValue().equals(stockOutItem.getStatus()) && stockOutItem.getRealQuantity() > 0) {
                AccountPayableItem accountPayableItem = new AccountPayableItem();
                accountPayableItem.setSku(stockOutItem.getSku());
                accountPayableItem.setAccountPayable(accountPayable);
                accountPayableItem.setPrice(stockOutItem.getPrice());
                accountPayableItem.setQuantity(stockOutItem.getRealQuantity());
                accountPayableItem.setTaxRate(stockOutItem.getTaxRate());
                accountPayableItems.add(accountPayableItem);
            }
        }

        accountPayable.setAccountPayableItems(accountPayableItems);
        accountPayable = accountPayableService.save(accountPayable);
        vendorAccountFacade.updateVendorAccount(vendor, accountPayable.getAmount(), BigDecimal.ZERO);

        if (accountPayable.getAmount().compareTo(BigDecimal.ZERO) != 0) {
            VendorAccountHistory vendorAccountHistory = new VendorAccountHistory();
            vendorAccountHistory.setAccountDate(accountPayable.getCreateDate());
            vendorAccountHistory.setAccountPayable(accountPayable);
            vendorAccountHistory.setUnWriteoffAmount(accountPayable.getAmount());
            vendorAccountHistory.setAmount(BigDecimal.ZERO);
            vendorAccountHistory.setVendor(vendor);
            vendorAccountHistory.setCreateDate(new Date());
            vendorAccountHistory.setType(VendorAccountOperationType.PAYABLE.getValue());
            vendorAccountHistoryFacade.saveVendorAccountHistory(vendorAccountHistory);
        }
    }

    public HttpEntity<byte[]> exportAccountPayables(AccountPayableListRequest request) throws Exception {

        List<List<AccountPayableWrapper>> beans = new ArrayList<>();
        beans.add(accountPayableService.getAllAccountPayables(request));

        final HashMap<String, Object> beanParams = new HashMap<>();

        List<String> sheetNames = new ArrayList<>();
        sheetNames.add("应付列表");

        return ExportExcelUtils.generateExcelBytes(beans, "accountPayables", sheetNames, beanParams, "account-payables.xls", ExportExcelUtils.ACCOUNT_PAYABLES_TEMPLATE);
    }

    public HttpEntity<byte[]> exportAccountPayableWriteOffs(AccountPaymentWriteOffListRequest request) throws Exception {

        List<List<AccountPayableWriteOffWrapper>> beans = new ArrayList<>();
        beans.add(accountPayableService.getAllAccountPayableWriteOffs(request));

        final HashMap<String, Object> beanParams = new HashMap<>();

        List<String> sheetNames = new ArrayList<>();
        sheetNames.add("应付款核销列表");

        return ExportExcelUtils.generateExcelBytes(beans, "accountPayableWriteOffs", sheetNames, beanParams, "account-payable-writeoffs.xls", ExportExcelUtils.ACCOUNT_PAYABLE_WRITEOFFS_TEMPLATE);
    }


}

