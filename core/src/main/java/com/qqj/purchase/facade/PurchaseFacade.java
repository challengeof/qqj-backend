package com.qqj.purchase.facade;

import com.qqj.org.domain.Customer;
import com.qqj.org.domain.Stock;
import com.qqj.org.domain.StockItem;
import com.qqj.org.enumeration.CustomerAuditStatus;
import com.qqj.org.enumeration.CustomerLevel;
import com.qqj.org.facade.OrgFacade;
import com.qqj.org.service.CustomerService;
import com.qqj.product.controller.PurchaseInfo;
import com.qqj.product.domain.Product;
import com.qqj.product.service.ProductService;
import com.qqj.purchase.controller.AuditPurchaseRequest;
import com.qqj.purchase.controller.PurchaseListRequest;
import com.qqj.purchase.controller.PurchaseRequest;
import com.qqj.purchase.domain.Purchase;
import com.qqj.purchase.domain.PurchaseItem;
import com.qqj.purchase.enumeration.PurchaseAuditStatus;
import com.qqj.purchase.service.PurchaseService;
import com.qqj.purchase.wrapper.PurchaseWrapper;
import com.qqj.response.Response;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PurchaseFacade {

    @Autowired
    private OrgFacade orgFacade;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    public Response purchase(Customer parent, PurchaseRequest purchaseRequest) {
        Purchase purchase = new Purchase();
        purchase.setCustomer(customerService.getCustomerById(purchaseRequest.getCustomerId()));
        purchase.setCreateTime(new Date());
        purchase.setDirectLeader(orgFacade.getDirectLeader(parent));
        purchase.setTeam(parent.getTeam());

        //如果是直属总代注册补货，直接提交总部审批，否则由直属总代审批。
        if (CustomerLevel.get(parent.getLevel()) == CustomerLevel.LEVEL_0) {
            purchase.setStatus(PurchaseAuditStatus.WAITING_HQ.getValue());
        } else {
            purchase.setStatus(PurchaseAuditStatus.WAITING_DIRECT_LEADER.getValue());
        }

        List<PurchaseItem> purchaseItems = new ArrayList<PurchaseItem>();

        for (PurchaseInfo purchaseInfo : purchaseRequest.getPurchaseInfoList()) {
            PurchaseItem purchaseItem = new PurchaseItem();
            purchaseItem.setProduct(productService.get(purchaseInfo.getProductId()));
            purchaseItem.setQuantity(purchaseInfo.getQuantity());
            purchaseItem.setPurchase(purchase);
            purchaseItems.add(purchaseItem);
        }

        purchase.setPurchaseItems(purchaseItems);

        purchaseService.save(purchase);

        return Response.successResponse;
    }

    @Transactional
    public Response auditPurchase(AuditPurchaseRequest request) {
        Purchase purchase = purchaseService.get(request.getPurchaseId());

        purchase = purchaseService.auditPurchase(purchase, request);

        CustomerAuditStatus status = CustomerAuditStatus.get(purchase.getStatus());
        if (status == CustomerAuditStatus.PASS) {
            return addStock(purchase);
        }

        return Response.successResponse;
    }

    @Transactional
    private Response addStock(Purchase purchase) {
        Customer customer = purchase.getCustomer();
        Stock stock = customer.getStock();

        List<StockItem> additionalStockItems = new ArrayList<>();
        for (PurchaseItem purchaseItem : purchase.getPurchaseItems()) {
            Product tmpProduct = purchaseItem.getProduct();
            long tmpProductId = tmpProduct.getId();
            int tmpQuantity = purchaseItem.getQuantity();
            boolean exists = false;
            for (StockItem stockItem : stock.getStockItems()) {
                long productId = stockItem.getProduct().getId();
                if (tmpProductId == productId) {
                    exists = true;
                    stockItem.setQuantity(stockItem.getQuantity() + tmpQuantity);
                    break;
                }
            }
            if (!exists) {
                StockItem stockItem = new StockItem();
                stockItem.setQuantity(tmpQuantity);
                stockItem.setStock(stock);
                stockItem.setProduct(tmpProduct);
                additionalStockItems.add(stockItem);
            }
        }

        if (CollectionUtils.isNotEmpty(additionalStockItems)) {
            stock.getStockItems().addAll(additionalStockItems);
        }

        return Response.successResponse;
    }

    public Response<PurchaseWrapper> getPurchaseList(Customer currentCustomer, PurchaseListRequest request) {
        return purchaseService.getPurchaseList(currentCustomer, request);
    }
}