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

import com.mishu.cgwy.product.vo.SkuVo;
import com.mishu.cgwy.product.wrapper.SkuWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * web
 * Container that holds the result of a ProductSearch
 *
 * @author xudong
 */
public class ProductSearchResult {

    protected List<SkuWrapper> skus = new ArrayList<>();

    protected Long totalResults;
    protected Integer page;
    protected Integer pageSize;

    public List<SkuWrapper> getSkus() {
        return skus;
    }

    public void setSkus(List<SkuWrapper> skus) {
        this.skus = skus;
    }

    public Long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Long totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getStartResult() {
        return (skus == null || skus.size() == 0) ? 0 : ((page - 1) * pageSize) + 1;
    }

    public Long getEndResult() {
        return Math.min(page * pageSize, totalResults);
    }

    public Integer getTotalPages() {
        return (skus == null || skus.size() == 0) ? 1 : (int) Math.ceil(totalResults * 1.0 / pageSize);
    }

}
