/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.mishu.cgwy.search;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;


/**
 * Container that holds additional criteria to consider when performing searches for Products
 *
 * @author xudong
 */
@Data
public class ProductSearchCriteria {

    public static String PAGE_SIZE_STRING = "pageSize";
    public static String PAGE_NUMBER = "page";
    public static String SORT_STRING = "sort";
    public static String QUERY_STRING = "q";

    private Integer page = 0;
    private Integer pageSize = Integer.MAX_VALUE;
    private String sortField;
    private boolean asc = false;


    private Long categoryId;
    private String query;
    private Long warehouseId;
    private Long cityId;
//    private Long blockId;
    private Long discountCityId;
    private Map<String, String[]> filterCriteria = new HashMap<String, String[]>();


}
