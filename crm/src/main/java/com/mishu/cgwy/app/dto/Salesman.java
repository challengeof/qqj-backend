package com.mishu.cgwy.app.dto;

import lombok.Data;

@Data
public class Salesman {

	/** 管理员id */
	private String id;
	/** 真实姓名 */
	private String realname;
	/** 拥有餐馆数 */
	private long restaurantNumber;
	/** 登录名*/
	private String username;
}
