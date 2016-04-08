package com.mishu.cgwy.product.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.organization.domain.Organization;

import lombok.Data;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import javax.persistence.*;

import java.io.IOException;
import java.util.*;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:33 PM
 */
@Entity
@Data
public class Product {
    private static ObjectMapper objectMapper = new ObjectMapper();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

//    private String description;

    private String barCode;

    /**
     * 标准包装/散装
     */
    private boolean discrete;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_file_id")
    private MediaFile mediaFile;*/
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_mediafile_xref", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "media_file_id"))
    private List<MediaFile> mediaFiles = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;


    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Sku> skus = new ArrayList<Sku>();

    private String specification;//规格

    private Integer shelfLife;   //保质期

    @Column(columnDefinition = "text")
    private String properties;

    private String details;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;


    public Map<String, String> getPropertyMap(){
        try {
            if (StringUtils.isBlank(properties)) {
                return new HashMap<String, String>();
            }else {
                return objectMapper.readValue(properties, new TypeReference<Map<String, String>>() {
                });
            }
        }catch (IOException E) {
            throw new RuntimeException();
        }

    }

    public void setPropertyMap(Map<String,String> propertyMap){

        JSONObject jsonObject = new JSONObject(propertyMap);
        this.properties =jsonObject.toString();

    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", discrete=" + discrete +
//                ", capacityInBundle=" + capacityInBundle +
                '}';
    }
}
