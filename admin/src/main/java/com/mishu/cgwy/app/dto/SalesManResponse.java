package com.mishu.cgwy.app.dto;

import java.util.List;

import com.mishu.cgwy.error.RestError;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SalesManResponse extends RestError {

	/** 销售人员集合 */
	private List<Salesman> admins;
}
