package com.qqj.weixin.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qqj.qiniu.service.Uploader;
import com.qqj.response.query.QueryResponse;
import com.qqj.response.query.WeixinUserStatisticsResponse;
import com.qqj.weixin.controller.WeixinUserListRequest;
import com.qqj.weixin.controller.WeixinUserRequest;
import com.qqj.weixin.domain.WeixinPic;
import com.qqj.weixin.domain.WeixinUser;
import com.qqj.weixin.enumeration.WeixinPicType;
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
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

    @Transactional
    public void addWeixinUser(WeixinUserRequest request) throws Exception {

        String code = request.getCode();
        String[] accessTokenInfo = getWxOAuth2Token(code);
        String openId = accessTokenInfo[0];
//        String accessToken = accessTokenInfo[1];
        String accessToken = request.getAccessToken();

        WeixinUser weixinUser = new WeixinUser();
        weixinUser.setStatus(WeixinUserStatus.STATUS_0.getValue());
        weixinUser.setTelephone(request.getTelephone());
        weixinUser.setBirthday(request.getBirthday());
        weixinUser.setCreateTime(new Date());
        weixinUser.setName(request.getName());
        weixinUser.setOpenId(openId);

        String[] serverIds = request.getServerIds();

        WeixinPic weixinPic1 = new WeixinPic();
        weixinPic1.setUser(weixinUser);
        weixinPic1.setCreateTime(new Date());
        weixinPic1.setType(WeixinPicType.Type_1.getValue());
        weixinPic1.setQiNiuHash(getQiNiuHash(serverIds[0], accessToken, openId, WeixinPicType.Type_1.getValue()));
        weixinUser.getPics().add(weixinPic1);

        WeixinPic weixinPic2 = new WeixinPic();
        weixinPic2.setUser(weixinUser);
        weixinPic2.setCreateTime(new Date());
        weixinPic2.setType(WeixinPicType.Type_2.getValue());
        weixinPic2.setQiNiuHash(getQiNiuHash(serverIds[1], accessToken, openId, WeixinPicType.Type_2.getValue()));
        weixinUser.getPics().add(weixinPic2);

        weixinUserService.addWeixinUser(weixinUser);
    }

    private String getQiNiuHash(String serverId, String accessToken, String openId, Short type) throws Exception {
        logger.info(String.format("serverId:%s,accessToken:%s", serverId, accessToken));
        InputStream is = null;
        FileOutputStream os = null;
        String fileName = String.format("%s_%s.jpg", openId, type);

        try {
            String url = String.format("http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s", accessToken, serverId);
            HttpURLConnection http = (HttpURLConnection)new URL(url).openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlenco");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.connect();
            is = http.getInputStream();

            byte[] data = new byte[1024];
            int len = 0;

            os = new FileOutputStream(fileName);

            while ((len = is.read(data)) != -1) {
                os.write(data, 0, len);
            }
        } finally {
            is.close();
            os.close();
        }

        File file = new File(fileName);
        Uploader uploader = new Uploader(file.getAbsolutePath(), fileName);
        return uploader.upload();
    }

    public String[] getWxOAuth2Token(String code) throws IOException {

        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx81aeb23b12ef998a&secret=8db5e50f9238893734f3343d297fbcd5&code=CODE&grant_type=authorization_code";

        url = url.replace("CODE", code);

        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = new DefaultHttpClient();

        HttpResponse execute = httpClient.execute(httpGet);
        String openId = null;
        String accessToken = null;
        if (execute.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK) {

            String result = EntityUtils.toString(execute.getEntity(), "utf-8");
            logger.info("oauth2/access_token:" + result);
            JsonNode jsonNode = objectMapper.readTree(result);
            openId = jsonNode.get("openid").asText();
            accessToken = jsonNode.get("access_token").asText();
        }
        return new String[]{openId, accessToken};
    }

    public WeixinUserWrapper getWeixinUser(Long id) {
        return weixinUserService.getWeixinUser(id);
    }
}