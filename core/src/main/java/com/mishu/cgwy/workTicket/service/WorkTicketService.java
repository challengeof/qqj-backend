package com.mishu.cgwy.workTicket.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.domain.Restaurant_;
import com.mishu.cgwy.profile.service.CustomerService;
import com.mishu.cgwy.profile.service.RestaurantService;
import com.mishu.cgwy.profile.vo.RestaurantInfoVo;
import com.mishu.cgwy.profile.vo.RestaurantVo;
import com.mishu.cgwy.profile.wrapper.AddressWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.workTicket.controller.WorkTicketListRequest;
import com.mishu.cgwy.workTicket.controller.WorkTicketRequest;
import com.mishu.cgwy.workTicket.domain.WorkTicket;
import com.mishu.cgwy.workTicket.domain.WorkTicket_;
import com.mishu.cgwy.workTicket.repository.WorkTicketRepository;
import com.mishu.cgwy.workTicket.vo.WorkTicketVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bowen on 16/2/29.
 */
@Service
public class WorkTicketService {

    @Autowired
    private WorkTicketRepository workTicketRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CustomerService customerService;

    @Transactional
    public void createWorkTicket(AdminUser operator, WorkTicketRequest request) {

        WorkTicket workTicket = new WorkTicket();
        copyWorkTicket(operator, workTicket, request);
        workTicketRepository.save(workTicket);

    }

    private void copyWorkTicket(AdminUser operator, WorkTicket workTicket, WorkTicketRequest request) {

        Order order = null;
        Restaurant restaurant = null;
        AdminUser adminUser = null;

        if (request.getOrderId() != null) {

            order = orderService.getOrderById(request.getOrderId());
        }

        if (request.getRestaurantTelephone() != null) {

            Customer customer = customerService.findCustomerByUsername(request.getRestaurantTelephone());
            if(customer !=null){
                List<Restaurant> restaurants = customer.getRestaurant();
                if (!restaurants.isEmpty()) {
                    restaurant = restaurants.get(0);
                }
            }
        }



        if (request.getRestaurantId() != null) {

            restaurant = restaurantService.getOne(request.getRestaurantId());
        }

        if (restaurant == null && request.getUsername() != null) {

            Customer customer = customerService.findCustomerByUsername(request.getUsername());
            if(customer != null){
                List<Restaurant> restaurants = customer.getRestaurant();
                if (restaurants != null && !restaurants.isEmpty()) {
                    restaurant = restaurants.get(0);
                }
            }
        }

        if (request.getFollowUpId() != null) {

            adminUser = adminUserService.getAdminUser(request.getFollowUpId());
        }

        workTicket.setOperator(operator);
        workTicket.setStatus(request.getStatus());
        workTicket.setConsultants(request.getConsultants());
        workTicket.setConsultantsTelephone(request.getConsultantsTelephone());
        workTicket.setCreateTime(new Date());
        workTicket.setProblemSources(request.getProblemSources());
        workTicket.setProcess(request.getProcess());
        workTicket.setContent(request.getContent());


        workTicket.setUsername(request.getUsername());

        if (order != null) {

            workTicket.setOrder(order);
        }
        if (restaurant != null) {

            workTicket.setRestaurant(restaurant);
        }
        if (adminUser != null) {

            workTicket.setFollowUp(adminUser);
        }

    }

    @Transactional
    public void updateWorkTicket(AdminUser operator,Long id, WorkTicketRequest request) {

        WorkTicket workTicket = workTicketRepository.getOne(id);
        copyWorkTicket(operator, workTicket, request);
        workTicketRepository.save(workTicket);
    }

    public WorkTicketVo getWorkTicket(Long id) {

        WorkTicketVo workTicketVo = new WorkTicketVo();
        WorkTicket workTicket = workTicketRepository.getOne(id);
        copyProperties(workTicket, workTicketVo);
        return workTicketVo;
    }

    private void copyProperties(WorkTicket workTicket, WorkTicketVo workTicketVo) {

        workTicketVo.setId(workTicket.getId());
        workTicketVo.setContent(workTicket.getContent());
        workTicketVo.setProblemSources(workTicket.getProblemSources());
        workTicketVo.setProcess(workTicket.getProcess());
        workTicketVo.setConsultants(workTicket.getConsultants());
        workTicketVo.setConsultantsTelephone(workTicket.getConsultantsTelephone());
        workTicketVo.setCreateTime(workTicket.getCreateTime());
        workTicketVo.setStatus(workTicket.getStatus());

        workTicketVo.setUsername(workTicket.getUsername());

        if (workTicket.getRestaurant() != null) {
            RestaurantVo restaurant = new RestaurantVo();
            restaurant.setId(workTicket.getRestaurant().getId());
            restaurant.setTelephone(workTicket.getRestaurant().getTelephone());
            restaurant.setName(workTicket.getRestaurant().getName());
            restaurant.setReceiver(workTicket.getRestaurant().getReceiver());
            restaurant.setAddress(new AddressWrapper(workTicket.getRestaurant().getAddress()));
            workTicketVo.setRestaurant(restaurant);
        }
        if (workTicket.getFollowUp() != null) {
            AdminUserVo vo = new AdminUserVo();
            vo.setId(workTicket.getFollowUp().getId());
            vo.setRealname(workTicket.getFollowUp().getRealname());
            vo.setTelephone(workTicket.getFollowUp().getTelephone());
            workTicketVo.setFollowUp(vo);
        }

        if (workTicket.getOrder() != null) {

            workTicketVo.setOrderId(workTicket.getOrder().getId());
        }

    }

    public QueryResponse<WorkTicketVo> findWorkTickets(WorkTicketListRequest request) {

        Page<WorkTicket> workTickets =  workTicketRepository.findAll(new WorkTicketSpecification(request), new PageRequest(request.getPage(), request.getPageSize()));
        List<WorkTicketVo> workTicketVos = new ArrayList<>();
        QueryResponse<WorkTicketVo> response = new QueryResponse<>();

        for (WorkTicket workTicket : workTickets.getContent()) {
            WorkTicketVo workTicketVo = new WorkTicketVo();
            copyProperties(workTicket, workTicketVo);
            workTicketVos.add(workTicketVo);
        }
        response.setContent(workTicketVos);
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(workTickets.getTotalElements());

        return response;
    }

    public QueryResponse<WorkTicketVo> findWorkTicketsByFollowUp(AdminUser followUp) {

        List<WorkTicket> workTickets = workTicketRepository.findByFollowUp(followUp);
        List<WorkTicketVo> workTicketVos = new ArrayList<>();
        QueryResponse<WorkTicketVo> response = new QueryResponse<>();
        for (WorkTicket workTicket : workTickets) {
            WorkTicketVo workTicketVo = new WorkTicketVo();
            copyProperties(workTicket, workTicketVo);
            workTicketVos.add(workTicketVo);
        }
        response.setTotal(workTickets.size());
        response.setContent(workTicketVos);

        return response;
    }

    private static class WorkTicketSpecification implements Specification<WorkTicket>{

        private final WorkTicketListRequest request;

        public WorkTicketSpecification(WorkTicketListRequest request) {
            this.request = request;
        }
        @Override
        public Predicate toPredicate(Root<WorkTicket> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

            List<Predicate> predicates = new ArrayList<>();
            query.orderBy(cb.desc(root.get(WorkTicket_.createTime)));
            if (request.getOrderId() != null) {

                predicates.add(cb.equal(root.get(WorkTicket_.order).get(Order_.id), request.getOrderId()));
            }
            if (request.getRestaurantId() != null) {

                predicates.add(cb.equal(root.get(WorkTicket_.restaurant).get(Restaurant_.id), request.getRestaurantId()));
            }
            if (StringUtils.isNotBlank(request.getConsultants())) {

                predicates.add(cb.like(root.get(WorkTicket_.consultants), "%" + request.getConsultants() + "%"));
            }
            if (StringUtils.isNotBlank(request.getRestaurantName())) {

                predicates.add(cb.like(root.get(WorkTicket_.restaurant).get(Restaurant_.name), "%" + request.getRestaurantName() + "%"));
            }
            if (StringUtils.isNotBlank(request.getConsultantsTelephone())) {

                predicates.add(cb.equal(root.get(WorkTicket_.consultantsTelephone), request.getConsultantsTelephone()   ));
            }
            if (request.getOrderId() != null) {

                predicates.add(cb.equal(root.get(WorkTicket_.order).get(Order_.id), request.getOrderId()));
            }
            if (request.getProcess() != null) {

                predicates.add(cb.equal(root.get(WorkTicket_.process), request.getProcess()));
            }
            if (request.getProblemSources() != null) {

                predicates.add(cb.equal(root.get(WorkTicket_.problemSources), request.getProblemSources()));
            }
            if (request.getStatus() != null) {

                predicates.add(cb.equal(root.get(WorkTicket_.status), request.getStatus()));
            }
            if (request.getStartDate() != null) {

                predicates.add(cb.greaterThanOrEqualTo(root.get(WorkTicket_.createTime), request.getStartDate()));
            }
            if (request.getEndDate() != null) {

                predicates.add(cb.lessThanOrEqualTo(root.get(WorkTicket_.createTime), request.getEndDate()));
            }
            if (request.getFollowUpId() != null) {

                predicates.add(cb.equal(root.get(WorkTicket_.followUp).get(AdminUser_.id), request.getFollowUpId()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }
    }
}
