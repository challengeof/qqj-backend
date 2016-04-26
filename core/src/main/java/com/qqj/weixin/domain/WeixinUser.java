package com.qqj.weixin.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wangguodong on 16/4/26.
 */
@Entity
@Data
@Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
public class WeixinUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String openId;

    private String nickname;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "open_id", foreignKey = @ForeignKey(name = "f_open_id"))
    private List<WeixinPic> pics = new ArrayList<WeixinPic>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
}
