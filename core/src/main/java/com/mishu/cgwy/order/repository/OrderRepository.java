package com.mishu.cgwy.order.repository;

import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.profile.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    public List<Order> findByRestaurantIdAndStatus(Long restaurantId,
                                                   int orderStatus);

    public List<Order> findByRestaurantId(Long restaurantId);

    public List<Order> findByCustomer(Customer customer);

    public List<Order> findByCustomerId(Long id);

    public List<Order> findByExpectedArrivedDate(Date expectedArrivedDate);

    public List<Order> findByExpectedArrivedDateAndCustomerAdminUserId(Date date,Long adminUserId);

    public List<Order> findByStatus(Integer value);

    @Query("select o.restaurant, sum(o.total) from Order o where o.status!=-1 and o.restaurant.id in (?1) group by o.restaurant")
    List<Object[]> getRestaurantConsumption(List<Long> restaurantIds);

    public List<Order> findByRestaurantIdAndSequenceAndStatus(Long restaurantId, Long sequence, Integer status);
}
