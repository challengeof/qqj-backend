package com.qqj.weixin.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qqj.qiniu.service.Uploader;
import com.qqj.response.query.QueryResponse;
import com.qqj.response.query.WeixinUserStatisticsResponse;
import com.qqj.utils.WeChatSystemContext;
import com.qqj.weixin.controller.UploadPicResponse;
import com.qqj.weixin.controller.WeixinPicRequest;
import com.qqj.weixin.controller.WeixinUserListRequest;
import com.qqj.weixin.controller.WeixinUserRequest;
import com.qqj.weixin.domain.WeixinPic;
import com.qqj.weixin.domain.WeixinUser;
import com.qqj.weixin.enumeration.WeixinUserStatus;
import com.qqj.weixin.service.WeixinUserService;
import com.qqj.weixin.wrapper.WeixinPicWrapper;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class WeixinFacade {

    private static Logger logger = LoggerFactory.getLogger(WeixinFacade.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static DateFormat df = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    private WeixinUserService weixinUserService;

    private static String appId = "wx81aeb23b12ef998a";

    private static String secret = "8db5e50f9238893734f3343d297fbcd5";

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

        String openId = request.getOpenId();

        WeixinUser weixinUser = weixinUserService.findWeixinUserByOpenId(openId);
        weixinUser.setStatus(WeixinUserStatus.STATUS_0.getValue());
        weixinUser.setTelephone(request.getTelephone());

        String userId = request.getUserId();
        String birthDay = userId.substring(6, 14);
        weixinUser.setBirthday(df.parse(birthDay));
        weixinUser.setCreateTime(new Date());
        weixinUser.setName(request.getName());
        weixinUser.setOpenId(openId);
        weixinUser.setHeight(request.getHeight());
        weixinUser.setCity(request.getCity());
        weixinUser.setWechat(request.getWechat());
        weixinUser.setBlog(request.getBlog());
        weixinUser.setUserId(userId);

        weixinUserService.saveWeixinUser(weixinUser);
    }

    @Transactional
    public UploadPicResponse uploadPic(WeixinPicRequest request) throws Exception {

        String openId = request.getOpenId();
        String accessToken = WeChatSystemContext.getInstance().getAccessToken(appId, secret);

        WeixinUser weixinUser = weixinUserService.findWeixinUserByOpenId(openId);
        if (weixinUser == null) {
            weixinUser = new WeixinUser();
            weixinUser.setOpenId(openId);
            weixinUser.setStatus(WeixinUserStatus.STATUS_TMP.getValue());
        }

        WeixinPic weixinPic = new WeixinPic();
        weixinPic.setUser(weixinUser);
        weixinPic.setCreateTime(new Date());
        weixinPic.setType(request.getType());
        String key = getQiNiuHash(request.getServerId(), accessToken, openId, request.getType());
        weixinPic.setQiNiuHash(key);
        weixinUser.getPics().add(weixinPic);

        weixinUserService.saveWeixinUser(weixinUser);

        UploadPicResponse res = new UploadPicResponse();
        res.setUrl(String.format("%s%s?%s&%s", WeixinPicWrapper.default7NiuDomain, key, "imageView2/0/h/100/format/png", "v=" + System.currentTimeMillis()));

        return res;
    }

    private String getQiNiuHash(String serverId, String accessToken, String openId, Short type) throws Exception {
        logger.info(String.format("serverId:%s,accessToken:%s", serverId, accessToken));
        InputStream is = null;
        FileOutputStream os = null;
        String fileName = String.format("%s_%s.jpg", openId, type);

        try {
            String url = String.format("https://api.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s", accessToken, serverId);

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
            logger.info("bowen:" + os.toString());
            while ((len = is.read(data)) != -1) {
                os.write(data, 0, len);
            }
        } finally {
            is.close();
            os.close();
        }

        File file = new File(fileName);
        Uploader uploader = new Uploader(file.getAbsolutePath(), fileName);
        String key = uploader.upload();
        file.delete();
        return key;
    }

    public String getWxOAuth2Token(String code) throws IOException {

        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx81aeb23b12ef998a&secret=8db5e50f9238893734f3343d297fbcd5&code=CODE&grant_type=authorization_code";

        url = url.replace("CODE", code);

        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = new DefaultHttpClient();

        HttpResponse execute = httpClient.execute(httpGet);
        String openId = null;
        if (execute.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK) {

            String result = EntityUtils.toString(execute.getEntity(), "utf-8");
            logger.info("oauth2/access_token:" + result);
            JsonNode jsonNode = objectMapper.readTree(result);
            openId = jsonNode.get("openid").asText();
        }
        return openId;
    }

    public WeixinUserWrapper getWeixinUser(String openId) {
        return new WeixinUserWrapper(weixinUserService.findWeixinUserByOpenId(openId));
    }

    public WeixinUserWrapper getWeixinUserOpenId(String code) throws Exception {
        WeixinUserWrapper wrapper = new WeixinUserWrapper();
        wrapper.setOpenId(getWxOAuth2Token(code));
        return wrapper;
    }

    public WeixinUserWrapper getWeixinUserStatus(String openId) {
        WeixinUser weixinUser = weixinUserService.findWeixinUserByOpenId(openId);
        return new WeixinUserWrapper(weixinUser);
    }
}