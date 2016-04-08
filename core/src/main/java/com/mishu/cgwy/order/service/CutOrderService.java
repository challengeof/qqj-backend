package com.mishu.cgwy.order.service;

import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.order.domain.CutOrder;
import com.mishu.cgwy.order.domain.CutOrder_;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.order.repository.CutOrderRepository;
import com.mishu.cgwy.purchase.controller.CutOrderListRequest;
import com.mishu.cgwy.purchase.enumeration.CutOrderStatus;
import com.mishu.cgwy.purchase.vo.CutOrderVo;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.Depot_;
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
import java.util.List;

/**
 * Created by wangwei on 15/10/22.
 */
@Service
public class CutOrderService {

    @Autowired
    private CutOrderRepository cutOrderRepository;

    @Transactional(readOnly = true)
    public CutOrder getCutOrder(Long id) {
        return cutOrderRepository.findOne(id);
    }

    @Transactional
    public CutOrder saveCutOrder(CutOrder curtOrder) {
        return cutOrderRepository.save(curtOrder);
    }

    public QueryResponse<CutOrderVo> getCutOrderList(final CutOrderListRequest request) {
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());

        Page<CutOrder> page = cutOrderRepository.findAll(new Specification<CutOrder>() {
            @Override
            public Predicate toPredicate(Root<CutOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(CutOrder_.city).get(City_.id), request.getCityId()));
                }

                if (request.getDepotId() != null) {
                    predicates.add(cb.equal(root.get(CutOrder_.depot).get(Depot_.id), request.getDepotId()));
                }

                predicates.add(cb.not(cb.equal(root.get(CutOrder_.status), CutOrderStatus.COMMITED.getValue())));

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);

        List<CutOrderVo> list = new ArrayList<>();
        for (CutOrder item : page.getContent()) {
            CutOrderVo cutOrderVo = new CutOrderVo();
            cutOrderVo.setId(item.getId());
            cutOrderVo.setAdministrator(item.getOperator().getRealname());
            cutOrderVo.setCutDate(item.getCutDate());
            cutOrderVo.setStatus(CutOrderStatus.get(item.getStatus()));
            list.add(cutOrderVo);
        }

        QueryResponse<CutOrderVo> res = new QueryResponse<CutOrderVo>();
        res.setContent(list);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());

        return res;
    }

    public List<CutOrder> getCutOrderList(final List<Long> ids) {
        return cutOrderRepository.findAll(new Specification<CutOrder>() {
            @Override
            public Predicate toPredicate(Root<CutOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return root.get(CutOrder_.id).in(ids);
            }
        });
    }

    public CutOrder getOne(Long cutOrderId) {
        return cutOrderRepository.getOne(cutOrderId);
    }

    public CutOrder getCutOrderByOrderId(final Long id) {
        return cutOrderRepository.findOne(new Specification<CutOrder>() {
            @Override
            public Predicate toPredicate(Root<CutOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.join(CutOrder_.orders).get(Order_.id), id);
            }
        });
    }
}
