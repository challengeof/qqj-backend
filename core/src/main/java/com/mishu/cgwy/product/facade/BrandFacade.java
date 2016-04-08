package com.mishu.cgwy.product.facade;

import com.mishu.cgwy.product.domain.Brand;
import com.mishu.cgwy.product.dto.BrandData;
import com.mishu.cgwy.product.dto.BrandRequest;
import com.mishu.cgwy.product.service.BrandService;
import com.mishu.cgwy.product.wrapper.BrandWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BrandFacade {

    @Autowired
    private BrandService brandService;

    @Transactional
    public BrandWrapper updateBrand(BrandData brandData) {

        Brand brand = brandData.getId() != null ? brandService.getBrandById(brandData.getId()) : new Brand();
        brand.setBrandName(brandData.getBrandName());
        brand.setStatus(brandData.getStatus());
        brand.setLastModified(new Date());
        return new BrandWrapper(brandService.updateBrand(brand));
    }

    @Transactional(readOnly = true)
    public QueryResponse<BrandWrapper> getBrandList(BrandRequest request) {

        QueryResponse<BrandWrapper> response = new QueryResponse<>();
        List<BrandWrapper> list = new ArrayList<>();
        Page<Brand> page = brandService.getBrandList(request);
        for (Brand brand : page.getContent()) {
            list.add(new BrandWrapper(brand));
        }
        response.setContent(list);
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        return response;
    }

    @Transactional(readOnly = true)
    public BrandWrapper getBrandById(Long id) {
        return new BrandWrapper(brandService.getBrandById(id));
    }
}
