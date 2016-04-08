package com.mishu.cgwy.utils;

import com.mishu.cgwy.product.domain.Category;

import java.util.ArrayList;
import java.util.List;

public class SkuCategoryUtils {

    public static Long getTopCategoryId (Category category) {
        if (category == null) {
            return null;
        }
        Category parentCategory = category.getParentCategory();
        if (parentCategory == null || parentCategory.getId() == -2) {
            return category.getId();
        } else {
            return getTopCategoryId (parentCategory);
        }
    }

    public static List<Category> getChildrenCategories(Category category) {
        List<Category> categories = new ArrayList<>();
        if (category == null) {
            return categories;
        }
        categories.add(category);
        for (Category childCategory : category.getChildrenCategories()) {
            categories.addAll(getChildrenCategories(childCategory));
        }
        return categories;
    }

    public static List<Long> getChildrenCategoryIds(Category category) {
        List<Long> categoryIds = new ArrayList<>();
        if (category == null) {
            return categoryIds;
        }
        categoryIds.add(category.getId());
        for (Category childCategory : category.getChildrenCategories()) {
            categoryIds.addAll(getChildrenCategoryIds(childCategory));
        }
        return categoryIds;
    }

}
