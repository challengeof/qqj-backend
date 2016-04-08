package com.mishu.cgwy.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import com.mishu.cgwy.common.domain.Block_;
import com.mishu.cgwy.profile.domain.Customer;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.order.domain.OrderItem_;
import com.mishu.cgwy.order.repository.OrderItemRepository;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.domain.Customer_;
import com.mishu.cgwy.common.domain.Zone_;
import com.mishu.cgwy.common.domain.Warehouse_;

@Service
public class OrderItemService {
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Autowired
	private EntityManager entityManager;
	
    @Transactional
    public Long getQuantity(Long skuId, final Date date, final Long warehouseId) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        final Root<OrderItem> root = query.from(OrderItem.class);

        query.select(cb.sum(root.get(OrderItem_.countQuantity)));

        query.where(cb.greaterThanOrEqualTo(root.get(OrderItem_.order).get(Order_.submitDate), date),
                cb.lessThanOrEqualTo(root.get(OrderItem_.order).get(Order_.submitDate), DateUtils.addDays(date, 1)),
                cb.equal(root.get(OrderItem_.sku).get(Sku_.id), skuId),
                cb.equal(root.get(OrderItem_.order).get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id), warehouseId));

        return entityManager.createQuery(query).getSingleResult().longValue();
    }

    @Transactional(readOnly = true)
    public OrderItem getOrderItem(Long id) {
        return orderItemRepository.findOne(id);
    }

    @Transactional
    public OrderItem saveOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    @Transactional
    public Long getQuantityByCustomerAndSku(Long skuId, final Date date, final Customer customer) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        final Root<OrderItem> root = query.from(OrderItem.class);

        query.select(cb.sum(root.get(OrderItem_.countQuantity)));

        query.where(cb.greaterThanOrEqualTo(root.get(OrderItem_.order).get(Order_.submitDate), date),
                cb.lessThanOrEqualTo(root.get(OrderItem_.order).get(Order_.submitDate), DateUtils.addDays(date, 1)),
                cb.equal(root.get(OrderItem_.sku).get(Sku_.id), skuId),
                cb.equal(root.get(OrderItem_.order).get(Order_.customer), customer));

        Integer resp = entityManager.createQuery(query).getSingleResult();

        return resp == null ? 0L : resp;
    }
}

