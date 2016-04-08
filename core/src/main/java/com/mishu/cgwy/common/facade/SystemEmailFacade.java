package com.mishu.cgwy.common.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.controller.SystemEmailRequest;
import com.mishu.cgwy.common.domain.SystemEmail;
import com.mishu.cgwy.common.dto.SystemEmailData;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.common.service.SystemEmailService;
import com.mishu.cgwy.common.wrapper.SystemEmailWrapper;
import com.mishu.cgwy.error.UserDefinedException;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.Set;

@Service
public class SystemEmailFacade {

    @Autowired
    private SystemEmailService systemEmailService;

    @Autowired
    private LocationService locationService;

    @Transactional(readOnly = true)
    public QueryResponse<SystemEmailWrapper> getSystemEmailList(SystemEmailRequest request, AdminUser adminUser) {

        QueryResponse<SystemEmailWrapper> res = new QueryResponse<>();
        Page<SystemEmail> page = systemEmailService.getSystemEmailList(request, adminUser);
        for (SystemEmail systemEmail : page.getContent()) {
            res.getContent().add(new SystemEmailWrapper(systemEmail));
        }
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        return res;
    }

    @Transactional
    public SystemEmailWrapper addSystemEmail(SystemEmailData systemEmailData){
        SystemEmail systemEmail = new SystemEmail();
        systemEmail.setName(systemEmailData.getName());
        systemEmail.setCity(locationService.getCity(systemEmailData.getCityId()));
        systemEmail.setSendCc(systemEmailData.getSendCc());
        systemEmail.setSendTo(systemEmailData.getSendTo());
        systemEmail.setType(systemEmailData.getType());
        return new SystemEmailWrapper(systemEmailService.addSystemEmail(systemEmail));
    }

    @Transactional
    public SystemEmailWrapper updateSystemEmail(Long id, SystemEmailData systemEmailData){
        SystemEmail systemEmail = systemEmailService.findOne(id);
        SystemEmail findSystemEmail = systemEmailService.findSystemEmailByCityAndType(systemEmailData.getCityId(), systemEmailData.getType());
        if (findSystemEmail != null && !findSystemEmail.getId().equals(systemEmail.getId())){
            throw new UserDefinedException("该城市已设置过该类型的Email");
        }

        systemEmail.setName(systemEmailData.getName());
        systemEmail.setCity(locationService.getCity(systemEmailData.getCityId()));
        systemEmail.setSendCc(systemEmailData.getSendCc());
        systemEmail.setSendTo(systemEmailData.getSendTo());
        systemEmail.setType(systemEmailData.getType());
        return new SystemEmailWrapper(systemEmailService.saveSystemEmail(systemEmail));
    }

    @Transactional
    public void deleteSystemEmail(SystemEmailData systemEmailData) {
        Set<Long> ids = systemEmailData.getSystemEmailIds();
        Iterator<Long> idIterator = ids.iterator();
        while (idIterator.hasNext()) {
            Long id = idIterator.next();
            systemEmailService.deleteSystemEmail(id);
        }
    }

    @Transactional(readOnly = true)
    public SystemEmailWrapper findSystemEmail(Long id) {
        return new SystemEmailWrapper(systemEmailService.findOne(id));
    }

}
