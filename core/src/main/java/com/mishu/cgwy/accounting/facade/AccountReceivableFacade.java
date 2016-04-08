package com.mishu.cgwy.accounting.facade;

import com.mishu.cgwy.accounting.domain.AccountReceivable;
import com.mishu.cgwy.accounting.domain.AccountReceivableWriteoff;
import com.mishu.cgwy.accounting.dto.AccountReceivableRequest;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableStatus;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableType;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableWriteOffStatus;
import com.mishu.cgwy.accounting.service.AccountReceivableService;
import com.mishu.cgwy.accounting.service.RestaurantAccountHistoryService;
import com.mishu.cgwy.accounting.wrapper.AccountReceivableWrapper;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.dto.AdminUserQueryRequest;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.common.repository.CityRepository;
import com.mishu.cgwy.error.UserDefinedException;
import com.mishu.cgwy.response.query.QuerySummationResponse;
import com.mishu.cgwy.stock.repository.DepotRepository;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by admin on 10/12/15.
 */
@Service
public class AccountReceivableFacade {

    @Autowired
    private AccountReceivableService accountReceivableService;
    @Autowired
    private RestaurantAccountHistoryService restaurantAccountHistoryService;
    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private DepotRepository depotRepository;

    private static final String ACCOUNT_RECEIVABLE_TEMPLATE = "/template/account-receivable-list.xls";
    private static final String ACCOUNT_RECEIVABLE_WRITEOFF_TEMPLATE = "/template/account-receivable-writeoff-list.xls";

    @Transactional
    public void writeoff(AdminUser adminUser, AccountReceivableRequest accountReceivableRequest) {

        for (Long id : accountReceivableRequest.getAccountReceivableIds()) {

            AccountReceivable accountReceivable = accountReceivableService.findOne(id);
            if (accountReceivable != null && AccountReceivableStatus.UNWRITEOFF.getValue().equals(accountReceivable.getStatus())) {

                if (accountReceivable.getStockOut() != null && !accountReceivable.getStockOut().isSettle()) {
                    throw new UserDefinedException("订单" + accountReceivable.getStockOut().getOrder().getId() + "未收款,不能销账");
                }
                AccountReceivableWriteoff accountReceivableWriteoff = accountReceivableService.writeoff(accountReceivable, adminUser, new Date());
                if (accountReceivableWriteoff != null) {
                    accountReceivable.setWriteOffAmount(accountReceivable.getWriteOffAmount().add(accountReceivableWriteoff.getWriteOffAmount()));
                    accountReceivable.setWriteOffer(accountReceivableWriteoff.getWriteOffer());
                    accountReceivable.setWriteOffDate(accountReceivableWriteoff.getWriteOffDate());
                    accountReceivable.setStatus(AccountReceivableStatus.WRITEOFF.getValue());
                    accountReceivableService.save(accountReceivable);

                    restaurantAccountHistoryService.createRestaurantAccountHistory(accountReceivableWriteoff.getWriteOffAmount().multiply(new BigDecimal(-1))
                            , accountReceivableWriteoff.getWriteOffAmount().multiply(new BigDecimal(-1)), accountReceivableWriteoff.getWriteOffDate()
                            , accountReceivable.getRestaurant(), null, null, accountReceivableWriteoff);
                }
            }
        }
    }

    @Transactional
    public void writeoffCancel(AdminUser adminUser, AccountReceivableRequest request) {

        Long id = request.getAccountReceivableWriteoffId();
        Date cancelDate = request.getCancelDate();
        AccountReceivableWriteoff accountReceivableWriteoff = accountReceivableService.findOneWriteoff(id);
        if (accountReceivableWriteoff != null && AccountReceivableWriteOffStatus.VALID.getValue().equals(accountReceivableWriteoff.getStatus())) {

            if (DateUtils.truncate(accountReceivableWriteoff.getWriteOffDate(), Calendar.DATE).compareTo(DateUtils.truncate(cancelDate, Calendar.DATE)) > 0) {
                throw new UserDefinedException("取消销账日期不能小于销账日期");
            }

            accountReceivableWriteoff = accountReceivableService.writeoffCancel(accountReceivableWriteoff, adminUser, cancelDate);
            AccountReceivable accountReceivable = accountReceivableWriteoff.getAccountReceivable();
            if (accountReceivable != null) {
                accountReceivable.setStatus(AccountReceivableStatus.UNWRITEOFF.getValue());
                accountReceivable.setWriteOffAmount(accountReceivable.getWriteOffAmount().subtract(accountReceivableWriteoff.getWriteOffAmount()));
                accountReceivableService.save(accountReceivable);
            }

            restaurantAccountHistoryService.removeRestaurantAccountHistory(id, 2);
        }
    }

    @Transactional(readOnly = true)
    public QuerySummationResponse<AccountReceivableWrapper> getAccountReceivableList(AccountReceivableRequest accountReceivableRequest) {

        QuerySummationResponse<AccountReceivableWrapper> res = new QuerySummationResponse<>();
        List<AccountReceivableWrapper> accountReceivableWrappers = new ArrayList<>();
        Page<AccountReceivable> page = accountReceivableService.getAccountReceivableList(accountReceivableRequest);
        for (AccountReceivable accountReceivable : page.getContent()) {
            accountReceivableWrappers.add(new AccountReceivableWrapper(accountReceivable));
        }
        res.setContent(accountReceivableWrappers);
        res.setPage(accountReceivableRequest.getPage());
        res.setPageSize(accountReceivableRequest.getPageSize());
        res.setTotal(page.getTotalElements());
        BigDecimal[] amounts = accountReceivableService.getAccountReceivableAmounts(accountReceivableRequest);
        res.setAmount(amounts);
        return res;
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportAccountReceivableList(AccountReceivableRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        List<AccountReceivableWrapper> accountReceivableWrappers = new ArrayList<>();
        for (AccountReceivable accountReceivable : accountReceivableService.getAccountReceivableList(request).getContent()) {
            accountReceivableWrappers.add(new AccountReceivableWrapper(accountReceivable));
        }

        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotRepository.getOne(request.getDepotId()).getName());
        beans.put("accountReceivableType", request.getAccountReceivableType() == Integer.MAX_VALUE ? "全部" : AccountReceivableType.fromInt(request.getAccountReceivableType()).getName());
        beans.put("accountReceivableStatus", request.getAccountReceivableStatus() == Integer.MAX_VALUE ? "全部" : AccountReceivableStatus.fromInt(request.getAccountReceivableStatus()).getName());
        beans.put("startReceiveDate", request.getStartReceiveDate());
        beans.put("endReceiveDate", request.getEndReceiveDate());
        beans.put("list", accountReceivableWrappers);
        beans.put("now", new Date());
        beans.put("operator", operator.getRealname());
        String fileName = String.format("accountReceivable-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, ACCOUNT_RECEIVABLE_TEMPLATE);
    }

    @Transactional(readOnly = true)
    public QuerySummationResponse<AccountReceivableWrapper> getAccountReceivableWriteoffList(AccountReceivableRequest accountReceivableRequest) {

        QuerySummationResponse<AccountReceivableWrapper> res = new QuerySummationResponse<>();
        List<AccountReceivableWrapper> accountReceivableWrappers = new ArrayList<>();
        Page<AccountReceivableWriteoff> page = accountReceivableService.getAccountReceivableWriteoffList(accountReceivableRequest);
        for (AccountReceivableWriteoff accountReceivableWriteoff : page.getContent()) {
            accountReceivableWrappers.add(new AccountReceivableWrapper(accountReceivableWriteoff));
        }
        res.setContent(accountReceivableWrappers);
        res.setPage(accountReceivableRequest.getPage());
        res.setPageSize(accountReceivableRequest.getPageSize());
        res.setTotal(page.getTotalElements());
        BigDecimal[] amounts = accountReceivableService.getAccountReceivableByWriteoffAmount(accountReceivableRequest);
        res.setAmount(amounts);
        return res;
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportAccountReceivableWriteoffList(AccountReceivableRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        List<AccountReceivableWrapper> accountReceivableWrappers = new ArrayList<>();
        for (AccountReceivableWriteoff accountReceivableWriteoff : accountReceivableService.getAccountReceivableWriteoffList(request).getContent()) {
            accountReceivableWrappers.add(new AccountReceivableWrapper(accountReceivableWriteoff));
        }

        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotRepository.getOne(request.getDepotId()).getName());
        beans.put("accountReceivableType", request.getAccountReceivableType() == Integer.MAX_VALUE ? "全部" : AccountReceivableType.fromInt(request.getAccountReceivableType()).getName());
        beans.put("accountReceivableWriteoffStatus", request.getAccountReceivableWriteoffStatus() == Integer.MAX_VALUE ? "全部" : AccountReceivableWriteOffStatus.fromInt(request.getAccountReceivableStatus()).getName());
        beans.put("startReceiveDate", request.getStartReceiveDate());
        beans.put("endReceiveDate", request.getEndReceiveDate());
        beans.put("list", accountReceivableWrappers);
        beans.put("now", new Date());
        beans.put("operator", operator.getRealname());
        String fileName = String.format("accountReceivableWriteoff-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, ACCOUNT_RECEIVABLE_WRITEOFF_TEMPLATE);
    }

    public List<AdminUserVo> getTrackerList(AdminUserQueryRequest request) {
        request.setPage(0);
        request.setGlobal(false);
        request.setPageSize(Integer.MAX_VALUE);
        List<AdminUserVo> adminUserVoList = new ArrayList<>();
        for (AdminUser adminUser : adminUserService.getAdminUser(request)) {
            AdminUserVo vo = new AdminUserVo();
            vo.setId(adminUser.getId());
            vo.setRealname(adminUser.getRealname());
            adminUserVoList.add(vo);
        }
        return adminUserVoList;
    }

}
