package com.qqj.weixin.service;

import com.qqj.response.query.QueryResponse;
import com.qqj.utils.EntityUtils;
import com.qqj.weixin.controller.WeixinUserListRequest;
import com.qqj.weixin.domain.WeixinUser;
import com.qqj.weixin.domain.WeixinUser_;
import com.qqj.weixin.enumeration.WeixinUserGroup;
import com.qqj.weixin.repository.WeixinUserRepository;
import com.qqj.weixin.wrapper.WeixinUserWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeixinUserService {

    private static Logger logger = LoggerFactory.getLogger(WeixinUserService.class);


    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private WeixinUserRepository adminUserRepository;

    @Transactional(readOnly = true)
    public QueryResponse<WeixinUserWrapper> getWeixinUserList(final WeixinUserListRequest request) {
        final PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize());

        Page<WeixinUser> page = adminUserRepository.findAll(new Specification<WeixinUser>() {
            @Override
            public Predicate toPredicate(Root<WeixinUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();


                if (request.getGroup() != null) {
                    WeixinUserGroup weixinUserGroup = WeixinUserGroup.get(request.getGroup());
                    if (weixinUserGroup.getStart() != null) {
                        try {
                            predicates.add(cb.greaterThanOrEqualTo(root.get(WeixinUser_.birthday), df.parse(weixinUserGroup.getStart())));
                        } catch (ParseException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                    if (weixinUserGroup.getEnd() != null) {
                        try {
                            predicates.add(cb.lessThan(root.get(WeixinUser_.birthday), df.parse(weixinUserGroup.getEnd())));
                        } catch (ParseException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }

                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(WeixinUser_.status), request.getStatus()));
                }

                if (request.getTelephone() != null) {
                    predicates.add(cb.like(root.get(WeixinUser_.telephone), String.format("%%%s%%", request.getTelephone())));
                }

                query.orderBy(cb.asc(root.get(WeixinUser_.id)));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageRequest);

        QueryResponse<WeixinUserWrapper> response = new QueryResponse<>();
        response.setContent(EntityUtils.toWrappers(page.getContent(), WeixinUserWrapper.class));
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());

        return response;
    }
}
