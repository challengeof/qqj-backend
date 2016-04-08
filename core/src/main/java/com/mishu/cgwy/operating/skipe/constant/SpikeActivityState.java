package com.mishu.cgwy.operating.skipe.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mishu.cgwy.operating.skipe.domain.Spike;
import com.mishu.cgwy.operating.skipe.wrapper.SpikeWrapper;

import java.util.Date;

/**
 * Created by king-ck on 2016/1/12.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SpikeActivityState {
    invalid(0,SpikeState.INVALID,"已失效"){
        @Override
        public boolean checkActivity(SpikeState spikeState, Date beginTime, Date endTime) {
            return spikeState==this.spikeState;
        }
    }, unStart(1,SpikeState.EFFECTIVE,"未开始"){
        @Override
        public boolean checkActivity(SpikeState spikeState, Date beginTime, Date endTime) {
            return spikeState==this.spikeState && beginTime.getTime()>System.currentTimeMillis();
        }
    }  , process (2,SpikeState.EFFECTIVE,"进行中") {
        @Override
        public boolean checkActivity(SpikeState spikeState, Date beginTime, Date endTime) {
            long ctime =System.currentTimeMillis();
            return spikeState==this.spikeState  && beginTime.getTime()<= ctime  && ctime<endTime.getTime();
        }
    }, end (3,SpikeState.EFFECTIVE,"已结束") {
        @Override
        public boolean checkActivity(SpikeState spikeState, Date beginTime, Date endTime) {
            long ctime =System.currentTimeMillis();
            return spikeState==this.spikeState   && ctime>=endTime.getTime();
        }
    };

    public final int val;
    public final SpikeState spikeState;
    public final String desc;
    private SpikeActivityState(int val, SpikeState spikeState, String desc) {
        this.val = val;
        this.spikeState=spikeState;
        this.desc = desc;
    }
    public abstract boolean checkActivity(SpikeState spikeState, Date beginTime,Date endTime );


    public static SpikeActivityState parseSpikeActivity(Integer state, Date beginTime, Date endTime){
        SpikeState spikeState = SpikeState.fromInt(state);
        for(SpikeActivityState activityState : SpikeActivityState.values()){
            if(activityState.checkActivity(spikeState, beginTime, endTime)){
                return activityState;
            }
        }
        return null;
    }
    public static SpikeActivityState parseSpikeActivity(Spike spike){
        return parseSpikeActivity(spike.getState(),spike.getBeginTime(),spike.getEndTime());
    }

    public static SpikeActivityState parseSpikeActivity(SpikeWrapper spike){
       return parseSpikeActivity(spike.getState(), spike.getBeginTime(), spike.getEndTime());
    }

    public static boolean checkSpikeState(SpikeWrapper spike,SpikeActivityState... activityState){
        if(null != activityState){
            SpikeActivityState cstate = parseSpikeActivity(spike);

            for(SpikeActivityState sas : activityState){
                if(cstate==sas){
                    return true;
                }
            }
        }
        return false;
    }


}
