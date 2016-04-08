package com.mishu.cgwy.operating.skipe.service;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.operating.skipe.constant.SpikeActivityState;
import com.mishu.cgwy.operating.skipe.controller.SpikeListRequest;
import com.mishu.cgwy.operating.skipe.domain.Spike;
import com.mishu.cgwy.operating.skipe.domain.SpikeItem;
import com.mishu.cgwy.operating.skipe.domain.SpikeItem_;
import com.mishu.cgwy.operating.skipe.domain.Spike_;
import com.mishu.cgwy.operating.skipe.repository.SpikeItemRepository;
import com.mishu.cgwy.operating.skipe.repository.SpikeRepository;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.order.domain.OrderItem_;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.domain.Customer_;
import com.mishu.cgwy.utils.JpaQueryUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.*;

/**
 * Created by king-ck on 2016/1/7.
 */
@Lazy
@Service
public class SpikeService {
    @Autowired
    private SpikeItemRepository spikeItemRepository;
    @Autowired
    private SpikeRepository spikeRepository;

    @Autowired
    private EntityManager entityManager;
    private Object allSpike;

    public Page<Spike> getSpikePage(SpikeListRequest request){

        final PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize(),
                new Sort(request.isAsc()?Sort.Direction.ASC:Sort.Direction.DESC, request.getSortField())
        );
        return this.spikeRepository.findAll(pageRequest);

    }

    @Transactional(readOnly = true)
    public List<Long> getSpikeIds(final City city, final SpikeActivityState... progress) {
        List<Long> result = JpaQueryUtils.valSelect(Spike.class, new SpikeActivitySpecification(city, progress), entityManager, new JpaQueryUtils.SelectPathGetting<Spike, List<Long>, SpikeActivitySpecification>() {
            @Override
            public Selection<?>[] getSelectPath(CriteriaBuilder cb, CriteriaQuery query, Root<Spike> root, SpikeActivitySpecification specification) {
                return new Selection<?>[]{
                        root.get(Spike_.id)
                };
            }
            @Override
            public List<Long> resultWrappe(List<Tuple> tuples) {
                List<Long> spikeIds = new ArrayList<Long>();
                for(Tuple tuple : tuples){
                    spikeIds.add(tuple.get(0,Long.class));
                }
                return spikeIds;
            }
        });

        return result;
    }

    @Transactional(readOnly = true)
    public List<Spike> getSpikeList(final City city, final SpikeActivityState... progress) {
        Sort sort = new Sort(Sort.Direction.ASC , Spike_.id.getName());

        List<Spike> all =new ArrayList<>();
        for(SpikeActivityState saState : progress ) {
            List<Spike> spikes = spikeRepository.findAll(new SpikeActivitySpecification(city, saState), sort);
            all.addAll(spikes);
        }
        return all;
    }

    @Transactional(readOnly = true)
    public List<Spike> getAllSpike() {
        return spikeRepository.findAll();

    }

    @Transactional(readOnly = true)
    public List<SpikeItem> getAllSpikeItem() {
        return spikeItemRepository.findAll() ;
    }

    @Transactional(readOnly = true)
    public List<SpikeItem> getSpikeItems(final Long[] spikeItemIds) {

       return spikeItemRepository.findAll(new Specification<SpikeItem>() {
            @Override
            public Predicate toPredicate(Root<SpikeItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                return root.get(SpikeItem_.id).in(spikeItemIds);
            }
        });
    }

    public static class SpikeActivitySpecification implements Specification<Spike>{
        private City city;
        private SpikeActivityState[] progress;
        public SpikeActivitySpecification(final City city, final SpikeActivityState... progress){
            this.city=city;
            this.progress=progress;
        }
        @Override
        public Predicate toPredicate(Root<Spike> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            List<Predicate> allPred=new ArrayList<>();

            if(progress!=null) {
                Date now = new Date();
                List<Predicate> pres = new ArrayList<Predicate>();
                for (SpikeActivityState activityState : progress) {
                    if (activityState == SpikeActivityState.invalid) {
                        pres.add(
                                cb.equal(root.get(Spike_.state), activityState.spikeState.getVal())
                        );
                    }
                    if (activityState == SpikeActivityState.unStart) {
                        pres.add(cb.isTrue(cb.and(
                                cb.equal(root.get(Spike_.state),activityState.spikeState.getVal()),
                                cb.greaterThan(root.get(Spike_.beginTime), now)
                        )));
                    }
                    if (activityState == SpikeActivityState.process) {
                        pres.add(cb.isTrue(cb.and(
                                cb.equal(root.get(Spike_.state),activityState.spikeState.getVal()),
                                cb.lessThanOrEqualTo(root.get(Spike_.beginTime), now),
                                cb.greaterThan(root.get(Spike_.endTime), now)
                        )));
                    }
                    if (activityState == SpikeActivityState.end) {
                        pres.add(cb.isTrue(cb.and(
                                cb.equal(root.get(Spike_.state), activityState.spikeState.getVal()),
                                cb.lessThanOrEqualTo(root.get(Spike_.endTime), now)
                        )));
                    }
                }
                allPred.add(cb.or(pres.toArray(new Predicate[]{})));
            }

            if(city!=null){
                allPred.add(cb.equal(root.get(Spike_.city).get(City_.id), city.getId()));
            }
            Predicate ePred = cb.and(allPred.toArray(new Predicate[]{}));
            return ePred;
        }
    }


    @Transactional(readOnly = true)
    public Spike getSpike(Long id) {
        return spikeRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public List<SpikeItem> getSpikeItemsBySpikeId(final Long spikeId){
        return spikeItemRepository.findAll(new Specification<SpikeItem>() {
            @Override
            public Predicate toPredicate(Root<SpikeItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get(SpikeItem_.spike).get(Spike_.id),spikeId);
            }
        });
    }


    @Transactional(rollbackFor = Exception.class)
    public void addOrModify(Spike spike, SpikeItem[] items) {
        spike =spikeRepository.save(spike);
        if(spike.getItems()!=null ){
            for(SpikeItem sitem : spike.getItems()){
                if(!ArrayUtils.contains(items,sitem)){
                    spikeItemRepository.delete(sitem);
                }
            }
        }
        for( SpikeItem item : items ) {
            item.setSpike(spike);
            spikeItemRepository.save(item);
        }
    }



    @Transactional(rollbackFor = Exception.class)
    public void updateSpikeState(Long id, Integer state) {
        Spike spike= spikeRepository.getOne(id);

        spike.setState(state);
        spikeRepository.save(spike);
    }

    @Transactional(readOnly = true)
    public SpikeItem getSpikeItem(Long spikeItemId) {

        return this.spikeItemRepository.getOne(spikeItemId);
    }


    public int increaseTakeNum(Long spikeItemId, int quantity) {

        return this.spikeRepository.increaseTakeNum(spikeItemId, quantity);
    }



    @Transactional(readOnly = true)
    public Long getSpikeCustomerCurrentNum(final Customer customer, final Long spikeItemId) {

        Specification<OrderItem> specification = new Specification<OrderItem>() {
            @Override
            public Predicate toPredicate(Root<OrderItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> preds = new ArrayList<>();
                if(spikeItemId!=null){
                    preds.add(cb.equal(root.get(OrderItem_.spikeItem).get(SpikeItem_.id),spikeItemId));
                }
                if(customer!=null){
                    preds.add(cb.equal(root.get(OrderItem_.order).get(Order_.customer).get(Customer_.id),customer.getId()));
                }
                preds.add(cb.notEqual(root.get(OrderItem_.order).get(Order_.status), OrderStatus.UNCOMMITTED.getValue()));
                return cb.and(preds.toArray(new Predicate[]{}));
            }
        };

        return JpaQueryUtils.lineCount(OrderItem.class, specification,entityManager);
    }
}
