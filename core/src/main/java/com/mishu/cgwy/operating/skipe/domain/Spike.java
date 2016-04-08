package com.mishu.cgwy.operating.skipe.domain;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.operating.skipe.constant.SpikeState;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by king-ck on 2016/1/7.
 */
@Entity
@Data
public class Spike implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
//    @ManyToOne
//    @JoinColumn(name = "warehouse_id")
//    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "operater_id")
    private AdminUser operater;

    @OneToMany(mappedBy = "spike")
    private List<SpikeItem> items;

    @Column(length=2000)
    private String description;

    private Date beginTime;

    private Date endTime;

    private Date createTime;

    private Date lastModify; // 最后一次修改时间

    @ManyToOne
    @JoinColumn(name = "last_operater_id")
    private AdminUser lastModifyOperater; //最后修改人

    private int state= SpikeState.EFFECTIVE.getVal(); //状态

    @Override
    public String toString() {
        return "Spike{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", createTime=" + createTime +
                ", state=" + state +
                '}';
    }



}
