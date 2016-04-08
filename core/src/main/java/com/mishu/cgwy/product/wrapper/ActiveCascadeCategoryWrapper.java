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
public class ActiveCascadeCategoryWrapper {
    private Long id;

    private String name;

    private MediaFileWrapper mediaFile;

    private String hierarchyName;

    private Long parentCategoryId;

    private int displayOrder;

    private CategoryStatus status = CategoryStatus.INACTIVE;

    private List<ActiveCascadeCategoryWrapper> children = new ArrayList<>();

    public ActiveCascadeCategoryWrapper() {

    }

    public ActiveCascadeCategoryWrapper(Category category) {
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

        for (Category child : category.getChildrenCategories()) {
            if (child.getStatus() == CategoryStatus.ACTIVE.getValue()) {
                children.add(new ActiveCascadeCategoryWrapper(child));
            }
        }

    }


}
