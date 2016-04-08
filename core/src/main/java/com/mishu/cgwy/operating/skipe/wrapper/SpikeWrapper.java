package com.mishu.cgwy.operating.skipe.wrapper;

import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.common.wrapper.CityWrapper;
import com.mishu.cgwy.operating.skipe.constant.SpikeActivityState;
import com.mishu.cgwy.operating.skipe.domain.Spike;
import com.mishu.cgwy.operating.skipe.domain.SpikeItem;
import com.mishu.cgwy.operating.skipe.util.SpikeUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by king-ck on 2016/1/7.
 */
@Data
public class SpikeWrapper {
    private Long id;          //秒杀活动id
    private CityWrapper city;  //所在城市
//    private WarehouseWrapper warehouse;
    private AdminUserVo operater;
    private String description; //秒杀活动描述
    private Date beginTime;  // 开始时间
    private Date endTime;    // 结束时间
    private Date createTime;
    private Date lastModify; //最后修改时间
    private AdminUserVo lastModifyOperater; // 最后修改人
    private Integer state;   //活动人工状态  1 生效  0 失效

    private Integer activeState; // 存活状态 0 失效 1 未开始 2 进行中 3 已结束
    private List<SpikeItemWrapper> spikeItems=new ArrayList<>();


    private boolean canModify; //是否可以修改

    public static List<SpikeWrapper> toWrappers(List<Spike> spikes){
        List lt =new ArrayList();
        for(Spike spike : spikes){
            lt.add(new SpikeWrapper(spike));
        }
        return lt;
    }

    public SpikeWrapper() {}

    public SpikeWrapper(Spike spike , List<SpikeItem> items) {
        this(spike);
        if(items!=null){
            for(SpikeItem sitem : items) {
                spikeItems.add(new SpikeItemWrapper(sitem,SpikeActivityState.parseSpikeActivity(spike)));
            }
        }
    }

    public SpikeWrapper(Spike spike ) {

        this.id=spike.getId();
        if(spike.getCity()!=null){
            this.city=new CityWrapper(spike.getCity());
        }
        if(spike.getOperater()!=null){
            this.operater=new AdminUserVo();
            this.operater.setId(spike.getOperater().getId());
            this.operater.setUsername(spike.getOperater().getUsername());
            this.operater.setRealname(spike.getOperater().getRealname());
        }
        if(spike.getLastModifyOperater()!=null){
            this.lastModifyOperater=new AdminUserVo();
            this.lastModifyOperater.setId(spike.getOperater().getId());
            this.lastModifyOperater.setUsername(spike.getOperater().getUsername());
            this.lastModifyOperater.setRealname(spike.getOperater().getRealname());
        }

        this.description=spike.getDescription();
        this.beginTime=spike.getBeginTime();
        this.endTime=spike.getEndTime();
        this.createTime=spike.getCreateTime();
        this.lastModify=spike.getLastModify();
        this.state=spike.getState();
        this.canModify = SpikeUtil.checkCanModify(spike);

        SpikeActivityState acState= SpikeActivityState.parseSpikeActivity(this.state, this.beginTime, this.endTime);
        if(acState!=null){
            this.activeState=acState.val;
        }
    }
}
