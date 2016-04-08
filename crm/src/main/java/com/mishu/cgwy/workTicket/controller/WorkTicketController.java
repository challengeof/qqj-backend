package com.mishu.cgwy.workTicket.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.workTicket.constants.ProblemSources;
import com.mishu.cgwy.workTicket.constants.Process;
import com.mishu.cgwy.workTicket.service.WorkTicketService;
import com.mishu.cgwy.workTicket.vo.WorkTicketVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by bowen on 16/2/29.
 */
@Controller
@RequestMapping(value = "/api/work-ticket")
public class WorkTicketController {

    @Autowired
    private WorkTicketService workTicketService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public void createWorkTicket(@CurrentAdminUser AdminUser operator, @RequestBody WorkTicketRequest request) {

        workTicketService.createWorkTicket(operator, request);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void updateWorkTicket(@CurrentAdminUser AdminUser operator,@PathVariable("id") Long id, @RequestBody WorkTicketRequest request) {

        workTicketService.updateWorkTicket(operator,id, request);
    }

    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    @ResponseBody
    public WorkTicketVo getWorkTicket(@PathVariable("id") Long id) {

        return workTicketService.getWorkTicket(id);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<WorkTicketVo> findWorkTickets(WorkTicketListRequest request) {

        return workTicketService.findWorkTickets(request);
    }

    @RequestMapping(value = "/myTasks", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<WorkTicketVo> findWorkTicketsByFollowUp(@CurrentAdminUser AdminUser followUp) {

        return workTicketService.findWorkTicketsByFollowUp(followUp);
    }

    @RequestMapping(value = "/problemSources", method = RequestMethod.GET)
    @ResponseBody
    public List<ProblemSources> listProblemSources() {

        final ProblemSources[] values = ProblemSources.values();
        final List<ProblemSources> problemSources = new ArrayList<>(Arrays.asList(values));
        return problemSources;
    }

    @RequestMapping(value = "/process", method = RequestMethod.GET)
    @ResponseBody
    public List<Process> listProcess() {

        final Process[] values = Process.values();
        final List<Process> processes = new ArrayList<>(Arrays.asList(values));
        return processes;

    }
}
