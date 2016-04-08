package com.mishu.cgwy.product.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Data;

@Data
public class RproductItem {

	@JsonProperty("barCode")
	private Long barCode;

	//@JsonProperty("cbarcode")
	@JsonIgnore
	private Long cbarcode;

	@JsonProperty("wresingnum")
	private Long wresingnum;

	@JsonProperty("pronum")
	private Long pronum;

	@JsonProperty("reamount")
//	@JsonIgnore
	private  BigDecimal reamount;

	//@JsonProperty("itempro")
	@JsonIgnore
	private  String itempro;

	@JsonProperty("returnnum")
	private Long returnnum;

	@JsonProperty("storagenum")
	private Long storagenum;

	@JsonProperty("stolocanum")
	private Long stolocanum;

}
