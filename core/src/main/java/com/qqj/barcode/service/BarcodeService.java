package com.qqj.barcode.service;

import com.qqj.admin.domain.AdminUser;
import com.qqj.admin.vo.AdminUserVo;
import com.qqj.barcode.domain.Barcode;
import com.qqj.barcode.domain.BarcodeItem;
import com.qqj.barcode.domain.BarcodeItem_;
import com.qqj.barcode.domain.Barcode_;
import com.qqj.barcode.dto.BarcodeRequest;
import com.qqj.barcode.dto.QueryBarcodeRequest;
import com.qqj.barcode.repository.BarcodeItemRepository;
import com.qqj.barcode.repository.BarcodeRepository;
import com.qqj.barcode.vo.BarcodeItemVo;
import com.qqj.barcode.vo.BarcodeVo;
import com.qqj.response.query.QueryResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bowen on 16/4/26.
 */
@Service
public class BarcodeService {

    private static Logger logger = LoggerFactory.getLogger(BarcodeService.class);

    @Autowired
    private BarcodeRepository barcodeRepository;

    @Autowired
    private BarcodeItemRepository barcodeItemRepository;

    @Transactional
    public void createBarcode(AdminUser operator, BarcodeRequest request) {

        Barcode barcode = new Barcode();

        copyProperties(operator, request, barcode);

        barcodeRepository.save(barcode);
    }

    public QueryResponse<BarcodeVo> findBarcode(QueryBarcodeRequest request) {

        Page<Barcode> barCodes = barcodeRepository.findAll(new BarcodeSpecification(request), new PageRequest(request.getPage(), request.getPageSize()));
        List<BarcodeVo> barcodeVos = new ArrayList<>();
        QueryResponse<BarcodeVo> response = new QueryResponse<>();
        for (Barcode barcode : barCodes.getContent()) {
            BarcodeVo barcodeVo = copyBarcodeVo(barcode);
            barcodeVos.add(barcodeVo);
        }
        response.setContent(barcodeVos);
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(barCodes.getTotalElements());
        return response;
    }

    private BarcodeVo copyBarcodeVo(Barcode barcode) {
        BarcodeVo barcodeVo = new BarcodeVo();
        barcodeVo.setExpressNo(barcode.getExpressNo());
        barcodeVo.setCreateTime(barcode.getCreateTime());
        barcodeVo.setBoxCode(barcode.getBoxCode());
        barcodeVo.setId(barcode.getId());
        AdminUserVo adminUser = new AdminUserVo();
        adminUser.setId(barcode.getOperator().getId());
        adminUser.setRealname(barcode.getOperator().getRealname());
        adminUser.setTelephone(barcode.getOperator().getTelephone());
        barcodeVo.setOperator(adminUser);
        return barcodeVo;
    }

    private BarcodeItemVo copyBarcodeItemVo(BarcodeItem barcodeItem) {
        BarcodeItemVo barcodeItemVo = new BarcodeItemVo();
        barcodeItemVo.setBarcode(copyBarcodeVo(barcodeItem.getBarcode()));
        barcodeItemVo.setBagCode(barcodeItem.getBagCode());
        barcodeItemVo.setId(barcodeItem.getId());
        return barcodeItemVo;
    }

    private void copyProperties(AdminUser operator, BarcodeRequest request, Barcode barcode) {
        List<BarcodeItem> barcodeItems = new ArrayList<>();

        for (String bagCode : request.getBarcodeItems()) {

            BarcodeItem barcodeItem = new BarcodeItem();
            barcodeItem.setBagCode(bagCode);
            barcodeItem.setBarcode(barcode);
            barcodeItems.add(barcodeItem);
        }
        barcode.setBarcodeItems(barcodeItems);
        barcode.setBoxCode(request.getBoxCode());
        barcode.setCreateTime(new Date());
        barcode.setExpressNo(request.getExpressNo());
        barcode.setOperator(operator);
    }

    public QueryResponse<BarcodeItemVo> findBarcodeItem(QueryBarcodeRequest request) {

        Page<BarcodeItem> barcodeItems = barcodeItemRepository.findAll(new BarcodeItemSpecification(request), new PageRequest(request.getPage(), request.getPageSize()));

        List<BarcodeItemVo> barcodeItemVos = new ArrayList<>();
        QueryResponse<BarcodeItemVo> response = new QueryResponse<>();
        for (BarcodeItem barcodeItem : barcodeItems.getContent()) {
            BarcodeItemVo barcodeItemVo = copyBarcodeItemVo(barcodeItem);
            barcodeItemVos.add(barcodeItemVo);
        }
        response.setPageSize(request.getPageSize());
        response.setPage(request.getPage());
        response.setContent(barcodeItemVos);
        response.setTotal(barcodeItems.getTotalElements());

        return response;
    }

    private static class BarcodeSpecification implements Specification<Barcode>{

        private final QueryBarcodeRequest request;

        public BarcodeSpecification(QueryBarcodeRequest request) {
            this.request = request;
        }

        @Override
        public Predicate toPredicate(Root<Barcode> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(request.getBoxCode())) {
                predicates.add(cb.equal(root.get(Barcode_.boxCode), request.getBoxCode()));

            }
            if (StringUtils.isNotBlank(request.getExpressNo())) {
                predicates.add(cb.equal(root.get(Barcode_.expressNo), request.getExpressNo()));

            }
            if (request.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Barcode_.createTime), request.getStartDate()));

            }
            if (request.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Barcode_.createTime), request.getEndDate()));

            }
            if (request.getBarcodeId() != null) {
                predicates.add(cb.equal(root.get(Barcode_.id), request.getBarcodeId()));

            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));

        }
    }

    private static class BarcodeItemSpecification implements Specification<BarcodeItem>{

        private final QueryBarcodeRequest request;

        public BarcodeItemSpecification(QueryBarcodeRequest request) {
            this.request = request;
        }

        @Override
        public Predicate toPredicate(Root<BarcodeItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(request.getBoxCode())) {
                predicates.add(cb.equal(root.get(BarcodeItem_.barcode).get(Barcode_.boxCode), request.getBoxCode()));
            }
            if (StringUtils.isNotBlank(request.getExpressNo())) {
                predicates.add(cb.equal(root.get(BarcodeItem_.barcode).get(Barcode_.expressNo), request.getExpressNo()));
            }
            if (request.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(BarcodeItem_.barcode).get(Barcode_.createTime), request.getStartDate()));
            }
            if (request.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(BarcodeItem_.barcode).get(Barcode_.createTime), request.getEndDate()));
            }
            if (request.getBarcodeItemId() != null) {
                predicates.add(cb.equal(root.get(BarcodeItem_.id), request.getBarcodeItemId()));
            }
            if (StringUtils.isNotBlank(request.getBagCode())) {
                predicates.add(cb.equal(root.get(BarcodeItem_.bagCode), request.getBagCode()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));

        }
    }

}
