package com.mishu.cgwy.promotion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.common.domain.Block_;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.common.domain.Warehouse_;
import com.mishu.cgwy.common.repository.CityRepository;
import com.mishu.cgwy.common.repository.WarehouseRepository;
import com.mishu.cgwy.coupon.PromotionUtils;
import com.mishu.cgwy.coupon.constant.PromotionConstant;
import com.mishu.cgwy.coupon.constant.PromotionRuleConstant;
import com.mishu.cgwy.coupon.controller.PromotionRequest;
import com.mishu.cgwy.coupon.wrapper.PromotionFullWrapper;
import com.mishu.cgwy.inventory.domain.BundleDynamicSkuPriceStatus_;
import com.mishu.cgwy.inventory.domain.DynamicSkuPrice_;
import com.mishu.cgwy.inventory.domain.SingleDynamicSkuPriceStatus_;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.order.repository.OrderRepository;
import com.mishu.cgwy.order.wrapper.PromotionWrapper;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.product.service.SkuService;
import com.mishu.cgwy.profile.domain.Customer_;
import com.mishu.cgwy.profile.domain.Restaurant_;
import com.mishu.cgwy.promotion.controller.PromotionListRequest;
import com.mishu.cgwy.promotion.controller.PromotionStatisticsRequest;
import com.mishu.cgwy.promotion.domain.PromotableItems;
import com.mishu.cgwy.promotion.domain.PromotableItems_;
import com.mishu.cgwy.promotion.domain.Promotion;
import com.mishu.cgwy.promotion.domain.Promotion_;
import com.mishu.cgwy.promotion.repository.PromotionRepository;
import com.mishu.cgwy.promotion.vo.PromotionStatisticsVo;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.StockOut;
import com.mishu.cgwy.stock.domain.StockOutItem;
import com.mishu.cgwy.stock.domain.StockOutItem_;
import com.mishu.cgwy.stock.domain.StockOut_;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import com.mishu.cgwy.utils.ExpressionUtils;
import com.mishu.cgwy.utils.JpaQueryUtils;
import com.mishu.cgwy.utils.NumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.elasticsearch.common.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: xudong
 * Date: 4/29/15
 * Time: 1:42 AM
 */
@Service
public class PromotionService {
    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SkuService skuService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;
    public final static String PROMOTION_FULLCUT_LIST = "/template/promotion-fullcut-list.xls";
    public final static String PROMOTION_FULLGIFT_LIST = "/template/promotion-fullgift-list.xls";

    @Transactional(readOnly = true)
    public List<Promotion> findApplicablePromotion(final Order order, Date current, final Warehouse warehouse, final Organization organization) {
        final List<Promotion> candidatePromotions = getCandidatePromotions(current);
        return new ArrayList<>(Collections2.filter(candidatePromotions, new com.google.common.base.Predicate<Promotion>() {
            @Override
            public boolean apply(Promotion input) {
                return couldOfferApplyToOrder(input, order, warehouse, organization);
            }
        }));
    }

    private List<Promotion> getCandidatePromotions(final Date date) {
        return promotionRepository.findAll(new Specification<Promotion>() {
            @Override
            public Predicate toPredicate(Root<Promotion> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.and(
                        cb.lessThanOrEqualTo(root.get(Promotion_.start), date),
                        cb.greaterThanOrEqualTo(root.get(Promotion_.end), date),
                        cb.isTrue(root.get(Promotion_.enabled)));
            }
        });
    }

    public boolean couldOfferApplyToOrder(Promotion promotion, Order order, Warehouse warehouse, Organization organization) {
        HashMap<String, Object> vars = new HashMap<>();
        vars.put("order", order);
        vars.put("warehouse", warehouse);
        vars.put("city", warehouse.getCity());
        vars.put("organization", organization);
        return ExpressionUtils.executeExpression(PromotionUtils.getRealRule(promotion.getRule(), promotion.getRuleValue()), vars);
    }

    @Transactional
    public void convert() throws Exception {
        List<Promotion> promotionList = promotionRepository.findAll();
        for (Promotion promotion : promotionList) {
            String rule = promotion.getRule();
            PromotionRequest request = new PromotionRequest();

            Pattern warehousePattern = Pattern.compile("warehouse\\.id\\=\\=(\\d+)");
            Matcher warehouseMatcher = warehousePattern.matcher(rule);
            if (warehouseMatcher.find()) {
                request.setWarehouseId(Long.valueOf(warehouseMatcher.group(1)));
            }

            Pattern totalPattern = Pattern.compile("order\\.subTotal\\>\\=(\\d+)");
            Matcher totalMatcher = totalPattern.matcher(rule);
            if (totalMatcher.find()) {
                request.setUseRestrictionsTotalMin(new BigDecimal(totalMatcher.group(1)));
            }

            Pattern categoryPattern = Pattern.compile("OrderService\\.getOrderAmountByCategories\\(order((,-?\\d+)?)\\)\\>\\=(\\d+)");
            Matcher categoryMatcher = categoryPattern.matcher(rule);
            if (categoryMatcher.find()) {
                List<Long> cateList = new ArrayList<>();
                String cateStr = categoryMatcher.group(1);
                for (String cate : cateStr.split(",")) {
                    if (StringUtils.isNoneBlank(cate)) {
                        cateList.add(Long.valueOf(cate));
                    }
                }
                request.setUseRestrictionsCategoryIds(cateList.toArray(new Long[cateList.size()]));
            }

            Pattern maxPattern = Pattern.compile("OrderService\\.getOrderAmountByCategories\\(order((,-?\\d+)?)\\)\\<(\\d+)");
            Matcher maxMatcher = maxPattern.matcher(rule);
            if (maxMatcher.find()) {
                request.setUseRestrictionsTotalMax(new BigDecimal(maxMatcher.group(3)));
            } else {
                request.setUseRestrictionsTotalMax(new BigDecimal(99999999));
            }

//            no brands
//            request.setBrandId();

            Pattern buySkuPattern = Pattern.compile("OrderService\\.getPromotionBySku\\(order,(\\d+),(true|false),(\\d+)\\)");
            Matcher buySkuMatcher = buySkuPattern.matcher(rule);
            if (buySkuMatcher.find()) {
                request.setBuyQuantity(Integer.valueOf(buySkuMatcher.group(1)));
                request.setBuySkuUnit(Boolean.valueOf(buySkuMatcher.group(2)));
                request.setBuySkuId(Long.valueOf(buySkuMatcher.group(3)));
            }

            Pattern cityPattern = Pattern.compile("city\\.id\\=\\=(\\d+)");
            Matcher cityMatcher = cityPattern.matcher(rule);
            if (cityMatcher.find()) {
                request.setCityId(Long.valueOf(cityMatcher.group(1)));
            }

            request.setDescription(promotion.getDescription());
            request.setDiscount(promotion.getDiscount());
            request.setEnd(promotion.getEnd());
            request.setLimitedQuantity(promotion.getLimitedQuantity());

            Pattern oPattern = Pattern.compile("organization\\.id\\=\\=(\\d+)");
            Matcher oMatcher = oPattern.matcher(rule);
            if (oMatcher.find()) {
                request.setOrganizationId(Long.valueOf(oMatcher.group(1)));
            }
            request.setPromotionPattern(promotion.getPromotionConstants());
            request.setPromotionType(promotion.getType());
            if (promotion.getPromotableItems() != null && promotion.getPromotableItems().getSku() != null) {
                request.setQuantity(promotion.getPromotableItems().getQuantity());
                request.setSkuId(promotion.getPromotableItems().getSku().getId());
                request.setSkuUnit(promotion.getPromotableItems().isBundle());
            }

            request.setStart(promotion.getStart());

            System.out.println("id:" + promotion.getId() + "\t" + new ObjectMapper().writeValueAsString(request));

            updatePromotion(promotion.getId(), request);
        }
    }

    @Transactional
    public void updatePromotion(Long id, PromotionRequest promotionRequest) {

        Promotion promotion = promotionRepository.getOne(id);
        BeanUtils.copyProperties(formPromotion(promotionRequest), promotion, "id", "createTime");
        promotionRepository.save(promotion);
    }

    private Promotion formPromotion(PromotionRequest promotionRequest) {
        Promotion promotion = new Promotion();
        promotion.setEnabled(Boolean.TRUE);
        promotion.setPromotionConstants(promotionRequest.getPromotionPattern());
        promotion.setType(promotionRequest.getPromotionType());

        if (promotionRequest.getDiscount() != null) {

            promotion.setDiscount(promotionRequest.getDiscount());

        } else {

            promotion.setDiscount(BigDecimal.ZERO);
        }


        PromotableItems promotableItems = new PromotableItems();
        if (PromotionConstant.ORDER_WITH_A_GIFT_SEND.getType().equals(promotionRequest.getPromotionType()) || PromotionConstant.TWO_FOR_ONE.getType().equals(promotionRequest.getPromotionType())) {

            promotableItems.setSku(skuService.getOne(promotionRequest.getSkuId()));
            promotableItems.setQuantity(promotionRequest.getQuantity());
            promotableItems.setBundle(promotionRequest.isSkuUnit());
        } else {
            promotableItems.setQuantity(0);
        }
        promotion.setPromotableItems(promotableItems);
        promotion.setLimitedQuantity(promotionRequest.getLimitedQuantity());
        promotion.setStart(promotionRequest.getStart());
        promotion.setEnd(promotionRequest.getEnd());
        promotion.setDescription(promotionRequest.getDescription());
        promotion.setRule(getRule(promotionRequest));

        try {
            promotion.setRuleValue(new ObjectMapper().writeValueAsString(promotionRequest));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return promotion;
    }

    private String getRule(PromotionRequest promotionRequest) {
        String useRule = "";

        if (PromotionConstant.TWO_FOR_ONE.getType().equals(promotionRequest.getPromotionType())) {
            useRule = append(useRule,String.format("true==OrderService.getPromotionBySku(order,%s,%s,%s)", PromotionRuleConstant.buyQuantityRule, PromotionRuleConstant.buySkuUnitRule, PromotionRuleConstant.buySkuIdRule));
        }

        if (promotionRequest.getBrandId() != null && promotionRequest.getUseRestrictionsTotalMin() != null) {
            useRule = append(useRule, String.format("OrderService.getOrderAmountByBrands(order,%s)>=%s", PromotionRuleConstant.brandIdRule, PromotionRuleConstant.useRestrictionsTotalMinRule));
        }

        if (promotionRequest.getBrandId() != null && promotionRequest.getUseRestrictionsTotalMax() != null) {
            useRule = append(useRule, String.format("OrderService.getOrderAmountByBrands(order,%s)<%s", PromotionRuleConstant.brandIdRule, PromotionRuleConstant.useRestrictionsTotalMaxRule));
        }

        if (promotionRequest.getOrganizationId() != null) {
            useRule = append(useRule, String.format("organization.id==%s", PromotionRuleConstant.organizationIdRule));
            if (ArrayUtils.isNotEmpty(promotionRequest.getUseRestrictionsCategoryIds())) {
                if (promotionRequest.getUseRestrictionsTotalMin() != null) {
                    useRule = append(useRule, String.format("OrderService.getOrderAmountByCategories(order,%s)>=%s", PromotionRuleConstant.useRestrictionsCategoryIdsRule, PromotionRuleConstant.useRestrictionsTotalMinRule));
                }
                if (promotionRequest.getUseRestrictionsTotalMax() != null) {
                    useRule = append(useRule, String.format("OrderService.getOrderAmountByCategories(order,%s)<%s", PromotionRuleConstant.useRestrictionsCategoryIdsRule, PromotionRuleConstant.useRestrictionsTotalMaxRule));
                }
            }
        }

        if (promotionRequest.getCityId() != null) {
            useRule = append(useRule, String.format("city.id==%s", PromotionRuleConstant.cityIdRule));
        }

        if (promotionRequest.getUseRestrictionsTotalMin() != null) {
            useRule = append(useRule, String.format("order.subTotal>=%s", PromotionRuleConstant.useRestrictionsTotalMinRule));
        }

        if (promotionRequest.getUseRestrictionsTotalMax() != null) {
            useRule = append(useRule, String.format("order.subTotal<%s", PromotionRuleConstant.useRestrictionsTotalMaxRule));
        }

        if (promotionRequest.getWarehouseId() != null) {
            useRule = append(useRule, String.format("warehouse.id==%s", PromotionRuleConstant.warehouseIdRule));
        }

        return useRule == null ? Boolean.TRUE.toString() : useRule;
    }

    private String append(String str, String appendStr) {
        return str += StringUtils.isEmpty(str) ? appendStr : "&&" + appendStr;
    }

    @Transactional
    public void createPromotion(PromotionRequest promotionRequest) {

        Promotion promotion = formPromotion(promotionRequest);
        promotion.setCreateTime(new Date());
        promotionRepository.save(promotion);

    }

    @Transactional(readOnly = true)
    public QueryResponse<PromotionWrapper> getPromotionList(final PromotionListRequest request) {

        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());

        List<PromotionWrapper> promotionList = new ArrayList<>();
        Page<Promotion> page = promotionRepository.findAll(new Specification<Promotion>() {
            @Override
            public Predicate toPredicate(Root<Promotion> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if (request.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(Promotion_.createTime), request.getStartDate()));
                }

                if (request.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(Promotion_.createTime), request.getEndDate()));
                }

                if (request.getPromotionType() != null) {
                    predicates.add(cb.equal(root.get(Promotion_.type), request.getPromotionType()));
                }
                query.orderBy(cb.desc(root.get(Promotion_.id)));
                return CollectionUtils.isEmpty(predicates) ? cb.and() : cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        },pageable);
        for (Promotion promotion : page.getContent()) {
            promotionList.add(new PromotionWrapper(promotion));

        }

        QueryResponse<PromotionWrapper> res = new QueryResponse<PromotionWrapper>();
        res.setContent(promotionList);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());

        return res;
    }

    @Transactional(readOnly = true)
    public PromotionFullWrapper getPromotion(Long id) throws Exception {
        return new PromotionFullWrapper(promotionRepository.getOne(id));
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportPromotionFullList(final PromotionStatisticsRequest request, List datas,String fileName, String template) throws Exception {

        Map<String, Object> beans = new HashMap<>();
        beans.put("list", datas);
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("warehouse", request.getWarehouseId() == null ? "全部" : warehouseRepository.getOne(request.getWarehouseId()).getName());
        beans.put("now", new Date());
        return ExportExcelUtils.generateExcelBytes(beans, fileName, template);
    }

    @Transactional(rollbackFor=Exception.class)
    public PromotionStatisticsVo getPromotionStatisticsSum(final PromotionStatisticsRequest request) {
        return JpaQueryUtils.valSelect(Order.class, new PromotionStatisticsSpecification(request), this.entityManager, new JpaQueryUtils.SelectPathGetting<Order, PromotionStatisticsVo, PromotionStatisticsSpecification>() {
            @Override
            public Selection<?>[] getSelectPath(CriteriaBuilder cb, CriteriaQuery query, Root<Order> root, PromotionStatisticsSpecification specification) {
                return new Selection<?>[]{
                        // 成本额
                        cb.prod(specification.getStockOutItemJoin().get(StockOutItem_.receiveQuantity) ,
                                specification.getStockOutItemJoin().get(StockOutItem_.avgCost)),
                        // 销售额
                        cb.prod(specification.getPromotionJoin().get(Promotion_.promotableItems).get(PromotableItems_.quantity),
                                cb.<Number>selectCase().when(
                                        cb.isTrue(specification.getPromotionJoin().get(Promotion_.promotableItems).get(PromotableItems_.bundle)),
                                        specification.getDynamicSkuPriceJoin().get(DynamicSkuPrice_.bundlePriceStatus).get(BundleDynamicSkuPriceStatus_.bundleSalePrice)
                                ).otherwise(specification.getDynamicSkuPriceJoin().get(DynamicSkuPrice_.singlePriceStatus).get(SingleDynamicSkuPriceStatus_.singleSalePrice))
                        )
                };
            }

            @Override
            public PromotionStatisticsVo resultWrappe(List<Tuple> tuples) {
                BigDecimal avgCostAmount=new BigDecimal(0);     //成本额 = 成本单价*数量
                BigDecimal saleCostAmount=new BigDecimal(0);    //销售额= 销售单价*数量
                for(Tuple tuple : tuples){
                    avgCostAmount=avgCostAmount.add(tuple.get(0, BigDecimal.class)==null? new BigDecimal(0) :tuple.get(0, BigDecimal.class));
                    saleCostAmount=saleCostAmount.add(tuple.get(1, BigDecimal.class)==null? new BigDecimal(0) :tuple.get(1, BigDecimal.class));
                }

                PromotionStatisticsVo promotionStatisticsVo = new PromotionStatisticsVo();
                promotionStatisticsVo.setAvgCostAmount(avgCostAmount);
                promotionStatisticsVo.setSaleCostAmount(saleCostAmount);
                return promotionStatisticsVo;
            }
        });
    }

    /**
     * 查询满赠
     * @param request
     * @return
     */
    @Transactional(rollbackFor=Exception.class)
    public Page<PromotionStatisticsVo> getPromotionStatistics(final PromotionStatisticsRequest request) {
        final PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize(),
                new Sort(request.isAsc()?Sort.Direction.ASC:Sort.Direction.DESC, request.getSortField())
        );
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery query = cb.createTupleQuery();
        final Root<Order> root = query.from(Order.class);

        PromotionStatisticsSpecification promotionStatisticsSpecification = new PromotionStatisticsSpecification(request);
        query.where(promotionStatisticsSpecification.toPredicate(root, query, cb));
        query.multiselect(
                root.get(Order_.id),
                root.get(Order_.submitDate),
                promotionStatisticsSpecification.getStockOutJoin().get(StockOut_.receiveDate),
                root.get(Order_.restaurant).get(Restaurant_.id),
                root.get(Order_.restaurant).get(Restaurant_.name),
                promotionStatisticsSpecification.getPromotionJoin().get(Promotion_.type),
                promotionStatisticsSpecification.getPromotionJoin().get(Promotion_.id),
                promotionStatisticsSpecification.getPromotionJoin().get(Promotion_.promotableItems).get(PromotableItems_.sku).get(Sku_.id),
                promotionStatisticsSpecification.getPromotionJoin().get(Promotion_.promotableItems).get(PromotableItems_.sku).get(Sku_.product).get(Product_.name),
                promotionStatisticsSpecification.getPromotionJoin().get(Promotion_.promotableItems).get(PromotableItems_.bundle),
                promotionStatisticsSpecification.getPromotionJoin().get(Promotion_.promotableItems).get(PromotableItems_.quantity),
                promotionStatisticsSpecification.getStockOutItemJoin().get(StockOutItem_.receiveQuantity),
                promotionStatisticsSpecification.getPromotionJoin().get(Promotion_.description),
                cb.selectCase().when(cb.isNull(promotionStatisticsSpecification.getStockOutItemJoin().get(StockOutItem_.avgCost)),new BigDecimal(0))
                        .otherwise(promotionStatisticsSpecification.getStockOutItemJoin().get(StockOutItem_.avgCost)),
                promotionStatisticsSpecification.getDynamicSkuPriceJoin().get(DynamicSkuPrice_.singlePriceStatus).get(SingleDynamicSkuPriceStatus_.singleSalePrice),//单品销售价
                promotionStatisticsSpecification.getDynamicSkuPriceJoin().get(DynamicSkuPrice_.bundlePriceStatus).get(BundleDynamicSkuPriceStatus_.bundleSalePrice),//打包销售价
                promotionStatisticsSpecification.getSkuJoin().get(Sku_.capacityInBundle),//转化率
//                单位
                cb.<String>selectCase().when(cb.isTrue(promotionStatisticsSpecification.getPromotionJoin().get(Promotion_.promotableItems).get(PromotableItems_.bundle)),
                        promotionStatisticsSpecification.getSkuJoin().get(Sku_.bundleUnit)
                ).otherwise(promotionStatisticsSpecification.getSkuJoin().get(Sku_.singleUnit))
        );
        query.orderBy(QueryUtils.toOrders(pageRequest.getSort(), root, cb));
        TypedQuery typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pageRequest.getOffset());
        typedQuery.setMaxResults(pageRequest.getPageSize());

        List<Tuple> tuples = typedQuery.getResultList();
        List<PromotionStatisticsVo> datas = new ArrayList<>();
        for (Tuple tuple : tuples) {
            PromotionStatisticsVo vo = new PromotionStatisticsVo();
            vo.setOrderId(tuple.get(0, Long.class));
            vo.setSubmitDate(tuple.get(1, Date.class));
            vo.setReceiveDate(tuple.get(2, Date.class));
            vo.setRestaurantId(tuple.get(3, Long.class));
            vo.setRestaurantName(tuple.get(4, String.class));
            vo.setPromotionType(PromotionConstant.getPromotionConstantByType(tuple.get(5, Integer.class)));
            vo.setPromotionId(tuple.get(6, Long.class));
            vo.setSkuId(tuple.get(7, Long.class));
            vo.setSkuName(tuple.get(8, String.class));
            Boolean bundle = tuple.get(9, Boolean.class);
            vo.setBundle(bundle);
            Integer quantity = tuple.get(10, Integer.class);
            vo.setQuantity(quantity);
            Integer receiveQuantity = tuple.get(11, Integer.class);
            vo.setReceiveQuantity(receiveQuantity);
            vo.setPromotionDes(tuple.get(12, String.class));
            BigDecimal avgCost = NumberUtils.cancelNull(tuple.get(13, BigDecimal.class));
            vo.setAvgCost(avgCost);
            BigDecimal saleCost = NumberUtils.cancelNull(tuple.get(14, BigDecimal.class));
            vo.setSaleCost(saleCost);
            BigDecimal saleCostBundle = tuple.get(15, BigDecimal.class);
            Integer capacityInBundle = tuple.get(16, Integer.class);
            vo.setSaleCostBundle(saleCostBundle == null || (saleCost.compareTo(new BigDecimal(0)) == 0) ? saleCost.multiply(new BigDecimal(capacityInBundle)) : new BigDecimal(0));
            vo.setCapacityInBundle(capacityInBundle);
            vo.setAvgCostAmount(avgCost.multiply(new BigDecimal(receiveQuantity)));
            vo.setSaleCostAmount((bundle ? saleCostBundle : saleCost).multiply(new BigDecimal(quantity)));
            vo.setSkuUnit(tuple.get(17,String.class));

            datas.add(vo);
        }
        //查结果集数量
        long lineCnt = JpaQueryUtils.lineCount(Order.class, new PromotionStatisticsSpecification(request), entityManager);
        Page<PromotionStatisticsVo> result = new PageImpl<PromotionStatisticsVo>(datas, pageRequest, lineCnt);
        return result;
    }

    /**
     * 满减合计
     * @param request
     * @return
     */
    @Transactional(rollbackFor=Exception.class)
    public PromotionStatisticsVo getPromotionStatisticsFullCutSum(final PromotionStatisticsRequest request) {
        return JpaQueryUtils.valSelect(Order.class, new PromotionStatisticsSpecification(request), this.entityManager, new JpaQueryUtils.SelectPathGetting<Order, PromotionStatisticsVo, PromotionStatisticsSpecification>() {
            @Override
            public Selection<?>[] getSelectPath(CriteriaBuilder cb, CriteriaQuery query, Root<Order> root, PromotionStatisticsSpecification specification) {
                return new Selection<?>[]{
                    specification.getPromotionJoin().get(Promotion_.discount)           // 优惠额度
                };
            }
            @Override
            public PromotionStatisticsVo resultWrappe(List<Tuple> tuples) {
                BigDecimal discountSum=new BigDecimal(0);     //成本额 = 成本单价*数量
                for(Tuple tuple : tuples){
                    discountSum=discountSum.add(tuple.get(0, BigDecimal.class)==null? new BigDecimal(0) :tuple.get(0, BigDecimal.class));
                }
                PromotionStatisticsVo vo = new PromotionStatisticsVo();
                vo.setDiscount(discountSum);

                return vo;
            }
        });
    }


    /**
     * 查询满减
     */
    @Transactional(rollbackFor=Exception.class)
    public Page<PromotionStatisticsVo> getPromotionStatisticsFullCut(final PromotionStatisticsRequest request) {
        final PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize(),
                new Sort(request.isAsc()?Sort.Direction.ASC:Sort.Direction.DESC, request.getSortField())
        );
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        final CriteriaQuery query = cb.createTupleQuery();
        final Root<Order> root = query.from(Order.class);

        PromotionStatisticsSpecification promotionStatisticsSpecification = new PromotionStatisticsSpecification(request);
        query.where(promotionStatisticsSpecification.toPredicate(root, query, cb));
        query.multiselect(
                root.get(Order_.id),
//                orderId
                root.get(Order_.submitDate),

                promotionStatisticsSpecification.getStockOutJoin().get(StockOut_.receiveDate),
//                orderSubmitTime
                root.get(Order_.restaurant).get(Restaurant_.id),
//                restaurantId
                root.get(Order_.restaurant).get(Restaurant_.name),
//                restaurantName
                promotionStatisticsSpecification.getPromotionJoin().get(Promotion_.type),
//                promotionType
                promotionStatisticsSpecification.getPromotionJoin().get(Promotion_.id),
//                promotionId
                promotionStatisticsSpecification.getPromotionJoin().get(Promotion_.discount),
//                discount
                promotionStatisticsSpecification.getPromotionJoin().get(Promotion_.description)
//                promotionDes

        );
        query.orderBy(QueryUtils.toOrders(pageRequest.getSort(), root, cb));
        TypedQuery typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pageRequest.getOffset());
        typedQuery.setMaxResults(pageRequest.getPageSize());

        List<Tuple> tuples = typedQuery.getResultList();
        List<PromotionStatisticsVo> datas = new ArrayList<>();
        for (Tuple tuple : tuples) {
            PromotionStatisticsVo vo = new PromotionStatisticsVo();
            vo.setOrderId(tuple.get(0,Long.class));
            vo.setSubmitDate(tuple.get(1, Date.class));
            vo.setReceiveDate(tuple.get(2, Date.class));
            vo.setRestaurantId(tuple.get(3, Long.class));
            vo.setRestaurantName(tuple.get(4, String.class));
            vo.setPromotionType(PromotionConstant.getPromotionConstantByType(tuple.get(5, Integer.class)));
            vo.setPromotionId(tuple.get(6, Long.class));
            vo.setDiscount(tuple.get(7, BigDecimal.class));
            vo.setPromotionDes(tuple.get(8, String.class));
            datas.add(vo);
        }
        //查结果集数量
        long lineCnt = JpaQueryUtils.lineCount(Order.class, new PromotionStatisticsSpecification(request), entityManager);
        Page<PromotionStatisticsVo> result = new PageImpl<PromotionStatisticsVo>(datas, pageRequest, lineCnt);
        return result;
    }

    private class PromotionStatisticsSpecification implements Specification<Order>{
        private PromotionStatisticsRequest request;
        public PromotionStatisticsSpecification(PromotionStatisticsRequest request) {
            this.request = request;
        }
//        private ListJoin dynamicSkuPrice;
//        private Join<StockOut, Order> orderJoin ;
        private SetJoin<Order, Promotion> promotionJoin ;
        private ListJoin<StockOut, StockOutItem> stockOutItemJoin;
        private Join skuJoin;
        private Join promotableItemsJoin;
        private ListJoin dynamicSkuPriceJoin;
        private ListJoin<Order, StockOut> stockOutJoin;
//        public Join<StockOut, Order> getOrderJoin() {
//            return orderJoin;
//        }
        public ListJoin<Order, StockOut> getStockOutJoin() {
            return stockOutJoin;
        }

        public SetJoin<Order, Promotion> getPromotionJoin() {
            return promotionJoin;
        }
        public ListJoin<StockOut, StockOutItem> getStockOutItemJoin() {
            return stockOutItemJoin;
        }

        public Join getSkuJoin() {
            return skuJoin;
        }
        public Join getPromotableItemsJoin() {
            return promotableItemsJoin;
        }
        public ListJoin getDynamicSkuPriceJoin() {
            return dynamicSkuPriceJoin;
        }

        @Override
        public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            stockOutJoin = root.join(Order_.stockOuts,JoinType.LEFT);
            stockOutItemJoin=  stockOutJoin.join(StockOut_.stockOutItems, JoinType.LEFT);
            promotionJoin = root.join(Order_.promotions);
            promotableItemsJoin= promotionJoin.join(Promotion_.promotableItems);
            skuJoin= promotableItemsJoin.join(PromotableItems_.sku,JoinType.LEFT);
            dynamicSkuPriceJoin=skuJoin.join(Sku_.dynamicSkuPrice,JoinType.LEFT);

            query.groupBy(root.get(Order_.id));
            PromotionConstant promotionType = PromotionConstant.getPromotionConstantByType(request.getPromotionType());
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(Order_.status), OrderStatus.COMPLETED.getValue())); //只查询已完成的订单
            if(promotionType == PromotionConstant.TWO_FOR_ONE  || promotionType == PromotionConstant.ORDER_WITH_A_GIFT_SEND) {
                predicates.add(
                        cb.or(
                            cb.isNull(stockOutJoin.get(StockOut_.id)),
                            cb.isTrue(
                                cb.and(
                                    cb.equal(stockOutItemJoin.get(StockOutItem_.sku).get(Sku_.id), promotionJoin.get(Promotion_.promotableItems).get(PromotableItems_.sku).get(Sku_.id)),
                                    cb.equal(stockOutItemJoin.get(StockOutItem_.price), 0)
                                )
                            )
                        )
                );
                predicates.add(cb.equal(dynamicSkuPriceJoin.get(DynamicSkuPrice_.sku).get(Sku_.id),promotionJoin.get(Promotion_.promotableItems).get(PromotableItems_.sku).get(Sku_.id)));
                predicates.add(cb.equal(dynamicSkuPriceJoin.get(DynamicSkuPrice_.warehouse).get(Warehouse_.id), root.get(Order_.restaurant).get(Restaurant_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id)));
            }
            if(null!=request.getStokoutTimeFront()){
                predicates.add(cb.greaterThanOrEqualTo(stockOutJoin.get(StockOut_.receiveDate), request.getStokoutTimeFront()));
            }
            if(null!=request.getStokoutTimeBak()){
                predicates.add(cb.lessThan(stockOutJoin.get(StockOut_.receiveDate), request.getStokoutTimeBak()));
            }

            if(null!=request.getPromotionType()){
                predicates.add(cb.equal(promotionJoin.get(Promotion_.type), request.getPromotionType()));
            }
            if(null!=request.getCityId()){
                predicates.add(cb.equal(root.get(Order_.restaurant).get(Restaurant_.customer).get(Customer_.city).get(City_.id), request.getCityId()));
            }
            if(null!=request.getWarehouseId()){
                predicates.add(cb.equal(root.get(Order_.restaurant).get(Restaurant_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id), request.getWarehouseId()));
            }
            if(null!=request.getOrderId()){
                predicates.add(cb.equal(root.get(Order_.id), request.getOrderId()));
            }
            if(null!=request.getOrderSubmitFront()){
                predicates.add(cb.greaterThanOrEqualTo(root.get(Order_.submitDate), request.getOrderSubmitFront()));
            }
            if(null!=request.getOrderSubmitBack()){
                predicates.add(cb.lessThan(root.get(Order_.submitDate), request.getOrderSubmitBack()));
            }
            if(null!=request.getPromotionId()){
                predicates.add(cb.equal(promotionJoin.get(Promotion_.id), request.getPromotionId()));
            }
            if(null!=request.getSkuId()){
                predicates.add(cb.equal(promotionJoin.get(Promotion_.promotableItems).get(PromotableItems_.sku).get(Sku_.id), request.getSkuId()));
            }
            if(null!=request.getSkuName()){
                predicates.add(cb.like(promotionJoin.get(Promotion_.promotableItems).get(PromotableItems_.sku).get(Sku_.product).get(Product_.name), "%" + request.getSkuName() + "%"));
            }
            if(null!=request.getRestaurantId()){
                predicates.add(cb.equal(root.get(Order_.restaurant).get(Restaurant_.id), request.getRestaurantId()));
            }
            if(null!=request.getRestaurantName()){
                predicates.add(cb.like(root.get(Order_.restaurant).get(Restaurant_.name), "%" + request.getRestaurantName() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[]{}));
        }
    }

    @Transactional
    public int reduceLimited(Long id, int number) {

        return promotionRepository.reduceLimited(id, number);
    }

    @Transactional
    public int invalidatePromotion(Long id) {
        return promotionRepository.invalidatePromotion(id);
    }

}
