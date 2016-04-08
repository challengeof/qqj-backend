package com.mishu.cgwy.profile.repository;

import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * User: xudong
 * Date: 12/2/14
 * Time: 3:26 PM
 */
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByCustomerId(Long customerId);
}
