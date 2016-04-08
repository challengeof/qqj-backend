package com.mishu.cgwy.vendor.facade;

import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.purchase.service.PurchaseOrderService;
import com.mishu.cgwy.response.Response;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.utils.EntityUtils;
import com.mishu.cgwy.vendor.controller.VendorOrderHistoryListRequest;
import com.mishu.cgwy.vendor.domain.VendorOrderItem;
import com.mishu.cgwy.vendor.service.VendorOrderItemService;
import com.mishu.cgwy.vendor.wrapper.VendorOrderHistory;
import com.mishu.cgwy.vendor.wrapper.VendorOrderItemWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by guodong on 15/12/29.
 */
@Service
public class VendorOrderFacade {

    @Autowired
    private VendorOrderItemService vendorOrderItemService;

    @Transactional
    public QueryResponse<VendorOrderItemWrapper> getVendorOrderItemsNotReady(Vendor vendor, Long depotId) {
        List<VendorOrderItem> vendorOrderItems = sumWhenDepotIdIsNull(depotId, vendorOrderItemService.getVendorOrderItemsNotReady(vendor.getId(), depotId));

        List<VendorOrderItemWrapper> wrappers = new ArrayList<>();
        for (VendorOrderItem vendorOrderItem : vendorOrderItems) {
            wrappers.add(new VendorOrderItemWrapper(vendorOrderItem, VendorOrderItemWrapper.VendorOrderItemCalculateType.NOT_READY));
        }

        return new QueryResponse(wrappers);
    }

    private List<VendorOrderItem> sumWhenDepotIdIsNull(Long depotId, List<VendorOrderItem> items) {
        if (depotId != null) {
            return items;
        }

        Map<Long, VendorOrderItem> sumMap = new HashMap<Long, VendorOrderItem>();

        for (VendorOrderItem item : items) {
            Sku sku = item.getSku();
            VendorOrderItem vendorOrderSum = sumMap.get(sku.getId());

            if (vendorOrderSum == null) {
                vendorOrderSum = new VendorOrderItem();
                vendorOrderSum.setSku(sku);
                vendorOrderSum.setDepot(item.getDepot());
                vendorOrderSum.setVendor(item.getVendor());
                vendorOrderSum.setQuantityNeed(item.getQuantityNeed());
                vendorOrderSum.setQuantityReady(item.getQuantityReady());
                sumMap.put(sku.getId(), vendorOrderSum);
            } else {
                vendorOrderSum.setQuantityNeed(vendorOrderSum.getQuantityNeed() + item.getQuantityNeed());
                vendorOrderSum.setQuantityReady(vendorOrderSum.getQuantityReady() + item.getQuantityReady());
            }

        }

        return new ArrayList<VendorOrderItem>(sumMap.values());
    }


    public QueryResponse<VendorOrderItemWrapper> getVendorOrderItemsReady(Vendor vendor, Long depotId) {
        List<VendorOrderItem> vendorOrderItems = sumWhenDepotIdIsNull(depotId, vendorOrderItemService.getVendorOrderItemsReady(vendor.getId(), depotId));

        List<VendorOrderItemWrapper> wrappers = new ArrayList<>();
        for (VendorOrderItem vendorOrderItem : vendorOrderItems) {
            wrappers.add(new VendorOrderItemWrapper(vendorOrderItem, VendorOrderItemWrapper.VendorOrderItemCalculateType.READY));
        }

        return new QueryResponse(wrappers);
    }

    public Response submit(Vendor vendor, Long[] ids) {
        return vendorOrderItemService.submit(vendor, ids);
    }

    public QueryResponse<VendorOrderHistory> getVendorOrderHistory(Vendor vendor, VendorOrderHistoryListRequest request) {
        return vendorOrderItemService.getVendorOrderHistory(vendor, request);
    }
}
