package com.mishu.cgwy.product.dto;

import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.product.constants.Constants;
import com.mishu.cgwy.product.domain.Category;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by kaicheng on 3/19/15.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryD2Response extends RestError {
    private Long CategoryD1Id;
    private String CategoryD1Name;
    private Long CategoryD2Id;
    private String CategoryD2Name;
    private String now;

    public CategoryD2Response() {
    }

    public CategoryD2Response(Category d2) {
        //二级分类
        if (d2.getParentCategory() != null && d2.getParentCategory().getParentCategory() == null
                && d2.getChildrenCategories() != null) {
            if (d2.getParentCategory() != null) {
                this.setCategoryD1Id(d2.getParentCategory().getId());
                this.setCategoryD1Name(d2.getParentCategory().getName());
            }
            this.setCategoryD2Id(Constants.CATEGORY_D2_IN_D2);
            this.setCategoryD2Name("");
            this.setNow(d2.getName());
        }

        //三级分类
        else if (d2.getChildrenCategories().size() == 0) {
            this.setNow(d2.getName());
            Category parent = d2.getParentCategory();
            if (parent != null) {
                this.setCategoryD2Id(parent.getId());
                this.setCategoryD2Name(parent.getName());
                if (parent.getParentCategory() != null) {
                    this.setCategoryD1Id(parent.getParentCategory().getId());
                    this.setCategoryD1Name(parent.getParentCategory().getName());
                }
            }
        }

    }
}
