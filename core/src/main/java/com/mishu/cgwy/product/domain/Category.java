package com.mishu.cgwy.product.domain;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.MediaFile;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: xudong
 * Date: 2/28/15
 * Time: 12:58 PM
 */
@Entity
@Data
@EqualsAndHashCode(of = {"id"})
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_file_id")
    private MediaFile mediaFile;

    private int status;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = true, insertable=false, updatable=false)
    private Category parentCategory;

    @OneToMany(cascade = {CascadeType.ALL})
    @OrderColumn(name = "displayOrder")
    @JoinColumn(name = "parent_id")
    private List<Category> childrenCategories = new ArrayList<Category>();

    private Boolean showSecond; //是否在二级列表显示，针对三级分类

    @ManyToMany
    @JoinTable(name = "category_city_xref", joinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "city_id", referencedColumnName = "id"))
    private Set<City> cities = new HashSet<>();

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }


}
