package com.mishu.cgwy.utils.baidu.push;

import com.baidu.yun.push.exception.PushClientException;
import com.baidu.yun.push.exception.PushServerException;

/**
 * Created by linsen on 15/12/31.
 */
public class PushMain {

    public static void main(String[] args){
        PushBaiduUtil pushBaiduMain = new PushBaiduUtil();
        PushMsgModel model = new PushMsgModel();
        model.setChannelIds(new String[]{"3971561786671672831"});
        model.setDeviceType(PushMsgModel.DEVICE_TYPE_ANDROID);
        model.setTitle("今天在测试");
        model.setDescription("测试详情");
        /*try {
            pushBaiduMain.pushBatchMsg(model);
        } catch (PushClientException e) {
            e.printStackTrace();
        } catch (PushServerException e) {
            e.printStackTrace();
        }*/
    }
}
