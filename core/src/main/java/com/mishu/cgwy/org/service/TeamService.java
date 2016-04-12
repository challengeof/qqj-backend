package com.mishu.cgwy.org.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.org.controller.TeamListRequest;
import com.mishu.cgwy.org.domain.Team;
import com.mishu.cgwy.org.domain.Team_;
import com.mishu.cgwy.org.repository.TeamRepository;
import com.mishu.cgwy.org.vo.TeamVo;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public QueryResponse<TeamVo> getTeamList(final TeamListRequest request) {
        final PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize());

        Page<Team> page = teamRepository.findAll(new Specification<Team>() {
            @Override
            public Predicate toPredicate(Root<Team> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (request.getName() != null) {
                    predicates.add(cb.equal(root.get(Team_.name), request.getName()));
                }

                if (request.getFounder() != null) {
                    predicates.add(cb.like(root.get(Team_.founder).get(AdminUser_.realname), String.format("%%%s%%", request.getFounder())));
                }

                if (request.getTelephone() != null) {
                    predicates.add(cb.like(root.get(Team_.founder).get(AdminUser_.telephone), String.format("%%%s%%", request.getTelephone())));
                }

                query.orderBy(cb.desc(root.get(Team_.id)));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageRequest);

        List<TeamVo> teamVoList = new ArrayList<>();
        List<Team> teamList = page.getContent();

        for (Team team : teamList) {
            TeamVo teamVo = new TeamVo();
            teamVo.setName(team.getName());
            AdminUser founder = team.getFounder();
            teamVo.setFounder(founder.getRealname());
            teamVo.setTelephone(founder.getTelephone());
            teamVoList.add(teamVo);
        }

        QueryResponse<TeamVo> response = new QueryResponse<>();
        response.setContent(teamVoList);
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());

        return response;
    }

}
