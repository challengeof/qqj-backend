package com.mishu.cgwy.profile.security;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by king-ck on 2015/10/9.
 */
public class TokenLoginAuthenticationfilter extends GenericFilterBean {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private TokenLoginService tokenLoginService;

    private String usertokenParam="_user";
    private String tokenSplit="$";
    private List<String> validRefererHost =new ArrayList<>();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String usertoken = request.getParameter(this.getUsertokenParam());

        boolean checkResult =this.volidCheck(usertoken,request);
        if(checkResult){
            UsernamePwd upwd=this.parseToken(usertoken);
            String password = CustomPasswordEncoder.createPassword(upwd.username,upwd.password);
            boolean loginResult = tokenLoginService.autoLogin( upwd.username, password,request);

            if(!loginResult){
                logger.error(String.format("token登陆失败 %s", usertoken));
            }
        }

        chain.doFilter(req, res);
    }

    public boolean volidCheck(String token, HttpServletRequest request) {
        String referer =  request.getHeader("Referer");
//      String referer="http://1.ccic2.com/jws/new/index.html";
        if(StringUtils.isBlank(token) || StringUtils.isBlank(referer) || validRefererHost.size()==0  ){
            return false;
        }
        UsernamePwd upwd = parseToken(token);
        if( upwd==null ){
            return false;
        }
        try {
            URL url = new URL(referer);
            String refererhost = url.getHost();
            if( validRefererHost.contains(refererhost)  ){
                return true;
            }else{
                logger.info(String.format("skip, token referer is %s  ,",referer));
            }
        } catch (Exception e) {
            logger.error("token login error",e);
        }
        return false;
    }

    private UsernamePwd parseToken(String token){
        String[] userInfo =StringUtils.split(token,this.getTokenSplit());

        if(userInfo.length<2){
            return null;
        }
        UsernamePwd upwd=new UsernamePwd();
        upwd.username=userInfo[0];
        upwd.password=userInfo[1];
        return upwd;
    }

    private static class UsernamePwd{
        public String username;
        public String password;
    }

    public TokenLoginService getTokenLoginService() {
        return tokenLoginService;
    }

    public void setTokenLoginService(TokenLoginService tokenLoginService) {
        this.tokenLoginService = tokenLoginService;
    }

    public String getUsertokenParam() {
        return usertokenParam;
    }

    public void setUsertokenParam(String usertokenParam) {
        this.usertokenParam = usertokenParam;
    }

    public String getTokenSplit() {
        return tokenSplit;
    }

    public void setTokenSplit(String tokenSplit) {
        this.tokenSplit = tokenSplit;
    }

    public List<String> getValidRefererHost() {
        return validRefererHost;
    }

    public void setValidRefererHost(List<String> validRefererHost) {
        this.validRefererHost = validRefererHost;
    }
}
