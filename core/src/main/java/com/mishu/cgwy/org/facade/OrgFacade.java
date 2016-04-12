package com.mishu.cgwy.org.facade;

import com.mishu.cgwy.org.controller.TeamListRequest;
import com.mishu.cgwy.org.service.TeamService;
import com.mishu.cgwy.org.vo.TeamVo;
import com.mishu.cgwy.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrgFacade {

    @Autowired
    private TeamService teamService;

    @Transactional(readOnly = true)
    public Response<TeamVo> getTeamList(TeamListRequest request) {
        return teamService.getTeamList(request);
    }
}
