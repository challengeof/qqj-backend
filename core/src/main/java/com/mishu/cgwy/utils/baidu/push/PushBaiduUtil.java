package com.mishu.cgwy.utils.baidu.push;

import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;
import com.baidu.yun.push.auth.PushKeyPair;
import com.baidu.yun.push.client.BaiduPushClient;
import com.baidu.yun.push.constants.BaiduPushConstants;
import com.baidu.yun.push.exception.PushClientException;
import com.baidu.yun.push.exception.PushServerException;
import com.baidu.yun.push.model.*;

/**
 * Created by linsen on 16/1/7.
 */
public class PushBaiduUtil {
    private final static String API_KEY = "zdfuy34n1x9XWmmGQiuhgP3q";
    private final static String SECRE_KEY = "04U65Fc5BLrwEu9ZXhNlVieCH1QV1oi8";

    public PushKeyPair pair = null;
    BaiduPushClient pushClient = null;

    public PushBaiduUtil(){
        pair = new PushKeyPair(API_KEY , SECRE_KEY);
        PushMsgModel model = new PushMsgModel();

        pushClient = new BaiduPushClient(pair, BaiduPushConstants.CHANNEL_REST_URL);
        pushClient.setChannelLogHandler(new YunLogHandler() {
            @Override
            public void onHandle(YunLogEvent event) {
                System.out.println(event.getMessage());
            }
        });

    }


    public void pushBatchMsgByAndroid(PushMsgModel pushModel) throws PushClientException, PushServerException {
        PushBatchUniMsgRequest request = new PushBatchUniMsgRequest();
        request.addChannelIds(pushModel.getChannelIds());
        request.addDeviceType(pushModel.getDeviceType());
        request.addMessage("{\"title\":\"" + pushModel.getTitle() + "\" ," + "\"description\":\"" + pushModel.getDescription() + "\"}");
        request.addTopicId("BaiduPush");
        request.addMsgExpires(new Integer(3600 * 5));
        request.setMessageType(1); //0：透传消息， 1：通知，默认值为0
        PushBatchUniMsgResponse response = pushClient.pushBatchUniMsg(request);
    }


    public void pushBatchMsgByIos(PushMsgModel pushModel) throws PushClientException,PushServerException{
        String message = "{\"title\":\"" + pushModel.getTitle() + "\" ," + "\"description\":\"" + pushModel.getDescription() + "\"}";
        String[] channelArray = pushModel.getChannelIds();
        for(String channelId : channelArray) {
            try {
                PushMsgToSingleDeviceRequest request = new PushMsgToSingleDeviceRequest();
                request.addChannelId(channelId);
                request.addMsgExpires(new Integer(3600 * 5));
                request.setMessageType(1);
                request.addMessage(message);
                request.addDeviceType(PushMsgModel.DEVICE_TYPE_IOS); //设置设备类型，deviceType => 1 for web, 2 for pc,3 for android, 4 for ios, 5 for wp.
                PushMsgToSingleDeviceResponse response = pushClient.pushMsgToSingleDevice(request);
            } catch (PushClientException e) {
                if (BaiduPushConstants.ERROROPTTYPE) {
                    throw e;
                } else {
                    e.printStackTrace();
                }
            } catch (PushServerException e) {
                if (BaiduPushConstants.ERROROPTTYPE) {
                    throw e;
                } else {
                    //System.out.println(String.format("requestId: %d, errorCode: %d, errorMsg: %s",e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
                }
            }
        }
    }



    public void pushMsgToAll(PushMsgModel pushMsgModel) throws PushClientException,PushServerException {
        PushMsgToAllRequest request = new PushMsgToAllRequest();
        request.setMsgExpires(new Integer(3600 * 5));
        request.setMessageType(1);
        request.setDeviceType(pushMsgModel.getDeviceType());

        if(pushMsgModel.getDeviceType() == PushMsgModel.DEVICE_TYPE_IOS){
            request.setDeployStatus(2); //1:IOS开发状态 |2:生产状态
        }
        // 5. http request
        PushMsgToAllResponse response = pushClient.pushMsgToAll(request);
        // Http请求返回值解析
//        System.out.println("msgId: " + response.getMsgId() + ",sendTime: " + response.getSendTime() + ",timerId: " + response.getTimerId());
    }
}
