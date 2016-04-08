package com.mishu.cgwy.accounting.service;

import com.mishu.cgwy.accounting.domain.AccountReceivableItem;
import com.mishu.cgwy.accounting.domain.AccountReceivableItem_;
import com.mishu.cgwy.accounting.domain.AccountReceivable_;
import com.mishu.cgwy.accounting.dto.ProfitRequest;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableType;
import com.mishu.cgwy.accounting.repository.AccountReceivableItemRepository;
import com.mishu.cgwy.accounting.wrapper.*;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.common.domain.Block_;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.common.domain.Warehouse_;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.product.domain.Category;
import com.mishu.cgwy.product.domain.Category_;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.product.repository.CategoryRepository;
import com.mishu.cgwy.profile.domain.Customer_;
import com.mishu.cgwy.profile.domain.Restaurant_;
import com.mishu.cgwy.stock.domain.*;
import com.mishu.cgwy.utils.JpaQueryUtils;
import com.mishu.cgwy.utils.SkuCategoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiao1zhao2 on 15/12/3.
 */
@Service
public class ProfitService {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private AccountReceivableItemRepository accountReceivableItemRepository;
    @Autowired
    private CategoryRepository categoryRepository;


    public Page<SkuSellSummeryProfitWrapper> getSkuSellSummeryProfit(final ProfitRequest request) {

        final PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize());
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery query = cb.createTupleQuery();
        final Root<AccountReceivableItem> root = query.from(AccountReceivableItem.class);
        //销量
        Expression quantity = cb.<Integer>selectCase()
                .when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.get(AccountReceivableItem_.quantity))
                .when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.RETURN.getValue()), cb.prod(root.get(AccountReceivableItem_.quantity), -1)).otherwise(
                        root.get(AccountReceivableItem_.quantity)
                );
        //销售额
        Expression priceAmount = cb.sum(cb.prod(quantity, root.get(AccountReceivableItem_.price)));
        //成本额
        Expression avgCostAmount = cb.sum(cb.prod(quantity, root.get(AccountReceivableItem_.avgCost)));

        query.multiselect(
                root.get(AccountReceivableItem_.sku).get(Sku_.id),//SKUID
                root.get(AccountReceivableItem_.sku).get(Sku_.product).get(Product_.name),//产品名称
                root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type),//类型
                root.get(AccountReceivableItem_.sku).get(Sku_.capacityInBundle),//转换率
                root.get(AccountReceivableItem_.sku).get(Sku_.singleUnit),//基本单位
                cb.sum(quantity),//销量
                root.get(AccountReceivableItem_.sku).get(Sku_.bundleUnit),//打包单位
                //打包销量
                priceAmount,// 销售额
                avgCostAmount// 成本额
        );
        Specification<AccountReceivableItem> spec = new Specification<AccountReceivableItem>() {
            @Override
            public Predicate toPredicate(Root<AccountReceivableItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.groupBy(root.get(AccountReceivableItem_.sku).get(Sku_.id));
                return new ProfitSpecification(request).toPredicate(root, query, cb);
            }
        };

        query.where(spec.toPredicate(root, query, cb));
        TypedQuery typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pageRequest.getOffset());
        typedQuery.setMaxResults(pageRequest.getPageSize());
        List<Tuple> tuples = typedQuery.getResultList();
        List<SkuSellSummeryProfitWrapper> datas = new ArrayList<>();
        for (Tuple tuple : tuples) {
            datas.add(new SkuSellSummeryProfitWrapper(
                    ((Number) tuple.get(0)).longValue(), String.valueOf(tuple.get(1)), ((Number) tuple.get(2)).intValue(),
                    ((Number) tuple.get(3)).intValue(), String.valueOf(tuple.get(4)), ((Number) tuple.get(5)).intValue(),
                    String.valueOf(tuple.get(6)), (BigDecimal) tuple.get(7), (BigDecimal) tuple.get(8)
            ));
        }
        //查结果集数量
        long lineCnt = JpaQueryUtils.lineCount(AccountReceivableItem.class, spec, entityManager);
        Page<SkuSellSummeryProfitWrapper> result = new PageImpl<SkuSellSummeryProfitWrapper>(datas, pageRequest, lineCnt);
        return result;
    }


    @Transactional(readOnly = true)
    public Page<CustomerSkuProfitWrapper> getAccountReceivableItemGroupByCustomerSku(final ProfitRequest request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery query = cb.createTupleQuery();
        Root<AccountReceivableItem> root = query.from(AccountReceivableItem.class);
        //销量
        Expression quantity = cb.<Integer>selectCase()
                .when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.get(AccountReceivableItem_.quantity))
                .when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.RETURN.getValue()), cb.prod(root.get(AccountReceivableItem_.quantity), -1)).otherwise(
                        root.get(AccountReceivableItem_.quantity)
                );
        //销售额
        Expression priceAmount = cb.sum(cb.prod(quantity, root.get(AccountReceivableItem_.price)));
        //成本额
        Expression avgCostAmount = cb.sum(cb.prod(quantity, root.get(AccountReceivableItem_.avgCost)));
        query.multiselect(
                root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.restaurant).get(Restaurant_.customer).get(Customer_.adminUser).get(AdminUser_.realname),//销售员
                root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.restaurant).get(Restaurant_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.name),//市场
                root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.restaurant).get(Restaurant_.id),//餐馆 ID
                root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.restaurant).get(Restaurant_.name),//餐馆名称

                root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type),//类型
                root.get(AccountReceivableItem_.sku).get(Sku_.id),//SKU ID
                root.get(AccountReceivableItem_.sku).get(Sku_.product).get(Product_.name),//产品名称
                root.get(AccountReceivableItem_.sku).get(Sku_.singleUnit),//单位
                cb.sum(quantity),//销量
                priceAmount,// 销售额
                avgCostAmount// 成本额
        );
        Specification<AccountReceivableItem> spec = new Specification<AccountReceivableItem>() {
            @Override
            public Predicate toPredicate(Root<AccountReceivableItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.groupBy(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.restaurant).get(Restaurant_.id),
                        root.get(AccountReceivableItem_.sku).get(Sku_.id));
                return new ProfitSpecification(request).toPredicate(root, query, cb);
            }
        };

        query.where(spec.toPredicate(root, query, cb));
        PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize());
        TypedQuery typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pageRequest.getOffset());
        typedQuery.setMaxResults(pageRequest.getPageSize());
        List<Tuple> tuples = typedQuery.getResultList();

        List<CustomerSkuProfitWrapper> profitWrappers = new ArrayList<>();
        for (Tuple tuple : tuples) {
            CustomerSkuProfitWrapper profitWrapper = new CustomerSkuProfitWrapper(
                    String.valueOf(tuple.get(0)),
                    String.valueOf(tuple.get(1)),
                    ((Number) tuple.get(2)).longValue(),
                    String.valueOf(tuple.get(3)),
                    ((Number) tuple.get(4)).intValue(),
                    ((Number) tuple.get(5)).longValue(),
                    String.valueOf(tuple.get(6)),
                    String.valueOf(tuple.get(7)),
                    ((Number) tuple.get(8)).intValue(),
                    (BigDecimal) tuple.get(9),
                    (BigDecimal) tuple.get(10)
            );
            profitWrappers.add(profitWrapper);
        }
        Long cnt = JpaQueryUtils.lineCount(AccountReceivableItem.class, spec, entityManager);
        Page<CustomerSkuProfitWrapper> result = new PageImpl<CustomerSkuProfitWrapper>(profitWrappers, pageRequest, cnt == null ? 0 : cnt.intValue());
        return result;

    }

    @Transactional(readOnly = true)
    public List<WarehouseCategoryProfitWrapper> getWarehouseCategoryProfitWrapperList(ProfitRequest request) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<AccountReceivableItem> root = query.from(AccountReceivableItem.class);
        Expression warehouseName = cb.selectCase().when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.join(AccountReceivableItem_.accountReceivable).join(AccountReceivable_.stockOut, JoinType.LEFT).join(StockOut_.depot, JoinType.LEFT).get(Depot_.name)).otherwise(root.join(AccountReceivableItem_.accountReceivable).join(AccountReceivable_.stockIn, JoinType.LEFT).join(StockIn_.depot, JoinType.LEFT).get(Depot_.name));
//        root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.restaurant).get(Restaurant_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.name);
        Expression categoryName = root.get(AccountReceivableItem_.sku).get(Sku_.product).get(Product_.category).get(Category_.parentCategory).get(Category_.parentCategory).get(Category_.name);
        Expression salesAmount = cb.sum(cb.prod(root.get(AccountReceivableItem_.price), cb.<Integer>selectCase().when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.get(AccountReceivableItem_.quantity)).otherwise(cb.prod(root.get(AccountReceivableItem_.quantity), -1))));
        Expression avgCostAmount = cb.sum(cb.prod(root.get(AccountReceivableItem_.avgCost), cb.<Integer>selectCase().when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.get(AccountReceivableItem_.quantity)).otherwise(cb.prod(root.get(AccountReceivableItem_.quantity), -1))));
        Expression profitAmount = cb.sum(cb.prod(cb.diff(root.get(AccountReceivableItem_.price), root.get(AccountReceivableItem_.avgCost)), cb.<Integer>selectCase().when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.get(AccountReceivableItem_.quantity)).otherwise(cb.prod(root.get(AccountReceivableItem_.quantity), -1))));
        query.multiselect(warehouseName, categoryName, salesAmount, avgCostAmount, profitAmount);
        query.groupBy(categoryName, warehouseName);
        query.where(new ProfitSpecification(request).toPredicate(root, query, cb));
        List<WarehouseCategoryProfitWrapper> list = new ArrayList<>();
        for (Tuple tuple : entityManager.createQuery(query).getResultList()) {
            list.add(new WarehouseCategoryProfitWrapper(tuple.get(0).toString(), tuple.get(1).toString(), new ProfitWrapper((BigDecimal) tuple.get(2), (BigDecimal) tuple.get(3), (BigDecimal) tuple.get(4))));
        }
        return list;
    }

    @Transactional(readOnly = true)
    public List<CategorySellerProfitWrapper> getCategorySellerProfitWrapperList(ProfitRequest request) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<AccountReceivableItem> root = query.from(AccountReceivableItem.class);
        //get seller from order
        Expression sellerName = cb.selectCase().when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.join(AccountReceivableItem_.accountReceivable).join(AccountReceivable_.stockOut, JoinType.LEFT).join(StockOut_.order, JoinType.LEFT).join(Order_.adminUser, JoinType.LEFT).get(AdminUser_.realname)).otherwise(root.join(AccountReceivableItem_.accountReceivable).join(AccountReceivable_.stockIn, JoinType.LEFT).join(StockIn_.sellReturn, JoinType.LEFT).join(SellReturn_.order, JoinType.LEFT).join(Order_.adminUser, JoinType.LEFT).get(AdminUser_.realname));
        Expression categoryName = root.get(AccountReceivableItem_.sku).get(Sku_.product).get(Product_.category).get(Category_.parentCategory).get(Category_.parentCategory).get(Category_.name);
        Expression salesAmount = cb.sum(cb.prod(root.get(AccountReceivableItem_.price), cb.<Integer>selectCase().when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.get(AccountReceivableItem_.quantity)).otherwise(cb.prod(root.get(AccountReceivableItem_.quantity), -1))));
        Expression avgCostAmount = cb.sum(cb.prod(root.get(AccountReceivableItem_.avgCost), cb.<Integer>selectCase().when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.get(AccountReceivableItem_.quantity)).otherwise(cb.prod(root.get(AccountReceivableItem_.quantity), -1))));
        Expression profitAmount = cb.sum(cb.prod(cb.diff(root.get(AccountReceivableItem_.price), root.get(AccountReceivableItem_.avgCost)), cb.<Integer>selectCase().when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.get(AccountReceivableItem_.quantity)).otherwise(cb.prod(root.get(AccountReceivableItem_.quantity), -1))));
        query.multiselect(sellerName, categoryName, salesAmount, avgCostAmount, profitAmount);
        query.groupBy(sellerName, categoryName);
        query.where(new ProfitSpecification(request).toPredicate(root, query, cb));
        List<CategorySellerProfitWrapper> list = new ArrayList<>();
        for (Tuple tuple : entityManager.createQuery(query).getResultList()) {
            list.add(new CategorySellerProfitWrapper(tuple.get(0) == null ? "" : tuple.get(0).toString(), tuple.get(1).toString(), new ProfitWrapper((BigDecimal) tuple.get(2), (BigDecimal) tuple.get(3), (BigDecimal) tuple.get(4))));
        }
        return list;
    }

    @Transactional(readOnly = true)
    public Page<SkuSalesWrapper> getSkuSalesWrapperList(final ProfitRequest request) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<AccountReceivableItem> root = query.from(AccountReceivableItem.class);
        Expression skuId = root.get(AccountReceivableItem_.sku).get(Sku_.id);
        Expression skuName = root.get(AccountReceivableItem_.sku).get(Sku_.product).get(Product_.name);
        Expression categoryName = root.get(AccountReceivableItem_.sku).get(Sku_.product).get(Product_.category).get(Category_.parentCategory).get(Category_.parentCategory).get(Category_.name);
        Expression capacityInBundle = root.get(AccountReceivableItem_.sku).get(Sku_.capacityInBundle);
        Expression skuSingleUnit = root.get(AccountReceivableItem_.sku).get(Sku_.singleUnit);
        Expression quantity = cb.sum(cb.<Integer>selectCase().when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.get(AccountReceivableItem_.quantity)).otherwise(cb.prod(root.get(AccountReceivableItem_.quantity), -1)));
        Expression skuBundleUnit = root.get(AccountReceivableItem_.sku).get(Sku_.bundleUnit);
        Expression salesAmount = cb.sum(cb.prod(root.get(AccountReceivableItem_.price), cb.<Integer>selectCase().when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.get(AccountReceivableItem_.quantity)).otherwise(cb.prod(root.get(AccountReceivableItem_.quantity), -1))));
        query.multiselect(skuId, skuName, categoryName, capacityInBundle, skuSingleUnit, quantity, skuBundleUnit, salesAmount);
        query.groupBy(root.get(AccountReceivableItem_.sku).get(Sku_.id));
        query.where(new ProfitSpecification(request).toPredicate(root, query, cb));

        PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize());
        List<SkuSalesWrapper> skuSales = new ArrayList<>();
        for (Tuple tuple : entityManager.createQuery(query).setFirstResult(pageRequest.getOffset()).setMaxResults(pageRequest.getPageSize()).getResultList()) {
            skuSales.add(new SkuSalesWrapper(((Number) tuple.get(0)).longValue(), tuple.get(1).toString(), tuple.get(2).toString(), ((Number) tuple.get(3)).intValue(), tuple.get(4).toString(), ((Number) tuple.get(5)).intValue(), tuple.get(6).toString(), (BigDecimal) tuple.get(7)));
        }

        CriteriaQuery<Long> cntQuery = cb.createQuery(Long.class);
        Root<AccountReceivableItem> cntRoot = cntQuery.from(AccountReceivableItem.class);
        cntQuery.select(cb.countDistinct(cntRoot.get(AccountReceivableItem_.sku).get(Sku_.id))).where(new ProfitSpecification(request).toPredicate(cntRoot, cntQuery, cb));
        Long total = entityManager.createQuery(cntQuery).getSingleResult();

        return new PageImpl<>(skuSales, pageRequest, total == null ? 0 : total);
    }

    @Transactional(readOnly = true)
    public BigDecimal getSkuSalesAmountSummation(final ProfitRequest request) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Number> query = cb.createQuery(Number.class);
        Root<AccountReceivableItem> root = query.from(AccountReceivableItem.class);
        query.select(cb.sum(cb.prod(root.get(AccountReceivableItem_.price), cb.<Integer>selectCase().when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.get(AccountReceivableItem_.quantity)).otherwise(cb.prod(root.get(AccountReceivableItem_.quantity), -1)))));
        query.where(new ProfitSpecification(request).toPredicate(root, query, cb));
        return (BigDecimal) entityManager.createQuery(query).getSingleResult();
    }

    @Transactional(readOnly = true)
    public Page<CustomerSellerProfitWrapper> getCustomerSellerProfitWrapperList(final ProfitRequest request) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<AccountReceivableItem> root = query.from(AccountReceivableItem.class);
        Expression restaurantId = root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.restaurant).get(Restaurant_.id);
        Expression categoryName = root.get(AccountReceivableItem_.sku).get(Sku_.product).get(Product_.category).get(Category_.parentCategory).get(Category_.parentCategory).get(Category_.name);
        Expression salesAmount = cb.sum(cb.prod(root.get(AccountReceivableItem_.price), cb.<Integer>selectCase().when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.get(AccountReceivableItem_.quantity)).otherwise(cb.prod(root.get(AccountReceivableItem_.quantity), -1))));
        Expression avgCostAmount = cb.sum(cb.prod(root.get(AccountReceivableItem_.avgCost), cb.<Integer>selectCase().when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.get(AccountReceivableItem_.quantity)).otherwise(cb.prod(root.get(AccountReceivableItem_.quantity), -1))));
        Expression profitAmount = cb.sum(cb.prod(cb.diff(root.get(AccountReceivableItem_.price), root.get(AccountReceivableItem_.avgCost)), cb.<Integer>selectCase().when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.get(AccountReceivableItem_.quantity)).otherwise(cb.prod(root.get(AccountReceivableItem_.quantity), -1))));
        query.multiselect(restaurantId, categoryName, salesAmount, avgCostAmount, profitAmount);
        query.groupBy(root.get(AccountReceivableItem_.sku).get(Sku_.product).get(Product_.category).get(Category_.parentCategory).get(Category_.parentCategory).get(Category_.id), root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.restaurant).get(Restaurant_.id));
        query.orderBy(cb.desc(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.restaurant).get(Restaurant_.customer).get(Customer_.adminUser).get(AdminUser_.id)));
        query.where(new ProfitSpecification(request).toPredicate(root, query, cb));

        PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize());
        List<CustomerSellerProfitWrapper> profits = new ArrayList<>();
        for (Tuple tuple : entityManager.createQuery(query).setFirstResult(pageRequest.getOffset()).setMaxResults(pageRequest.getPageSize()).getResultList()) {
            profits.add(new CustomerSellerProfitWrapper(((Number) tuple.get(0)).longValue(), tuple.get(1).toString(), new ProfitWrapper((BigDecimal) tuple.get(2), (BigDecimal) tuple.get(3), (BigDecimal) tuple.get(4))));
        }

        CriteriaQuery<Long> cntQuery = cb.createQuery(Long.class);
        Root<AccountReceivableItem> cntRoot = cntQuery.from(AccountReceivableItem.class);
        cntQuery.select(cb.countDistinct(cntRoot.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.restaurant).get(Restaurant_.id))).where(new ProfitSpecification(request).toPredicate(cntRoot, cntQuery, cb));
        Long total = entityManager.createQuery(cntQuery).getSingleResult();
        //TODO sometimes total maybe less than the size of profits
        return new PageImpl<>(profits, pageRequest, total == null ? 0 : Math.max(total, profits.size()));
    }

    @Transactional(readOnly = true)
    public Page<AccountReceivableItem> getAccountReceivableItem(final ProfitRequest request) {
        return accountReceivableItemRepository.findAll(new ProfitSpecification(request), new PageRequest(request.getPage(), request.getPageSize()));
    }

    private class ProfitSpecification implements Specification<AccountReceivableItem> {

        private ProfitRequest request;

        public ProfitSpecification(ProfitRequest request) {
            this.request = request;
        }

        @Override
        public Predicate toPredicate(Root<AccountReceivableItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            List<Predicate> predicates = new ArrayList<>();
            if (request.getAccountReceivableType() != Integer.MAX_VALUE) {
                predicates.add(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), request.getAccountReceivableType()));
            }
            if (request.getCategoryId() != null) {
                final List<Long> categoryIds = new ArrayList<>();
                Category category = categoryRepository.findOne(request.getCategoryId());
                if (category != null) {
                    categoryIds.addAll(SkuCategoryUtils.getChildrenCategoryIds(category));
                }
                if (!categoryIds.isEmpty()) {
                    predicates.add(root.get(AccountReceivableItem_.sku).get(Sku_.product).get(Product_.category).get(Category_.id).in(categoryIds));
                }
            }
            if (request.getCityId() != null) {
                predicates.add(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.restaurant).get(Restaurant_.customer).get(Customer_.block).get(Block_.city).get(City_.id), request.getCityId()));
            }
            if (request.getWarehouseId() != null) {
                predicates.add(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.restaurant).get(Restaurant_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id), request.getWarehouseId()));
            }
            if (request.getSkuId() != null) {
                predicates.add(cb.equal(root.get(AccountReceivableItem_.sku).get(Sku_.id), request.getSkuId()));
            }
            if (request.getSkuName() != null) {
                predicates.add(cb.like(root.get(AccountReceivableItem_.sku).get(Sku_.product).get(Product_.name), "%" + request.getSkuName() + "%"));
            }
            if (request.getCustomerName() != null) {
                predicates.add(cb.like(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.restaurant).get(Restaurant_.name), "%" + request.getCustomerName() + "%"));
            }
            if (request.getRestaurantId() != null) {
                predicates.add(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.restaurant).get(Restaurant_.id), request.getRestaurantId()));
            }
            if (request.getRestaurantName() != null) {
                predicates.add(cb.like(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.restaurant).get(Restaurant_.name), "%" + request.getRestaurantName() + "%"));
            }
            if (request.getSellerName() != null) {
                predicates.add(cb.like(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.restaurant).get(Restaurant_.customer).get(Customer_.adminUser).get(AdminUser_.realname), "%" + request.getSellerName() + "%"));
            }
            if (request.getStartOrderDate() != null || request.getEndOrderDate() != null) {
                Join<StockOut, Order> stockOutOrder = root.join(AccountReceivableItem_.accountReceivable, JoinType.LEFT).join(AccountReceivable_.stockOut, JoinType.LEFT).join(StockOut_.order, JoinType.LEFT);
                Join<SellReturn, Order> sellReturnOrder = root.join(AccountReceivableItem_.accountReceivable, JoinType.LEFT).join(AccountReceivable_.stockIn, JoinType.LEFT).join(StockIn_.sellReturn, JoinType.LEFT).join(SellReturn_.order, JoinType.LEFT);
                if (request.getStartOrderDate() != null) {
                    predicates.add(cb.or(cb.greaterThanOrEqualTo(stockOutOrder.get(Order_.submitDate), request.getStartOrderDate()), cb.greaterThanOrEqualTo(sellReturnOrder.get(Order_.submitDate), request.getStartOrderDate())));
                }
                if (request.getEndOrderDate() != null) {
                    predicates.add(cb.or(cb.lessThanOrEqualTo(stockOutOrder.get(Order_.submitDate), request.getEndOrderDate()), cb.lessThanOrEqualTo(sellReturnOrder.get(Order_.submitDate), request.getEndOrderDate())));
                }
            }
            if (request.getStartReceiveDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.createDate), request.getStartReceiveDate()));
            }
            if (request.getEndReceiveDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.createDate), request.getEndReceiveDate()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }
    }

}
