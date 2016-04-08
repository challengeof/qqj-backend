package com.mishu.cgwy.utils.jiguang.push;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.Notification;

/**
 * Created by linsen on 16/1/19.
 */
public class JPushMain {

    public static void main(String[] args){
        JPushUtil jUtil = new JPushUtil();
        JPushMsgModel model = new JPushMsgModel();
        model.setMsg("推送消息");
        model.setTags(new String[]{"成都"});
//        System.out.println(jUtil.pushByTags(model));
        System.out.println(jUtil.pushToAll(model));
    }
}
