package com.mishu.cgwy.score.repository;

import com.mishu.cgwy.score.domain.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by bowen on 15/11/10.
 */
public interface ScoreRepository extends JpaRepository<Score, Long>, JpaSpecificationExecutor<Score> {

    @Modifying
    @Query("update Score s set s.exchangeScore = s.exchangeScore + :score, s.updateTime=now() where s.id = :id")
    public int increaseExchangeScore(@Param("id") Long id  ,@Param("score") Long score);

    @Modifying
    @Query("update Score s set s.totalScore = s.totalScore + :score, s.updateTime=now() where s.id = :id")
    public void increaseTotalScore(@Param("id") Long id  ,@Param("score") Long score);

}
