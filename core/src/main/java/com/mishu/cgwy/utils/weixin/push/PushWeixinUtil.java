package com.mishu.cgwy.utils.weixin.push;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.common.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linsen on 16/1/4.
 */
public class PushWeixinUtil {

    private static final String tokenURL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxfa5e3a091a9a2dbb&secret=ddb0abfb89d4e5ae3f90a7a1f7760c70";


    public static void main(String[] args){

        try{
            List openids = new ArrayList();
            openids.add("oLFPWs3qWn3zTV4IFL1azEivpz4Y");
            openids.add("oLFPWs1xX9GK4UDQPJAXxFTulG84");
            openids.add("oLFPWs2MkC7GIRCDmZXYhtoKT7L8");
            openids.add("oLFPWs54N6CTCAxSFPr9FBHvMDVo"); //我的
            openids.add("oLFPWs-QlyVr83UENBvzZeY0qlAA");

            PushWeixinUtil main = new PushWeixinUtil();
            main.pushWXPreview("oLFPWs54N6CTCAxSFPr9FBHvMDVo", "G7uGB32DrcEH97y_BgsEacfMuyqaQ1SC6R5MQ-dGYvI");
//            System.out.println(main.getMediaList());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getMediaList() throws IOException {
        String token = getToken();
        if(token == null) return null;

        String sendCustomerURL = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token="; //客服回复接口 上限5W
        HttpPost httpPost = new HttpPost(sendCustomerURL + token);
        String content = "{\"type\":\"news\" , \"offset\":\"0\" , \"count\" : \"5\"}";
        StringEntity entity = new StringEntity(content, "utf-8");
        httpPost.setEntity(entity);
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(httpPost);
        JSONObject jsonObject = JSONObject.fromObject(EntityUtils.toString(response.getEntity(), "utf-8"));
        JSONArray jsonArray = jsonObject.getJSONArray("item");


        if (jsonArray != null && jsonArray.size() > 0){
            JSONObject result = new JSONObject();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject itemObj = jsonArray.getJSONObject(i);
                String mediaId = itemObj.getString("media_id");
                String mediaTitle = itemObj.getJSONObject("content").getJSONArray("news_item").getJSONObject(0).getString("title");
                result.put(mediaId , mediaTitle);
            }
            return result.toString();
        }
        return null;
    }

    /**
     * 发送预览
     * */
    public boolean pushWXPreview(String openid, String mediaId) throws IOException{
        String token = getToken();
        if(token == null) return false;
        String sendContent = "{\"touser\":\""+openid+"\" , \"mpnews\":{\"media_id\":\""+mediaId+"\"}, \"msgtype\":\"mpnews\"}";
        String sendMsgURL = "https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=";
        HttpPost httpPost = new HttpPost(sendMsgURL + token);
        StringEntity entity = new StringEntity(sendContent, "utf-8");
        httpPost.setEntity(entity);

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(httpPost);
//        System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
        return true;
    }


    public boolean pushWXMessage(List openids , String mediaId) throws IOException {
        String token = getToken();
        if(token == null) return false;
        String sendContent = "{\"touser\":[USER_OPEN_ID] , \"mpnews\":{\"media_id\":\""+mediaId+"\"}, \"msgtype\":\"mpnews\"}";
        StringBuilder articlesArray = new StringBuilder();
        for (int i = 0; i < openids.size(); i++) {
            String openid = (String)openids.get(i);
            if (!StringUtils.isBlank(openid))
                articlesArray.append("\"").append(openid).append("\"");
            if (i != openids.size() - 1)
                articlesArray.append(",");
        }

        String sendMsgURL = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token="; //客服回复接口 上限5W
        HttpPost httpPost = new HttpPost(sendMsgURL + token);
        sendContent = sendContent.replace("USER_OPEN_ID", articlesArray.toString());
        StringEntity entity = new StringEntity(sendContent, "utf-8");
        httpPost.setEntity(entity);

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(httpPost);
//        System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
        return true;
    }

    /**
     * 获取token
     * */
    private String getToken() throws IOException{
        HttpGet httpGet = new HttpGet(tokenURL);
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse execute = httpClient.execute(httpGet);
        String token = null;
        ObjectMapper objectMapper = new ObjectMapper();
        if (execute.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK) {
            JsonNode jsonNode = objectMapper.readTree(EntityUtils.toString(execute.getEntity(), "utf-8"));
            token = jsonNode.get("access_token").asText();
            return token;
        } else {
            return null;
        }
    }
}
