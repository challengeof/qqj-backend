package com.mishu.cgwy.profile.domain;

import com.mishu.cgwy.common.domain.City;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by kaicheng on 3/18/15.
 */
@Entity
@Data
public class RestaurantType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private RestaurantType parentRestaurantType;

    @OneToMany(mappedBy = "parentRestaurantType", fetch = FetchType.LAZY)
    /*@OrderBy("displayOrder asc")*/
    private List<RestaurantType> childRestaurantTypes = new ArrayList<>();

    //排序序号
    /*private Integer displayOrder;*/

    //第几级餐馆类型
    private Integer type;

    @ManyToMany
    @JoinTable(name = "restaurantType_city_xref", joinColumns = @JoinColumn(name = "restaurantType_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "city_id", referencedColumnName = "id"))
    private Set<City> cities = new HashSet<>();

    @Override
    public String toString() {
        return "RestaurantType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
