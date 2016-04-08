package com.mishu.cgwy.app.dto;

import lombok.Data;

@Data
public class UserResponse {

	/** 餐馆类型 */
	private String resType;
	/** 注册电话 */
	private String username;
	/** 店名 */
	private String name;
	/** 详细地址 */
	private String address;
	/** 收货人 */
	private String realname;
	/** 电话 */
	private String telephone;
	/** 营业执照号 */
	private String license;
	/** 分享人 */
	private String share1;
	/** 分享人2 */
	private String share2;
	/** 销售客服 */
	private String adminName;
	/** 注册时间 */
	private String createTime;
	/** 区域 */
	private String region;
	private String regionId;
	private long userId;
	private long restaurantId;
}
