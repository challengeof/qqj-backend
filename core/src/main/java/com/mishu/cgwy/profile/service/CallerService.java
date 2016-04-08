package com.mishu.cgwy.profile.service;

import com.mishu.cgwy.product.controller.CallerRequest;
import com.mishu.cgwy.profile.controller.caller.CallerListRequest;
import com.mishu.cgwy.profile.domain.Caller;
import com.mishu.cgwy.profile.repository.CallerRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by king-ck on 2015/9/29.
 */
@Service
public class CallerService {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CallerRepository callerRepository;

    public Caller findByPhone(String phone) {
        return callerRepository.findByPhone(phone);
    }


    @Transactional
    public Caller saveCaller(CallerRequest callerRequest) throws InvocationTargetException, IllegalAccessException {
        Caller caller= this.callerRepository.findByPhone(callerRequest.getPhone());
        if(caller==null){
            caller=new Caller();
            caller.setPhone(callerRequest.getPhone());
            caller.setCreateDate(new Date());
        }
        caller.setDetail(callerRequest.getDetail());
        caller.setName(callerRequest.getName());
        caller.setModifyDate(new Date());
        return callerRepository.save(caller);
    }

    public Caller saveCallerModifyTime(String phone){
        Caller caller= this.callerRepository.findByPhone(phone);

        caller.setModifyDate(new Date());
        return callerRepository.save(caller);
    }


//    public void relationRestaurant(String phone, Long restaurantid) {
//        Caller caller= this.callerRepository.findByPhone(phone);
//        if(caller==null)
//            return;
//        List<Restaurant> restaurants = caller.getRestaurants()==null ? new ArrayList():caller.getRestaurants();
//        Restaurant restaurant = new Restaurant();
//        restaurant.setId(restaurantid);
//        restaurants.add(restaurant);
//
//        callerRepository.save(caller);
//    }

    public Page<Object[]> findCaller(final CallerListRequest request) {

        // 在jpa里没找到合适的 左联接查询 改用sql
        //优化查询速度
        final String selectSql = " SELECT c.id ,c.detail ,c.name ,c.phone ,c.create_date createDate, c.modify_date modifyDate, IF(r1.`id` IS NULL,r2.`id`,r1.`id`) AS resId, IF(r1.`id` IS NULL,r2.`name`,r1.`name`) AS resName, IF(r1.`id` IS NULL,r2.`receiver`,r1.`receiver`) AS resReceiver  " ;
        final String fromSql =" FROM caller c LEFT JOIN `customer` o ON c.`phone`=o.username LEFT JOIN `restaurant` r1 ON (o.id IS NULL AND r1.`customer_id`=o.`id` ) LEFT JOIN `restaurant` r2 ON (o.id IS NOT NULL AND c.`phone`=r2.`telephone`) ";
        Map<String ,Object> param = new HashMap<>();
        StringBuffer whereQl=new StringBuffer();
        whereQl.append(" where 1=1 ");

        //拼条件
        if (StringUtils.isNotBlank(request.getName())) {
            whereQl.append(" and c.name=:name ");
            param.put("name",request.getName());
        }

        if (StringUtils.isNotBlank(request.getPhone())) {
            whereQl.append(" and c.phone=:phone");
            param.put("phone",request.getPhone());
        }

        if(request.getCallerId()!=null){
            whereQl.append(" and c.id=:id ");
            param.put("id",request.getCallerId());
        }

        if (request.getCreateDate() != null) {
            whereQl.append(" and c.create_date>=:createdate  and c.create_date<date_add(:createdate, interval 1 day)");
            param.put("createdate",request.getCreateDate());
        }

        if (request.getModifyDate() != null) {
            whereQl.append(" and c.modify_date>=:modifydate  and c.modify_date<date_add(:modifydate, interval 1 day) ");
            param.put("modifydate", request.getModifyDate());
        }

        if(StringUtils.isNotBlank(request.getCompany())){
            whereQl.append(" and r.name=:company ");
            param.put("company", request.getCompany());
        }
        if(StringUtils.isNotBlank(request.getReceiver())){
            whereQl.append(" and r.receiver=:receiver ");
            param.put("receiver", request.getReceiver());
        }

        String orderQl =String.format(" order by %s %s ,c.phone",request.getSortField(),request.isAsc()?"asc":"desc");
        String sql= selectSql+fromSql+whereQl.toString()+orderQl;
        Query sqlQuery = entityManager.createNativeQuery(sql);

        //设置参数
        for(Map.Entry<String ,Object> entry : param.entrySet()){
            sqlQuery.setParameter(entry.getKey(),entry.getValue());
        }
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());
        sqlQuery.setFirstResult(pageable.getOffset());
        sqlQuery.setMaxResults(pageable.getPageSize());

        List<Object[]> result = sqlQuery.getResultList();
        Integer count = this.countQuery(fromSql,whereQl.toString(),param);

        Page<Object[]> page = new PageImpl<Object[]>(result,pageable,count);

        return page;

    }

    public Integer countQuery(String fromSql, String whereSql,Map<String ,Object> param ){


        String selectQl ="select count(1) ";
        String querySql = selectQl+fromSql+whereSql;
        Query query = entityManager.createNativeQuery(querySql);

        //设置参数
        for(Map.Entry<String ,Object> entry : param.entrySet()){
            query.setParameter(entry.getKey(),entry.getValue());
        }
        return ((BigInteger) query.getSingleResult()).intValue();
    }


//    public Page<Object[]> findCaller(final CallerListRequest request) {
//
//        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());
//
//
//        Map<String ,Object> param = new HashMap<>();
//
//        String selectQl = " select c  ";
//        String fromQl =" from Caller as c left join c.restaurants as r   ";
//        StringBuffer whereQl=new StringBuffer();
//        whereQl.append(" where 1=1 and (c.phone=r.telephone or r.telephone is null )");
//
//
//        //拼条件
//        if (StringUtils.isNotBlank(request.getName())) {
//            String whereql=String.format(" and c.%s=:%s ",Caller_.name.getName(),Caller_.name.getName());
//            whereQl.append(whereql);
//            param.put(Caller_.name.getName(),request.getName());
//        }
//
//        if (StringUtils.isNotBlank(request.getPhone())) {
////            predicates.add(cb.equal(root.get(Caller_.phone),request.getPhone()));
//            String whereql=String.format(" and c.%s=:%s ",Caller_.phone.getName(),Caller_.phone.getName());
//            whereQl.append(whereql);
//            param.put(Caller_.phone.getName(),request.getPhone());
//        }
//
//        if(request.getCallerId()!=null){
//            String whereql=String.format(" and c.%s=:%s ",Caller_.id.getName(),Caller_.id.getName());
//            whereQl.append(whereql);
//            param.put(Caller_.id.getName(),request.getCallerId());
//        }
//
//        if (request.getCreateDate() != null) {
//            String whereql=String.format(" and c.%s>=:%s  and c.%s<:%s",
//                    Caller_.createDate.getName(),Caller_.createDate.getName() ,Caller_.createDate.getName(),Caller_.createDate.getName());
//            whereQl.append(whereql);
//            param.put(Caller_.createDate.getName(),request.getCreateDate());
//        }
//
//        if (request.getModifyDate() != null) {
//
//            String whereql=String.format(" and c.%s>=:%s  and c.%s<:%s",
//                    Caller_.modifyDate.getName(),Caller_.modifyDate.getName() ,Caller_.modifyDate.getName(),Caller_.modifyDate.getName());
//            whereQl.append(whereql);
//            param.put(Caller_.modifyDate.getName(),request.getModifyDate());
//        }
//
//        String orderQl =String.format(" order by c.%s %s",request.getSortField(),request.isAsc()?"asc":"desc");
//        //拼接
//
//        String jpql = selectQl+fromQl+whereQl.toString()+orderQl;
//
////        final CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
//
//        Query query = entityManager.createQuery(jpql);
//        //设置分页
//        query.setFirstResult(pageable.getOffset());
//        query.setMaxResults(pageable.getPageSize());
//
//        //设置参数
//        for(Map.Entry<String ,Object> entry : param.entrySet()){
//            query.setParameter(entry.getKey(),entry.getValue());
//        }
//
//        List<Object[]> result= query.getResultList();
//
//        int count =countQuery(fromQl, whereQl.toString(),request);
//
//        Page<Object[]> pgs = new PageImpl(result,pageable,count);
//
//        return pgs;
////        return callerRepository.findAll(new Specification<Caller>() {
////            @Override
////            public Predicate toPredicate(Root<Caller> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
////
////                if(StringUtils.isNotBlank(request.getCompany()) || StringUtils.isNotBlank(request.getReceiver())){
////
////                    final ListJoin<Caller, Restaurant> join = root.join(Caller_.restaurant);
//////                    List<Predicate> joinPredicate = new ArrayList<Predicate>();
//////                    ListJoin<Caller, Restaurant> ljoin = root.join(Caller_.restaurant);
////
////                    if(StringUtils.isNotBlank(request.getCompany())){
////                        join.on(join.get(Restaurant_.name).in(request.getCompany()));
//////                        joinPredicate.add(cb.equal(ljoin.get(Restaurant_.name),request.getCompany()));
////                    }
////                    if(StringUtils.isNotBlank(request.getReceiver())){
////                        join.on(join.get(Restaurant_.receiver).in(request.getReceiver()));
//////                        joinPredicate.add(cb.equal(ljoin.get(Restaurant_.receiver),request.getReceiver()));
////                    }
//////                    join.on(joinPredicate.toArray(new Predicate[]{}));
////                }
////
////                query.
////
////
////                Path orderPath= null;
////
////                if(request.getSortField().equals("createDate")){
////                    orderPath= root.get(Caller_.createDate);
////                }
////                if(request.getSortField().equals("modifyDate")){
////                    orderPath= root.get(Caller_.modifyDate);
////                }
////                if(request.getSortField().equals("id")){
////                    orderPath= root.get(Caller_.id);
////                }
////                if(orderPath!=null) {
////                    if (request.isAsc()) {
////                        query.orderBy(cb.asc(orderPath));
////                    } else {
////                        query.orderBy(cb.desc(orderPath));
////                    }
////                }
////
////                List<Predicate> predicates = new ArrayList<>();
////
////                if (StringUtils.isNotBlank(request.getName())) {
////                    predicates.add(cb.equal(root.get(Caller_.name),request.getName()));
////                }
////
////                if (StringUtils.isNotBlank(request.getPhone())) {
////                    predicates.add(cb.equal(root.get(Caller_.phone),request.getPhone()));
////                }
////
////                if(request.getCallerId()!=null){
////                    predicates.add(cb.equal(root.get(Caller_.id),request.getCallerId()));
////                }
////
////                if (request.getCreateDate() != null) {
////                    predicates.add(cb.greaterThanOrEqualTo(root.get(Caller_.createDate), request.getCreateDate()));
////                    predicates.add(cb.lessThan(root.get(Caller_.createDate), DateUtils.addDays(request.getCreateDate(), 1)));
////                }
////
////                if (request.getModifyDate() != null) {
////                    predicates.add(cb.greaterThanOrEqualTo(root.get(Caller_.modifyDate), request.getModifyDate()));
////                    predicates.add(cb.lessThan(root.get(Caller_.modifyDate), DateUtils.addDays(request.getModifyDate(), 1)));
////                }
//
////                return cb.and(predicates.toArray(new Predicate[]{}));
////            }
////        },pageable);
//    }
//
//
//    public Integer countQuery(String fromQl, String whereQl,CallerListRequest request){
//
//        String selectQl ="select count(1) ";
//        String querySql = selectQl+fromQl+whereQl;
//        Query query = entityManager.createQuery(querySql);
//
//        //设置参数
//        query.setParameter(Caller_.name.getName(),request.getName());
//        query.setParameter(Caller_.phone.getName(),request.getPhone());
//        query.setParameter(Caller_.id.getName(),request.getCallerId());
//        query.setParameter(Caller_.createDate.getName(),request.getCreateDate());
//        query.setParameter(Caller_.modifyDate.getName(),request.getModifyDate());
//
//        return (Integer) query.getSingleResult();
//    }

}
