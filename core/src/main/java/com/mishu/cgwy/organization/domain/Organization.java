package com.mishu.cgwy.organization.domain;

import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.product.domain.Category;
import com.mishu.cgwy.product.domain.Product;
import lombok.Data;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@org.hibernate.annotations.Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class Organization {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private Date createDate;

	private boolean enabled = true;

	private String telephone;

    private boolean selfSupport = false;

	@ManyToMany
	@JoinTable(name = "organization_city_xref", joinColumns = @JoinColumn(name = "organization_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "city_id", referencedColumnName = "id"))
	private Set<City> cities = new HashSet<>();

	@ManyToMany
	@JoinTable(name = "organization_warehouse_xref", joinColumns = @JoinColumn(name = "organization_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "warehouse_id", referencedColumnName = "id"))
	private Set<Warehouse> warehouses = new HashSet<>();

	@ManyToMany
	@JoinTable(name = "organization_block_xref", joinColumns = @JoinColumn(name = "organization_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "block_id", referencedColumnName = "id"))
	@BatchSize(size = 50)
	private Set<Block> blocks = new HashSet<>();

	@Override
	public String toString() {
		return "Organization{" +
				"id=" + id +
				", name='" + name + '\'' +
				", createDate=" + createDate +
				", enabled=" + enabled +
				", telephone='" + telephone + '\'' +
				", selfSupport=" + selfSupport +
				'}';
	}
}
