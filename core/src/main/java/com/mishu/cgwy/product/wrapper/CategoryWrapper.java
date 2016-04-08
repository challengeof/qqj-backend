package com.mishu.cgwy.product.wrapper;

import com.mishu.cgwy.common.wrapper.MediaFileWrapper;
import com.mishu.cgwy.product.domain.Category;
import com.mishu.cgwy.product.domain.CategoryStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 3/18/15
 * Time: 4:15 PM
 */
@Data
@Deprecated
public class CategoryWrapper {
    private Long id;

    private String name;

    private MediaFileWrapper mediaFile;

    private String hierarchyName;

    private Long parentCategoryId;

    private Long mainParentCategoryId;

    private int displayOrder;

    private CategoryStatus status = CategoryStatus.INACTIVE;

    private List<CategoryWrapper> children = new ArrayList<>();

    private Boolean showSecond;

    public CategoryWrapper() {

    }

    public  CategoryWrapper(Category category) {
        id = category.getId();
        name = category.getName();
        mediaFile = category.getMediaFile() == null ? null : new MediaFileWrapper(category.getMediaFile());

        hierarchyName = name;

        Category current = category;
        while (current.getParentCategory() != null) {
            hierarchyName = current.getParentCategory().getName() + "-" + hierarchyName;
            current = current.getParentCategory();
        }

        parentCategoryId = category.getParentCategory() == null ? null : category.getParentCategory().getId();

        status = CategoryStatus.fromInt(category.getStatus());

        showSecond = category.getShowSecond();

    }


}
