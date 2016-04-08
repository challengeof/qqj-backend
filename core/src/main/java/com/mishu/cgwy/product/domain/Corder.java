package com.mishu.cgwy.product.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JacksonXmlRootElement(localName = "corder")
public class Corder {
	@JsonProperty("corderInfo")
	private CorderInfo corderInfo;

	@JsonProperty("rproduct_item")
	@JacksonXmlElementWrapper(useWrapping=true, localName="rproductInfo")
	private List<RproductItem> rproductInfo = new ArrayList<RproductItem>();

	public static void main(String[] args) {
		Corder corder = new Corder();
		CorderInfo cor = new CorderInfo();
		cor.setAuditor("000");
		cor.setExpressnum("111");
		cor.setOrdernum("888");
		cor.setReoperator("222");
		cor.setRetime("333");
		cor.setReturntime("444");
		cor.setStoaffirm("555");
		cor.setWresingnum(666L);

		corder.setCorderInfo(cor);

		RproductItem rproductItem = new RproductItem();
		rproductItem.setBarCode(111111l);
		corder.getRproductInfo().add(rproductItem);
		corder.getRproductInfo().add(rproductItem);

		try {
			System.out.println(new XmlMapper().writeValueAsString(corder).replaceFirst("\\s*xmlns=\"\"\\s*", ""));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

	}
}
