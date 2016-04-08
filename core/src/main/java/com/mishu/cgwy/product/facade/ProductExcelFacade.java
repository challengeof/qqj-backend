package com.mishu.cgwy.product.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.VendorService;
import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.common.service.MediaFileService;
import com.mishu.cgwy.common.vo.MediaFileVo;
import com.mishu.cgwy.common.wrapper.SimpleWarehouseWrapper;
import com.mishu.cgwy.inventory.domain.BundleDynamicSkuPriceStatus;
import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import com.mishu.cgwy.inventory.domain.SingleDynamicSkuPriceStatus;
import com.mishu.cgwy.inventory.service.ContextualInventoryService;
import com.mishu.cgwy.order.controller.SkuSaleResponse;
import com.mishu.cgwy.order.controller.SkuSaleWrapper;
import com.mishu.cgwy.order.controller.SkuSalesRequest;
import com.mishu.cgwy.order.facade.OrderFacade;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import com.mishu.cgwy.product.controller.DynamicPriceQueryRequest;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.service.ProductService;
import com.mishu.cgwy.product.vo.BrandVo;
import com.mishu.cgwy.product.vo.CategoryVo;
import com.mishu.cgwy.product.vo.ProductVo;
import com.mishu.cgwy.product.vo.SkuVo;
import com.mishu.cgwy.product.wrapper.CategoryWrapper;
import com.mishu.cgwy.product.wrapper.DynamicSkuPriceWrapper;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.product.wrapper.SkuWrapper;
import com.mishu.cgwy.search.SearchService;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import com.mishu.cgwy.utils.ZipFileUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * @author wangwei
 * Date : 2015/06/09
 * Time : 12:55
 *
 */
@Service
public class ProductExcelFacade {
	
	@Autowired
    private ProductService productService;
	
	@Autowired
	private ProductFacade productFacade;

    @Autowired
    private ContextualInventoryService inventoryService;

    @Autowired
    private MediaFileService mediaFileService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private LocationService locationService;
    
    @Autowired
    private VendorService vendorService;
    
    @Autowired
    private ProductTempFacade productTempFacade;
    
    @Autowired
    private DynamicPriceTempFacade dynamicPriceTempFacade;
    
    @Autowired
    private OrderFacade orderFacade;

	@Autowired
	private OrganizationService organizationService;

    private ExecutorService taskExecutor = Executors.newCachedThreadPool();
    
	enum SkuSaleExcelHeader{
		SKU_ID("SKU_ID"),SKU_NAME ("SKU名称"),PRODUCT_BRAND ("品牌"), SKU_CAPACITYINBUNDLE("转化率"), SKU_SINGLE_SALE("单品销量"), SKU_BUNDLE_SALE("打包销量"),
		SKU_COUNT_SALE("总销量(单品)"),SKU_SELLCANCEL("总取消量(单品)"), SKU_SELLRETURN("总退货量(单品)");
		private String name;
		private SkuSaleExcelHeader(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static SkuSaleExcelHeader indexOf(String name) {
			for(int i=0; i<values().length ;i++) {
				if(values()[i].getName().equals(name)) {
					return values()[i];
				}
			}
				return null;
		}
    
	}
	
	enum ExcelHeader {
    	PRODUCT_ID("商品ID"), SKU_ID("SKU_ID"),
        PRODUCT_NAME ("商品名称"),       PRODUCT_CATEGORY ("商品分类"),    PRODUCT_BRAND ("品牌"),	PRODUCT_DISCRETE("标准/散货"),
		PRODUCT_BUNDLE ("转化比率"),	PRODUCT_SPECIFICATION ("规格"),    PRODUCT_RATE("税率"),   PRODUCT_STATUS("商品状态"),
		PRODUCT_SINGLE_UNIT ("单品单位"),  PRODUCT_SINGLEGROSS_WIGHT("单品毛重"), PRODUCT_SINGLELONG("单品长"), PRODUCT_SINGLEWIDTH("单品宽"), PRODUCT_SINGLEHEIGHT("单品高"),
		PRODUCT_BUNDLE_UNIT ("打包单位"),  PRODUCT_BUNDLEGROSS_WIGHT("打包毛重"), PRODUCT_BUNDLELONG("打包长"), PRODUCT_BUNDLEWIDTH("打包宽"), PRODUCT_BUNDLEHEIGHT("打包高"),
		PRODUCT_SHELF_LIFE ("保质期"),  PRODUCT_DETAIL ("详情"),  	PRODUCT_ORIGIN ("产地"),
        PRODUCT_LICENCE ("产品生产许可证"),    PRODUCT_EXECUTIVE_STANDARD ("产品执行标准"),    PRODUCT_COMPANY ("生产厂商"),
        PRODUCT_SAVE_CONDITION ("保存条件"),    PRODUCT_INGREDIENT ("配料表"),   PRODUCT_IMAGE_EXIST("是否存在图片"),
		SKU_MARKET ("市场"), SKU_SINGLE_SALE_PRICE ("单品售价"), SKU_SINGLE_STATUS ("单品状态"), SKU_SINGLE_INSALE ("单品是否可售"),
		SKU_BUNDLE_SALE_PRICE ("打包售价"), SKU_BUNDLE_STATUS ("打包状态"), SKU_BUNDLE_INSALE ("打包是否可售")
//		PRODUCT_BARCODE ("条形码"),
        ;
    	private String name;
    	private ExcelHeader(String name) {
    		this.name = name;
    	}

    	public String getName() {
    		return name;
    	}

    	public static ExcelHeader indexOf(String name) {
    		for(int i=0; i<values().length ;i++) {
    			if(values()[i].getName().equals(name)) {
    				return values()[i];
    			}
    		}
    		return null;
    	}
    }

    public File dynamicPriceExcelExport(DynamicPriceQueryRequest request, AdminUser adminUser, String fileDir, String fileName){
    	File dir = new File(fileDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, fileName);

        Page<DynamicSkuPrice> page = inventoryService.queryDynamicPrice(request, adminUser);
        List<DynamicSkuPrice> list = new ArrayList<>(); 
		list.addAll(page.getContent());
        
    	FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	
		Workbook wb = new HSSFWorkbook();
    	Sheet sheet = wb.createSheet();
    	Row firstRow = sheet.createRow(0);
    	Map<ExcelHeader, Integer> mapIndex = new HashMap<>();
    	
    	for(int i = 0; i < ExcelHeader.values().length; i ++){
    		mapIndex.put(ExcelHeader.values()[i], i);
    		Cell cell = firstRow.createCell(i);
    		cell.setCellValue(ExcelHeader.values()[i].name);
    	}

    	int rowIndex = 1;
    	while (list.size() != 0) {
    		DynamicSkuPrice dynamicSkuPrice = list.get(0);
    		Row row = sheet.createRow(rowIndex);
    		rowIndex ++;

    		list.remove(dynamicSkuPrice);
    		if(null != dynamicSkuPrice.getSku() && null != dynamicSkuPrice.getSku().getProduct()) {
				Sku sku = dynamicSkuPrice.getSku();
				Product product = dynamicSkuPrice.getSku().getProduct();


    			if(mapIndex.containsKey(ExcelHeader.PRODUCT_ID)){
    				row.createCell(mapIndex.get(ExcelHeader.PRODUCT_ID)).setCellValue(product.getId());
    			}
				if(mapIndex.containsKey(ExcelHeader.SKU_ID)) {
					row.createCell(mapIndex.get(ExcelHeader.SKU_ID)).setCellValue(sku.getId());
				}
    			if(mapIndex.containsKey(ExcelHeader.PRODUCT_NAME)){
    				row.createCell(mapIndex.get(ExcelHeader.PRODUCT_NAME)).setCellValue(product.getName());
    			}
    			if(mapIndex.containsKey(ExcelHeader.PRODUCT_CATEGORY)){
    				String categoryName = product.getCategory() != null ? new CategoryWrapper(product.getCategory()).getHierarchyName() : null;
    				if(null != categoryName) {
    					row.createCell(mapIndex.get(ExcelHeader.PRODUCT_CATEGORY)).setCellValue(categoryName);
    				}
    			}
    			if(mapIndex.containsKey(ExcelHeader.PRODUCT_BRAND)){
    				row.createCell(mapIndex.get(ExcelHeader.PRODUCT_BRAND)).setCellValue(product.getBrand() != null ? product.getBrand().getBrandName() : null);
    			}
    			if(mapIndex.containsKey(ExcelHeader.PRODUCT_DISCRETE)) {
    				row.createCell(mapIndex.get(ExcelHeader.PRODUCT_DISCRETE)).setCellValue(product.isDiscrete() ? "标准" : "散货");
    			}
    			if(mapIndex.containsKey(ExcelHeader.PRODUCT_BUNDLE)){
    				row.createCell(mapIndex.get(ExcelHeader.PRODUCT_BUNDLE)).setCellValue(sku.getCapacityInBundle());
    			}
    			//规格
    			if(mapIndex.containsKey(ExcelHeader.PRODUCT_SPECIFICATION)){
    				row.createCell(mapIndex.get(ExcelHeader.PRODUCT_SPECIFICATION)).setCellValue(product.getSpecification());
    			}
				if (mapIndex.containsKey(ExcelHeader.PRODUCT_RATE)) {
					if (sku.getRate() != null) {
						row.createCell(mapIndex.get(ExcelHeader.PRODUCT_RATE)).setCellValue(sku.getRate().toString());
					}
				}

				if (mapIndex.containsKey(ExcelHeader.PRODUCT_STATUS)) {
					row.createCell(mapIndex.get(ExcelHeader.PRODUCT_STATUS)).setCellValue(SkuStatus.fromInt(sku.getStatus()).getName());
				}
				if (mapIndex.containsKey(ExcelHeader.PRODUCT_SINGLE_UNIT)) {
					row.createCell(mapIndex.get(ExcelHeader.PRODUCT_SINGLE_UNIT)).setCellValue(sku.getSingleUnit());
				}
				if (mapIndex.containsKey(ExcelHeader.PRODUCT_SINGLEGROSS_WIGHT)) {
					if (sku.getSingleGross_wight() != null) {
						row.createCell(mapIndex.get(ExcelHeader.PRODUCT_SINGLEGROSS_WIGHT)).setCellValue(sku.getSingleGross_wight().toString());
					}
				}
				if (mapIndex.containsKey(ExcelHeader.PRODUCT_SINGLELONG)) {
					if (sku.getSingleLong() != null) {
						row.createCell(mapIndex.get(ExcelHeader.PRODUCT_SINGLELONG)).setCellValue(sku.getSingleLong().toString());
					}
				}
				if (mapIndex.containsKey(ExcelHeader.PRODUCT_SINGLEWIDTH)) {
					if (sku.getSingleWidth() != null) {
						row.createCell(mapIndex.get(ExcelHeader.PRODUCT_SINGLEWIDTH)).setCellValue(sku.getSingleWidth().toString());
					}
				}
				if (mapIndex.containsKey(ExcelHeader.PRODUCT_SINGLEHEIGHT)) {
					if (sku.getSingleHeight() != null) {
						row.createCell(mapIndex.get(ExcelHeader.PRODUCT_SINGLEHEIGHT)).setCellValue(sku.getSingleHeight().toString());
					}
				}


				if (mapIndex.containsKey(ExcelHeader.PRODUCT_BUNDLE_UNIT)) {
					row.createCell(mapIndex.get(ExcelHeader.PRODUCT_BUNDLE_UNIT)).setCellValue(sku.getBundleUnit());
				}
				if (mapIndex.containsKey(ExcelHeader.PRODUCT_BUNDLEGROSS_WIGHT)) {
					if (sku.getBundleGross_wight() != null) {
						row.createCell(mapIndex.get(ExcelHeader.PRODUCT_BUNDLEGROSS_WIGHT)).setCellValue(sku.getBundleGross_wight().toString());
					}
				}
				if (mapIndex.containsKey(ExcelHeader.PRODUCT_BUNDLELONG)) {
					if (sku.getBundleLong() != null) {
						row.createCell(mapIndex.get(ExcelHeader.PRODUCT_BUNDLELONG)).setCellValue(sku.getBundleLong().toString());
					}
				}
				if (mapIndex.containsKey(ExcelHeader.PRODUCT_BUNDLEWIDTH)) {
					if (sku.getBundleWidth() != null) {
						row.createCell(mapIndex.get(ExcelHeader.PRODUCT_BUNDLEWIDTH)).setCellValue(sku.getBundleWidth().toString());
					}
				}
				if (mapIndex.containsKey(ExcelHeader.PRODUCT_BUNDLEHEIGHT)) {
					if (sku.getBundleHeight() != null) {
						row.createCell(mapIndex.get(ExcelHeader.PRODUCT_BUNDLEHEIGHT)).setCellValue(sku.getBundleHeight().toString());
					}
				}
				//保质期
				if(mapIndex.containsKey(ExcelHeader.PRODUCT_SHELF_LIFE)){
					Cell cell = row.createCell(mapIndex.get(ExcelHeader.PRODUCT_SHELF_LIFE));
					if (null != product.getShelfLife()) {
						cell.setCellValue(product.getShelfLife());
					}
				}
				if(mapIndex.containsKey(ExcelHeader.PRODUCT_DETAIL)){
					Cell cell = row.createCell(mapIndex.get(ExcelHeader.PRODUCT_DETAIL));
					if (null != product.getDetails()) {
						cell.setCellValue(product.getDetails());
					}
				}

				Map<String, String> propertyMap = product.getPropertyMap();

    			//产地
    			if(mapIndex.containsKey(ExcelHeader.PRODUCT_ORIGIN)){
    				row.createCell(mapIndex.get(ExcelHeader.PRODUCT_ORIGIN)).setCellValue(propertyMap.get("origin"));
    			}
				//生产许可证
				if(mapIndex.containsKey(ExcelHeader.PRODUCT_LICENCE)){
					row.createCell(mapIndex.get(ExcelHeader.PRODUCT_LICENCE)).setCellValue(propertyMap.get("licence"));
				}
				//执行标准
				if(mapIndex.containsKey(ExcelHeader.PRODUCT_EXECUTIVE_STANDARD)){
					row.createCell(mapIndex.get(ExcelHeader.PRODUCT_EXECUTIVE_STANDARD)).setCellValue(propertyMap.get("executive_standard"));
				}
				//生产厂商
				if(mapIndex.containsKey(ExcelHeader.PRODUCT_COMPANY)){
					row.createCell(mapIndex.get(ExcelHeader.PRODUCT_COMPANY)).setCellValue(propertyMap.get("create_company"));
				}
				//保存条件
				if(mapIndex.containsKey(ExcelHeader.PRODUCT_SAVE_CONDITION)){
					row.createCell(mapIndex.get(ExcelHeader.PRODUCT_SAVE_CONDITION)).setCellValue(propertyMap.get("save_condition"));
				}
				//配料表
				if(mapIndex.containsKey(ExcelHeader.PRODUCT_INGREDIENT)){
					row.createCell(mapIndex.get(ExcelHeader.PRODUCT_INGREDIENT)).setCellValue(propertyMap.get("ingredient"));
				}


    			//产品图片
    			if(mapIndex.containsKey(ExcelHeader.PRODUCT_IMAGE_EXIST)){
    				List<MediaFile> mediaFiles = product.getMediaFiles();


    				StringBuffer value = new StringBuffer("");
    				if(!mediaFiles.isEmpty()){
						for (MediaFile mediaFile : mediaFiles) {
							if(MediaFile.DEFAULT_IMAGE.equals(mediaFile.getQiNiuHash())) {
								value.append(MediaFile.defaultPhoto);
							} else {
								value.append(mediaFile.getUrl());
							}
							value.append(";");
						}
    				}else {
						value.append(MediaFile.noPhoto);
    				}
    				row.createCell(mapIndex.get(ExcelHeader.PRODUCT_IMAGE_EXIST)).setCellValue(value.toString());
    			}


    			dynamicRowCreateCell(dynamicSkuPrice, mapIndex, row);
    		}
    	}
    	
    	try {
			wb.write(out);
			if(null != out) {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return file;
    }
    
    
    private void dynamicRowCreateCell(DynamicSkuPrice dynamicSkuPrice, Map<ExcelHeader, Integer> mapIndex, Row row){
    	if(null != dynamicSkuPrice.getSku()) {
			if (mapIndex.containsKey(ExcelHeader.SKU_MARKET)) {
				row.createCell(mapIndex.get(ExcelHeader.SKU_MARKET)).setCellValue(dynamicSkuPrice.getWarehouse().getName());
			}
			if (dynamicSkuPrice.getSinglePriceStatus() != null) {
				SingleDynamicSkuPriceStatus singleStatus = dynamicSkuPrice.getSinglePriceStatus();
				if (mapIndex.containsKey(ExcelHeader.SKU_SINGLE_SALE_PRICE)) {
					if (singleStatus.getSingleSalePrice() != null) {
						row.createCell(mapIndex.get(ExcelHeader.SKU_SINGLE_SALE_PRICE)).setCellValue(singleStatus.getSingleSalePrice().toString());
					}
				}
				if (mapIndex.containsKey(ExcelHeader.SKU_SINGLE_STATUS)) {
					row.createCell(mapIndex.get(ExcelHeader.SKU_SINGLE_STATUS)).setCellValue(singleStatus.isSingleAvailable() ? "上架" : "下架");
				}
				if (mapIndex.containsKey(ExcelHeader.SKU_SINGLE_INSALE)) {
					row.createCell(mapIndex.get(ExcelHeader.SKU_SINGLE_INSALE)).setCellValue(singleStatus.isSingleInSale() ? "可售" : "不可售");
				}
			}
			if (dynamicSkuPrice.getBundlePriceStatus() != null) {
				BundleDynamicSkuPriceStatus bundleStatus = dynamicSkuPrice.getBundlePriceStatus();
				if (mapIndex.containsKey(ExcelHeader.SKU_BUNDLE_SALE_PRICE)) {
					if (bundleStatus.getBundleSalePrice() != null) {
						row.createCell(mapIndex.get(ExcelHeader.SKU_BUNDLE_SALE_PRICE)).setCellValue(bundleStatus.getBundleSalePrice().toString());
					}
				}
				if (mapIndex.containsKey(ExcelHeader.SKU_BUNDLE_STATUS)) {
					row.createCell(mapIndex.get(ExcelHeader.SKU_BUNDLE_STATUS)).setCellValue(bundleStatus.isBundleAvailable() ? "上架" : "下架");
				}
				if (mapIndex.containsKey(ExcelHeader.SKU_BUNDLE_INSALE)) {
					row.createCell(mapIndex.get(ExcelHeader.SKU_BUNDLE_INSALE)).setCellValue(bundleStatus.isBundleInSale() ? "可售" : "不可售");
				}
			}
		}
    }
    

    public Map<String, Object> productExcelImport(MultipartFile file, Long organizationId ,AdminUser adminUser) throws IOException {
    	FileUtils.copyInputStreamToFile(file.getInputStream(), new File(ExportExcelUtils.excelFolderName + "/excelUpload/" + DateFormatUtils.format(new Date(), "yyyyMMdd") + "/" + file.getOriginalFilename() + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")));
		Workbook wb = null;
		try {
			wb = new XSSFWorkbook(file.getInputStream());
		} catch (Exception e1) {
			wb = new HSSFWorkbook(file.getInputStream());
		}
		Map<ExcelHeader, Integer> arrayIndex = new HashMap<>();

		Sheet sheet = wb.getSheetAt(0);
		int rows = sheet.getPhysicalNumberOfRows();

		Row firstRow = sheet.getRow(0);
		int firstRowCell = firstRow.getPhysicalNumberOfCells();
		
		int maxRowLength = 0;

		for (int i = 0; i <= firstRowCell; i++) {
			Cell cell = firstRow.getCell(i);
			String value = cellType(cell);
			if(StringUtils.isNotBlank(value)) {
				ExcelHeader index = ExcelHeader.indexOf(value);
				if (index != null) {
					maxRowLength = i;
					arrayIndex.put(index, i);
				}
			}
        }


		Map<String, ProductVo> productMap = new HashMap<>();
		List<String> exceptionMsg = new ArrayList<>();
		Map<String, Row> readyProduct = new HashMap<>();
		Map<String, Row> errorProduct = new HashMap<>();
		Map<Integer, Row> errorRows = new HashMap<>();
		for (int r = 1; r < rows; r++) {
			Row row = sheet.getRow(r);
			if (row == null) {
				continue;
			}

			
			try {
				excelHeaderIsNotNull(arrayIndex, row, ExcelHeader.PRODUCT_NAME);
				ProductVo p = null;

				//商品名称
				Organization organization = organizationService.findById(organizationId);


				if(arrayIndex.containsKey(ExcelHeader.PRODUCT_ID)){
					String product_id = cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_ID)));
					if (StringUtils.isNotEmpty(product_id)) {
						Product product = productService.getProduct(Double.valueOf(product_id).longValue());
						if (product != null) {
							p = productTempFacade.initProductVo(product);
						} else {
							throw new RuntimeException(", 库中商品ID对应的商品不存在");
						}
					}
				}

				if (p == null) {
					List<Product> list = productService.findByProductName(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_NAME))), organization);
					if(list.isEmpty()) {
						p = new ProductVo();
						p.setName(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_NAME))));
						MediaFile mediaFile = mediaFileService.getMediaFile(MediaFile.DEFAULT_IMAGE);
						MediaFileVo mediaFileVo = new MediaFileVo();
						mediaFileVo.setId(mediaFile.getId());
						mediaFileVo.setUrl(mediaFile.getUrl());
						p.getMediaFiles().add(mediaFileVo);
						//不能为空：商品名称，商品分类，大箱包装，规格，单品状态， 单品市场价格
						excelHeaderIsNotNull(arrayIndex, row, ExcelHeader.PRODUCT_NAME, ExcelHeader.PRODUCT_CATEGORY, ExcelHeader.PRODUCT_BUNDLE, ExcelHeader.PRODUCT_DISCRETE,
								ExcelHeader.PRODUCT_SPECIFICATION, ExcelHeader.PRODUCT_RATE, ExcelHeader.PRODUCT_STATUS, ExcelHeader.PRODUCT_SINGLE_UNIT, ExcelHeader.PRODUCT_BUNDLE_UNIT, ExcelHeader.PRODUCT_SHELF_LIFE);
					} else if(list.size() > 1){
						throw new RuntimeException(", 库中多个商品名字重复");
					} else {
						p = productTempFacade.initProductVo(list.get(0));
					}
				}

				OrganizationVo organizationVo = new OrganizationVo();
				organizationVo.setId(organization.getId());
				organizationVo.setName(organization.getName());
				p.setOrganization(organizationVo);

				if(errorProduct.containsKey(p.getName())) {
					throw new RuntimeException(", 表中多个商品名字重复");
				}

				if(readyProduct.containsKey(p.getName())) {
					errorProduct.put(p.getName(), readyProduct.get(p.getName()));
					readyProduct.remove(p.getName());
					productMap.remove(p.getName());
					throw new RuntimeException(", 表中多个商品名字重复");
				}else {
					readyProduct.put(p.getName(), row);
				}
				
				//商品分类
				if(arrayIndex.containsKey(ExcelHeader.PRODUCT_CATEGORY)){
					String type = cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_CATEGORY)));
					if(StringUtils.isNotBlank(type)){
						Category category = findCategoryInList(type.trim());
						if(null != category) {
							CategoryVo categoryVo = new CategoryVo();
							categoryVo.setId(category.getId());
							categoryVo.setName(category.getName());

							String hierarchyName = category.getName();
							Category current = category;
							while (current.getParentCategory() != null) {
								hierarchyName = current.getParentCategory().getName() + "-" + hierarchyName;
								current = current.getParentCategory();
							}
							categoryVo.setHierarchyName(hierarchyName);
							p.setCategory(categoryVo);
						}else {
							throw new RuntimeException(", "+ ExcelHeader.PRODUCT_CATEGORY.getName() +"不合法");
						}
					}
				}

				//品牌
				if(arrayIndex.containsKey(ExcelHeader.PRODUCT_BRAND) && StringUtils.isNotBlank(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_BRAND))))){
					String brandName = cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_BRAND)));
					brandName = brandName.trim();
					if(brandName.startsWith("LEFT") || brandName.startsWith("left")){
						throw new RuntimeException(", " + ExcelHeader.PRODUCT_BRAND.getName() + "不合法(公式)");
					}
					Brand brand = findBrandInList(brandName);
					BrandVo brandVo = new BrandVo();
					if(null == brand){
						brand = new Brand();
						brand.setBrandName(brandName);
						brand = productService.saveBrand(brand);
					}
					brandVo.setId(brand.getId());
					brandVo.setBrandName(brand.getBrandName());
					p.setBrand(brandVo);
				}
				
				//标准、散货
				if(arrayIndex.containsKey(ExcelHeader.PRODUCT_DISCRETE)) {
					excelHeaderIsNotNull(arrayIndex, row, ExcelHeader.PRODUCT_DISCRETE);
					String discreate = cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_DISCRETE)));
					if(discreate.equals("标准")) {
						p.setDiscrete(true);
					} else if (discreate.equals("散货")) {
						p.setDiscrete(false);
					} else {
						throw new RuntimeException(", " + ExcelHeader.PRODUCT_DISCRETE.getName() + "非法");
					}
				}

				List<SkuVo> skus = p.getSkus();

				//单品
				SkuVo sku = null;
				if (!skus.isEmpty()) {
					sku = skus.get(0);
				}
				if (null == sku) {
					sku = new SkuVo();
					p.getSkus().add(sku);
				}

				//大箱包装
				//TODO 转化率全部通过后台数据库修改,新增可以增进去
				if(arrayIndex.containsKey(ExcelHeader.PRODUCT_BUNDLE) && sku.getId() == null) {
					sku.setCapacityInBundle(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_BUNDLE)) != null ? new Double(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_BUNDLE)))).intValue() : 1);
				}

				//规格
				if(arrayIndex.containsKey(ExcelHeader.PRODUCT_SPECIFICATION)){
					p.setSpecification(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_SPECIFICATION))));
				}

				//税率
				if (arrayIndex.containsKey(ExcelHeader.PRODUCT_RATE)) {
					sku.setRate(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_RATE)) != null ? new BigDecimal(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_RATE)))) : RateValue.A.getValue());
				} else {
					sku.setRate(RateValue.A.getValue());
				}

				if (arrayIndex.containsKey(ExcelHeader.PRODUCT_STATUS)) {
					sku.setStatus(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_STATUS)) != null ? SkuStatus.fromValue(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_STATUS)))) : null);
				}

				/*单品*/

				if (arrayIndex.containsKey(ExcelHeader.PRODUCT_SINGLE_UNIT)) {
					sku.setSingleUnit(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_SINGLE_UNIT))));
				}

				if (arrayIndex.containsKey(ExcelHeader.PRODUCT_SINGLEGROSS_WIGHT)) {
					sku.setSingleGross_wight(StringUtils.isNotEmpty(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_SINGLEGROSS_WIGHT)))) ? cellPriceValue(ExcelHeader.PRODUCT_SINGLEGROSS_WIGHT, cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_SINGLEGROSS_WIGHT)))) : null);
				}

				if (arrayIndex.containsKey(ExcelHeader.PRODUCT_SINGLELONG)) {
					if (StringUtils.isNotBlank(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_SINGLELONG))))) {
						sku.setSingleLong(cellPriceValue(ExcelHeader.PRODUCT_SINGLELONG, cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_SINGLELONG)))));
					}
				}

				if (arrayIndex.containsKey(ExcelHeader.PRODUCT_SINGLEWIDTH)) {
					sku.setSingleWidth(StringUtils.isNotBlank(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_SINGLEWIDTH)))) ? cellPriceValue(ExcelHeader.PRODUCT_SINGLEWIDTH, cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_SINGLEWIDTH)))) : null);
				}

				if (arrayIndex.containsKey(ExcelHeader.PRODUCT_SINGLEHEIGHT)) {
					sku.setSingleHeight(StringUtils.isNotBlank(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_SINGLEHEIGHT)))) ? cellPriceValue(ExcelHeader.PRODUCT_SINGLEHEIGHT, cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_SINGLEHEIGHT)))) : null);
				}

				/*打包*/

				if (arrayIndex.containsKey(ExcelHeader.PRODUCT_BUNDLE_UNIT)) {
					sku.setBundleUnit(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_BUNDLE_UNIT))));
				}

				if (arrayIndex.containsKey(ExcelHeader.PRODUCT_BUNDLEGROSS_WIGHT)) {
					sku.setBundleGross_wight(StringUtils.isNotBlank(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_BUNDLEGROSS_WIGHT)))) ? cellPriceValue(ExcelHeader.PRODUCT_BUNDLEGROSS_WIGHT, cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_BUNDLEGROSS_WIGHT)))) : null);
				}

				if (arrayIndex.containsKey(ExcelHeader.PRODUCT_BUNDLELONG)) {
					sku.setBundleLong(StringUtils.isNotBlank(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_BUNDLELONG)))) ? cellPriceValue(ExcelHeader.PRODUCT_BUNDLELONG, cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_BUNDLELONG)))) : null);
				}

				if (arrayIndex.containsKey(ExcelHeader.PRODUCT_BUNDLEWIDTH)) {
					sku.setBundleWidth(StringUtils.isNotBlank(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_BUNDLEWIDTH)))) ? cellPriceValue(ExcelHeader.PRODUCT_BUNDLEWIDTH, cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_BUNDLEWIDTH)))) : null);
				}

				if (arrayIndex.containsKey(ExcelHeader.PRODUCT_BUNDLEHEIGHT)) {
					sku.setBundleHeight(StringUtils.isNotBlank(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_BUNDLEHEIGHT)))) ? cellPriceValue(ExcelHeader.PRODUCT_BUNDLEHEIGHT, cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_BUNDLEHEIGHT)))) : null);
				}

				//保质期
				if(arrayIndex.containsKey(ExcelHeader.PRODUCT_SHELF_LIFE)){
					p.setShelfLife(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_SHELF_LIFE)) == null ? null : Double.valueOf(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_SHELF_LIFE)))).intValue());
				}

				//详情
				if(arrayIndex.containsKey(ExcelHeader.PRODUCT_DETAIL)){
					p.setDetails(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_DETAIL)) != null ? row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_DETAIL)).getStringCellValue() : null);
				}

				//图片
				if (arrayIndex.containsKey(ExcelHeader.PRODUCT_IMAGE_EXIST)) {
					String unionUrl = row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_IMAGE_EXIST)) != null ? cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_IMAGE_EXIST))) : null;
					if (StringUtils.isNotBlank(unionUrl)) {
						p.getMediaFiles().clear();
						String[] urls = unionUrl.split(";");
						for (String url : urls) {
							MediaFile mediaFile = mediaFileService.getMediaFileByUrl(url);
							if (null != mediaFile) {
								MediaFileVo mediaFileVo = new MediaFileVo();
								mediaFileVo.setId(mediaFile.getId());
								mediaFileVo.setUrl(mediaFile.getUrl());
								p.getMediaFiles().add(mediaFileVo);
							}
						}
					}
					if (p.getMediaFiles().isEmpty()) {
						MediaFile mediaFile = mediaFileService.getMediaFile(MediaFile.DEFAULT_IMAGE);
						MediaFileVo mediaFileVo = new MediaFileVo();
						mediaFileVo.setId(mediaFile.getId());
						mediaFileVo.setUrl(mediaFile.getUrl());
						p.getMediaFiles().add(mediaFileVo);
					}
				}

				Map<String, String> property = p.getProperties();

				if (property == null) {
					property = new HashMap<>();
				}

				//产地
				if(arrayIndex.containsKey(ExcelHeader.PRODUCT_ORIGIN)) {
					property.put("origin", row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_ORIGIN)) == null ? null : cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_ORIGIN))));
				}

				//产品生产许可证
				if(arrayIndex.containsKey(ExcelHeader.PRODUCT_LICENCE)){
					property.put("licence", row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_LICENCE)) == null ? null : cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_LICENCE))));
				}
				//产品执行标准
				if(arrayIndex.containsKey(ExcelHeader.PRODUCT_EXECUTIVE_STANDARD)){
					property.put("executive_standard", row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_EXECUTIVE_STANDARD)) == null ? null : cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_EXECUTIVE_STANDARD))));
				}
				//生产厂商
				if(arrayIndex.containsKey(ExcelHeader.PRODUCT_COMPANY)){
					property.put("create_company", row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_COMPANY)) == null ? null : cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_COMPANY))));
				}
				//保存条件
				if(arrayIndex.containsKey(ExcelHeader.PRODUCT_SAVE_CONDITION)) {
					property.put("save_condition", row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_SAVE_CONDITION)) == null ? null : cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_SAVE_CONDITION))));
				}
				//配料表
				if(arrayIndex.containsKey(ExcelHeader.PRODUCT_INGREDIENT)) {
					property.put("ingredient", row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_INGREDIENT)) == null ? null : cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_INGREDIENT))));
				}

				productMap.put(p.getName(), p);
			} catch (RuntimeException e) {
				errorRows.put(row.getRowNum(), row);
				String errorMsg = "导入excel第 " + (row.getRowNum() + 1) + " 行出错" + e.getMessage();
				row.createCell(maxRowLength + 2).setCellValue(errorMsg);
				exceptionMsg.add(errorMsg);
			}
		}

		saveProductAndSku(productMap, adminUser);
		Map<String, Object> map = new HashMap<>();
		map.put("productListSize", productMap.size());
		map.put("exceptionMsg", exceptionMsg);
		
		if(!errorProduct.isEmpty()) {
			Iterator<String> its = errorProduct.keySet().iterator();
			while (its.hasNext()) {
				Row row = errorProduct.get(its.next());
				errorRows.put(row.getRowNum(), row);
				String errorMsg = "导入excel第 " + (row.getRowNum() + 1) + " 行出错，表中多个商品名称重复";
				row.createCell(maxRowLength + 2).setCellValue(errorMsg);
				exceptionMsg.add(errorMsg);
			}
		}
		File errorFile = excelImportError(arrayIndex, ExportExcelUtils.excelFolderName, "excelImportError.xls", errorRows);
		if(null != errorFile) {
			map.put("errorFileName", errorFile.getPath());
		}
        if(arrayIndex.isEmpty()) {
            map.put("headMsg", "excel表头为空");
        }
		return map;
	}

    

    public Map<String, Object> dynamicPriceExcelImport(MultipartFile file, Long organizationId, AdminUser adminUser) throws IOException {
		FileUtils.copyInputStreamToFile(file.getInputStream(), new File(ExportExcelUtils.excelFolderName + "/excelUpload/" + DateFormatUtils.format(new Date(), "yyyyMMdd") + "/" + file.getOriginalFilename() + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")));
		Workbook wb = null;
		try {
			wb = new XSSFWorkbook(file.getInputStream());
		} catch (Exception e1) {
			wb = new HSSFWorkbook(file.getInputStream());
		}
		Map<ExcelHeader, Integer> arrayIndex = new HashMap<>();

		Sheet sheet  = wb.getSheetAt(0);
		int rows = sheet.getPhysicalNumberOfRows();

		Row firstRow = sheet.getRow(0);
		int firstRowCell = firstRow.getPhysicalNumberOfCells();

		int maxRowLength = 0;

		for (int i = 0; i <= firstRowCell; i++) {
			Cell cell = firstRow.getCell(i);
			String value = cellType(cell);
			if(StringUtils.isNotBlank(value)) {
				ExcelHeader index = ExcelHeader.indexOf(value);
				if (index != null) {
					maxRowLength = i;
					arrayIndex.put(index, i);
				}
			}
        }


		Map<String, DynamicSkuPriceWrapper> priceMap = new HashMap<>();
		List<String> exceptionMsg = new ArrayList<String>();

		Map<String, Row> readyProduct = new HashMap<>();
		Map<String, Row> errorProduct = new HashMap<>();

		Map<Integer, Row> errorRows = new HashMap<>();
		for (int r = 1; r < rows; r++) {

			Row row = sheet.getRow(r);
			if (row == null) {
				continue;
			}

			try {


				Product p = null;

				if(arrayIndex.containsKey(ExcelHeader.PRODUCT_ID)){
					String product_id = cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_ID)));
					if (StringUtils.isNotEmpty(product_id)) {
						p = productService.getProduct(Double.valueOf(product_id).longValue());
					}
				}

				if (p == null && arrayIndex.containsKey(ExcelHeader.SKU_ID)) {
					String sku_Id = cellType(row.getCell(arrayIndex.get(ExcelHeader.SKU_ID)));
					if (StringUtils.isNotEmpty(sku_Id)) {
						Sku sku = productService.getSku(Double.valueOf(sku_Id).longValue());
						if (sku != null) {
							p  = sku.getProduct();
						}
					}
				}

				if (p == null) {
					throw new RuntimeException(", 商品未导入，请先导入商品");
					//商品名称
					/*excelHeaderIsNotNull(arrayIndex, row, ExcelHeader.PRODUCT_NAME);*/
					/*List<Product> list = productService.findByProductName(cellType(row.getCell(arrayIndex.get(ExcelHeader.PRODUCT_NAME))), organization);
					if(list.isEmpty()) {
						throw new RuntimeException(", 商品未导入，请先导入商品");
					} else if(list.size() > 1){
						throw new RuntimeException(", 多个商品名字重复");
					} else {
						p = list.get(0);
					}*/
				}

				if(errorProduct.containsKey(p.getName())) {
					throw new RuntimeException(", 表中多个商品名字重复");
				}

				if(readyProduct.containsKey(p.getName())) {
					errorProduct.put(p.getName(), readyProduct.get(p.getName()));
					readyProduct.remove(p.getName());
					priceMap.remove(p.getName() + "1");
					priceMap.remove(p.getName() + "2");
					throw new RuntimeException(", 表中多个商品名字重复");
				}else {
					readyProduct.put(p.getName(), row);
				}

				List<Sku> skus = p.getSkus();

				//单品
				SkuWrapper sku = null;
				if(!skus.isEmpty()) {
					sku = new SkuWrapper(skus.get(0));
				}
				if(null == sku){
					sku = new SkuWrapper();
				}

				//市场
				Warehouse warehouse = null;
				if(arrayIndex.containsKey(ExcelHeader.SKU_MARKET) && null != cellType(row.getCell(arrayIndex.get(ExcelHeader.SKU_MARKET)))){

					warehouse = findWarehouseInList(cellType(row.getCell(arrayIndex.get(ExcelHeader.SKU_MARKET))));

					if (warehouse == null) {
						throw new RuntimeException(", "+ ExcelHeader.SKU_MARKET.getName() +"非法");
					}

					DynamicSkuPriceWrapper dynamicSkuPrice = null;
					if(null != warehouse && null != sku.getId()){
						DynamicSkuPrice dynamicSkuPrice2 = inventoryService.getDynamicSkuPrice(sku.getId(), warehouse.getId());
						if(null != dynamicSkuPrice2) {
							dynamicSkuPrice = new DynamicSkuPriceWrapper(dynamicSkuPrice2);
						}
					}
					if (dynamicSkuPrice == null) {
						dynamicSkuPrice = new DynamicSkuPriceWrapper();
						SimpleSkuWrapper simpleSkuWrapper = new SimpleSkuWrapper();
						simpleSkuWrapper.setId(sku.getId());
						simpleSkuWrapper.setName(sku.getName());
						dynamicSkuPrice.setSku(simpleSkuWrapper);
						//必填字段验证 :单品市场状态，单品购买价格，单品售价，单品库存
						excelHeaderIsNotNull(arrayIndex, row, ExcelHeader.SKU_SINGLE_SALE_PRICE, ExcelHeader.SKU_SINGLE_STATUS, ExcelHeader.SKU_SINGLE_INSALE,
								ExcelHeader.SKU_BUNDLE_SALE_PRICE, ExcelHeader.SKU_BUNDLE_STATUS, ExcelHeader.SKU_BUNDLE_INSALE);
					}

					dynamicSkuPrice.setWarehouse(new SimpleWarehouseWrapper(warehouse));

					//单品状态
					if(arrayIndex.containsKey(ExcelHeader.SKU_SINGLE_STATUS) && row.getCell(arrayIndex.get(ExcelHeader.SKU_SINGLE_STATUS)) != null){
						String cellValue = cellType(row.getCell(arrayIndex.get(ExcelHeader.SKU_SINGLE_STATUS)));
						if(cellValue.equals("上架")){
							dynamicSkuPrice.getSingleDynamicSkuPriceStatus().setSingleAvailable(true);
						}else if(cellValue.equals("下架")){
							dynamicSkuPrice.getSingleDynamicSkuPriceStatus().setSingleAvailable(false);
						}else {
							throw new RuntimeException(", "+ ExcelHeader.SKU_SINGLE_STATUS.getName() +"非法");
						}
					}

					//单品可售
					if(arrayIndex.containsKey(ExcelHeader.SKU_SINGLE_INSALE) && row.getCell(arrayIndex.get(ExcelHeader.SKU_SINGLE_INSALE)) != null){
						String cellValue = cellType(row.getCell(arrayIndex.get(ExcelHeader.SKU_SINGLE_INSALE)));
						if(cellValue.equals("可售")){
							dynamicSkuPrice.getSingleDynamicSkuPriceStatus().setSingleInSale(true);
						}else if(cellValue.equals("不可售")){
							dynamicSkuPrice.getSingleDynamicSkuPriceStatus().setSingleInSale(false);
						}else {
							throw new RuntimeException(", "+ ExcelHeader.SKU_SINGLE_INSALE.getName() +"非法");
						}
					}

					//单品售价
					if(arrayIndex.containsKey(ExcelHeader.SKU_SINGLE_SALE_PRICE)){
						if (cellType(row.getCell(arrayIndex.get(ExcelHeader.SKU_SINGLE_SALE_PRICE))) != null) {
							if (dynamicSkuPrice.getSingleDynamicSkuPriceStatus().isSingleAvailable()) {
								cellAvailablePriceValue(ExcelHeader.SKU_SINGLE_SALE_PRICE, cellType(row.getCell(arrayIndex.get(ExcelHeader.SKU_SINGLE_SALE_PRICE))));
							}
							dynamicSkuPrice.getSingleDynamicSkuPriceStatus().setSingleSalePrice(cellPriceValue(ExcelHeader.SKU_SINGLE_SALE_PRICE, cellType(row.getCell(arrayIndex.get(ExcelHeader.SKU_SINGLE_SALE_PRICE)))));
						}
					}



					//打包状态
					if(arrayIndex.containsKey(ExcelHeader.SKU_BUNDLE_STATUS) && row.getCell(arrayIndex.get(ExcelHeader.SKU_BUNDLE_STATUS)) != null){
						String cellValue = cellType(row.getCell(arrayIndex.get(ExcelHeader.SKU_BUNDLE_STATUS)));
						if(cellValue.equals("上架")){
							dynamicSkuPrice.getBundleDynamicSkuPriceStatus().setBundleAvailable(true);
						}else if(cellValue.equals("下架")){
							dynamicSkuPrice.getBundleDynamicSkuPriceStatus().setBundleAvailable(false);
						}else {
							throw new RuntimeException(", "+ ExcelHeader.SKU_BUNDLE_STATUS.getName() +"非法");
						}
					}

					//打包可售
					if(arrayIndex.containsKey(ExcelHeader.SKU_BUNDLE_INSALE) && row.getCell(arrayIndex.get(ExcelHeader.SKU_BUNDLE_INSALE)) != null){
						String cellValue = cellType(row.getCell(arrayIndex.get(ExcelHeader.SKU_BUNDLE_INSALE)));
						if(cellValue.equals("可售")){
							dynamicSkuPrice.getBundleDynamicSkuPriceStatus().setBundleInSale(true);
						}else if(cellValue.equals("不可售")){
							dynamicSkuPrice.getBundleDynamicSkuPriceStatus().setBundleInSale(false);
						}else {
							throw new RuntimeException(", "+ ExcelHeader.SKU_BUNDLE_INSALE.getName() +"非法");
						}
					}

					//打包售价
					if(arrayIndex.containsKey(ExcelHeader.SKU_BUNDLE_SALE_PRICE)){
						if (cellType(row.getCell(arrayIndex.get(ExcelHeader.SKU_BUNDLE_SALE_PRICE))) != null) {
							if (dynamicSkuPrice.getBundleDynamicSkuPriceStatus().isBundleAvailable()) {
								cellAvailablePriceValue(ExcelHeader.SKU_BUNDLE_SALE_PRICE, cellType(row.getCell(arrayIndex.get(ExcelHeader.SKU_BUNDLE_SALE_PRICE))));
							}
							dynamicSkuPrice.getBundleDynamicSkuPriceStatus().setBundleSalePrice(cellPriceValue(ExcelHeader.SKU_BUNDLE_SALE_PRICE, cellType(row.getCell(arrayIndex.get(ExcelHeader.SKU_BUNDLE_SALE_PRICE)))));
						}
					}

					priceMap.put(p.getName() + "1", dynamicSkuPrice);
				}


			} catch (RuntimeException e) {
				errorRows.put(row.getRowNum(), row);
				String errorMsg = "导入excel第 " + (row.getRowNum() + 1) + " 行出错" + e.getMessage();
				row.createCell(maxRowLength + 2).setCellValue(errorMsg);
				exceptionMsg.add(errorMsg);
			}

		}
		saveDynamicPrice(priceMap, adminUser);
		Map<String, Object> nmap = new HashMap<>();
		nmap.put("priceListSize", priceMap.size());
		nmap.put("exceptionMsg", exceptionMsg);

		if(!errorProduct.isEmpty()) {
			Iterator<String> its = errorProduct.keySet().iterator();
			while (its.hasNext()) {
				Row row = errorProduct.get(its.next());
				errorRows.put(row.getRowNum(), row);
				String errorMsg = "导入excel第 " + (row.getRowNum() + 1) + " 行出错，表中多个商品名称重复";
				row.createCell(maxRowLength + 2).setCellValue(errorMsg);
				exceptionMsg.add(errorMsg);
			}
		}
		File errorFile = excelImportError(arrayIndex, ExportExcelUtils.excelFolderName, "excelImportError.xls", errorRows);
		if(null != errorFile) {
			nmap.put("errorFileName", errorFile.getPath());
		}
        if(arrayIndex.isEmpty()) {
            nmap.put("headMsg", "excel表头为空");
        }
		return nmap;
    }
    
    public File excelImportError(Map<ExcelHeader, Integer> mapIndex, String fileDir, String fileName, Map<Integer, Row> rows) {
    	Workbook wb = new HSSFWorkbook();
    	Sheet sheet = wb.createSheet();
    	Row firstRow = sheet.createRow(0);
    	
    	if(!mapIndex.isEmpty() && !rows.isEmpty()) {
    		Iterator<ExcelHeader> iterator = mapIndex.keySet().iterator();
    		while(iterator.hasNext()) {
    			ExcelHeader header = iterator.next();
    			firstRow.createCell(mapIndex.get(header)).setCellValue(header.getName());
    		}
    		
    		int index = 1;
    		
    		if(!rows.isEmpty()) {
    			Iterator<Integer> its = rows.keySet().iterator();
    			while (its.hasNext()) {
    				Row row = rows.get(its.next());
    				Row newRow = sheet.createRow(index ++);
    				for(int i = 0; i < row.getLastCellNum(); i ++) {
    					newRow.createCell(i).setCellValue(cellType(row.getCell(i)));
    				}
    			}
    		}
    		
    		File dir = new File(fileDir);
    		if(!dir.exists()) {
    			dir.mkdirs();
    		}
    		FileOutputStream out = null;
    		File file = new File(fileDir, fileName);
    		try {
    			out = new FileOutputStream(file);
    			wb.write(out);
    			if(null != out) {
    				out.close();
    			}
    		} catch (FileNotFoundException e1) {
    			e1.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		return file;
    	}
    	return null;
    }
    

    
    
    @Transactional
    private void saveProductAndSku(Map<String, ProductVo> productMap, AdminUser adminUser){
    	if(!productMap.isEmpty()) {
    		Iterator<String> iterator = productMap.keySet().iterator();
    		while(iterator.hasNext()) {
    			ProductVo product = productMap.get(iterator.next());

    			productTempFacade.saveChangeDetail(product, product.getId(), new ChangeDetail(), adminUser);
    		}
    	}
    }
    
    @Transactional
    private void saveDynamicPrice(Map<String, DynamicSkuPriceWrapper> priceMap, AdminUser adminUser){
    	if(!priceMap.isEmpty()) {
    		Iterator<String> its = priceMap.keySet().iterator();
    		while (its.hasNext()) {
    			DynamicSkuPriceWrapper dynamicSkuPrice = priceMap.get(its.next());
    			dynamicPriceTempFacade.saveChangeDetail(dynamicSkuPrice, dynamicSkuPrice.getId(), new ChangeDetail(), adminUser);
    		}
    	}
    }


    private Category findCategoryInList(String hierarchyName){
		List<CategoryWrapper> categories = productFacade.listAllCategories();
		for(CategoryWrapper category : categories){
			if(category.getHierarchyName().equals(hierarchyName)){
				return productService.getCategory(category.getId());
			}
		}
		return null;
	}


	private Brand findBrandInList(String brandName){
		List<Brand> brands = productService.findAllBrands();
		for(Brand brand : brands){
			if(brand.getBrandName().equals(brandName)){
				return brand;
			}
		}
		return null;
	}
	
	private Warehouse findWarehouseInList(String warehouseName){
        final List<Warehouse> warehouses = locationService.getAllWarehouses();
		for(Warehouse warehouse : warehouses){
			if(warehouse.getName().equals(warehouseName)){
				return warehouse;
			}
		}
		return null;
	}
	
	private String cellType(Cell cell) {
		if(null == cell) {
			return null;
		}
		int type = cell.getCellType();
		switch (type) {
		case Cell.CELL_TYPE_BLANK:
			return null;
		case Cell.CELL_TYPE_BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case Cell.CELL_TYPE_ERROR : 
			return String.valueOf(cell.getErrorCellValue());
		case Cell.CELL_TYPE_FORMULA : 
			return String.valueOf(cell.getCellFormula());
		case Cell.CELL_TYPE_NUMERIC : 
			return String.valueOf(cell.getNumericCellValue());
		case Cell.CELL_TYPE_STRING : 
			return cell.getStringCellValue();
		default :
			return null;
		}
	}


	private BigDecimal cellAvailablePriceValue(ExcelHeader excelHeader ,String price) {
		if (StringUtils.isBlank(price)) {
			throw new RuntimeException(", "+ excelHeader.getName() +"不能为空");
		}
		BigDecimal nprice = null;
		try {
			nprice = new BigDecimal(price);
			if(nprice.compareTo(BigDecimal.ZERO) <= 0) {
				throw new RuntimeException(", " + excelHeader.getName() + "不能小于等于0");
			}
			if(nprice.compareTo(BigDecimal.valueOf(10000)) >= 0) {
				throw new RuntimeException(", " + excelHeader.getName() + "不能大于等于10000");
			}

		} catch (NumberFormatException e) {
			throw new NumberFormatException(", " + excelHeader.getName() + "格式错误");
		}
		return nprice;
	}

	//价格异常
	private BigDecimal cellPriceValue(ExcelHeader excelHeader ,String price) {
		if (StringUtils.isBlank(price)) {
			throw new RuntimeException(", "+ excelHeader.getName() +"不能为空");
		}
		BigDecimal nprice = null;
		try {
			nprice = new BigDecimal(price);
			if(nprice.compareTo(BigDecimal.ZERO) < 0) {
				throw new RuntimeException(", " + excelHeader.getName() + "不能小于0");
			}
			if(nprice.compareTo(BigDecimal.valueOf(10000)) >= 0) {
				throw new RuntimeException(", " + excelHeader.getName() + "不能大于等于10000");
			}
			
		} catch (NumberFormatException e) {
			throw new NumberFormatException(", " + excelHeader.getName() + "格式错误");
		}
		return nprice;
	}
	
	//库存异常
	private int cellStockValue(ExcelHeader excelHeader, String stock) {
		if (StringUtils.isBlank(stock)) {
			throw new RuntimeException(", "+ excelHeader.getName() +"不能为空");
		}
		int nStock;
		try {
			nStock = Double.valueOf(stock).intValue();
			if(nStock < 0) {
				throw new RuntimeException(", "+ excelHeader.getName() +"不能小于0");
			}
		} catch (NumberFormatException e) {
			throw new NumberFormatException(", " + excelHeader.getName() + "格式错误");
		}
		return nStock;
	}
	
	//判断是否为空
	private boolean excelHeaderIsNotNull(Map<ExcelHeader, Integer> arrayIndex, Row row, ExcelHeader... excelHeaders){
		for(ExcelHeader excelHeader : excelHeaders) {
			if(!arrayIndex.containsKey(excelHeader)) {
                throw new RuntimeException(", 表头" + excelHeader.getName() + "不能为空");
            }
            if(arrayIndex.containsKey(excelHeader) && StringUtils.isBlank(cellType(row.getCell(arrayIndex.get(excelHeader))))) {
				throw new RuntimeException(", " + excelHeader.getName() + "不能为空");
			}
		}
		return true;
	}
	
	public File skuSaleExport(SkuSalesRequest request, AdminUser adminUser,
			String fileDir, String fileName) {
		File dir = new File(fileDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }
    	
        File file = new File(dir, fileName);
        
       SkuSaleResponse response = orderFacade.findSkuSales(request, adminUser);
       
       List<SkuSaleWrapper> sales = response.getSkuSales();
		
        
    	FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	
		Workbook wb = new HSSFWorkbook();
    	Sheet sheet = wb.createSheet();
    	
    	//设置字体
    	Font font = wb.createFont();    
    	font.setFontName("仿宋_GB2312");    
    	font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示    
    	font.setFontHeightInPoints((short) 12);    
    	
    
    	//设置样式
    	CellStyle style = wb.createCellStyle();  
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中  
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中 
        style.setFont(font);
        
        //合并单元格
    	sheet.addMergedRegion(new CellRangeAddress(0,(short)0,0,(short)4));
    	Row headerRow = sheet.createRow(0);
    	headerRow.setHeight((short)500);
    	Cell header = headerRow.createCell((short)0);
    	
    	//标题栏目
    	Warehouse warehouse = null;
    	if(request.getWarehouseId() != null){
    		warehouse = locationService.getWarehouse(request.getWarehouseId());
    	}
    	DateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
    	String start = null;
    	String end = null;
    	if(request.getStart() != null){
    		 start = formate.format(request.getStart());
    	}
    	if(request.getEnd() != null){
    		 end = formate.format(request.getEnd());
    	}
    	String headStr = ("总记录数"+sales.size()+"   起始时间："+start+"  结束时间："+end+"   市场："+
    	(warehouse == null ? null : warehouse.getDisplayName()));
    	header.setCellValue(headStr);
    	header.setCellStyle(style);

    	
    	Row firstRow = sheet.createRow(1);
    	Map<SkuSaleExcelHeader, Integer> mapIndex = new HashMap<>();
    	
    	//设置header  start-date  end-date   warehouse
    	for(int i = 0; i < SkuSaleExcelHeader.values().length; i ++){
    		mapIndex.put(SkuSaleExcelHeader.values()[i], i);
    		Cell cell = firstRow.createCell(i);
    		cell.setCellValue(SkuSaleExcelHeader.values()[i].name);
    		cell.setCellStyle(style);
    	}
    	
    	sheet.setColumnWidth(mapIndex.get(SkuSaleExcelHeader.SKU_NAME), 50*256);
    	int rowIndex = 2;
    	while (sales.size() != 0) {
    		
    		SkuSaleWrapper skuSaleWrapper  = sales.get(0);
    		Sku sku = productService.getSku(skuSaleWrapper.getSkuId());
    		Row row = sheet.createRow(rowIndex);
    		rowIndex ++;
    		sales.remove(skuSaleWrapper);
    		if(mapIndex.containsKey(SkuSaleExcelHeader.SKU_ID)){
    			Cell cell = row.createCell(mapIndex.get(SkuSaleExcelHeader.SKU_ID));
    			cell.setCellStyle(style);
    			cell.setCellValue(sku.getId());
    		}
    		if(mapIndex.containsKey(SkuSaleExcelHeader.SKU_NAME)){
    			Cell cell = row.createCell(mapIndex.get(SkuSaleExcelHeader.SKU_NAME));
    			cell.setCellStyle(style);
    			cell.setCellValue(sku.getName());
    		}
    		
    		if(mapIndex.containsKey(SkuSaleExcelHeader.PRODUCT_BRAND)){
    			Brand brand = sku.getProduct().getBrand() ;
    			Cell cell = row.createCell(mapIndex.get(SkuSaleExcelHeader.PRODUCT_BRAND));
    			cell.setCellStyle(style);
    			cell.setCellValue(brand == null ? null:brand.getBrandName());
    		}
			if(mapIndex.containsKey(SkuSaleExcelHeader.SKU_CAPACITYINBUNDLE)) {
				Cell cell = row.createCell(mapIndex.get(SkuSaleExcelHeader.SKU_CAPACITYINBUNDLE));
				cell.setCellStyle(style);
				cell.setCellValue(skuSaleWrapper.getCapacityInBundle());
			}
    		if(mapIndex.containsKey(SkuSaleExcelHeader.SKU_SINGLE_SALE)){
    			Cell cell = row.createCell(mapIndex.get(SkuSaleExcelHeader.SKU_SINGLE_SALE));
    			cell.setCellStyle(style);
    			cell.setCellValue(skuSaleWrapper.getSingleSale());
    		}
			if(mapIndex.containsKey(SkuSaleExcelHeader.SKU_BUNDLE_SALE)){
				Cell cell = row.createCell(mapIndex.get(SkuSaleExcelHeader.SKU_BUNDLE_SALE));
				cell.setCellStyle(style);
				cell.setCellValue(skuSaleWrapper.getBundleSale());
			}
    		if(mapIndex.containsKey(SkuSaleExcelHeader.SKU_COUNT_SALE)){
    			Cell cell = row.createCell(mapIndex.get(SkuSaleExcelHeader.SKU_COUNT_SALE));
    			cell.setCellStyle(style);
    			cell.setCellValue(skuSaleWrapper.getCountSale());
    		}
			if(mapIndex.containsKey(SkuSaleExcelHeader.SKU_SELLCANCEL)){
				Cell cell = row.createCell(mapIndex.get(SkuSaleExcelHeader.SKU_SELLCANCEL));
				cell.setCellStyle(style);
				cell.setCellValue(skuSaleWrapper.getSellCancel());
			}
			if(mapIndex.containsKey(SkuSaleExcelHeader.SKU_SELLRETURN)){
				Cell cell = row.createCell(mapIndex.get(SkuSaleExcelHeader.SKU_SELLRETURN));
				cell.setCellStyle(style);
				cell.setCellValue(skuSaleWrapper.getSellReturn());
			}
    	}
    	
    	try {
			wb.write(out);
			if(null != out) {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return file;
		
		
	}


	public Map<String, Object> productPhotoImport(MultipartFile uploadFile, Long organizationId, AdminUser adminUser, Integer type) throws IOException {
		Map<String, Object> map = new HashMap<>();
		
		String fileName = uploadFile.getOriginalFilename();
		if(!fileName.endsWith("zip")) {
			map.put("errorMsg", "文件格式必须为.zip");
			return map;
		}

		String zipDir = ExportExcelUtils.excelFolderName + "/zipPhotoFile/" + DateFormatUtils.format(new Date(), "yyyyMMdd");
		
		
		List<File> files = ZipFileUtils.unZipFiles(uploadFile, zipDir);
//
//		if (adminUser.getOrganization() == null) {
//			map.put("errorMsg", "权限不够");
//		}

		if(files.isEmpty()) {
			map.put("errorMsg", "文件名称以及内部文件夹名称必须为英文");
			return map;
		}

		Map<ProductVo, File> productMap = new HashMap<>();
		List<File> errorFileList = new ArrayList<>();
		for(File file : files) {
			String name = file.getName();
			if(name.contains(".")) {
				String productName = name.substring(
						name.lastIndexOf("/") + 1,
						name.lastIndexOf("."));

				if(type.equals(1)){
					String tempName = productName.replaceAll("-", "").replaceAll("|", "%");
					tempName = StringUtils.removeStart(tempName, "%");
					List<Product> list = productService.findProductsByPartName(organizationId, tempName);
					if (!list.isEmpty()) {
						if (list.size() == 1) {
							Product product = list.get(0);
							ProductVo productVo = productTempFacade.initProductVo(product);
							productMap.put(productVo , file);
						} else {
							int count = 0;
							Product tempProduct = null;
							for(Product product : list) {
								if(product.getName().matches(productName + "(\\\\*.*){0,1}$")) {
									count ++ ;
									if(count > 1) {
										break;
									}
									tempProduct = product;
								}
							}
							if (count == 1) {
								productMap.put(productTempFacade.initProductVo(tempProduct) , file);
							} else {
								errorFileList.add(file);
							}
						}

					} else {
						errorFileList.add(file);
					}
				} else {
					if(StringUtils.isNotBlank(productName)) {
						Long productId = null;
						try {
							productId = Long.valueOf(productName);
							Product product = productService.findById(productId);
							if(!product.getOrganization().getId().equals(organizationId)) {
								throw new RuntimeException("organizationId错误");
							}
							productMap.put(productTempFacade.initProductVo(product) , file);
						} catch (NumberFormatException e) {
							errorFileList.add(file);
						} catch (RuntimeException e) {
							errorFileList.add(file);
						}
					} else {
						errorFileList.add(file);
					}
				}
			}
		}
		
		
		map.put("productListSize", productMap.size());

		if(!errorFileList.isEmpty()) {
			File errorFile = photoImportError(zipDir, "photoImportError.zip", errorFileList);
			map.put("errorSize", errorFileList.size());
			map.put("errorFileName", errorFile.getPath());
		}

		savePhotoProduct(productMap, adminUser, zipDir);
		
		return map;
	}
	
	private File photoImportError(String fileDir, String fileName, List<File> list){
		File file = new File(fileDir, fileName);
		return ZipFileUtils.zipFiles(file, list.toArray(new File[list.size()]));
	}
	
	private void savePhotoProduct(final Map<ProductVo, File> productWrappers, final AdminUser adminUser, final String zipDir) {
		
		taskExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					if(!productWrappers.isEmpty()) {
						Iterator<ProductVo> iterator = productWrappers.keySet().iterator();
						while(iterator.hasNext()) {
							ProductVo wrapper = iterator.next();
							File file = productWrappers.get(wrapper);
							
							MediaFile mediaFile = mediaFileService.saveMediaFile(new FileInputStream(file));
							wrapper.getMediaFiles().clear();
							MediaFileVo mediaFileVo = new MediaFileVo();
							mediaFileVo.setId(mediaFile.getId());
							mediaFileVo.setUrl(mediaFile.getUrl());
							wrapper.getMediaFiles().add(mediaFileVo);
							productTempFacade.saveChangeDetailPhoto(wrapper, adminUser);
						}
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				FileUtils.deleteQuietly(new File(zipDir));
			}
		});
	}
	
}
