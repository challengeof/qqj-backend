package com.mishu.cgwy.order.domain;

import javax.persistence.*;

import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.score.domain.ScoreLog;
import lombok.Data;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.profile.domain.Customer;

@Data
@Entity
public class Evaluate {
	@Id
	private Long id;
	
	private int productQualityScore;
	private int deliverySpeedScore;
	private int trackerServiceScore;
	
	private String msg;

//	private boolean scoreIsSend; //是否已派送积分

	@OneToOne
	@JoinColumn(name = "scoreLog_id")
	private ScoreLog scoreLog;
	
	@OneToOne
	@JoinColumn(name = "id")
	@MapsId
	private Order order;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@ManyToOne
	@JoinColumn(name = "admin_id")
	private AdminUser adminUser;
	
	@ManyToOne
	@JoinColumn(name = "tracker_id")
	private AdminUser tracker;


	@ManyToOne
	@JoinColumn(name = "organization_id")
	private Organization organization;

	

}
