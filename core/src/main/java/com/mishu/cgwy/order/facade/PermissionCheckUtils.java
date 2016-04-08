package com.mishu.cgwy.order.facade;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminRole;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.vo.AdminRoleVo;
import com.mishu.cgwy.error.PermissionDeniedException;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.profile.domain.Customer;

import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 4/5/15
 * Time: 10:28 PM
 */
public class PermissionCheckUtils {
    public static void checkRegisterAdminUserPermission(AdminUser adminUser, AdminUser operator) {

        if (hasRole(operator, AdminRole.Administrator)) {
            return;
        }

        if (hasRole(operator, AdminRole.CustomerServiceAssistant)) {
            if (adminUser.getAdminRoles().size() == 1 && adminUser.getAdminRoles().iterator().next().getName().equals
                    (AdminRole.CustomerService)) {
                return;
            }
        }

        if (hasRole(operator, AdminRole.LogisticsAssistant)) {
            if (adminUser.getAdminRoles().size() == 1 && adminUser.getAdminRoles().iterator().next().getName().equals
                    (AdminRole.LogisticsStaff)) {
                return;
            }
        }

        throw new PermissionDeniedException();

    }

    private static boolean hasRole(AdminUser operator, String roleName) {
        for (AdminRole role : operator.getAdminRoles()) {
            if (role.getName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

    public static List<AdminRoleVo> filterAccessibleAdminRole(List<AdminRoleVo> roles, AdminUser operator) {
        if (hasRole(operator, AdminRole.Administrator)) {
            return roles;
        }

        if (hasRole(operator, AdminRole.CustomerServiceAssistant)) {
            return new ArrayList<>(Collections2.filter(roles, new Predicate<AdminRoleVo>() {
                @Override
                public boolean apply(AdminRoleVo input) {
                    return input.getName().equals(AdminRole.CustomerService);
                }
            }));
        }

        if (hasRole(operator, AdminRole.LogisticsAssistant)) {
            return new ArrayList<>(Collections2.filter(roles, new Predicate<AdminRoleVo>() {
                @Override
                public boolean apply(AdminRoleVo input) {
                    return input.getName().equals(AdminRole.LogisticsStaff);
                }
            }));
        }

        return new ArrayList<>();
    }


    public static void checkOrderUpdatePermission(Order order, AdminUser adminUser) {
        // TODO
        // throw new PermissionDeniedException();
    }

    public static void checkFulfillmentUpdatePermission(Order order, AdminUser adminUser) {
        // TODO

        // throw new PermissionDeniedException();

    }

    public static void checkCustomerUpdatePermission(Customer customer, AdminUser adminUser) {
        // TODO

        // throw new PermissionDeniedException();
    }

    public static boolean canViewAllInBlock(AdminUser adminUser) {
        boolean r = false;
        for (AdminRole role : adminUser.getAdminRoles()) {
            switch (role.getName()) {
                case AdminRole.Administrator:
                    r = true;
                    break;
                case AdminRole.CustomerServiceSupervisor:
                    r = true;
                    break;
                case AdminRole.FinancialStaff:
                    r = true;
                    break;
                case AdminRole.PurchaseStaff:
                    r = true;
                    break;
                case AdminRole.PurchaseSupervisor:
                    r = true;
                    break;
                case AdminRole.OperationsSupervisor:
                    r = true;
                    break;
                case AdminRole.OperationsStaff:
                    r = true;
                    break;
                case AdminRole.LogisticsSupervisor:
                    r = true;
                    break;
                case AdminRole.LogisticsStaff:
                    r = true;
                    break;
                case AdminRole.OrderDistribution:
                    r = true;
                    break;
                case AdminRole.DepotManage:
                    r = true;
                    break;
                case AdminRole.FinanceManage:
                    r = true;
                    break;
                case AdminRole.FinanceOut:
                    r = true;
                    break;
                case AdminRole.PayAndOut:
                    r = true;
                    break;
            }
        }

        return r;
    }

    public static Boolean canViewAllTracker(AdminUser tracker) {
        boolean r = false;
        for (AdminRole adminRole : tracker.getAdminRoles()) {
            if (adminRole.getName().equals(AdminRole.LogisticsSupervisor)) {
                r = true;
            }
        }
        return r;
    }


}
