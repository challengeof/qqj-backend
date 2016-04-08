package com.mishu.cgwy.accounting.service;

import com.mishu.cgwy.accounting.domain.*;
import com.mishu.cgwy.accounting.repository.RestaurantAccountHistoryRepository;
import com.mishu.cgwy.accounting.repository.RestaurantAccountRepository;
import com.mishu.cgwy.profile.domain.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by admin on 10/12/15.
 */
@Service
public class RestaurantAccountHistoryService {

    @Autowired
    private RestaurantAccountHistoryRepository restaurantAccountHistoryRepository;
    @Autowired
    private RestaurantAccountRepository restaurantAccountRepository;
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void createRestaurantAccountHistory(BigDecimal amount, BigDecimal unWriteoffAmount, Date accountDate
            , Restaurant restaurant, AccountReceivable accountReceivable, Collectionment collectionment, AccountReceivableWriteoff accountReceivableWriteoff) {

        if (amount.compareTo(BigDecimal.ZERO) != 0 || unWriteoffAmount.compareTo(BigDecimal.ZERO) != 0) {

            RestaurantAccountHistory restaurantAccountHistory = new RestaurantAccountHistory();
            restaurantAccountHistory.setRestaurant(restaurant);
            restaurantAccountHistory.setAmount(amount);
            restaurantAccountHistory.setCreateDate(new Date());
            restaurantAccountHistory.setAccountDate(accountDate);
            restaurantAccountHistory.setAccountReceivable(accountReceivable);
            restaurantAccountHistory.setAccountReceivableWriteoff(accountReceivableWriteoff);
            restaurantAccountHistory.setCollectionment(collectionment);
            restaurantAccountHistory.setUnWriteoffAmount(unWriteoffAmount);
            restaurantAccountHistoryRepository.save(restaurantAccountHistory);

            RestaurantAccount restaurantAccount = restaurantAccountRepository.findOne(restaurant.getId());
            if (restaurantAccount != null) {

                if (amount.compareTo(BigDecimal.ZERO) != 0) {
                    restaurantAccount.setAmount(restaurantAccount.getAmount() != null ? restaurantAccount.getAmount().add(amount) : amount);
                }
                if (unWriteoffAmount.compareTo(BigDecimal.ZERO) != 0) {
                    restaurantAccount.setUnWriteoffAmount(restaurantAccount.getUnWriteoffAmount() != null ? restaurantAccount.getUnWriteoffAmount().add(unWriteoffAmount) : unWriteoffAmount);
                }
                restaurantAccountRepository.save(restaurantAccount);

            } else {
                restaurantAccount = new RestaurantAccount();
                restaurantAccount.setRestaurant(restaurant);
                restaurantAccount.setAmount(amount);
                restaurantAccount.setUnWriteoffAmount(unWriteoffAmount);
                restaurantAccountRepository.save(restaurantAccount);
            }
        }

    }

    @Transactional
    public void removeRestaurantAccountHistory(Long id, int type) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<RestaurantAccountHistory> query = cb.createQuery(RestaurantAccountHistory.class);
        final Root<RestaurantAccountHistory> root = query.from(RestaurantAccountHistory.class);

        List<Predicate> predicates = new ArrayList<>();
        if (type == 0) {//AccountReceivable

            predicates.add(cb.equal(root.get(RestaurantAccountHistory_.accountReceivable).get(AccountReceivable_.id), id));
        } else if (type == 1) {//Collectionment

            predicates.add(cb.equal(root.get(RestaurantAccountHistory_.collectionment).get(Collectionment_.id), id));
        } else {//AccountReceivableWriteoff

            predicates.add(cb.equal(root.get(RestaurantAccountHistory_.accountReceivableWriteoff).get(AccountReceivableWriteoff_.id), id));
        }
        query.where(predicates.toArray(new Predicate[predicates.size()]));

        List<RestaurantAccountHistory> restaurantAccountHistories = entityManager.createQuery(query).getResultList();
        if (restaurantAccountHistories != null && restaurantAccountHistories.size() > 0) {
            Iterator<RestaurantAccountHistory> restaurantAccountHistoryIterator = restaurantAccountHistories.iterator();
            while (restaurantAccountHistoryIterator.hasNext()) {
                RestaurantAccountHistory restaurantAccountHistory = restaurantAccountHistoryIterator.next();

                RestaurantAccount restaurantAccount = restaurantAccountRepository.findOne(restaurantAccountHistory.getRestaurant().getId());
                if (restaurantAccount != null) {

                    if (restaurantAccountHistory.getAmount() != null && restaurantAccountHistory.getAmount().compareTo(BigDecimal.ZERO) != 0) {
                        restaurantAccount.setAmount(restaurantAccount.getAmount() != null ? restaurantAccount.getAmount().subtract(restaurantAccountHistory.getAmount()) : BigDecimal.ZERO.subtract(restaurantAccountHistory.getAmount()));
                    }
                    if (restaurantAccountHistory.getUnWriteoffAmount() != null && restaurantAccountHistory.getUnWriteoffAmount().compareTo(BigDecimal.ZERO) != 0) {
                        restaurantAccount.setUnWriteoffAmount(restaurantAccount.getUnWriteoffAmount() != null ? restaurantAccount.getUnWriteoffAmount().subtract(restaurantAccountHistory.getUnWriteoffAmount()) : BigDecimal.ZERO.subtract(restaurantAccountHistory.getUnWriteoffAmount()));
                    }
                    restaurantAccountRepository.save(restaurantAccount);

                }

                restaurantAccountHistoryRepository.delete(restaurantAccountHistory);
                restaurantAccountHistoryIterator.remove();
            }
        }
    }
}


