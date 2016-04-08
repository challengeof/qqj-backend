package com.mishu.cgwy.accounting.facade;

import com.mishu.cgwy.accounting.domain.Payment;
import com.mishu.cgwy.accounting.domain.VendorAccountHistory;
import com.mishu.cgwy.accounting.dto.PaymentData;
import com.mishu.cgwy.accounting.dto.PaymentListRequest;
import com.mishu.cgwy.accounting.enumeration.PaymentStatus;
import com.mishu.cgwy.accounting.enumeration.VendorAccountOperationType;
import com.mishu.cgwy.accounting.service.CollectionPaymentMethodService;
import com.mishu.cgwy.accounting.service.PaymentService;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.admin.service.VendorService;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.inventory.domain.Vendor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class PaymentFacade {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private LocationService locationService;

    @Autowired
    AdminUserService adminUserService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private VendorAccountFacade vendorAccountFacade;

    @Autowired
    private CollectionPaymentMethodService collectionPaymentMethodService;

    @Autowired
    private VendorAccountHistoryFacade vendorAccountHistoryFacade;

    @Transactional
    public void savePaymentData(AdminUser adminUser, PaymentData paymentData) {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.NORMAL.getValue());
        payment.setAmount(paymentData.getAmount());
        payment.setCity(locationService.getCity(paymentData.getCityId()));
        payment.setCollectionPaymentMethod(collectionPaymentMethodService.getOne(paymentData.getMethodId()));
        payment.setCreator(adminUserService.getOne(adminUser.getId()));
        payment.setPayDate(paymentData.getPayDate());
        payment.setCreateDate(new Date());
        payment.setRemark(paymentData.getRemark());
        Vendor vendor = vendorService.getVendorById(paymentData.getVendorId());
        payment.setVendor(vendorService.getVendorById(paymentData.getVendorId()));
        payment = paymentService.save(payment);

        vendorAccountFacade.updateVendorAccount(vendor, BigDecimal.ZERO, paymentData.getAmount());

        VendorAccountHistory vendorAccountHistory = new VendorAccountHistory();
        vendorAccountHistory.setAccountDate(payment.getPayDate());
        vendorAccountHistory.setPayment(payment);
        vendorAccountHistory.setAmount(payment.getAmount());
        vendorAccountHistory.setUnWriteoffAmount(BigDecimal.ZERO);
        vendorAccountHistory.setVendor(vendor);
        vendorAccountHistory.setCreateDate(new Date());
        vendorAccountHistory.setType(VendorAccountOperationType.PAYMENT.getValue());

        vendorAccountHistoryFacade.saveVendorAccountHistory(vendorAccountHistory);
    }

    @Transactional
    public void cancelPayment(AdminUser adminUser, Long id) {
        Payment payment = paymentService.getOne(id);
        payment.setStatus(PaymentStatus.CANCELED.getValue());
        payment.setCancelDate(new Date());
        payment.setCanceler(adminUserService.getOne(adminUser.getId()));
        paymentService.save(payment);

        Vendor vendor = vendorService.getVendorById(payment.getVendor().getId());
        vendorAccountFacade.updateVendorAccount(vendor, BigDecimal.ZERO, new BigDecimal(0).subtract(payment.getAmount()));

        List<VendorAccountHistory> vendorAccountHistoryList = vendorAccountHistoryFacade.findByPaymentId(id);

        if (CollectionUtils.isNotEmpty(vendorAccountHistoryList)) {
            for (VendorAccountHistory history : vendorAccountHistoryList) {
                vendorAccountHistoryFacade.deleteVendorAccountHistory(history.getId());
            }
        }
    }

    public HttpEntity<byte[]> exportPayment(PaymentListRequest request) throws Exception{
        return paymentService.exportPayment(request);
    }
}

