package com.mishu.cgwy.stock.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.error.UserDefinedException;
import com.mishu.cgwy.product.service.SkuService;
import com.mishu.cgwy.purchase.domain.PurchaseOrder_;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.*;
import com.mishu.cgwy.stock.dto.StockQueryRequest;
import com.mishu.cgwy.stock.dto.TransferData;
import com.mishu.cgwy.stock.dto.TransferItemData;
import com.mishu.cgwy.stock.dto.TransferRequest;
import com.mishu.cgwy.stock.repository.TransferRepository;
import com.mishu.cgwy.stock.wrapper.TransferWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TransferService {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private DepotService depotService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private StockService stockService;

    @Transactional
    public void saveTransfer(AdminUser adminUser, TransferData transferData) {
        Transfer transfer = new Transfer();
        transfer.setId(transferData.getId());
        transfer.setCreator(adminUserService.findOne(adminUser.getId()));
        transfer.setCreateDate(new Date());
        transfer.setSourceDepot(depotService.findOne(transferData.getSourceDepotId()));
        transfer.setTargetDepot(depotService.findOne(transferData.getTargetDepotId()));
        transfer.setStatus(TransferStatus.NOTCOMMITTED.getValue());
        transfer.setRemark(transferData.getRemark());

        List<TransferItem> items = new ArrayList<>();
        for (TransferItemData itemData : transferData.getItems()) {
            TransferItem item = new TransferItem();
            item.setId(itemData.getId());
            item.setSku(skuService.findOne(itemData.getSkuId()));
            item.setQuantity(itemData.getQuantity());
            item.setTransfer(transfer);
            items.add(item);
        }

        transfer.setTransferItems(items);
        transferRepository.save(transfer);
    }

    @Transactional
    public void complete(StockIn stockIn) {

        if (stockIn == null || stockIn.getTransfer() == null)
            return;

        Transfer transfer = transferRepository.findOne(stockIn.getTransfer().getId());
        transfer.setStatus(TransferStatus.COMPLETED.getValue());
        transferRepository.save(transfer);
    }

    @Transactional
    public void cancel(StockOut stockOut) {

        if (stockOut == null || stockOut.getTransfer() == null)
            return;

        Transfer transfer = transferRepository.findOne(stockOut.getTransfer().getId());
        transfer.setStatus(TransferStatus.CANSELED.getValue());
        transferRepository.save(transfer);
    }

    public QueryResponse<TransferWrapper> getTransfers(final TransferRequest request) {
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());

        Page<Transfer> page = transferRepository.findAll(new Specification<Transfer>() {
            @Override
            public Predicate toPredicate(Root<Transfer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(Transfer_.sourceDepot).get(Depot_.city).get(City_.id), request.getCityId()));
                }

                if (request.getSourceDepotId() != null) {
                    predicates.add(cb.equal(root.get(Transfer_.sourceDepot).get(Depot_.id), request.getSourceDepotId()));
                }

                if (request.getTargetDepotId() != null) {
                    predicates.add(cb.equal(root.get(Transfer_.targetDepot).get(Depot_.id), request.getTargetDepotId()));
                }
                if (request.getId() != null) {
                    predicates.add(cb.equal(root.get(Transfer_.id), request.getId()));
                }

                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(Transfer_.status), request.getStatus()));
                }

                if (request.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(Transfer_.createDate), request.getStartDate()));
                }

                if (request.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(Transfer_.createDate), request.getEndDate()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);

        List<TransferWrapper> list = new ArrayList<>();
        for (Transfer transfer : page.getContent()) {
            list.add(new TransferWrapper(transfer));
        }

        QueryResponse<TransferWrapper> res = new QueryResponse<TransferWrapper>();
        res.setContent(list);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());

        return res;
    }

    public Transfer getTransfer(Long id) {
        return transferRepository.getOne(id);
    }

    public int getStock(Depot depot, Long skuId) {
        StockQueryRequest request = new StockQueryRequest();
        request.setCityId(depot.getCity().getId());
        request.setDepotId(depot.getId());
        request.setSkuId(skuId);
        Integer stock = stockService.findDepotStockTotal(request);
        return stock == null ? 0 : stock.intValue();
    }

    @Transactional
    public void submit(Long id) {
        Transfer transfer = transferRepository.getOne(id);
        if (transfer == null) {
            return;
        }

        if (!TransferStatus.NOTCOMMITTED.getValue().equals(transfer.getStatus())) {
            throw new UserDefinedException("调拨单" + transfer.getId() + "状态已改变");
        }
        transfer.setStatus(TransferStatus.PENDINGAUDIT.getValue());
        transferRepository.save(transfer);
    }

    @Transactional
    public Transfer audit(AdminUser adminUser, TransferData transferData) {
        Transfer transfer = transferRepository.findOne(transferData.getId());
        if (transfer == null) {
            return null;
        }

        if (!TransferStatus.PENDINGAUDIT.getValue().equals(transfer.getStatus())) {
            throw new UserDefinedException("调拨单" + transfer.getId() + "状态已改变");
        }

        transfer.setAuditor(adminUserService.findOne(adminUser.getId()));
        transfer.setAuditDate(new Date());
        transfer.setOpinion(transferData.getOpinion());

        if (transferData.getApprovalResult()) {
            transfer.setStatus(TransferStatus.EXECUTION.getValue());
        } else {
            transfer.setStatus(TransferStatus.NOTCOMMITTED.getValue());
        }

        transferRepository.save(transfer);

        return transfer;
    }

    @Transactional
    public void save (Transfer transfer) {
        transferRepository.save(transfer);
    }

    @Transactional
    public void delete(Transfer transfer) {
        transferRepository.delete(transfer);
    }
}
