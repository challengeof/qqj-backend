package com.qqj.org.facade;

import com.qqj.org.controller.TeamListRequest;
import com.qqj.org.service.TeamService;
import com.qqj.org.vo.TeamVo;
import com.qqj.response.Response;
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
