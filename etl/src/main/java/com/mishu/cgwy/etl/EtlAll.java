package com.mishu.cgwy.etl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: xudong
 * Date: 3/27/15
 * Time: 5:29 PM
 */
@Service
public class EtlAll {
    @Autowired
    private CityEtl cityEtl;

    @Autowired
    private RegionEtl regionEtl;

    @Autowired
    private ZoneEtl zoneEtl;

    @Autowired
    private CategoryEtl categoryEtl;

    @Autowired
    private MediaFileEtl mediaFileEtl;


    @Autowired
    private BrandEtl brandEtl;
    @Autowired
    private RestaurantTypeEtl restaurantTypeEtl;
    @Autowired
    private AdminUserEtl adminUserEtl;

    @Autowired
    private ProductEtl productEtl;

    @Autowired
    private SkuEtl skuEtl;

    @Autowired
    private AdminRoleEtl adminRoleEtl;

    @Autowired
    private AdminUserRoleXrefEtl adminUserRoleXrefEtl;

    @Autowired
    private AdminPermissionEtl adminPermissionEtl;

    @Autowired
    private AdminRolePermissionXrefEtl adminRolePermissionXrefEtl;

//    @Autowired
//    private AgentEtl agentEtl;

    @Autowired
    private VendorEtl vendorEtl;

    @Autowired
    private WarehouseEtl warehouseEtl;

    @Autowired
    private DynamicSkuPriceEtl dynamicSkuPriceEtl;



//    @Autowired
//    private RestaurantEtl restaurantEtl;
    @Autowired
    private CustomerEtl customerEtl;
    @Autowired
    private OrderEtl orderEtl;
    @Autowired
    private OrderDetailEtl orderDetailEtl;
    @Autowired
    private OrderRefundEtl orderRefundEtl;

    @Autowired
    private VersionEtl versionEtl;

    @Autowired
    private ZoneTransferBlock zoneTransferBlock;

    @Autowired
    private AdminUserTransfer adminUserTransfer;
    @Autowired
    private OrderDataUpdateEtl orderDataUpdateEtl;

    @Autowired
    private BlockDataTransfer blockDataTransfer;

    @Transactional
    public void etl() {


//        cityEtl.transfer();
//        regionEtl.transfer();
//        warehouseEtl.init();
//        zoneEtl.transfer();
//
//        adminUserEtl.transfer();
//        adminRoleEtl.transfer();
//        adminUserRoleXrefEtl.transfer();
//        adminPermissionEtl.transfer();
//        adminRolePermissionXrefEtl.transfer();
//        agentEtl.transfer();
//        vendorEtl.transfer();
//        mediaFileEtl.transfer();
//
//        categoryEtl.transfer();
//        brandEtl.transfer();
//        restaurantTypeEtl.transfer();
//        productEtl.transfer();
//        skuEtl.transfer();
//        dynamicSkuPriceEtl.transfer();
//
//
//        customerEtl.transfer();
//        restaurantEtl.transfer();
//
//
//        orderEtl.transfer();
//        orderDetailEtl.transfer();
//        orderRefundEtl.transfer();

//        versionEtl.transfer();
//        zoneTransferBlock.transfer();
//        adminUserTransfer.transfer();
//        orderDataUpdateEtl.transfer();
        blockDataTransfer.getAllRestaurantPoints(1l);

    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"/application-persist.xml",
                "/application-context.xml"});
        EtlAll etlAll = applicationContext.getBean(EtlAll.class);
        etlAll.etl();

        applicationContext.close();

    }
}
