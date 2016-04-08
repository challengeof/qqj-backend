package com.mishu.cgwy.banner.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.banner.controller.BannerRequest;
import com.mishu.cgwy.banner.controller.PushRequest;
import com.mishu.cgwy.banner.domain.Banner;
import com.mishu.cgwy.banner.domain.Banner_;
import com.mishu.cgwy.banner.domain.Push;
import com.mishu.cgwy.banner.domain.Push_;
import com.mishu.cgwy.banner.dto.BannerUrl;
import com.mishu.cgwy.banner.dto.Message;
import com.mishu.cgwy.banner.repository.BannerRepository;
import com.mishu.cgwy.banner.repository.PushRepository;
import com.mishu.cgwy.banner.vo.BannerVo;
import com.mishu.cgwy.banner.vo.PushVo;
import com.mishu.cgwy.profile.repository.RestaurantRepository;
import com.mishu.cgwy.push.repository.PushMappingRepository;
import com.mishu.cgwy.utils.ExpressionUtils;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.*;

/**
 * Created by bowen on 15-5-25.
 */
@Service
public class BannerService {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private BannerRepository bannerRepository;
    @Autowired
    private PushRepository pushRepository;
    @Autowired
    private PushMappingRepository pushMappingRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<Banner> getApplicableBanner(Date current, final Long warehouseId, final Long cityId) {



        final List<Banner> candidateBanners = getCandidateBanners(current,warehouseId,cityId);
        return new ArrayList<>(Collections2.filter(candidateBanners, new com.google.common.base
                .Predicate<Banner>() {
            @Override
            public boolean apply(Banner input) {
                return couldOfferApplyToWarehouse(input, warehouseId, cityId);
            }
        }));
    }

    boolean couldOfferApplyToWarehouse(Banner banner, Long warehouseId, Long cityId) {
        HashMap<String, Object> vars = new HashMap<>();
        vars.put("cityId", cityId);
        vars.put("warehouseId", warehouseId);
        return ExpressionUtils.executeExpression(banner.getRule(), vars);
    }

    private List<Banner> getCandidateBanners(final Date date,final Long warehouseId, final Long cityId) {
        return bannerRepository.findAll(new Specification<Banner>() {
            @Override
            public Predicate toPredicate(Root<Banner> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if(warehouseId == null || warehouseId == 0L){
                    return cb.and(cb.lessThanOrEqualTo(root.get(Banner_.start), date), cb.greaterThanOrEqualTo(root
                            .get(Banner_.end), date),cb.equal(root.get(Banner_.rule),"cityId=="+cityId+"&&true"));
                }else{
                    return cb.or(cb.and(cb.lessThanOrEqualTo(root.get(Banner_.start), date), cb.greaterThanOrEqualTo(root
                                    .get(Banner_.end), date),cb.equal(root.get(Banner_.rule),"cityId=="+cityId+"&&true")),
                            cb.and(cb.lessThanOrEqualTo(root.get(Banner_.start), date), cb.greaterThanOrEqualTo(root
                                    .get(Banner_.end), date),cb.equal(root.get(Banner_.rule),"cityId=="+cityId+"&&warehouseId=="+warehouseId)));
                }

            }
        }, new Sort("orderValue"));
    }

    @Transactional(readOnly = true)
    public List<Push> getApplicablePush(Date current, final Long warehouseId, final Long cityId) {
        final List<Push> candidateBanners = getCandidatePushs(current);
        return new ArrayList<>(Collections2.filter(candidateBanners, new com.google.common.base
                .Predicate<Push>() {
            @Override
            public boolean apply(Push input) {
                return couldOfferApplyToWarehouse(input, warehouseId, cityId);
            }
        }));
    }


    private List<Push> getCandidatePushs(final Date date) {
        return pushRepository.findAll(new Specification<Push>() {
            @Override
            public Predicate toPredicate(Root<Push> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.and(cb.lessThanOrEqualTo(root.get(Push_.start), date), cb.greaterThanOrEqualTo(root
                        .get(Push_.end), date));
            }
        });
    }


    boolean couldOfferApplyToWarehouse(Push push, Long warehouseId, Long cityId) {
        HashMap<String, Object> vars = new HashMap<>();
        vars.put("cityId", cityId);
        vars.put("warehouseId", warehouseId);
        return ExpressionUtils.executeExpression(push.getRule(), vars);
    }

    @Transactional
    public void createBanner(BannerRequest bannerRequest) {
        Banner banner = new Banner();
        copyBanner(banner, bannerRequest);
        bannerRepository.save(banner);
    }

    @Transactional(readOnly = true)
    public List<BannerVo> getBanners(Long cityId) {
        List<Banner> bannerList = null;
        //根据 cityId 判断调用
        if(cityId != null && cityId != 0){
            bannerList = getBannersByCity(cityId);
        }else{
            bannerList = bannerRepository.findAll();
        }

        return getBannerVos(bannerList);
    }

    public List<BannerVo> getBannerVos(List<Banner> bannerList){
        List<BannerVo> bannerVoList = new ArrayList<>();
            for (Banner banner : bannerList) {
            BannerVo bannerVo = new BannerVo();
            bannerVo.setId(banner.getId());
            bannerVo.setStart(banner.getStart());
            bannerVo.setEnd(banner.getEnd());
            bannerVo.setDescription(banner.getDescription());
            bannerVo.setOrderValue(banner.getOrderValue());
            try {
                bannerVo.setBannerUrl(new ObjectMapper().readValue(banner.getContent(), BannerUrl.class));
            } catch (Exception e) {}
            bannerVoList.add(bannerVo);
        }
        return bannerVoList;
    }

    public List<Banner> getBannersByCity(Long cityId){
        StringBuilder stringBuilder = new StringBuilder();
        //cityId 组成规则
        if(cityId != null && cityId !=0){
            stringBuilder.append("cityId=="+cityId);
        }
        //创建查询
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery query = cb.createTupleQuery();
        final Root<Banner> root = query.from(Banner.class);
        //predicate 用于多条件查询
        query.where(cb.like(root.get("rule").as(String.class),"%"+stringBuilder.toString()+"%"));

        query.multiselect(
            root.get(Banner_.id),
            root.get(Banner_.start),
            root.get(Banner_.end),
            root.get(Banner_.content),
            root.get(Banner_.description),
            root.get(Banner_.rule),
            root.get(Banner_.orderValue),
            root.get(Banner_.shoppingTip),
            root.get(Banner_.welcomeMessage)
        );

        TypedQuery typedQuery = entityManager.createQuery(query);

        List<Tuple> tuples = typedQuery.getResultList();

        List<Banner> bannerList= new ArrayList<>();

        for(Tuple tuple : tuples){
            Banner banner = new Banner();
            banner.setId(tuple.get(0,Long.class));
            banner.setStart(tuple.get(1,Date.class));
            banner.setEnd(tuple.get(2,Date.class));
            banner.setContent(tuple.get(3,String.class));
            banner.setDescription(tuple.get(4,String.class));
            banner.setRule(tuple.get(5,String.class));
            banner.setOrderValue(tuple.get(6,Integer.class));
            banner.setShoppingTip(tuple.get(7,String.class));
            banner.setWelcomeMessage(tuple.get(8,String.class));
            bannerList.add(banner);
        }

        return bannerList;
    }


    @Transactional
    public Banner updateBanner(Long id, BannerRequest bannerRequest) {
        Banner banner = bannerRepository.getOne(id);
        copyBanner(banner, bannerRequest);

        /**add by xgl */
        //增加对banner 图的排序功能
        Integer orderValue = bannerRequest.getOrderValue();

        if(orderValue != null && orderValue != 0){
            bannerRepository.updateBannerOrder(banner.getId(),banner.getRule(),orderValue);
            banner.setOrderValue(orderValue);
        }

        return bannerRepository.save(banner);
    }

    @Transactional(readOnly = true)
    public BannerVo getBanner(Long id) {
        Banner banner = bannerRepository.getOne(id);
        BannerVo bannerVo = new BannerVo();
        bannerVo.setId(banner.getId());
        bannerVo.setRule(banner.getRule());
        bannerVo.setStart(banner.getStart());
        bannerVo.setEnd(banner.getEnd());
        bannerVo.setDescription(banner.getDescription());
        bannerVo.setOrderValue(banner.getOrderValue());
        try {
            bannerVo.setBannerUrl(new ObjectMapper().readValue(banner.getContent(), BannerUrl.class));
        } catch (Exception e) {}

        String [] str = banner.getRule().split("&&");
        String [] str1 = str[0].split("==");
        Long cityId = Long.valueOf(str1[1]);
        bannerVo.setCityId(cityId);

        long warehouseId = 0l;
        if (!str[1].equals("true")) {
            String[] str2 = str[1].split("==");
            warehouseId = Long.valueOf(str2[1]);
        }

        bannerVo.setWarehouseId(warehouseId);

        return bannerVo;
    }

    @Transactional
    public void createPush(PushRequest pushRequest) {

        Push push = new Push();
        copyPush(push, pushRequest);
        pushRepository.save(push);
    }

    @Transactional(readOnly = true)
    public PushVo getPush(Long id) {
        Push push = pushRepository.getOne(id);
        PushVo pushVo = new PushVo();
        pushVo.setId(push.getId());
        pushVo.setStart(push.getStart());
        pushVo.setEnd(push.getEnd());
        pushVo.setDescription(push.getDescription());
        pushVo.setRule(push.getRule());
        try {
            pushVo.setMessage(new ObjectMapper().readValue(push.getWelcomeMessage(), Message.class));
        } catch (Exception e) {}
        pushVo.setShoppingTip(push.getShoppingTip());

        String [] str = push.getRule().split("&&");
        String [] str1 = str[0].split("==");
        Long cityId = Long.valueOf(str1[1]);
        pushVo.setCityId(cityId);

        long warehouseId = 0l;
        if (!str[1].equals("true")) {
            String[] str2 = str[1].split("==");
            warehouseId = Long.valueOf(str2[1]);
        }
        pushVo.setWarehouseId(warehouseId);

        return pushVo;
    }

    @Transactional(readOnly = true)
    public List<PushVo> getPushes() {
        List<Push> pushList = pushRepository.findAll();
        List<PushVo> pushVoList = new ArrayList<>();
        for (Push push : pushList) {
            PushVo pushVo = new PushVo();
            pushVo.setId(push.getId());
            pushVo.setStart(push.getStart());
            pushVo.setEnd(push.getEnd());
            pushVo.setDescription(push.getDescription());
            pushVo.setRule(push.getRule());
            try {
                pushVo.setMessage(new ObjectMapper().readValue(push.getWelcomeMessage(), Message.class));
            } catch (Exception e) {}
            pushVo.setShoppingTip(push.getShoppingTip());
            pushVoList.add(pushVo);
        }
        return pushVoList;
    }

    @Transactional
    public Push updatePush(Long id, PushRequest request) {
        Push push = pushRepository.getOne(id);
        copyPush(push, request);
        return pushRepository.save(push);
    }

    private void copyBanner(Banner banner, BannerRequest bannerRequest) {

        banner.setDescription(bannerRequest.getDescription());
        banner.setStart(bannerRequest.getStart());
        banner.setEnd(bannerRequest.getEnd());
        StringBuilder stringBuilder = new StringBuilder();
        if (bannerRequest.getCityId() != null) {
            stringBuilder.append("cityId==" + bannerRequest.getCityId() + "&&");
        }
        if (bannerRequest.getWarehouseId().equals(0L)) {
//            banner.setRule(Boolean.TRUE.toString());
            stringBuilder.append(Boolean.TRUE.toString());
        } else {
//            banner.setRule(String.format("warehouseId==%s", bannerRequest.getWarehouseId()));
            stringBuilder.append(String.format("warehouseId==%s", bannerRequest.getWarehouseId()));
        }
        banner.setRule(stringBuilder.toString());
        if (bannerRequest.getBannerUrl() != null) {
            try {
                String content = objectMapper.writeValueAsString(bannerRequest.getBannerUrl());
                banner.setContent(content);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyPush(Push push, PushRequest pushRequest) {
        push.setDescription(pushRequest.getDescription());
        push.setStart(pushRequest.getStart());
        push.setEnd(pushRequest.getEnd());
        push.setShoppingTip(pushRequest.getShoppingTip());
        StringBuilder stringBuilder = new StringBuilder();
        if (pushRequest.getCityId() != null) {
            stringBuilder.append("cityId==" + pushRequest.getCityId() + "&&");
        }
        if (pushRequest.getWarehouseId().equals(0L)) {
//            push.setRule(Boolean.TRUE.toString());
            stringBuilder.append(Boolean.TRUE.toString());
        } else {
//            push.setRule(String.format("warehouseId==%s", pushRequest.getWarehouseId()));
            stringBuilder.append(String.format("warehouseId==%s", pushRequest.getWarehouseId()));
        }
        push.setRule(stringBuilder.toString());
        if (pushRequest.getMessage() != null) {
            try {
                String message = objectMapper.writeValueAsString(pushRequest.getMessage());
                push.setWelcomeMessage(message);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

}
