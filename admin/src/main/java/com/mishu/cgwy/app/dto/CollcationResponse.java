package com.mishu.cgwy.app.dto;

import java.util.List;

import com.mishu.cgwy.error.RestError;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class CollcationResponse extends RestError {

	/** 销售分配 集合 */
	private List<Collcation> rows;
}
