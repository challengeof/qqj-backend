package com.mishu.cgwy.utils;

import com.mishu.cgwy.order.domain.OrderItem;
import org.springframework.core.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by king-ck on 2015/12/8.
 */
public class JpaQueryUtils {


    public static <T> long lineCount( Class<T> fromCls,Specification<T> specification, EntityManager em ){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root root = query.from(fromCls);

        if (query.isDistinct()) {
            query.multiselect(cb.selectCase().when(cb.isNull(cb.countDistinct(root)), 0L).otherwise(cb.countDistinct(root)));
        } else {
            query.multiselect(cb.selectCase().when(cb.isNull(cb.count(root)), 0L).otherwise(cb.count(root)));
        }

        if(specification!=null){
            Predicate predicate = specification.toPredicate(root, query, cb);
            if (predicate != null) {
                query.where(predicate);
            }
        }
        List<Long> rlst = em.createQuery(query).getResultList();
        if(rlst.size()==1){
            return rlst.get(0);
        }else{
            return rlst.size();
        }
    }



    public static <T> long lineCount(Path pth, Class<T> fromCls,Specification<T> specification, EntityManager em ){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root root = query.from(fromCls);

        Path defaultP = pth==null? root:pth;

        if (query.isDistinct()) {

            query.multiselect(cb.selectCase().when(cb.isNull(cb.countDistinct(defaultP)), 0L).otherwise(cb.countDistinct(defaultP)));
        } else {
            query.multiselect(cb.selectCase().when(cb.isNull(cb.count(defaultP)), 0L).otherwise(cb.count(defaultP)));
        }

        if(specification!=null){
            Predicate predicate = specification.toPredicate(root, query, cb);
            if (predicate != null) {
                query.where(predicate);
            }
        }
        List<Long> rlst = em.createQuery(query).getResultList();
        if(rlst.size()==1){
            return rlst.get(0);
        }else{
            return rlst.size();
        }
    }

    public static <T,R,E extends Specification<T>> R valSelect(Class<T> fromCls, E specification, EntityManager em, PageRequest pageRequest,SelectPathGetting<T,R,E> pathG){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery query = cb.createTupleQuery();
        Root root = query.from(fromCls);

        if(specification!=null){
            Predicate predicate = specification.toPredicate(root, query, cb);
            if (predicate != null) {
                query.where(predicate);
            }
        }
        Selection<?>[] paths = pathG.getSelectPath(cb,query,root, specification);
        if(paths==null || paths.length==0){
            return null;
        }

        if(null!=pageRequest && pageRequest.getSort()!=null){
            List<javax.persistence.criteria.Order> orders = QueryUtils.toOrders(pageRequest.getSort(), root, cb);
            query.orderBy(orders);
        }

        query.multiselect(paths);
        TypedQuery tquery = em.createQuery(query);

        if(null!=pageRequest){
            tquery.setFirstResult(pageRequest.getOffset());
            tquery.setMaxResults(pageRequest.getPageSize());
        }
        List<Tuple> rlst = tquery.getResultList();
        return pathG.resultWrappe(rlst);

    }


    public static <T,R,E extends Specification<T>> R valSelect(Class<T> fromCls, E specification, EntityManager em,SelectPathGetting<T,R,E> pathG){
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery query = cb.createTupleQuery();
//        Root root = query.from(fromCls);
//
//        if(specification!=null){
//            Predicate predicate = specification.toPredicate(root, query, cb);
//            if (predicate != null) {
//                query.where(predicate);
//            }
//        }
//        Selection<?>[] paths = pathG.getSelectPath(cb,query,root, specification);
//        if(paths==null || paths.length==0){
//            return null;
//        }
//        query.multiselect(paths);
//        List<Tuple> rlst = em.createQuery(query).getResultList();
//        return pathG.resultWrappe(rlst);

        return valSelect(fromCls,specification,em,null,pathG);

    }

    public static interface SelectPathGetting<T,R,E extends Specification<T>>{
        public Selection<?>[] getSelectPath(CriteriaBuilder cb,CriteriaQuery query, Root<T> root,E specification);
        public R resultWrappe(List<Tuple> tuples);
    }

}
