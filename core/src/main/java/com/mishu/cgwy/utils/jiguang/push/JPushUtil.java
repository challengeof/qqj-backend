package com.mishu.cgwy.utils.jiguang.push;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.Notification;

/**
 * Created by linsen on 16/1/19.
 */
public class JPushUtil {

    private final static String API_KEY = "9d72265a3b936e4b2164d8a8";
    private final static String SECRE_KEY = "bb3273e2c1bef612bd4cb60a";

    public PushResult pushByTags (JPushMsgModel model){
        JPushClient jpushClient = new JPushClient(SECRE_KEY, API_KEY);

        PushPayload.Builder builder = PushPayload.newBuilder();
        builder.setPlatform(Platform.android_ios());
        builder.setAudience(Audience.newBuilder().addAudienceTarget(AudienceTarget.tag(model.getTags())).build());
        builder.setNotification(Notification.alert(model.getMsg()));
        PushPayload payload = builder.build();

        try {
            PushResult result = jpushClient.sendPush(payload);
            return result;
        } catch (APIConnectionException e) {
            //Connection error, should retry later
            return null;
        } catch (APIRequestException e) {
            //Should review the error, and fix the request
            //e.getStatus() , e.getErrorCode() , e.getErrorMessage()
            return null;
        }
    }

    public PushResult pushToAll (JPushMsgModel model){
        JPushClient jpushClient = new JPushClient(SECRE_KEY, API_KEY);

        PushPayload payload = PushPayload.alertAll(model.getMsg());
        try {
            PushResult result = jpushClient.sendPush(payload);
            return result;
        } catch (APIConnectionException e) {
            //Connection error, should retry later
            return null;
        } catch (APIRequestException e) {
            //Should review the error, and fix the request
            //e.getStatus() , e.getErrorCode() , e.getErrorMessage()
            return null;
        }
    }
}
