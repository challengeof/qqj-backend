package com.qqj.org.service;

import com.qqj.admin.domain.AdminUser_;
import com.qqj.org.controller.TeamListRequest;
import com.qqj.org.controller.TeamRequest;
import com.qqj.org.domain.Team;
import com.qqj.org.domain.Team_;
import com.qqj.org.repository.TeamRepository;
import com.qqj.org.wrapper.TeamWrapper;
import com.qqj.response.query.QueryResponse;
import com.qqj.utils.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public QueryResponse<TeamWrapper> getTeamList(final TeamListRequest request) {
        final PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize());

        Page<Team> page = teamRepository.findAll(new Specification<Team>() {
            @Override
            public Predicate toPredicate(Root<Team> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (request.getName() != null) {
                    predicates.add(cb.like(root.get(Team_.name), String.format("%%%s%%", request.getName())));
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

        QueryResponse<TeamWrapper> response = new QueryResponse<>();
        response.setContent(EntityUtils.toWrappers(page.getContent(), TeamWrapper.class));
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());

        return response;
    }

    @Transactional
    public void addTeam(TeamRequest request) {
        Team team = new Team();
        team.setName(request.getName());
        teamRepository.save(team);
    }

    public List<TeamWrapper> getAllTeams() {
        return EntityUtils.toWrappers(teamRepository.findAll(), TeamWrapper.class);
    }

    public Team getOne(Long id) {
        return teamRepository.getOne(id);
    }
}
