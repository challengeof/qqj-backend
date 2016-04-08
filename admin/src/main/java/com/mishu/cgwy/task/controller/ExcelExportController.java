package com.mishu.cgwy.task.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.task.facade.TaskFacade;
import com.mishu.cgwy.task.vo.TaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class ExcelExportController {

    @Autowired
    private TaskFacade taskFacade;

    @RequestMapping(value = "/api/task/excel/myTaskList", method = RequestMethod.GET)
    @ResponseBody
    public List<TaskVo> myTaskList(@CurrentAdminUser AdminUser adminUser) throws Exception {
        return taskFacade.getTaskList(adminUser);
    }

    @RequestMapping(value = "/api/task/excel/download", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]>  download(@RequestParam("taskId") Long taskId) throws Exception {
        return taskFacade.download(taskId);
    }
}
