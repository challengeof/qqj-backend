package com.qqj.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created by bowen on 16/5/1.
 */
@Getter
@Setter
public class WeChatSystemContext {

    private static Logger logger = LoggerFactory.getLogger(WeChatSystemContext.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    private String accessToken;//接口访问凭据

    private long createTime;//接口访问凭据创建时间，理论上是2小时后过期

    static class WeChatSystemContextHolder {
        static WeChatSystemContext instance = new WeChatSystemContext();
    }


    public static WeChatSystemContext getInstance() {
        return WeChatSystemContextHolder.instance;
    }

    //是否过期
    public boolean isExpired() {

        long time = new Date().getTime();

        //如果当前记录时间为0

        if (this.createTime <= 0) {
            return true;
        }

        //判断记录时间是否超过7200s
        if (this.createTime / 1000 + 7200 < time / 1000) {
            return true;
        }

        return false;
    }


    //记录接口访问凭证

    public void saveLocalAccessToken(String accessToken) {
        this.accessToken = accessToken;
        this.createTime = new Date().getTime();
    }

    public synchronized String getAccessToken(String appId, String secret) {
        if (this.accessToken == null || isExpired()) {
            String accessToken =  fetchAccessToken(appId, secret);
            saveLocalAccessToken(accessToken);
        }

        return accessToken;
    }

    public String fetchAccessToken(String appId, String secret)   {

        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential"
                + "&appid=" + appId
                + "&secret=" + secret;

        HttpGet httpGet = new HttpGet(url);

        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse execute = null;
        String access_token = null;
        try {
            execute = httpClient.execute(httpGet);

            if (execute.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK) {
                JsonNode jsonNode = objectMapper.readTree(org.apache.http.util.EntityUtils.toString(execute.getEntity(), "utf-8"));
                access_token = jsonNode.get("access_token").asText();
                logger.info("fanfan:" + access_token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_token;
    }
}
