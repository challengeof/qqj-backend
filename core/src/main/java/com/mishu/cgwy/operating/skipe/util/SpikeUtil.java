package com.mishu.cgwy.operating.skipe.util;

import com.mishu.cgwy.operating.skipe.constant.SpikeActivityState;
import com.mishu.cgwy.operating.skipe.domain.Spike;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;

/**
 * Created by king-ck on 2016/4/5.
 */
public abstract class SpikeUtil {

    private static final int SPIKE_BEFORE_MINUTE_TIME=-1; // 1 分钟

    public static boolean checkCanModify(Spike spike){
        //活动开始 1分钟前可以修改
        if(spike==null ){
            return false;
        }

        SpikeActivityState spikeActivityState = SpikeActivityState.parseSpikeActivity(spike);
        if(spikeActivityState != SpikeActivityState.unStart){
            return false;
        }
        Date now =new Date();
        Date beforeDt = DateUtils.addMinutes(spike.getBeginTime(),SPIKE_BEFORE_MINUTE_TIME);
        return now.before(beforeDt);
    }

}
