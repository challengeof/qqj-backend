package com.mishu.cgwy.org.controller;

import com.mishu.cgwy.org.facade.OrgFacade;
import com.mishu.cgwy.org.vo.TeamVo;
import com.mishu.cgwy.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by wangguodong on 16/4/12.
 */
@Controller
public class OrgController {

    @Autowired
    private OrgFacade orgFacade;

    @RequestMapping(value = "/org/team/list", method = RequestMethod.GET)
    @ResponseBody
    public Response<TeamVo> adminRoles(TeamListRequest request) {
        return orgFacade.getTeamList(request);
    }
}
