package com.mishu.cgwy.schedule.service;

import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.order.domain.OrderItem_;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.product.constants.RestaurantStatus;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.profile.constants.RestaurantGrade;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.domain.Restaurant_;
import com.mishu.cgwy.profile.service.RestaurantService;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by wangguodong on 15/8/14.
 */
@Service
public class ScheduleService {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private EntityManager entityManager;

    @Autowired
    RestaurantService restaurantService;

    private List<Tuple> getCustomerOrderAnalysis() {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Tuple> query = cb.createTupleQuery();
        final Root<Restaurant> root = query.from(Restaurant.class);


        final ListJoin<Restaurant, Order> join = root.join(Restaurant_.orders);
        query.multiselect(join.get(Order_.restaurant), cb.count(join.get(Order_.id)), cb.avg(join.get(Order_.subTotal)), cb.least(join.get(Order_.submitDate)), cb.greatest(join.get(Order_.submitDate)));
        query.where(
                cb.equal(join.get(Order_.restaurant).get(Restaurant_.id), root.get(Restaurant_.id)),
                cb.notEqual(join.get(Order_.restaurant).get(Restaurant_.status), RestaurantStatus.INACTIVE.getValue()),
                join.get(Order_.status).in(OrderStatus.COMMITTED.getValue(), OrderStatus.SHIPPING.getValue(), OrderStatus.COMPLETED.getValue())
        );
        query.groupBy(root.get(Restaurant_.id));

        return entityManager.createQuery(query).getResultList();
    }

    private List<Restaurant> getNewRestaurant() {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Restaurant> query = cb.createQuery(Restaurant.class);
        final Root<Restaurant> root = query.from(Restaurant.class);
        query.select(root);

        Subquery<Order> subQuery = query.subquery(Order.class);
        Root<Order> subRoot = subQuery.from(Order.class);
        subQuery.where(
                cb.equal(subRoot.get(Order_.restaurant).get(Restaurant_.id), root.get(Restaurant_.id)),
                subRoot.get(Order_.status).in(OrderStatus.COMMITTED.getValue(), OrderStatus.SHIPPING.getValue(), OrderStatus.COMPLETED.getValue())
        );

        query.where(
                cb.not(cb.exists(subQuery.select(subRoot))),
                cb.notEqual(root.get(Restaurant_.status), RestaurantStatus.INACTIVE.getValue())
        );

        return entityManager.createQuery(query).getResultList();
    }

    private List<Restaurant> getInactiveRestaurant() {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Restaurant> query = cb.createQuery(Restaurant.class);
        final Root<Restaurant> root = query.from(Restaurant.class);
        query.select(root);

        query.where(
            cb.equal(root.get(Restaurant_.status), RestaurantStatus.INACTIVE.getValue())
        );

        return entityManager.createQuery(query).getResultList();
    }

    public void customerOrderAnalysis() {
        try {
            for (Restaurant restaurant : getInactiveRestaurant()) {
                try {
                    if (!RestaurantGrade.INVALID.getGrade().equals(restaurant.getGrade()) || !Boolean.FALSE.equals(restaurant.getWarning())) {
                        restaurant.setGrade(RestaurantGrade.INVALID.getGrade());
                        restaurant.setWarning(Boolean.FALSE);
                        restaurantService.save(restaurant);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }

            for (Restaurant restaurant : getNewRestaurant()) {
                try {
                    Short grade = RestaurantGrade.NEW.getGrade();
                    Boolean warning = getWarning(grade, restaurant.getCreateTime());

                    if (!warning.equals(restaurant.getWarning()) || !grade.equals(restaurant.getGrade())) {
                        restaurant.setGrade(grade);
                        restaurant.setWarning(warning);
                        restaurantService.save(restaurant);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }

            for (Tuple tuple : getCustomerOrderAnalysis()) {
                try {
                    Restaurant restaurant = (Restaurant) tuple.get(0);
                    Long orderCount = (Long) tuple.get(1);
                    Double avgPrice = (Double) tuple.get(2);
                    Date firstSubmitDate = (Date) tuple.get(3);
                    Date lastSubmitDate = (Date) tuple.get(4);

                    Short restaurantGrade = getRestaurantGrade(orderCount, avgPrice, firstSubmitDate);

                    Boolean warning = getWarning(restaurantGrade, lastSubmitDate);
                    if (!restaurantGrade.equals(restaurant.getGrade())
                           // || !lastSubmitDate.equals(restaurant.getLastPurchaseTime())
                            || !warning.equals(restaurant.getWarning())) {
                        restaurant.setGrade(restaurantGrade);
                        //restaurant.setLastPurchaseTime(lastSubmitDate);//lastPurchaseTime modified when order submit
                        restaurant.setWarning(warning);
                        restaurantService.save(restaurant);
                    }

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private Short getRestaurantGrade(Long orderCount, Double avgPrice, Date firstSubmitDate) {
        RestaurantGrade gradeEnum = RestaurantGrade.COMMON;

        if (orderCount < 3 && DateUtils.addMonths(firstSubmitDate, 1).after(new Date())) {
            gradeEnum = RestaurantGrade.NEW;
        } else if (orderCount >= 8 && avgPrice.compareTo(200d) > 0) {
            gradeEnum = RestaurantGrade.HIGH_GRADE;
        }

        return gradeEnum.getGrade();
    }

    private Boolean getWarning(Short restaurantGrade, Date markDate) {
        Integer blankTime = com.mishu.cgwy.utils.DateUtils.getIntervalDays(markDate, new Date());
        return (restaurantGrade == RestaurantGrade.HIGH_GRADE.getGrade() && blankTime >= 7) || blankTime >= 15 ? Boolean.TRUE : Boolean.FALSE;
    }
}
