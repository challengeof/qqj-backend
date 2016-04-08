package com.mishu.cgwy.search;

import com.mishu.cgwy.product.domain.Product;
import com.mishu.cgwy.product.wrapper.BrandWrapper;

import java.io.IOException;
import java.util.List;

/**
 * User: xudong
 * Date: 3/10/15
 * Time: 4:01 PM
 */
public interface SearchService {
    public void rebuildIndex() throws IOException;

    public void createSkuPriceHistoryIndex() throws Exception;

    public void buildSkuPriceHistoryIndex() throws Exception;

    public void deleteSkuPriceHistoryIndex() throws Exception;

    public void indexProduct(Product product);

    public void flush();

    public SkuSearchHits findSkus(ProductSearchCriteria productSearchCriteria);

    public List<BrandWrapper> groupBrands(ProductSearchCriteria productSearchCriteria);
}
