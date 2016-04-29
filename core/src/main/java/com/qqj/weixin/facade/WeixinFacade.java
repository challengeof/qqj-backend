package com.qqj.weixin.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qqj.response.query.QueryResponse;
import com.qqj.response.query.WeixinUserStatisticsResponse;
import com.qqj.weixin.controller.WeixinUserListRequest;
import com.qqj.weixin.controller.WeixinUserRequest;
import com.qqj.weixin.domain.WeixinUser;
import com.qqj.weixin.enumeration.WeixinUserStatus;
import com.qqj.weixin.service.WeixinUserService;
import com.qqj.weixin.wrapper.WeixinUserWrapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Service
public class WeixinFacade {

    private static Logger logger = LoggerFactory.getLogger(WeixinFacade.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WeixinUserService weixinUserService;

    public QueryResponse<WeixinUserWrapper> getWeixinUserList(final WeixinUserListRequest request) {
        return weixinUserService.getWeixinUserList(request);
    }

    public void auditWeixinUser(Long id, Short status) {
        weixinUserService.auditWeixinUser(id, status);
    }

    public WeixinUserStatisticsResponse weixinUserStatistics() {
        return weixinUserService.weixinUserStatistics();
    }

    public void addWeixinUser(WeixinUserRequest request) throws Exception {
        String code = request.getCode();
        String openId = getWxOAuth2Token(code);
        WeixinUser weixinUser = new WeixinUser();
        weixinUser.setStatus(WeixinUserStatus.STATUS_0.getValue());
        weixinUser.setTelephone(request.getTelephone());
        weixinUser.setBirthday(request.getBirthday());
        weixinUser.setCreateTime(new Date());
        weixinUser.setName(request.getName());
        weixinUser.setOpenId(openId);
        logger.info("openId:" + openId);
        //        weixinUserService.addWeixinUser(request);
    }

    public String getWxOAuth2Token(String code) throws IOException {

        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx81aeb23b12ef998a&secret=8db5e50f9238893734f3343d297fbcd5&code=CODE&grant_type=authorization_code";

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