package com.mishu.cgwy.purchase.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.purchase.controller.ReturnNoteData;
import com.mishu.cgwy.purchase.controller.ReturnNoteItemData;
import com.mishu.cgwy.purchase.controller.ReturnNoteRequest;
import com.mishu.cgwy.purchase.domain.*;
import com.mishu.cgwy.purchase.service.PurchaseOrderItemService;
import com.mishu.cgwy.purchase.service.PurchaseOrderService;
import com.mishu.cgwy.purchase.service.ReturnNoteService;
import com.mishu.cgwy.purchase.vo.ReturnNoteItemVo;
import com.mishu.cgwy.purchase.vo.ReturnNoteVo;
import com.mishu.cgwy.stock.facade.StockOutFacade;
import com.mishu.cgwy.stock.service.DepotService;
import com.mishu.cgwy.stock.wrapper.DepotWrapper;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ReturnNoteFacade {

    private static Logger logger = LoggerFactory.getLogger(ReturnNoteFacade.class);

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private DepotService depotService;

    @Autowired
    private ReturnNoteService returnNoteService;

    @Autowired
    private StockOutFacade stockOutFacade;

    @Autowired
    private PurchaseOrderItemService purchaseOrderItemService;

    public ReturnNoteVo getReturnNoteTmp(Long id) {

        PurchaseOrder purchaseOrder = purchaseOrderService.getOne(id);

        ReturnNoteVo returnNoteWrapper = new ReturnNoteVo();
        returnNoteWrapper.setVendor(purchaseOrder.getVendor().getName());
        returnNoteWrapper.setDepot(new DepotWrapper(purchaseOrder.getDepot()));
        returnNoteWrapper.setType(PurchaseOrderType.fromInt(purchaseOrder.getType()));

        List<ReturnNoteItemVo> returnNoteItems = new ArrayList<>();

        for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
            if (item.getPurchaseQuantity() > 0) {
                ReturnNoteItemVo returnNoteItem = new ReturnNoteItemVo();
                returnNoteItem.setReturnPrice(item.getPrice());
                returnNoteItem.setReturnQuantity(0);
                returnNoteItem.setPurchaseOrderItem(purchaseOrderItemService.purchaseOrderItemToPurchaseOrderItemVo(null, item, null));
                returnNoteItems.add(returnNoteItem);
            }
        }

        returnNoteWrapper.setReturnNoteItems(returnNoteItems);
        return returnNoteWrapper;
    }

    @Transactional
    public void saveReturnNote(AdminUser adminUser, ReturnNoteData returnNoteData) {
        ReturnNote returnNote = new ReturnNote();
        returnNote.setId(returnNoteData.getId());
        returnNote.setCreator(adminUserService.findOne(adminUser.getId()));
        returnNote.setCreateTime(new Date());
        returnNote.setDepot(depotService.findOne(returnNoteData.getDepotId()));
        returnNote.setRemark(returnNoteData.getRemark());
        returnNote.setStatus(ReturnNoteStatus.PENDINGAUDIT.getValue());

        PurchaseOrder purchaseOrder = purchaseOrderService.getOne(returnNoteData.getPurchaseOrderId());
        returnNote.setType(purchaseOrder.getType());
        returnNote.setPurchaseOrder(purchaseOrder);

        List<ReturnNoteItem> returnNoteItems = new ArrayList<>();
        for (ReturnNoteItemData item : returnNoteData.getReturnNoteItems()) {
            ReturnNoteItem returnNoteItem = new ReturnNoteItem();
            returnNoteItem.setReturnQuantity(item.getReturnQuantity());
            returnNoteItem.setReturnPrice(item.getReturnPrice());
            if (item.getReturnQuantity() == 0) {
                continue;
            }
            returnNoteItem.setReturnNote(returnNote);
            PurchaseOrderItem purchaseOrderItem = purchaseOrderItemService.getOne(item.getPurchaseOrderItem().getId());
            purchaseOrderItem.setReturnQuantity((purchaseOrderItem.getReturnQuantity() == null ? 0 : purchaseOrderItem.getReturnQuantity()) + item.getReturnQuantity());
            returnNoteItem.setPurchaseOrderItem(purchaseOrderItem);
            returnNoteItems.add(returnNoteItem);
        }

        returnNote.setReturnNoteItems(returnNoteItems);

        returnNoteService.save(returnNote);
    }

    public ReturnNoteVo getReturnNote(Long id) {
        ReturnNote returnNote = returnNoteService.getOne(id);
        return returnNoteService.returnNoteToReturnNoteVo(returnNote);
    }

    @Transactional
    public void audit(AdminUser adminUser, ReturnNoteData returnNoteData) {
        ReturnNote returnNote = returnNoteService.getOne(returnNoteData.getId());
        returnNote.setAuditor(adminUserService.getOne(adminUser.getId()));
        returnNote.setAuditTime(new Date());
        returnNote.setOpinion(returnNoteData.getOpinion());

        if (returnNoteData.getApprovalResult()) {
            returnNote.setStatus(ReturnNoteStatus.AUDITED.getValue());
        } else {
            returnNote.setStatus(ReturnNoteStatus.REJECTED.getValue());
            for (ReturnNoteItem returnNoteItem : returnNote.getReturnNoteItems()) {
                PurchaseOrderItem purchaseOrderItem = purchaseOrderItemService.getOne(returnNoteItem.getPurchaseOrderItem().getId());
                purchaseOrderItem.setReturnQuantity(purchaseOrderItem.getReturnQuantity() - returnNoteItem.getReturnQuantity());
                purchaseOrderItemService.save(purchaseOrderItem);
            }
        }

        returnNoteService.save(returnNote);

        if (returnNoteData.getApprovalResult()) {
            stockOutFacade.createStockOut(returnNote);
        }
    }


    public HttpEntity<byte[]> printReturnNote(Long id) throws Exception {
        ReturnNote returnNote = returnNoteService.getOne(id);

        Map beans = new HashMap<>();
        beans.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        beans.put("printDate", new Date());
        beans.put("returnNote", returnNoteService.returnNoteToReturnNoteVo(returnNote));

        final String fileName = String.format("return-note-%s-%s-%s.xls", returnNote.getId(), returnNote.getPurchaseOrder().getVendor().getName(), DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
        return ExportExcelUtils.generateExcelBytes(beans, fileName, ExportExcelUtils.RETURN_NOTE_TEMPLATE);
    }

    public HttpEntity<byte[]> exportReturnNotes(ReturnNoteRequest request, AdminUser adminUser) throws Exception {

        List<ReturnNoteVo> list = returnNoteService.getReturnNotes(request, adminUser);

        final String fileName = "return-notes.xls";
        final HashMap<String, Object> beanParams = new HashMap<>();
        beanParams.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        List<List<ReturnNoteVo>> beans = new ArrayList<>();
        beans.add(list);

        List<String> sheetNames = new ArrayList<>();
        sheetNames.add("退货单列表");

        return ExportExcelUtils.generateExcelBytes(beans, "returnNotes", sheetNames, beanParams, fileName, ExportExcelUtils.RETURN_NOTES_TEMPLATE);
    }
}

