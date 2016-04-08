package com.mishu.cgwy.purchase.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.inventory.domain.Vendor_;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.purchase.controller.ReturnNoteRequest;
import com.mishu.cgwy.purchase.domain.*;
import com.mishu.cgwy.purchase.repository.ReturnNoteRepository;
import com.mishu.cgwy.purchase.vo.ReturnNoteItemVo;
import com.mishu.cgwy.purchase.vo.ReturnNoteVo;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.domain.Depot_;
import com.mishu.cgwy.stock.domain.StockOut;
import com.mishu.cgwy.stock.wrapper.DepotWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wangguodong on 15/10/10.
 */
@Service
public class ReturnNoteService {

    @Autowired
    ReturnNoteRepository returnNoteRepository;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private PurchaseOrderItemService purchaseOrderItemService;

    public void save(ReturnNote returnNote) {
        returnNoteRepository.save(returnNote);
    }

    private Specification<ReturnNote> getReturnNoteListSpecification(final ReturnNoteRequest request, final AdminUser adminUser) {
        return new Specification<ReturnNote>() {
            @Override
            public Predicate toPredicate(Root<ReturnNote> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (adminUser != null) {
                    Set<Long> cityIds = new HashSet<>();
                    Set<Long> depotIds = new HashSet<>();

                    for (City city : adminUser.getDepotCities()) {
                        cityIds.add(city.getId());
                    }
                    for (Depot depot : adminUser.getDepots()) {
                        depotIds.add(depot.getId());
                    }

                    List<Predicate> depotCondition = new ArrayList<>();
                    if (!cityIds.isEmpty()) {
                        depotCondition.add(root.get(ReturnNote_.depot).get(Depot_.city).get(City_.id).in(cityIds));
                    }
                    if (!depotIds.isEmpty()) {
                        depotCondition.add(root.get(ReturnNote_.depot).get(Depot_.id).in(depotIds));
                    }

                    if (!depotCondition.isEmpty()) {
                        predicates.add(cb.or(depotCondition.toArray(new Predicate[depotCondition.size()])));
                    } else {
                        predicates.add(cb.or());
                    }
                }

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(ReturnNote_.depot).get(Depot_.city).get(City_.id), request.getCityId()));
                }

                if (request.getVendorId() != null) {
                    predicates.add(cb.equal(root.get(ReturnNote_.purchaseOrder).get(PurchaseOrder_.vendor).get(Vendor_.id), request.getVendorId()));
                }

                if (request.getDepotId() != null) {
                    predicates.add(cb.equal(root.get(ReturnNote_.depot).get(Depot_.id), request.getDepotId()));
                }

                if (request.getPurchaseOrderId() != null) {
                    predicates.add(cb.equal(root.get(ReturnNote_.purchaseOrder).get(PurchaseOrder_.id), request.getPurchaseOrderId()));
                }

                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(ReturnNote_.status), request.getStatus()));
                }

                if (request.getStartDate() != null && request.getEndDate() != null) {
                    predicates.add(cb.between(root.get(ReturnNote_.createTime), request.getStartDate(), request.getEndDate()));
                }

                if (StringUtils.isNotBlank(request.getProductName())) {
                    Subquery<PurchaseOrderItem> subQuery = query.subquery(PurchaseOrderItem.class);
                    Root<PurchaseOrderItem> subRoot = subQuery.from(PurchaseOrderItem.class);

                    subQuery.where(
                            cb.equal(root.get(ReturnNote_.purchaseOrder).get(PurchaseOrder_.id), subRoot.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.id)),
                            cb.like(subRoot.get(PurchaseOrderItem_.sku).get(Sku_.product).get(Product_.name), String.format("%%%s%%", request.getProductName()))
                    );
                    predicates.add(cb.exists(subQuery.select(subRoot)));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    public QueryResponse<ReturnNoteVo> getReturnNoteList(ReturnNoteRequest request, AdminUser adminUser) {
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());

        Page<ReturnNote> page = returnNoteRepository.findAll(getReturnNoteListSpecification(request, adminUser), pageable);

        List<ReturnNoteVo> list = new ArrayList<>();
        for (ReturnNote returnNote : page.getContent()) {
            list.add(returnNoteToReturnNoteVo(returnNote));
        }

        QueryResponse<ReturnNoteVo> res = new QueryResponse<ReturnNoteVo>();
        res.setContent(list);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());

        return res;
    }

    public List<ReturnNoteVo> getReturnNotes(final ReturnNoteRequest request, final AdminUser adminUser) {
        List<ReturnNote> returnNotes = returnNoteRepository.findAll(getReturnNoteListSpecification(request, adminUser));

        List<ReturnNoteVo> list = new ArrayList<>();
        for (ReturnNote returnNote : returnNotes) {
            list.add(returnNoteToReturnNoteVo(returnNote));
        }
        return list;
    }

    public ReturnNoteVo returnNoteToReturnNoteVo(ReturnNote returnNote) {
        ReturnNoteVo returnNoteVo = new ReturnNoteVo();
        returnNoteVo.setId(returnNote.getId());
        returnNoteVo.setDepot(new DepotWrapper(returnNote.getDepot()));
        returnNoteVo.setRemark(returnNote.getRemark());
        returnNoteVo.setCreator(returnNote.getCreator() == null ? null : returnNote.getCreator().getRealname());
        returnNoteVo.setCreateTime(returnNote.getCreateTime());
        returnNoteVo.setAuditor(returnNote.getAuditor() == null ? null : returnNote.getAuditor().getRealname());
        returnNoteVo.setAuditTime(returnNote.getAuditTime());
        returnNoteVo.setOpinion(returnNote.getOpinion());
        returnNoteVo.setVendor(returnNote.getPurchaseOrder().getVendor().getName());
        returnNoteVo.setType(PurchaseOrderType.fromInt(returnNote.getType()));
        returnNoteVo.setStatus(ReturnNoteStatus.get(returnNote.getStatus()));
        returnNoteVo.setPurchaseOrder(purchaseOrderService.purchaseOrderToPurchaseOrderVo(returnNote.getPurchaseOrder()));

        List<ReturnNoteItemVo> returnNoteItemVoList = new ArrayList<>();
        BigDecimal purchaseTotal = new BigDecimal(0);
        BigDecimal returnTotal = new BigDecimal(0);
        for (ReturnNoteItem returnNoteItem : returnNote.getReturnNoteItems()) {
            ReturnNoteItemVo returnNoteItemVo = new ReturnNoteItemVo();
            Integer returnQuantity = returnNoteItem.getReturnQuantity();
            BigDecimal returnPrice = returnNoteItem.getReturnPrice();
            PurchaseOrderItem purchaseOrderItem = returnNoteItem.getPurchaseOrderItem();
            returnNoteItemVo.setId(returnNoteItem.getId());
            returnNoteItemVo.setReturnQuantity(returnQuantity);
            returnNoteItemVo.setReturnPrice(returnPrice);
            returnNoteItemVo.setPurchaseOrderItem(purchaseOrderItemService.purchaseOrderItemToPurchaseOrderItemVo(null, purchaseOrderItem, null));
            returnNoteItemVo.setReturnBundleQuantity(new BigDecimal(returnQuantity).divide(new BigDecimal(purchaseOrderItem.getSku().getCapacityInBundle()), 2, RoundingMode.HALF_UP));
            returnNoteItemVo.setReturnTotal(returnPrice.multiply(new BigDecimal(returnQuantity)));
            returnNoteItemVoList.add(returnNoteItemVo);

            purchaseTotal = purchaseTotal.add(returnNoteItem.getPurchaseOrderItem().getPrice().multiply(new BigDecimal(returnNoteItem.getReturnQuantity())));
            returnTotal = returnTotal.add(returnNoteItem.getReturnPrice().multiply(new BigDecimal(returnNoteItem.getReturnQuantity())));
        }
        returnNoteVo.setReturnNoteItems(returnNoteItemVoList);
        returnNoteVo.setPurchaseTotal(purchaseTotal);
        returnNoteVo.setReturnTotal(returnTotal);

        return returnNoteVo;
    }

    public ReturnNote getOne(Long id) {
        return returnNoteRepository.getOne(id);
    }

    @Transactional
    public void cancel(StockOut stockOut) {

        if (stockOut == null || stockOut.getReturnNote() == null)
            return;

        ReturnNote returnNote = returnNoteRepository.findOne(stockOut.getReturnNote().getId());
        returnNote.setStatus(ReturnNoteStatus.CANSELED.getValue());
        returnNoteRepository.save(returnNote);
    }

    @Transactional
    public void complete(StockOut stockOut) {

        if (stockOut == null || stockOut.getReturnNote() == null)
            return;

        ReturnNote returnNote = returnNoteRepository.findOne(stockOut.getReturnNote().getId());
        returnNote.setStatus(ReturnNoteStatus.COMPLETED.getValue());
        returnNoteRepository.save(returnNote);
    }

    @Transactional
    public List<ReturnNote> getReturnNoteByPurchaseOrderId(Long returnNoteId) {
        return returnNoteRepository.getReturnNoteByPurchaseOrderId(returnNoteId);
    }
}
