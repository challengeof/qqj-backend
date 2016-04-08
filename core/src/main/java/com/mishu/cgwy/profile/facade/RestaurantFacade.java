package com.mishu.cgwy.profile.facade;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.error.RestaurantTypeExistsException;
import com.mishu.cgwy.profile.controller.RestaurantQueryRequest;
import com.mishu.cgwy.profile.domain.RestaurantType;
import com.mishu.cgwy.profile.dto.RestaurantTypeRequest;
import com.mishu.cgwy.profile.dto.RestaurantTypeStatus;
import com.mishu.cgwy.profile.service.RestaurantService;
import com.mishu.cgwy.profile.vo.RestaurantTypeVo;
import com.mishu.cgwy.profile.wrapper.RestaurantTypeWrapper;
import com.mishu.cgwy.profile.wrapper.SimpleRestaurantWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.StockOut;
import com.mishu.cgwy.stock.domain.StockOutStatus;
import com.mishu.cgwy.stock.domain.StockOutType;
import com.mishu.cgwy.stock.dto.StockOutRequest;
import com.mishu.cgwy.stock.service.StockOutService;
import com.mishu.cgwy.utils.TreeJsonHasChild;
import com.mishu.cgwy.utils.TreeJsonHasChildRestaurantType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wangwei on 15/9/16.
 */
@Service
public class RestaurantFacade {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private StockOutService stockOutService;

    @Autowired
    private LocationService locationService;

    public QueryResponse<SimpleRestaurantWrapper> findRestaurantsDelivery(StockOutRequest request, AdminUser adminUser) {
        QueryResponse<SimpleRestaurantWrapper> response = new QueryResponse<>();

        request.setStockOutType(StockOutType.ORDER.getValue());
        request.setStockOutStatus(StockOutStatus.IN_STOCK.getValue());
        request.setPageSize(Integer.MAX_VALUE);
        request.setTrackerId(null);
        Page<StockOut> page = stockOutService.getStockOutList(request, adminUser);

        for (StockOut o : page) {
            response.getContent().add(new SimpleRestaurantWrapper(o.getOrder().getRestaurant()));
        }

        response.setPageSize(request.getPageSize());
        response.setPage(request.getPage());
        response.setTotal(page.getTotalElements());

        return response;
    }

    public List<RestaurantTypeWrapper> findLastRestaurantType() {
        List<RestaurantTypeWrapper> list =  new ArrayList<>();
        for (RestaurantType type : restaurantService.findLastRestaurantType()) {
            list.add(new RestaurantTypeWrapper(type));
        }
        return list;
    }

    public RestaurantTypeVo getRestaurantType(Long id) {
        RestaurantTypeVo restaurantTypeVo = new RestaurantTypeVo();
        RestaurantType restaurantType = restaurantService.findOne(id);
        copyProperties(restaurantTypeVo, restaurantType);
        return restaurantTypeVo;
    }

    public List<SimpleRestaurantWrapper> getRestaurantCandidates(RestaurantQueryRequest request) {
        return restaurantService.getRestaurantCandidates(request);
    }

    public List<TreeJsonHasChild> getRestaurantTypeTree(Long id, Integer status) {
        List<TreeJsonHasChild> trees = new ArrayList<>();
        final List<RestaurantType> subRestaurantTypes = getSubRestaurantTypes(id,status);

        for (RestaurantType restaurantType : subRestaurantTypes) {
            if (null != restaurantType) {
                TreeJsonHasChildRestaurantType tree = new TreeJsonHasChildRestaurantType();
                tree.setId(String.valueOf(restaurantType.getId()));
                tree.setText(restaurantType.getName());
                tree.setStatus(RestaurantTypeStatus.fromInt(restaurantType.getStatus()).getValue());
                tree.setChildren(getRestaurantTypeTree(restaurantType.getId(), status));
                for (City city : restaurantType.getCities()) {
                    tree.getCityIds().add(city.getId());
                }
                trees.add(tree);
            }
        }


        return trees;
    }

    public List<RestaurantType> getSubRestaurantTypes(Long parentRestaurantTypeId, final Integer status) {


        List<RestaurantType> restaurantTypes = new ArrayList<RestaurantType>();

        if (parentRestaurantTypeId != null && !parentRestaurantTypeId.equals(0l)) {

            restaurantTypes = restaurantService.getRestaurantType(parentRestaurantTypeId).getChildRestaurantTypes();

        } else {
            restaurantTypes = restaurantService.getTopRestaurantTypes();
        }

        if (status != null) {
            restaurantTypes = new ArrayList<>(Collections2.filter(restaurantTypes, new Predicate<RestaurantType>() {
                @Override
                public boolean apply(RestaurantType input) {
                    return input.getStatus() == status;
                }
            }));
        }

        return restaurantTypes;
    }

    @Transactional
    public void setRestaurantTypeCity(Long restaurantTypeId, Long cityId, Boolean active) {

        RestaurantType restaurantType = restaurantService.getRestaurantType(restaurantTypeId);
        City city = locationService.getCity(cityId);
        if (null != restaurantType && null != active && null != city) {
            if (active.equals(Boolean.TRUE)) {
                if (!restaurantType.getCities().contains(city)) {
                    restaurantType.getCities().add(city);
                }
            } else {
                if (active.equals(Boolean.FALSE)) {
                    if (restaurantType.getCities().contains(city)) {
                        restaurantType.getCities().remove(city);
                    }
                }
            }
            restaurantService.saveRestaurantType(restaurantType);
        }
    }

    @Transactional(readOnly = true)
    public List<RestaurantTypeVo> listAllRestaurantTypes() {
        List<RestaurantTypeVo> result = new ArrayList<RestaurantTypeVo>();

        final List<RestaurantType> allRestaurantTypes = restaurantService.findAllRestaurantTypes();

        Collections.sort(allRestaurantTypes, new Comparator<RestaurantType>() {
            @Override
            public int compare(RestaurantType o1, RestaurantType o2) {
                Integer level1 = getLevel(o1);
                Integer level2 = getLevel(o2);

                int levelCompare = level1.compareTo(level2);

                return levelCompare == 0 ? o1.getId().compareTo(o2.getId()) : levelCompare;
            }

            private Integer getLevel(RestaurantType restaurantType) {
                int level = 0;
                RestaurantType parent = null;

                while ((parent = restaurantType.getParentRestaurantType()) != null) {
                    level++;
                    restaurantType = parent;
                }
                return level;
            }

        });

        for (RestaurantType restaurantType : allRestaurantTypes) {
            RestaurantTypeVo restaurantTypeVo = new RestaurantTypeVo();
            copyProperties(restaurantTypeVo, restaurantType);
            result.add(restaurantTypeVo);
        }

        return result;
    }
    
    @Transactional
    public RestaurantTypeVo createRestaurantType(RestaurantTypeRequest request) {

        RestaurantType parentRestaurantType = null;
        if (request.getParentRestaurantTypeId() != null) {
            parentRestaurantType = restaurantService.getRestaurantType(request.getParentRestaurantTypeId());
        }

        checkRestaurantTypeNameDuplication(null, parentRestaurantType, request.getName());

        RestaurantType restaurantType = new RestaurantType();
        restaurantType.setName(request.getName());
        restaurantType.setParentRestaurantType(parentRestaurantType);

        if (parentRestaurantType != null) {
            parentRestaurantType.getChildRestaurantTypes().add(restaurantType);
        }

        restaurantType.setStatus(RestaurantTypeStatus.fromInt(request.getStatus()).getValue());

        restaurantType = restaurantService.saveRestaurantType(restaurantType);
        RestaurantTypeVo restaurantTypeVo = new RestaurantTypeVo();
        copyProperties(restaurantTypeVo, restaurantType);
        return restaurantTypeVo;
    }

    private void checkRestaurantTypeNameDuplication(Long restaurantTypeId, RestaurantType parentRestaurantType, String name) {
        List<RestaurantType> childrenRestaurantTypes = null;
        if (parentRestaurantType != null) {
            childrenRestaurantTypes = parentRestaurantType.getChildRestaurantTypes();
        } else {
            childrenRestaurantTypes = restaurantService.getTopRestaurantTypes();
        }

        for (RestaurantType childRestaurantType : childrenRestaurantTypes) {
            if (name.equals(childRestaurantType.getName()) && !childRestaurantType.getId().equals(restaurantTypeId)) {

                throw new RestaurantTypeExistsException();
            }
        }
    }

    @Transactional
    public RestaurantTypeVo updateRestaurantType(Long id, RestaurantTypeRequest request) {

        RestaurantType parentRestaurantType = null;
        if (request.getParentRestaurantTypeId() != null) {
            parentRestaurantType = restaurantService.getRestaurantType(request.getParentRestaurantTypeId());
        }

        RestaurantType restaurantType = restaurantService.getRestaurantType(id);
        if (null != request.getName()) {
            checkRestaurantTypeNameDuplication(id, parentRestaurantType, request.getName());

            if (parentRestaurantType != null) {
                checkRestaurantTypeCirculation(id, parentRestaurantType);
            }

            restaurantType.setName(request.getName());
        }

        if (restaurantType.getParentRestaurantType() != null) {
            if (parentRestaurantType == null) {
                restaurantType.getParentRestaurantType().getChildRestaurantTypes().remove(restaurantType);
            } else if (!parentRestaurantType.getId().equals(restaurantType.getParentRestaurantType().getId())) {
                restaurantType.getParentRestaurantType().getChildRestaurantTypes().remove(restaurantType);
                restaurantType.setParentRestaurantType(parentRestaurantType);
                parentRestaurantType.getChildRestaurantTypes().add(restaurantType);
            }
        } else {
            if (parentRestaurantType != null) {
                restaurantType.setParentRestaurantType(parentRestaurantType);
                parentRestaurantType.getChildRestaurantTypes().add(restaurantType);
            }
        }

        if (parentRestaurantType != null) {
            checkRestaurantTypeCirculation(id, parentRestaurantType);
        }


        restaurantType.setStatus(RestaurantTypeStatus.fromInt(request.getStatus()).getValue());

        restaurantType = restaurantService.saveRestaurantType(restaurantType);
        RestaurantTypeVo restaurantTypeVo = new RestaurantTypeVo();
        copyProperties(restaurantTypeVo, restaurantType);
        return restaurantTypeVo;
    }

    private void checkRestaurantTypeCirculation(Long id, RestaurantType parentRestaurantType) {
        do {
            if (parentRestaurantType.getId().equals(id)) {

                throw new RestaurantTypeExistsException();
            }

            parentRestaurantType = parentRestaurantType.getParentRestaurantType();
        } while (parentRestaurantType != null);

    }

    //有效餐馆类型
    public List<RestaurantTypeVo> findRestaurantType() {

        List<RestaurantTypeVo> list =  new ArrayList<>();
        for (RestaurantType restaurantType : restaurantService.findRestaurantType()) {
            RestaurantTypeVo restaurantTypeVo = new RestaurantTypeVo();
            copyProperties(restaurantTypeVo, restaurantType);
            list.add(restaurantTypeVo);
        }
        return list;
    }

    private void copyProperties(RestaurantTypeVo restaurantTypeVo, RestaurantType restaurantType) {
        String hierarchyName = restaurantType.getName();
        restaurantTypeVo.setId(restaurantType.getId());
        restaurantTypeVo.setName(hierarchyName);
        restaurantTypeVo.setHierarchyName(hierarchyName);
        RestaurantType current = restaurantType;
        while (current.getParentRestaurantType() != null) {
            hierarchyName = current.getParentRestaurantType().getName() + "-" + hierarchyName;
            current = current.getParentRestaurantType();
        }
        restaurantTypeVo.setHierarchyName(hierarchyName);
        restaurantTypeVo.setStatus(RestaurantTypeStatus.fromInt(restaurantType.getStatus()));
        restaurantTypeVo.setParentRestaurantTypeId(restaurantType.getParentRestaurantType() == null ? null : restaurantType.getParentRestaurantType().getId());
    }

    @Transactional(readOnly = true)
    public List<RestaurantTypeVo> getRestaurantTypeParent(Integer restaurantTypeStatus) {
        List<RestaurantTypeVo> list =  new ArrayList<>();
        List<RestaurantType> restaurantTypes = restaurantService.findRestaurantTypeParent(restaurantTypeStatus);
        for (RestaurantType restaurantType : restaurantTypes) {
            RestaurantTypeVo vo = new RestaurantTypeVo();
            vo.setId(restaurantType.getId());
            vo.setName(restaurantType.getName());
            vo.setStatus(RestaurantTypeStatus.fromInt(restaurantType.getStatus()));
            list.add(vo);
        }
        return list;
    }

    @Transactional(readOnly = true)
    public List<RestaurantTypeVo> getRestaurantTypeChild(Long parentId, final Integer status) {

        List<RestaurantTypeVo> list =  new ArrayList<>();
        RestaurantType restaurantType = restaurantService.getRestaurantType(parentId);
        List<RestaurantType> restaurantTypes = restaurantType.getChildRestaurantTypes();
        if (status != null) {
            restaurantTypes = new ArrayList<>(Collections2.filter(restaurantTypes, new Predicate<RestaurantType>() {
                @Override
                public boolean apply(RestaurantType input) {
                    return input.getStatus() == status;
                }
            }));
        }
        for (RestaurantType temp : restaurantTypes) {
            RestaurantTypeVo vo = new RestaurantTypeVo();
            vo.setId(temp.getId());
            vo.setName(temp.getName());
            vo.setStatus(RestaurantTypeStatus.fromInt(temp.getStatus()));
            list.add(vo);
        }
        return list;
    }
}
