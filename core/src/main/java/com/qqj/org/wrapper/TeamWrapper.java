package com.qqj.org.wrapper;

import com.qqj.org.domain.Team;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

/**
 * Created by wangguodong on 16/4/12.
 */
@Setter
@Getter
public class TeamWrapper {

    private Long id;

    private String name;

    private String founder;

    private String telephone;

    public TeamWrapper(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.founder = team.getFounder() == null ? StringUtils.EMPTY : team.getFounder().getName();
        this.telephone = team.getFounder() == null ? StringUtils.EMPTY : team.getFounder().getTelephone();
    }
}
