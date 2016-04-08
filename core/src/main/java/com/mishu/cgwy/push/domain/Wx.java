package com.mishu.cgwy.push.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by bowen on 15/10/16.
 */

public class Wx {

    private String appId = "wxfa5e3a091a9a2dbb";

    private String secret = "ddb0abfb89d4e5ae3f90a7a1f7760c70";

    ObjectMapper objectMapper = new ObjectMapper();

    HttpClient httpClient = new DefaultHttpClient();

    public String getAccessToken()   {

        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential"
                + "&appid=" + appId
                + "&secret=" + secret;

        HttpGet httpGet = new HttpGet(url);


        HttpResponse execute = null;
        String access_token = null;
        try {
            execute = httpClient.execute(httpGet);

            if (execute.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK) {
                JsonNode jsonNode = objectMapper.readTree(EntityUtils.toString(execute.getEntity(), "utf-8"));
                access_token = jsonNode.get("access_token").asText();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_token;
    }


    public void sendMessage(WxOAuth2Token wxOAuth2Token) throws IOException {


        String urlMsg = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + wxOAuth2Token.getAccessToken();

        HttpPost httpPost = new HttpPost(urlMsg);


        WxMessage wxMessage = new WxMessage();
        //"oLFPWs6qUcZBP-hunLYR5N9whjgI"
        wxMessage.setTouser(wxOAuth2Token.getOpenId());

        wxMessage.setMsgtype("text");

        wxMessage.setText(wxOAuth2Token.getContent());


        httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(wxMessage),"UTF-8"));
        httpClient.execute(httpPost);
    }
}