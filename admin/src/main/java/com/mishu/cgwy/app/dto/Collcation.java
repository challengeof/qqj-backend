package com.mishu.cgwy.app.dto;

import lombok.Data;

@Data
public class Collcation {
	/** 注册时间 */
	private String createTime;
	/** 餐馆地址 */
	private String rAddress;
	/** 营业执照号 */
	private String rLicense;
	/** 餐馆名称 */
	private String rName;
	/** 联系人 */
	private String rRealname;
	/** 联系电话 */
	private String rTelephone;
	/** 餐馆类型 */
	private String rType;
	/** 地区 */
	private String region;
	/** 地区Id */
	private long regionId;
	/** 分享人1 */
	private String share1;
	/** 分享人2 */
	private String share2;
	/** 注册电话 */
	private String telephone;
	/** 用户id */
	private long userId;
}
