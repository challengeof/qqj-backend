package com.mishu.cgwy.profile.service;

import com.mishu.cgwy.product.domain.Category;
import com.mishu.cgwy.product.service.ProductService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaicheng on 3/23/15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/application-persist.xml", "/application-context.xml", "/application-security.xml", "/application-search.xml"})
public class ProductServiceTest {
    @Autowired
    ProductService productService;

    /*@Before
    public void setup() {
        Category all = new Category();
        all.setName("调味品");

        Category category1 = new Category();
        category1.setName("酱油");
        category1.setParentCategory(all);


        Category category2 = new Category();
        category2.setName("醋");
        category2.setParentCategory(all);


        Category category3 = new Category();
        category3.setName("生抽");
        category3.setParentCategory(category1);

        Category category4 = new Category();
        category4.setName("老抽");
        category4.setParentCategory(category1);

        Category category5 = new Category();
        category5.setName("米醋");
        category5.setParentCategory(category2);

        Category category6 = new Category();
        category6.setName("白醋");
        category6.setParentCategory(category2);

        List<Category> list1 = new ArrayList<Category>();
        list1.add(category1);
        list1.add(category2);
        all.setChildrenCategories(list1);

        List<Category> list2 = new ArrayList<Category>();
        list2.add(category3);
        list2.add(category4);
        category1.setChildrenCategories(list2);

        List<Category> list3 = new ArrayList<Category>();
        list3.add(category5);
        list3.add(category6);
        category2.setChildrenCategories(list3);

        productService.saveCategory(all);
        productService.saveCategory(category1);
        productService.saveCategory(category2);
        productService.saveCategory(category3);
        productService.saveCategory(category4);
        productService.saveCategory(category5);
        productService.saveCategory(category6);


    }

    @Test
    @Transactional
    @Rollback
    public void testTopCategories() {
        List<Category> topCategories = productService.getTopCategories();
        Assert.assertEquals(topCategories.size(), 4);
    }*/
}
