package com.mishu.cgwy.profile.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.admin.repository.AdminUserRepository;
import com.mishu.cgwy.bonus.controller.SalesmanStatisticsRequest;
import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.domain.Block_;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.error.CustomerAuditExistsException;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.product.constants.RestaurantStatus;
import com.mishu.cgwy.product.repository.RestaurantTypeRepository;
import com.mishu.cgwy.profile.constants.*;
import com.mishu.cgwy.profile.controller.CustomerSellerChangeRequest;
import com.mishu.cgwy.profile.controller.RestaurantAuditInfoQueryRequest;
import com.mishu.cgwy.profile.controller.RestaurantInfoRequest;
import com.mishu.cgwy.profile.controller.RestaurantQueryRequest;
import com.mishu.cgwy.profile.domain.*;
import com.mishu.cgwy.profile.dto.RestaurantTypeStatus;
import com.mishu.cgwy.profile.repository.CustomerRepository;
import com.mishu.cgwy.profile.repository.RestaurantAlterLogRepository;
import com.mishu.cgwy.profile.repository.RestaurantAuditReviewRepository;
import com.mishu.cgwy.profile.repository.RestaurantRepository;
import com.mishu.cgwy.profile.vo.RestaurantInfoVo;
import com.mishu.cgwy.profile.wrapper.SimpleRestaurantWrapper;
import com.mishu.cgwy.utils.DateUtils;
import com.mishu.cgwy.utils.JpaQueryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.*;

@Service
@Slf4j
public class RestaurantService {


    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantTypeRepository restaurantTypeRepository;

    @Autowired
    private RestaurantAlterLogRepository restaurantAlterLogRepository;

    @Autowired
    private RestaurantAuditReviewRepository restaurantAuditReviewRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private EntityManager entityManager;

    public Restaurant getOne(Long id) {
        return restaurantRepository.getOne(id);
    }

    public List<Restaurant> findByCustomerId(Long customerId) {
        return restaurantRepository.findByCustomerId(customerId);
    }

    public List<Restaurant> findRestaurants(final Long[] ids) {

        return restaurantRepository.findAll(new Specification<Restaurant>() {
            @Override
            public Predicate toPredicate(Root<Restaurant> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = root.get(Restaurant_.id).in(ids);
                return predicate;
            }
        });

    }
    public long count(Specification<Restaurant> spec) {
        return restaurantRepository.count(spec);
    }

    public Page<Restaurant> findAll(Specification<Restaurant> spec, PageRequest pageable) {
        return restaurantRepository.findAll(spec, pageable);
    }
    public List<Restaurant> findByTelephone(final String phone){
        return restaurantRepository.findAll(new Specification<Restaurant>() {
            @Override
            public javax.persistence.criteria.Predicate toPredicate(Root<Restaurant> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {

                javax.persistence.criteria.Predicate equalRestaurantPhone = cb.equal(root.get(Restaurant_.telephone), phone);
                javax.persistence.criteria.Predicate equalCustomerPhone = cb.equal(root.get(Restaurant_.customer).get(Customer_.telephone),phone);
                return cb.or(equalRestaurantPhone, equalCustomerPhone);
            }
        });

    }
    public Restaurant save(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }


    //最后一级餐馆类型
    public List<RestaurantType> findLastRestaurantType() {
        return restaurantTypeRepository.findAll(new Specification<RestaurantType>() {
            @Override
            public javax.persistence.criteria.Predicate toPredicate(Root<RestaurantType> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get(RestaurantType_.type), 3);
            }
        });
    }

    public List<RestaurantType> findRestaurantType() {
        return restaurantTypeRepository.findAll(new Specification<RestaurantType>() {
            @Override
            public javax.persistence.criteria.Predicate toPredicate(Root<RestaurantType> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get(RestaurantType_.status), RestaurantTypeStatus.ACTIVE.getValue());
            }
        });
    }

    public List<RestaurantType> findRestaurantTypeParent(final Integer restaurantTypeStatus) {
        return restaurantTypeRepository.findAll(new Specification<RestaurantType>() {
            @Override
            public Predicate toPredicate(Root<RestaurantType> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (null != restaurantTypeStatus) {
                    predicates.add(cb.equal(root.get(RestaurantType_.status), RestaurantTypeStatus.fromInt(restaurantTypeStatus).getValue()));
                }
                predicates.add(cb.isNull(root.get(RestaurantType_.parentRestaurantType)));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
    }

    public RestaurantType findOne(Long id) {
        return restaurantTypeRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public Long newRestaurantCount(final SalesmanStatisticsRequest request) {
        return  restaurantRepository.count(new Specification<Restaurant>() {
            @Override
            public javax.persistence.criteria.Predicate toPredicate(Root<Restaurant> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();
                if (request.getStart() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Restaurant_.createTime), request.getStart()));
                }
                if (request.getEnd() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Restaurant_.createTime), request.getEnd()));
                }

                predicates.add(criteriaBuilder.equal(root.get(Restaurant_.customer).get(Customer_.adminUser).get(AdminUser_.id), request.getAdminUserId()));

                return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[predicates.size()]));
            }

        });
    }

    public List<SimpleRestaurantWrapper> getRestaurantCandidates(final RestaurantQueryRequest request) {
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());
        Page<Restaurant> page = restaurantRepository.findAll(new Specification<Restaurant>() {
            @Override
            public javax.persistence.criteria.Predicate toPredicate(Root<Restaurant> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                return cb.and(
                    cb.like(root.get(Restaurant_.name), String.format("%%%s%%", request.getName())),
                    root.get(Restaurant_.status).in(RestaurantStatus.ACTIVE.getValue())
                );
            }
        }, pageable);

        List<SimpleRestaurantWrapper> wrappers = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(page.getContent())) {
            for (Restaurant restaurant : page.getContent()) {
                wrappers.add(new SimpleRestaurantWrapper(restaurant));
            }
        }

        return wrappers;
    }

    @Transactional(readOnly = true)
    public RestaurantType getRestaurantType(Long parentRestaurantTypeId) {
        return restaurantTypeRepository.getOne(parentRestaurantTypeId);
    }

    @Transactional(readOnly = true)
    public List<RestaurantType> getTopRestaurantTypes() {

        return restaurantTypeRepository.findByParentRestaurantTypeIsNull();
    }

//    @Transactional
//    public void saveRestaurantAlterLog(Restaurant oldR, Restaurant newR, AdminUser operater) {
//
//
//        RestaurantInfoVo oldRinfoVo = RestaurantConveter.toRestaurantInfoVo(oldR);
//        RestaurantInfoVo newRinfoVo = RestaurantConveter.toRestaurantInfoVo(newR);
//        try {
//            Map<String, Object> diffProp = com.mishu.cgwy.utils.BeanUtils.propertyDiff(oldRinfoVo,newRinfoVo);
//            String rinfoJson = null;
//
//            rinfoJson = objectMapper.writeValueAsString(diffProp);
//            RestaurantAlterLog restaurantAlterLog = new RestaurantAlterLog();
//            restaurantAlterLog.setVal(rinfoJson);
//            restaurantAlterLog.setOperater(operater);
//            restaurantAlterLog.setRestaurant(newR);
//            restaurantAlterLog.setCreateDate(new Date());
//            restaurantAlterLogRepository.save(restaurantAlterLog);
//        } catch (Exception e) {
//            log.error("保存餐馆信息修改日志失败",e);
//            throw new RuntimeException(e);
//        }
//
//    }

    @Transactional
    public RestaurantType saveRestaurantType(RestaurantType restaurantType) {

        return restaurantTypeRepository.save(restaurantType);
    }
    public List<RestaurantType> findAllRestaurantTypes() {
        return restaurantTypeRepository.findAll();
    }


//    /**
//     * 获取餐馆列表
//     * @param request
//     * @return
//     */
//    public Page<Restaurant> findAllByReqeust(final RestaurantInfoRequest request, AdminUser operater) {
//
//        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(),
//                request.isAsc()? Sort.Direction.ASC:Sort.Direction.DESC,request.getSortField());
//
//        Page<Restaurant> restaurants =  restaurantRepository.findAll(new RestaurantInfoQuerySpecification(request),pageable);
//
//        return restaurants;
////        return this.findRestaurantInfoVos(request, new RestaurantInfoQuerySpecification(request) , operater);
//    }

//    /**
//     * 查客户公海列表
//     * @param request
//     * @param operater
//     * @return
//     */
//    public Page<Restaurant> findAllBySeaReqeust(final RestaurantInfoRequest request, AdminUser operater) {
//        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(), request.isAsc()? Sort.Direction.ASC:Sort.Direction.DESC,request.getSortField());
//
//        final RestaurantInfoQuerySpecification infoQuerySpecification = new RestaurantInfoQuerySpecification(request);
//        Specification<Restaurant> specification = new Specification() {
//
//
//            //未审核   或 没有销售   或 未进行申请认领的记录查询出来
//            @Override
//            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
//                Predicate predicate =infoQuerySpecification.toPredicate(root,query,cb);
////                Predicate predicate2 = cb.or(
////                        cb.equal(root.get(Restaurant_.status),  RestaurantStatus.UNDEFINED.getValue()),
////                        cb.isNull(infoQuerySpecification.getAdminUserJoin().get(AdminUser_.id))
////                );
//                Predicate predicate2 = cb.isNull(infoQuerySpecification.getAdminUserJoin().get(AdminUser_.id));
//
//                Subquery<RestaurantAuditReview> subQuery = query.subquery(RestaurantAuditReview.class);
//                Root<RestaurantAuditReview> subRoot = subQuery.from(RestaurantAuditReview.class);
//                subQuery.where(
//                        cb.equal(root.get(Restaurant_.id), subRoot.get(RestaurantAuditReview_.restaurant).get(Restaurant_.id)),
//                        cb.equal(subRoot.get(RestaurantAuditReview_.reqType), RestaurantAuditReviewType.claim.val),
//                        cb.equal(subRoot.get(RestaurantAuditReview_.status), RestaurantReviewStatus.NOT_CHECK.val)
//                );
//                Predicate predicate3 =  cb.not(cb.exists(subQuery.select(subRoot)));
//
//                return cb.and( predicate, predicate2, predicate3 );
//            }
//        };
//
//        return this.restaurantRepository.findAll(specification,pageable);
//
////        return this.findRestaurantInfoVos(request, specification, operater);
//    }

    public Page<RestaurantInfoVo> findRestaurantInfoVos(final RestaurantInfoRequest request, final Specification<Restaurant> spec,AdminUser operater){

        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(),
                request.isAsc()? Sort.Direction.ASC:Sort.Direction.DESC,request.getSortField());

        final RestaurantInfoQuerySpecification infoQuerySpecification = new RestaurantInfoQuerySpecification(request);
        List<RestaurantInfoVo> resultLt = JpaQueryUtils.valSelect(Restaurant.class, infoQuerySpecification, entityManager, pageable, new JpaQueryUtils.SelectPathGetting<Restaurant, List<RestaurantInfoVo>, Specification<Restaurant>>() {
            @Override
            public Selection<?>[] getSelectPath(CriteriaBuilder cb, CriteriaQuery query, Root<Restaurant> root, Specification<Restaurant> specification) {
                return new Selection<?>[]{
                        infoQuerySpecification.getCustomerJoin().get(Customer_.id),
                        root.get(Restaurant_.id),
                        root.get(Restaurant_.name),
                        infoQuerySpecification.getBlockJoin().get(Block_.id),
                        infoQuerySpecification.getBlockJoin().get(Block_.name),
                        infoQuerySpecification.getRestaurantTypeJoin().get(RestaurantType_.id),
                        root.get(Restaurant_.grade),
                        root.get(Restaurant_.status),
                        infoQuerySpecification.getDevUserJoin().get(AdminUser_.id),
                        infoQuerySpecification.getDevUserJoin().get(AdminUser_.realname),
//                        specification.getDevUserJoin().get(AdminUser_.realname),
                        infoQuerySpecification.getAdminUserJoin().get(AdminUser_.id),
                        infoQuerySpecification.getAdminUserJoin().get(AdminUser_.realname),
//                        specification.getAdminUserJoin().get(AdminUser_.realname),
                        infoQuerySpecification.getCustomerJoin().get(Customer_.createTime),
                        infoQuerySpecification.getCustomerJoin().get(Customer_.username),//客户注册号
                        root.get(Restaurant_.receiver),//联系人
                        root.get(Restaurant_.telephone),
                        root.get(Restaurant_.activeType)//活跃状态
//                        infoQuerySpecification.getCustomerJoin().get(Customer_.activeType) //活跃状态
                };
            }
            @Override
            public List<RestaurantInfoVo> resultWrappe(List<Tuple> tuples) {
                List<RestaurantInfoVo> infoVos = new ArrayList<RestaurantInfoVo>();
                for(Tuple tuple :  tuples){
                    RestaurantInfoVo infoVo = new RestaurantInfoVo();
                    infoVo.setCustomerId(tuple.get(0,Long.class));
                    infoVo.setRestaurantId(tuple.get(1,Long.class));
                    infoVo.setRestaurantName(tuple.get(2,String.class));
                    infoVo.setBlockId(tuple.get(3,Long.class));
                    infoVo.setBlockName(tuple.get(4,String.class));
                    infoVo.setRestaurantType(tuple.get(5,Long.class));
                    infoVo.setGrade(RestaurantGrade.getRestaurantGradeByCode(tuple.get(6,Short.class)).getGrade() );
                    infoVo.setRestaurantStatus(tuple.get(7,Integer.class));
                    infoVo.setRestaurantStatusName(RestaurantStatus.fromInt(infoVo.getRestaurantStatus()).getName());

                    infoVo.setDevUserId(tuple.get(8,Long.class));
                    infoVo.setDevUserName(tuple.get(9,String.class));
//                    infoVo.setDevUserName(tuple.get(9,String.class));
                    infoVo.setAdminUserId(tuple.get(10,Long.class));
                    infoVo.setAdminUserName(tuple.get(11,String.class));
//                    infoVo.setAdminUserName(tuple.get(11,String.class));
                    infoVo.setCustomerCreateDate(tuple.get(12, Date.class));
                    infoVo.setTelephone(tuple.get(13, String.class));
                    infoVo.setReceiver(tuple.get(14, String.class));
                    infoVo.setReceiverTelephone(tuple.get(15, String.class));
                    infoVo.setCustomerActiveType(tuple.get(16, Integer.class));
                    infoVos.add(infoVo);
                }
                return infoVos;
            }
        });
        Long total = JpaQueryUtils.lineCount(Restaurant.class,infoQuerySpecification,entityManager);
        Page<RestaurantInfoVo> rinfos = new PageImpl<RestaurantInfoVo>(resultLt,pageable,total);

        return rinfos;
    }

    @Transactional
    public void updateRestaurantAuditShowStatus(Long restaurantId,  RestaurantAuditReviewType reviewType, RestaurantReviewStatus reviewStatus){
        RestaurantAuditShowStatus showStatus = RestaurantAuditShowStatus.find(reviewType,reviewStatus);

        if(showStatus!=null){
            restaurantRepository.updateAuditShowStatus(showStatus.val,restaurantId);
        }
    }

    /**
     * 客户分配
     */
    @Transactional
    public void customerAllot(final CustomerSellerChangeRequest request, AdminUser operater) {
        assert  request.getAllotUser()!=null;

        List<Restaurant> restaurants = restaurantRepository.findAll(new Specification<Restaurant>() {
            @Override
            public Predicate toPredicate(Root<Restaurant> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return root.get(Restaurant_.id).in(request.getRestaurantId());
            }
        });
        RestaurantSellerType sellerType = RestaurantSellerType.fromInt(request.getSellerType());
        CustomerFollowUpStatus followUpStatus = CustomerFollowUpStatus.fromInt(request.getFollowUpStatus());
        AdminUser allotUser = adminUserRepository.getOne(request.getAllotUser());
        for(Restaurant restaurant : restaurants) {

            this.updateRestaurantAuditShowStatus(restaurant.getId(),RestaurantAuditReviewType.allot,RestaurantReviewStatus.PASS);

            //变更销售
            if(sellerType==RestaurantSellerType.dev){
                restaurant.getCustomer().setDevUser(allotUser);
            }else{
                restaurant.getCustomer().setAdminUser(allotUser);
                restaurant.getCustomer().setAdminUserFollowBegin(request.getBeginDate());
                restaurant.getCustomer().setAdminUserFollowEnd(request.getEndDate());
                restaurant.getCustomer().setFollowUpStatus(followUpStatus==null?null:followUpStatus.val);
            }
            customerRepository.save(restaurant.getCustomer());
        }
    }


    @Transactional
    public void addAuditReview( AdminUser operater, final RestaurantAuditReviewType reviewType, final Long... restaurantIds) {
        if(restaurantIds==null || restaurantIds.length==0){
            return ;
        }
        Date now =new Date();
//        List<Customer> customers = customerRepository.findByIdIn(Arrays.asList(restaurantIds));
        List<RestaurantAuditReview> auditReviews = restaurantAuditReviewRepository.findAll(new Specification<RestaurantAuditReview>() {
            @Override
            public Predicate toPredicate(Root<RestaurantAuditReview> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.and(
                            root.get(RestaurantAuditReview_.restaurant).get(Restaurant_.id).in(restaurantIds),
                            cb.equal(root.get(RestaurantAuditReview_.reqType),reviewType.val),
                            cb.equal(root.get(RestaurantAuditReview_.status), RestaurantReviewStatus.NOT_CHECK.val)
                        );
            }
        });

        if(auditReviews.size()!=0) {
            Collection<Long> rIds = Collections2.transform(auditReviews, new Function<RestaurantAuditReview, Long>() {
                @Override
                public Long apply(RestaurantAuditReview input) {
                    return input.getRestaurant().getId();
                }
            });
            throw new CustomerAuditExistsException();
        }

        begin:
        for(Long restaurantId : restaurantIds){
            for(RestaurantAuditReview review : auditReviews){
                if(review.getRestaurant().getId().equals(restaurantId)){
                    continue begin;
                }
            }
            RestaurantReviewStatus reviewStatus = RestaurantReviewStatus.NOT_CHECK;
            this.updateRestaurantAuditShowStatus(restaurantId,reviewType,reviewStatus);

            RestaurantAuditReview newReview = new RestaurantAuditReview();
            newReview.setCreateTime(now);
            Restaurant restaurant = new Restaurant();
            restaurant.setId(restaurantId);
            newReview.setRestaurant(restaurant);
            newReview.setReqType(reviewType.val);
            newReview.setStatus(reviewStatus.val);
            newReview.setCreateUser(operater);

            restaurantAuditReviewRepository.save(newReview);
        }

    }

    @Transactional(readOnly=true)
    public Page<RestaurantAuditReview> getAuditReviewList(final RestaurantAuditInfoQueryRequest request, AdminUser adminUser) {

        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize() ,
                                                        request.isAsc()? Sort.Direction.ASC: Sort.Direction.DESC ,
                                                        request.getSortField() );
        Page<RestaurantAuditReview> auditReviews = restaurantAuditReviewRepository.findAll(new Specification<RestaurantAuditReview>() {
            @Override
            public Predicate toPredicate(Root<RestaurantAuditReview> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> preds = new ArrayList<Predicate>();

                if(request.getReqType()!=null){
                    preds.add(cb.equal(root.get(RestaurantAuditReview_.reqType), request.getReqType()));
                }
                if(request.getRestaurantStatus()!=null){
                    preds.add(cb.equal(root.get(RestaurantAuditReview_.restaurant).get(Restaurant_.status), request.getRestaurantStatus()));
                }
                if(request.getRestaurantId()!=null){
                    preds.add(cb.equal(root.get(RestaurantAuditReview_.restaurant).get(Restaurant_.id), request.getRestaurantId()));
                }
                if(request.getRestaurantName()!=null){
                    preds.add(cb.like(root.get(RestaurantAuditReview_.restaurant).get(Restaurant_.name), "%"+request.getRestaurantName()+"%"));
                }
                if(request.getStatus()!=null){
                    preds.add(cb.equal(root.get(RestaurantAuditReview_.status), request.getStatus()));
                }
                if(request.getCreateUser()!=null){
                    preds.add(cb.equal(root.get(RestaurantAuditReview_.createUser).get(AdminUser_.id), request.getCreateUser()));
                }
                if(request.getCreateTimeFront()!=null){
                    preds.add(cb.greaterThanOrEqualTo(root.get(RestaurantAuditReview_.createTime), request.getCreateTimeFront()));
                }
                if(request.getCreateTimeBack()!=null){
                    preds.add(cb.lessThan(root.get(RestaurantAuditReview_.createTime), request.getCreateTimeBack()));
                }


                if(request.getOperateTimeFront()!=null){
                    preds.add(cb.greaterThanOrEqualTo(root.get(RestaurantAuditReview_.operateTime), request.getOperateTimeFront()));
                }
                if(request.getOperateTimeBack()!=null){
                    preds.add(cb.lessThan(root.get(RestaurantAuditReview_.operateTime), request.getOperateTimeBack()));
                }
                if(request.getOperater()!=null){
                    preds.add(cb.equal(root.get(RestaurantAuditReview_.operater), request.getOperater()));
                }

                return cb.and(preds.toArray(new Predicate[]{}));
            }
        },pageable);
        return auditReviews;
    }

    @Transactional
    public List<RestaurantAuditReview> auditReviewChange(Long[] auditReviewId, RestaurantReviewStatus reviewStatus, RestaurantAuditReviewType reviewType, AdminUser operater) {

        List<RestaurantAuditReview> auditReviews = restaurantAuditReviewRepository.findByIdIn(Arrays.asList(auditReviewId));
        for(RestaurantAuditReview auditReview : auditReviews){

            this.updateRestaurantAuditShowStatus(auditReview.getRestaurant().getId()  ,reviewType  ,reviewStatus);
            auditReview.setStatus(reviewStatus.val);
            auditReview.setReqType(reviewType.val);
            auditReview.setOperater(operater);
            auditReview.setOperateTime(new Date());
            restaurantAuditReviewRepository.save(auditReview);
        }
        return auditReviews;
    }

    public List<RestaurantAuditReview> getRestaurantAuditReview(Long[] auditInfoId) {
        return restaurantAuditReviewRepository.findByIdIn(Arrays.asList(auditInfoId));
    }



//    public List<RestaurantAuditReview> getRestaurantAuditReview(final Long[] auditInfoId) {
//
//        return restaurantAuditReviewRepository.findAll(new Specification<RestaurantAuditReview>() {
//            @Override
//            public Predicate toPredicate(Root<RestaurantAuditReview> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//                Predicate predicate = root.get(RestaurantAuditReview_.id).in(auditInfoId);
//                return predicate;
//            }
//        });
//
//    }

    private static class RestaurantInfoQuerySpecification implements Specification<Restaurant>{
        private RestaurantInfoRequest request;
        private Join<Customer, Block> blockJoin;
        private Join<Restaurant, Customer> customerJoin;

        private Join<Customer, AdminUser> devUserJoin;
        private Join<Customer, AdminUser> adminUserJoin;
        private Join<Restaurant, RestaurantType> restaurantTypeJoin;

        public Join<Restaurant, RestaurantType> getRestaurantTypeJoin() {
            return restaurantTypeJoin;
        }
        public RestaurantInfoQuerySpecification(RestaurantInfoRequest request) {
            this.request = request;
        }
        public Join<Customer, AdminUser> getDevUserJoin() {
            return devUserJoin;
        }
        public Join<Customer, AdminUser> getAdminUserJoin() {
            return adminUserJoin;
        }
        public Join<Restaurant, Customer> getCustomerJoin() {
            return customerJoin;
        }
        public Join<Customer, Block> getBlockJoin() {
            return blockJoin;
        }

        @Override
        public Predicate toPredicate(Root<Restaurant> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            customerJoin = root.join(Restaurant_.customer,JoinType.INNER);
            devUserJoin = customerJoin.join(Customer_.devUser,JoinType.LEFT);
            adminUserJoin = customerJoin.join(Customer_.adminUser,JoinType.LEFT);
            blockJoin = customerJoin.join(Customer_.block,JoinType.LEFT);
            restaurantTypeJoin = root.join(Restaurant_.type,JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();




            if(request.getBlockId()!=null){
                predicates.add(cb.equal(blockJoin.get(Block_.id),request.getBlockId()));
            }
            if(request.getCityId()!=null){
                predicates.add(cb.equal(blockJoin.join(Block_.city,JoinType.INNER).get(City_.id),request.getCityId()));
            }

            if(request.getCustomerActiveType()!=null){
//                customerJoin.get(Customer_.activeType)
                predicates.add(cb.equal( root.get(Restaurant_.activeType), request.getCustomerActiveType() ));

            }

            if(request.getCooperatingState()!=null){
                predicates.add(cb.equal( root.get(Restaurant_.cooperatingState), request.getCooperatingState() ));
            }

            if(request.getDevUserId()!=null){
                predicates.add(cb.equal(devUserJoin.get(AdminUser_.id),request.getDevUserId()));
            }
            if(request.getAdminUserId()!=null){
                predicates.add(cb.equal(adminUserJoin.get(AdminUser_.id),request.getAdminUserId()));
            }

            //注册时间区间
            if(request.getRegistDateFront()!=null){
                predicates.add(cb.greaterThanOrEqualTo(customerJoin.get(Customer_.createTime),request.getRegistDateFront()));
            }
            if(request.getRegistDateBack()!=null){
                predicates.add(cb.lessThan(customerJoin.get(Customer_.createTime),request.getRegistDateBack()));
            }

            if(request.getReceiver()!=null){
                predicates.add(cb.like(root.get(Restaurant_.receiver),"%"+request.getReceiver()+"%"));
            }
            if(request.getGrade()!=null){
                predicates.add(cb.equal(root.get(Restaurant_.grade),request.getGrade()));
            }
            if(request.getRestaurantId()!=null){
                predicates.add(cb.equal(root.get(Restaurant_.id),request.getRestaurantId()));
            }
            if(request.getRestaurantName()!=null){
                predicates.add(cb.equal(root.get(Restaurant_.name),request.getRestaurantName()));
            }
            if(request.getRestaurantStatus()!=null){
                predicates.add(cb.equal(root.get(Restaurant_.status),request.getRestaurantStatus()));
            }
            if(request.getRestaurantType()!=null){
                predicates.add(cb.equal(root.get(Restaurant_.type).get(RestaurantType_.id),request.getRestaurantType()));
            }
            return cb.and(predicates.toArray(new Predicate[]{}));
        }
    }

}
