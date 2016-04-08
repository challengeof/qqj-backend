package com.mishu.cgwy.product.repository;

import com.mishu.cgwy.product.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * User: xudong
 * Date: 3/5/15
 * Time: 6:59 PM
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentCategoryIsNull();
}
