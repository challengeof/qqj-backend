package com.mishu.cgwy.profile.security;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private TokenLoginService tokenLoginService;

    public static final String LOGIN_REQUEST_JSON = "loginRequestJson";

    private ObjectMapper mapper = new ObjectMapper();



    @Override
    protected String obtainPassword(HttpServletRequest request) {
        String password = null;

        // TODO
        if (request.getHeader("Content-Type") != null && request.getHeader("Content-Type").startsWith("application/json")) {
            final JsonNode jsonNode = (JsonNode) request.getAttribute(LOGIN_REQUEST_JSON);
            password = jsonNode.get(getPasswordParameter()).asText();

        } else {
            password = super.obtainPassword(request);
        }

        // 兼容历史代码规则
        final String username = obtainUsername(request);
        return CustomPasswordEncoder.createPassword(username,password);
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {

        // TODO
        if (request.getHeader("Content-Type") != null && request.getHeader("Content-Type").startsWith("application/json")) {
            return ((JsonNode) request.getAttribute(LOGIN_REQUEST_JSON)).get(getUsernameParameter()).asText();
        } else {
            return super.obtainUsername(request);
        }
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (request.getHeader("Content-Type") != null && request.getHeader("Content-Type").startsWith("application/json")) {
            try {
                JsonNode loginRequest = mapper.readValue(IOUtils.toString(request.getReader()), JsonNode.class);

                request.setAttribute(LOGIN_REQUEST_JSON, loginRequest);
            } catch (Exception e) {
                throw new AuthenticationServiceException("post body is not a valid json");
            }
        }
        if (checkToken(request)) {

            request.getSession().setAttribute("AdminUserId", getPassword(request));

            tokenLoginService.autoLogin(obtainUsername(request));

            return SecurityContextHolder.getContext().getAuthentication();

        }else{

            return super.attemptAuthentication(request, response);
        }

    }

    public Boolean checkToken(HttpServletRequest request) {

        String username = obtainUsername(request);
        String token = getPassword(request);
        try {

            final BoundHashOperations<String, String, Object> opss = redisTemplate
                    .boundHashOps(username);

            if (token.equals(String.valueOf(opss.get("token")))) {

                return Boolean.TRUE;

            } else {

                return Boolean.FALSE;
            }
        } catch (Exception e) {

            e.printStackTrace();

            return Boolean.FALSE;
        }
    }

    public String getPassword(HttpServletRequest request) {

        String password = null;

        // TODO
        if (request.getHeader("Content-Type") != null && request.getHeader("Content-Type").startsWith("application/json")) {
            final JsonNode jsonNode = (JsonNode) request.getAttribute(LOGIN_REQUEST_JSON);
            password = jsonNode.get(getPasswordParameter()).asText();

        } else {
            password = super.obtainPassword(request);
        }


        return password;
    }
}


