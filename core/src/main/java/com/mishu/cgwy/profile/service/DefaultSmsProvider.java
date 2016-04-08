package com.mishu.cgwy.profile.service;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * User: xudong
 * Date: 3/2/15
 * Time: 4:05 PM
 */
@Component
public class DefaultSmsProvider implements ISmsProvider {
    private RestTemplate restTemplate = new RestTemplate();

    private String sdk = "15321415905";
    private String code = "ms888888";
    private String subcode = "2739";
    private String baseUrl = "http://www.4001185185.com/sdk/smssdk!mt.action";

    @Override
    public boolean send(String message, String... telephone) {
        MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();

        request.add("sdk", sdk);
        request.add("code", code);
        request.add("phones", StringUtils.join(telephone, ","));
        request.add("msg", message);
        request.add("subcode", subcode);

        restTemplate.postForEntity(baseUrl, request, String.class);


        return true;
    }
}
