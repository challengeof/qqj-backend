package com.mishu.cgwy.saleVisit.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.service.RestaurantService;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.saleVisit.domain.SaleVisit;
import com.mishu.cgwy.saleVisit.request.SaleVisitPostData;
import com.mishu.cgwy.saleVisit.request.SaleVisitQueryRequest;
import com.mishu.cgwy.saleVisit.service.SaleVisitService;
import com.mishu.cgwy.saleVisit.vo.SaleVisitVo;
import com.mishu.cgwy.saleVisit.wrapper.SaleVisitWrapper;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by apple on 15/8/13.
 */
@Service
public class SaleVisitFacade {

    @Autowired
    private SaleVisitService saleVisitService;
    @Autowired
    private RestaurantService restaurantService;

    private static final String SALEVISIT_LIST = "/template/saleVisit-list.xls";

    @Transactional
    public SaleVisitWrapper updateSaleVisit(SaleVisitPostData data, AdminUser operator) {

        SaleVisit saleVisit = data.getSaleVisitId() == null ? new SaleVisit() : saleVisitService.getSaleVisitById(data.getSaleVisitId());
        saleVisit.setRestaurant(restaurantService.getOne(data.getRestaurantId()));
        saleVisit.setVisitTime(data.getVisitTime());
        saleVisit.setVisitStage(data.getVisitStage());
        saleVisit.setVisitPurposes(StringUtils.join(data.getVisitPurposes(), ','));
        saleVisit.setIntentionProductions(StringUtils.join(data.getIntentionProductions(), ','));
        saleVisit.setVisitTroubles(StringUtils.join(data.getVisitTroubles(), ','));
        saleVisit.setVisitSolutions(StringUtils.join(data.getVisitSolutions(), ','));
        saleVisit.setNextVisitTime(data.getNextVisitTime());
        saleVisit.setNextVisitStage(data.getNextVisitStage());
        saleVisit.setRemark(data.getRemark());
        saleVisit.setCreator(operator);
        saleVisit.setCreateTime(new Date());
        return new SaleVisitWrapper(saleVisitService.updateSaleVisit(saleVisit));
    }

    @Transactional
    public void deleteSaleVisit(Long id) {
        saleVisitService.deleteSaleVisit(id);
    }

    /*
    //暂时注释
    @Transactional(readOnly = true)
    public SaleVisitWrapper getSaleVisitById(Long id) {
        return new SaleVisitWrapper(saleVisitService.getSaleVisitById(id));
    }*/

    @Transactional(readOnly = true)
    public SaleVisitVo getSaleVisitById(Long id) {
        return new SaleVisitVo(saleVisitService.getSaleVisitById(id));
    }

    @Transactional(readOnly = true)
    public QueryResponse<SaleVisitWrapper> getSaleVisitPage(SaleVisitQueryRequest request) {

        List<SaleVisitWrapper> list = new ArrayList<>();
        Page<SaleVisit> saleVisits = saleVisitService.getSaleVisitPage(request);
        for (SaleVisit saleVisit : saleVisits) {
            list.add(new SaleVisitWrapper(saleVisit));
        }
        QueryResponse<SaleVisitWrapper> res = new QueryResponse<>();
        res.setContent(list);
        res.setTotal(saleVisits.getTotalElements());
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setQueryRequest(request);
        return res;
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> generateSaleVisitExcel(SaleVisitQueryRequest request, AdminUser operator) throws Exception{
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);

        Page<SaleVisit> saleVisits = saleVisitService.getSaleVisitPage(request);

        List<SaleVisitWrapper> saleVisitWrappers = new ArrayList<>();

        if(saleVisits != null && saleVisits.getSize() > 0){
            for (SaleVisit saleVisit : saleVisits) {
                saleVisitWrappers.add(new SaleVisitWrapper(saleVisit));
            }
        }

        Map<String,Object> beans = new HashMap<>();

        beans.put("list",saleVisitWrappers);

        HttpEntity<byte[]> httpEntity = ExportExcelUtils.generateExcelBytes(beans,"客户拜访列表.xls",SALEVISIT_LIST);
        return httpEntity;
    }


}
