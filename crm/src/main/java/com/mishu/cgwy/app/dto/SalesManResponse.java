package com.mishu.cgwy.app.dto;

import com.mishu.cgwy.error.RestError;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class SalesManResponse extends RestError {

	/** 销售人员集合 */
	private List<Salesman> admins;
}
