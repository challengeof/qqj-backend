package com.mishu.cgwy.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.product.domain.Category;
import com.mishu.cgwy.product.domain.CategoryStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaicheng on 3/19/15.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryAllOrD1Response extends RestError {
    private Integer d2Count = 0;
    private Integer d3Count = 0;

    @JsonProperty("categorys")
    List<CategoryD1InAllOrD1> categories = new ArrayList<CategoryD1InAllOrD1>(); //抓包显示是categorys

    public Integer getD3Count() {
        return 5000;
    }
    public CategoryAllOrD1Response() {
    }

    public CategoryAllOrD1Response(Category d1) {

        buildD1List(d1);
    }

    public CategoryAllOrD1Response(List<Category> all) {

        for (Category d1 : all) {
            if (d1.getStatus() == CategoryStatus.ACTIVE.getValue()) {
                buildD1List(d1);
            }
        }
    }

    @Transactional
    private void buildD1List(Category d1) {
        CategoryD1InAllOrD1 d1InAll = new CategoryD1InAllOrD1();
        d1InAll.setCategoryId(d1.getId());
        d1InAll.setName(d1.getName());

        if (d1.getMediaFile() != null) {
            d1InAll.setUrl(d1.getMediaFile().getUrl());
            d1InAll.setWebUrl(d1.getMediaFile().getUrl());
        }
        List<CategoryD2InAllOrD1> d2InAllList = new ArrayList<CategoryD2InAllOrD1>();
        if (d1.getChildrenCategories() != null) {
            for (Category d2 : d1.getChildrenCategories()) {
                if (d2.getStatus() == CategoryStatus.ACTIVE.getValue()){
                    CategoryD2InAllOrD1 d2InAll = new CategoryD2InAllOrD1();
                    d2InAll.setCategoryId(d2.getId());
                    d2InAll.setName(d2.getName());

                    if (d2.getMediaFile() != null) {
                        d2InAll.setUrl(d2.getMediaFile().getUrl());
                    }
                    List<CategoryD3InAllOrD1> d3InAllList = new ArrayList<CategoryD3InAllOrD1>();
                    if (d2.getChildrenCategories() != null) {
                        for (Category d3 : d2.getChildrenCategories()) {
                            if (d3.getStatus() == CategoryStatus.ACTIVE.getValue()){
                                CategoryD3InAllOrD1 d3InAll = new CategoryD3InAllOrD1();
                                d3InAll.setCategoryId(d3.getId());
                                d3InAll.setName(d3.getName());
                                d3InAllList.add(d3InAll);
                                this.d3Count++;
                                d1InAll.setD3Count(d1InAll.getD3Count()+1);

                            }
                        }
                    }
                    d2InAll.setSubCategory(d3InAllList);
                    d2InAllList.add(d2InAll);
                    this.d2Count++;
                    d1InAll.setD2Count(d1InAll.getD2Count() + 1);

                }

            }
        }
        d1InAll.setSubCategory(d2InAllList);
        this.categories.add(d1InAll);
    }

}

@Data
class CategoryD1InAllOrD1 {
    private Integer d2Count = 0;
    private Integer d3Count = 0;
    private Long categoryId;
    private String name;
    private String url;
    private String webUrl;
    private List<CategoryD2InAllOrD1> subCategory;
    public Integer getD3Count() {
        return 5000;
    }

}

@Data
class CategoryD2InAllOrD1 {
    private Long categoryId;
    private String name;
    private String url;
    private List<CategoryD3InAllOrD1> subCategory;
}

@Data
class CategoryD3InAllOrD1 {
    private Long categoryId;
    private String name;

}
