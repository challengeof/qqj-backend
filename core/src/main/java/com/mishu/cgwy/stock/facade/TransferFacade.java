package com.mishu.cgwy.stock.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.service.SkuService;
import com.mishu.cgwy.purchase.wrapper.SkuInfo;
import com.mishu.cgwy.stock.domain.Transfer;
import com.mishu.cgwy.stock.domain.TransferItem;
import com.mishu.cgwy.stock.dto.StockQueryRequest;
import com.mishu.cgwy.stock.dto.TransferData;
import com.mishu.cgwy.stock.service.StockService;
import com.mishu.cgwy.stock.service.StockTotalService;
import com.mishu.cgwy.stock.service.TransferService;
import com.mishu.cgwy.stock.wrapper.TransferItemWrapper;
import com.mishu.cgwy.stock.wrapper.TransferWrapper;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransferFacade {
    private static Logger logger = LoggerFactory.getLogger(TransferFacade.class);

    @Autowired
    private SkuService skuService;

    @Autowired
    private StockTotalService stockTotalService;

    @Autowired
    private StockService stockService;

    @Autowired
    private TransferService transferService;

    @Autowired
    private StockOutFacade stockOutFacade;

    public SkuInfo getSkuInfo(Long cityId, Long skuId, String[] depotIds) {

        Sku sku = skuService.findOne(skuId);

        int[] stocks = null;
        if (ArrayUtils.isNotEmpty(depotIds)) {
            stocks = new int[depotIds.length];
            int i = 0;
            for (String depotIdStr : depotIds) {
                StockQueryRequest request = new StockQueryRequest();
                request.setCityId(cityId);
                request.setDepotId(Long.valueOf(depotIdStr));
                request.setSkuId(skuId);
                Integer stock = stockService.findDepotStockTotal(request);
                stocks[i++] = stock == null ? 0 : stock.intValue();
            }
        }

        return new SkuInfo(sku, null, stocks);
    }

    @Transactional
    public void transferAudit(AdminUser adminUser, TransferData transferData) {

        Transfer transfer = transferService.audit(adminUser, transferData);
        if (transferData.getApprovalResult()) {
            stockOutFacade.createStockOut(transfer);
        }
    }

    @Transactional(readOnly = true)
    public TransferWrapper getTransfer(Long id) {

        Transfer transfer = transferService.getTransfer(id);
        List<TransferItemWrapper> wrappers = new ArrayList<>();
        for (TransferItem item : transfer.getTransferItems()) {
            TransferItemWrapper wrapper = new TransferItemWrapper(item);
            wrapper.setSourceDepotStock(transferService.getStock(transfer.getSourceDepot(), item.getSku().getId()));
            wrapper.setTargetDepotStock(transferService.getStock(transfer.getTargetDepot(), item.getSku().getId()));
            wrappers.add(wrapper);
        }
        TransferWrapper transferWrapper = new TransferWrapper(transfer);
        transferWrapper.setItems(wrappers);

        return transferWrapper;
    }
}

