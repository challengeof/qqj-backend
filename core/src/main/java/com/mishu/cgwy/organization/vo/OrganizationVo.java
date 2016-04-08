package com.mishu.cgwy.organization.vo;

import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.organization.domain.Organization;
import lombok.Data;
import org.elasticsearch.common.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class OrganizationVo {
	private Long id;

	private String name;

	private Date createDate;

	private boolean enabled = true;

	private String telephone;

	private String[] cityIds;

	private String[] warehouseIds;

	private String[] blockIds;
}
