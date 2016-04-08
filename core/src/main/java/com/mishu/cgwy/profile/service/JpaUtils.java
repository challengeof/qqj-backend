package com.mishu.cgwy.profile.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.common.domain.*;
import com.mishu.cgwy.order.facade.PermissionCheckUtils;
import com.mishu.cgwy.profile.constants.CustomerQueryType;
import com.mishu.cgwy.profile.constants.RestaurantAuditReviewType;
import com.mishu.cgwy.profile.constants.RestaurantReviewStatus;
import com.mishu.cgwy.profile.controller.RestaurantQueryRequest;
import com.mishu.cgwy.profile.domain.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * User: xudong
 * Date: 5/22/15
 * Time: 12:32 PM
 */
public class JpaUtils {

    public static Predicate getPredicate(CriteriaBuilder cb, Path<Restaurant> path, AdminUser adminUser, RestaurantQueryRequest request) {

        List<Predicate> predicates = new ArrayList<>();

        if (adminUser != null) {
            if (!adminUser.isGlobalAdmin()) {
                request.setOrganizationId(adminUser.getOrganizations().iterator().next().getId());
            }
            Set<Long> blockIds = new HashSet<>();
            Set<Long> warehouseIds = new HashSet<>();
            Set<Long> cityIds = new HashSet<>();
            for (City city : adminUser.getCities()) {
                cityIds.add(city.getId());
            }
            for (Warehouse warehouse : adminUser.getWarehouses()) {
                warehouseIds.add(warehouse.getId());
            }
            for (Block block : adminUser.getBlocks()) {
                blockIds.add(block.getId());
            }

            if (PermissionCheckUtils.canViewAllInBlock(adminUser)) {
                List<Predicate> blockCondition = new ArrayList<>();
                if (!blockIds.isEmpty()) {
                    blockCondition.add(path.get(Restaurant_.customer).get(Customer_.block).get(Block_.id).in(blockIds));
                }
                if (!warehouseIds.isEmpty()) {
                    blockCondition.add(path.get(Restaurant_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id).in(warehouseIds));
                }
                if (!cityIds.isEmpty()) {
                    blockCondition.add(path.get(Restaurant_.customer).get(Customer_.city).get(City_.id).in(cityIds));
                }
                if (!blockCondition.isEmpty()) {
                    predicates.add(cb.or(blockCondition.toArray(new Predicate[blockCondition.size()])));
                } else {
                    predicates.add(cb.or());
                }
            } else {
                predicates.add(cb.equal(path.get(Restaurant_.customer).get(Customer_.adminUser).get(AdminUser_.id), adminUser.getId()));
            }


        }

        if (request.getCityId() != null) {
            predicates.add(cb.equal(path.get(Restaurant_.customer).get(Customer_.city).get(City_.id), request.getCityId()));
        }
        if (request.getWarehouseId() != null) {
            predicates.add(cb.equal(path.get(Restaurant_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id), request.getWarehouseId()));
        }
        if (request.getId() != null) {
            predicates.add(cb.equal(path.get(Restaurant_.id), request.getId()));
        }
        if (request.getRegistPhone() != null) {
            predicates.add(cb.equal(path.get(Restaurant_.customer).get(Customer_.telephone), request.getRegistPhone()));
        }
        if (StringUtils.isNotBlank(request.getName())) {
            predicates.add(cb.like(path.get(Restaurant_.name), "%" + request.getName() + "%"));
        }
        if (request.getAdminUserId() != null && request.getAdminUserId() != 0l) {
            predicates.add(cb.equal(path.get(Restaurant_.customer).get(Customer_.adminUser).get(AdminUser_.id), request.getAdminUserId()));
        }
        if (request.isAdminUserIdIsNull()) {
            predicates.add(path.get(Restaurant_.customer).get(Customer_.adminUser).get(AdminUser_.id).isNull());
        }
        if (request.getStatus() != null) {
            predicates.add(cb.equal(path.get(Restaurant_.status), request.getStatus()));
        }
        if (StringUtils.isNotBlank(request.getTelephone())) {
            predicates.add(cb.equal(path.get(Restaurant_.telephone), request.getTelephone()));
        }
        if (request.getCreateTime() != null) {
            predicates.add(cb.and(cb.greaterThanOrEqualTo(path.get(Restaurant_.createTime), request.getCreateTime()), cb.lessThanOrEqualTo(path.get(Restaurant_.createTime), DateUtils.addDays(request.getCreateTime(), 1))));
        }
        if (request.getStart() != null) {
            predicates.add(cb.greaterThanOrEqualTo(path.get(Restaurant_.createTime), request.getStart()));
        }
        if (request.getEnd() != null) {
            predicates.add(cb.lessThanOrEqualTo(path.get(Restaurant_.createTime), DateUtils.addDays(request.getEnd(), 1)));
        }
        if (request.getBlankTime() != 0) {
            predicates.add(cb.lessThanOrEqualTo(path.get(Restaurant_.lastPurchaseTime), DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -request.getBlankTime())));
        }
        if (request.isNeverOrder() == Boolean.TRUE) {
            predicates.add(cb.isNull(path.get(Restaurant_.lastPurchaseTime)));
        }
        if (request.getGrade() != null) {
            predicates.add(cb.equal(path.get(Restaurant_.grade), request.getGrade()));
        }
        if (request.getWarning() != null) {
            if (request.getWarning() == 0) {
                predicates.add(cb.or(cb.equal(path.get(Restaurant_.openWarning), 0), cb.equal(path.get(Restaurant_.warning), 0)));
            } else if (request.getWarning() == 1) {
                predicates.add(cb.equal(path.get(Restaurant_.warning), 1));
                predicates.add(cb.equal(path.get(Restaurant_.openWarning), 1));
            }
        }
        if(request.getBlockId()!=null){
            predicates.add(cb.equal(path.get(Restaurant_.customer).get(Customer_.block).get(Block_.id),request.getBlockId()));
        }
        if(request.getRestaurantActiveType()!=null){
            predicates.add(cb.equal( path.get(Restaurant_.activeType), request.getRestaurantActiveType() ));
        }
        if(request.getCooperatingState()!=null){
            predicates.add(cb.equal( path.get(Restaurant_.cooperatingState), request.getCooperatingState() ));
        }
        if(request.getReceiver()!=null){
            predicates.add(cb.like(path.get(Restaurant_.receiver),"%"+request.getReceiver()+"%"));
        }
        if(request.getRestaurantType()!=null){
            predicates.add(cb.equal(path.get(Restaurant_.type).get(RestaurantType_.id),request.getRestaurantType()));
        }

        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
