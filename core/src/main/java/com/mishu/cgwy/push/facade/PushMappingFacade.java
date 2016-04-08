package com.mishu.cgwy.push.facade;

import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;
import com.baidu.yun.push.auth.PushKeyPair;
import com.baidu.yun.push.client.BaiduPushClient;
import com.baidu.yun.push.constants.BaiduPushConstants;
import com.baidu.yun.push.exception.PushClientException;
import com.baidu.yun.push.exception.PushServerException;
import com.baidu.yun.push.model.PushMsgToSingleDeviceRequest;
import com.baidu.yun.push.model.PushMsgToSingleDeviceResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.push.service.PushMappingService;
import com.mishu.cgwy.push.domain.*;
import lombok.extern.java.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by bowen on 15/9/23.
 */
@Service
@Log
public class PushMappingFacade {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PushMappingService pushMappingService;

    // 1. get apiKey and secretKey from developer console
    private String apiKey = null;

    private String secretKey = null;

    private Integer deviceType = 3;

    @Transactional
    public void pushNotificationDevice(Notification notification) throws PushClientException, PushServerException {

        for (Long customerId : notification.getCustomerIds()) {

            List<PushMapping> pushMappings = pushMappingService.findByCustomerId(customerId);

            if (!pushMappings.isEmpty()) {

                for (PushMapping pushMapping : pushMappings) {
                    notification.setBaiduChannelId(pushMapping.getBaiduChannelId());
                    if ("android".equals(pushMapping.getPlatform())) {

                        apiKey = "zdfuy34n1x9XWmmGQiuhgP3q";
                        secretKey = "04U65Fc5BLrwEu9ZXhNlVieCH1QV1oi8";
                        deviceType = 3;
                        pushNotification(notification);


                    } else if ("ios".equals(pushMapping.getPlatform())) {
                        apiKey = "QGi7yYY00oNPIe0Ug2gx1zZd";
                        secretKey = "Xn8yKmCCiv4bkCYZXziGAGNTUmpjdrjz";
                        deviceType = 4;
                        pushNotification(notification);


                    }else if ("weixin".equals(pushMapping.getPlatform())) {
                        Wx wx = new Wx();
                        String accessToken = wx.getAccessToken();
                        WxOAuth2Token wxOAuth2Token = new WxOAuth2Token();
                        wxOAuth2Token.setOpenId(pushMapping.getBaiduChannelId());
                        Text text = new Text();
                        text.setContent(notification.getDescription());
                        wxOAuth2Token.setContent(text);
                        wxOAuth2Token.setAccessToken(accessToken);
                        try {
                            wx.sendMessage(wxOAuth2Token);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }

    }

    public void pushNotification(Notification message) throws PushServerException, PushClientException {

        PushKeyPair pair = new PushKeyPair(apiKey, secretKey);

        // 2. build a BaidupushClient object to access released interfaces
        BaiduPushClient pushClient = new BaiduPushClient(pair,
                BaiduPushConstants.CHANNEL_REST_URL);

        // 3. register a YunLogHandler to get detail interacting information
        // in this request.
        pushClient.setChannelLogHandler(new YunLogHandler() {
            @Override
            public void onHandle(YunLogEvent event) {
                log.info(event.getMessage());
            }
        });

        try {
            // 4. specify request arguments
            JSONObject notification = new JSONObject();

            notification.put("title", message.getTitle());
            notification.put("description", message.getDescription());
            notification.put("sound", "ttt");
            notification.put("notification_builder_id", 0);
            notification.put("notification_basic_style", 4);
            if (message.getUrl() != null) {

                notification.put("open_type", 1);
                notification.put("url", message.getUrl());
            }


            PushMsgToSingleDeviceRequest request = new PushMsgToSingleDeviceRequest()
                    .addChannelId(message.getBaiduChannelId())
                    .addMsgExpires(86400*7). // message有效时间
                            addMessageType(1).// 1：通知,0:透传消息. 默认为0 注：IOS只有通知.
                            addMessage(notification.toString()).
                            addDeviceType(deviceType);// deviceType => 3:android, 4:ios
            // 5. http request
            PushMsgToSingleDeviceResponse response = pushClient
                    .pushMsgToSingleDevice(request);
            // Http请求结果解析打印
            log.info("msgId: " + response.getMsgId() + ",sendTime: "
                    + response.getSendTime());
        } catch (PushClientException e) {
            /*
             * ERROROPTTYPE 用于设置异常的处理方式 -- 抛出异常和捕获异常,'true' 表示抛出, 'false' 表示捕获。
			 */
            if (BaiduPushConstants.ERROROPTTYPE) {
                throw e;
            } else {
                e.printStackTrace();
            }
        } catch (PushServerException e) {
            if (BaiduPushConstants.ERROROPTTYPE) {
                throw e;
            } else {
                log.info(String.format(
                        "requestId: %d, errorCode: %d, errorMessage: %s",
                        e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }


    @Transactional
    public String getWxOAuth2Token(String code) throws IOException {

        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxfa5e3a091a9a2dbb&secret=ddb0abfb89d4e5ae3f90a7a1f7760c70&code=CODE&grant_type=authorization_code";

        url = url.replace("CODE", code);

        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = new DefaultHttpClient();

        HttpResponse execute = httpClient.execute(httpGet);
        String openId = null;
        if (execute.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK) {

            JsonNode jsonNode = objectMapper.readTree(EntityUtils.toString(execute.getEntity(), "utf-8"));
            openId = jsonNode.get("openid").asText();

        }
        return openId;
    }



}
