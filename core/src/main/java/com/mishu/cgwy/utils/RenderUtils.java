package com.mishu.cgwy.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * copy from springside
 *
 * @author wuxudong
 */
public class RenderUtils {

    // -- header const --//
    private static final String HEADER_ENCODING = "encoding";

    private static final String HEADER_NOCACHE = "no-cache";

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final boolean DEFAULT_NOCACHE = true;

    private static ObjectMapper mapper = new ObjectMapper();

    // -- 绕过jsp/freemaker直接输出文本的函数 --//

    /**
     * 直接输出内容的简便函数. eg. render("text/plain", "hello", "encoding:GBK");
     * render("text/plain", "hello", "no-cache:false"); render("text/plain",
     * "hello", "encoding:GBK", "no-cache:false");
     *
     * @param headers 可变的header数组，目前接受的值为"encoding:"或"no-cache:",默认值分别为UTF-8和true.
     */
    public static void render(HttpServletResponse response,
                              final String contentType, final String content,
                              final String... headers) {
        initResponseHeader(response, contentType, headers);
        try {
            response.getWriter().write(content);
            response.getWriter().flush();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 直接输出JSON,使用Jackson转换Java对象.
     *
     * @param data 可以是List<POJO>, POJO[], POJO, 也可以Map名值对.
     */
    public static void renderJson(HttpServletResponse response,
                                  final Object data) {
        initResponseHeader(response, ServletUtils.JSON_TYPE);
        try {
            String jsonString = mapper.writeValueAsString(data);
            response.getWriter().write(jsonString);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }


    /**
     * 直接输出支持跨域Mashup的JSONP.
     *
     * @param callbackName callback函数名.
     * @param object       Java对象,可以是List<POJO>, POJO[], POJO ,也可以Map名值对, 将被转化为json字符串.
     */
    public static void renderJsonp(HttpServletResponse response,
                                   final String callbackName, final Object object) {
        try {
            String jsonString = null;
            jsonString = mapper.writeValueAsString(object);

            String result = new StringBuilder().append(callbackName).append("(")
                    .append(jsonString).append(");").toString();

            // 渲染Content-Type为javascript的返回内容,输出结果为javascript语句,
            // 如callback197("{html:'Hello World!!!'}");
            render(response, ServletUtils.JS_TYPE, result);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 分析并设置contentType与headers.
     */
    private static HttpServletResponse initResponseHeader(
            HttpServletResponse response, final String contentType,
            final String... headers) {
        // 分析headers参数
        String encoding = DEFAULT_ENCODING;
        boolean noCache = DEFAULT_NOCACHE;
        for (String header : headers) {
            String headerName = StringUtils.substringBefore(header, ":");
            String headerValue = StringUtils.substringAfter(header, ":");

            if (StringUtils.equalsIgnoreCase(headerName, HEADER_ENCODING)) {
                encoding = headerValue;
            } else if (StringUtils.equalsIgnoreCase(headerName, HEADER_NOCACHE)) {
                noCache = Boolean.parseBoolean(headerValue);
            } else {
                throw new IllegalArgumentException(headerName
                        + "不是一个合法的header类型");
            }
        }

        // 设置headers参数
        String fullContentType = contentType + ";charset=" + encoding;
        response.setContentType(fullContentType);
        if (noCache) {
            ServletUtils.setDisableCacheHeader(response);
        }

        return response;
    }
}
