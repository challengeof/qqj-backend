package com.mishu.cgwy.purchase.facade;

import com.mishu.cgwy.order.domain.CutOrder;
import com.mishu.cgwy.order.service.CutOrderService;
import com.mishu.cgwy.purchase.controller.CutOrderListRequest;
import com.mishu.cgwy.purchase.enumeration.CutOrderStatus;
import com.mishu.cgwy.purchase.vo.CutOrderVo;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CutOrderFacade {
    @Autowired
    private CutOrderService cutOrderService;

    public QueryResponse<CutOrderVo> getCutOrderList(CutOrderListRequest request) {
        return cutOrderService.getCutOrderList(request);
    }

    @Transactional
    public void createAccordingResult(List<Long> cutOrders) {
        for (Long cutOrderId : cutOrders) {
            CutOrder cutOrder = cutOrderService.getOne(cutOrderId);
            cutOrder.setStatus(CutOrderStatus.NOTCOMMITED.getValue());
            cutOrderService.saveCutOrder(cutOrder);
        }
    }
}

