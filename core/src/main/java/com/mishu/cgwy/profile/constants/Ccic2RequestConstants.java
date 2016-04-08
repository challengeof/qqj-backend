package com.mishu.cgwy.profile.constants;

import com.mishu.cgwy.profile.controller.caller.CallerOutCallRequest;
import com.mishu.cgwy.profile.controller.caller.CallerSendSmsRequest;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by king-ck on 2015/10/13.
 */
public enum Ccic2RequestConstants {

    HOST("1.ccic2.com"),
    OUT_CALL("/interface/PreviewOutcall"),
    SEND_SMS("/interface/sms/SendSms");


    private final String content;

    private Ccic2RequestConstants(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

//    public static String requestOutCall(CallerOutCallRequest outCallRequest) throws Exception {
//        Field[] fields = CallerOutCallRequest.class.getDeclaredFields();
//        List<String> params = new ArrayList<>();
//        for(Field field : fields){
//            field.setAccessible(true);
//            params.add(field.getName()+ "="+field.get(outCallRequest));
//        }
//        String param = StringUtils.join(params,"&");
//        return toRequestGet(Ccic2RequestConstants.OUT_CALL, param);
//    }

    public String request(Object paramObj) throws Exception {

        Field[] fields = paramObj.getClass().getDeclaredFields();
        List<String> params = new ArrayList<>();
        for(Field field : fields){
            field.setAccessible(true);
            params.add(field.getName()+ "="+field.get(paramObj));
        }
        String param = StringUtils.join(params,"&");
        return toRequestGet(this, param);

    }
    public static String toRequestGet(Ccic2RequestConstants url, String param) throws Exception {

        String urlstr= url.getContent()+"?" + param;
        URL localURL = new URL("http", Ccic2RequestConstants.HOST.getContent(),urlstr);
        URLConnection connection = localURL.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection)connection;

        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;

        if (httpURLConnection.getResponseCode() >= 300) {
            throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }

        try {
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }

        } finally {

            if (reader != null) {
                reader.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

        }
        return resultBuffer.toString();

    }


}
