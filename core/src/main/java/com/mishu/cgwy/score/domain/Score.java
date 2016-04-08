package com.mishu.cgwy.score.domain;

import com.mishu.cgwy.profile.domain.Customer;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bowen on 15/11/10.
 */
@Entity
@Data
public class Score {

    @Id
    private Long id;

    private Long totalScore = 0L;

    private Long exchangeScore = 0L;

    @OneToOne()
    @MapsId
    @JoinColumn(name = "id")
    private Customer customer;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "score")
    private List<ScoreLog> scoreLogs = new ArrayList<>();

    public Long calculateAvailableScore() {

        return totalScore - exchangeScore < 0L ? 0L : totalScore - exchangeScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Score score = (Score) o;

        if (id != null ? !id.equals(score.id) : score.id != null) return false;
        if (totalScore != null ? !totalScore.equals(score.totalScore) : score.totalScore != null) return false;
        if (exchangeScore != null ? !exchangeScore.equals(score.exchangeScore) : score.exchangeScore != null)
            return false;
        if (createTime != null ? !createTime.equals(score.createTime) : score.createTime != null) return false;
        return !(updateTime != null ? !updateTime.equals(score.updateTime) : score.updateTime != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (totalScore != null ? totalScore.hashCode() : 0);
        result = 31 * result + (exchangeScore != null ? exchangeScore.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", totalScore=" + totalScore +
                ", exchangeScore=" + exchangeScore +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime + '\'' +
                '}';
    }
}
