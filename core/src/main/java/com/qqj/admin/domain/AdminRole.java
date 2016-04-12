package com.qqj.admin.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User: xudong
 * Date: 3/3/15
 * Time: 10:52 AM
 */
@Entity
@Getter
@Setter
@org.hibernate.annotations.Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class AdminRole {
    public static final String Administrator = "Administrator";
    public static final String CustomerServiceSupervisor = "CustomerServiceSupervisor";
    public static final String CustomerService = "CustomerService";
    public static final String OperationsSupervisor = "OperationsSupervisor";
    public static final String OperationsStaff = "OperationsStaff";
    public static final String FinancialStaff = "FinancialStaff";
    public static final String PurchaseStaff = "PurchaseStaff";
    public static final String PurchaseSupervisor = "PurchaseSupervisor";
    public static final String LogisticsSupervisor = "LogisticsSupervisor";
    public static final String LogisticsStaff = "LogisticsStaff";
    public static final String OrderDistribution = "OrderDistribution"; //订单分配员
    public static final String DepotManage = "DepotManage"; //实物库管
    public static final String FinanceManage = "FinanceManage"; //财务库管

    public static final String FinanceOut = "FinanceOut"; //财务出纳
    public static final String PayAndOut = "PayAndOut"; //应收应付


    public static final String CustomerServiceAssistant = "CustomerServiceAssistant";
    public static final String LogisticsAssistant = "LogisticsAssistant";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String displayName;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = AdminPermission.class)
    @JoinTable(name = "admin_role_permission_xref", joinColumns = @JoinColumn(name = "admin_role_id"),
            inverseJoinColumns = @JoinColumn(name = "admin_permission_id"))
    private Set<AdminPermission> adminPermissions = new HashSet<AdminPermission>();

}
