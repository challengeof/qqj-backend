package com.mishu.cgwy.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.mishu.cgwy.common.domain.Block_;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.elasticsearch.common.base.Function;
import org.elasticsearch.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Collections2;
import com.mishu.cgwy.common.domain.Warehouse_;
import com.mishu.cgwy.common.domain.Zone_;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.order.domain.Refund;
import com.mishu.cgwy.order.domain.Refund_;
import com.mishu.cgwy.profile.domain.Customer_;
import com.mishu.cgwy.product.domain.Sku_;

@Service
public class RefundService {
	
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public Long getReturnedQuantity(Long skuId, final Date date, final Long warehouseId) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        final Root<Refund> root = query.from(Refund.class);

        query.select(cb.sum(root.get(Refund_.countQuantity)));

        query.where(cb.greaterThanOrEqualTo(root.get(Refund_.order).get(Order_.submitDate), date),
                cb.lessThanOrEqualTo(root.get(Refund_.order).get(Order_.submitDate), DateUtils.addDays(date, 1)),
                cb.equal(root.get(Refund_.sku).get(Sku_.id), skuId),
                cb.equal(root.get(Refund_.order).get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id), warehouseId));

        Integer sum = entityManager.createQuery(query).getSingleResult();
        return sum == null ? 0l : sum.longValue();
    }
}
