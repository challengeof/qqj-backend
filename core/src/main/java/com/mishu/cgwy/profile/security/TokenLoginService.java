package com.mishu.cgwy.profile.security;

/**
 * Created by king-ck on 2015/10/9.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;

public class TokenLoginService {
    private static Logger logger = LoggerFactory.getLogger(TokenLoginService.class);
    private UserDetailsService userDetailsService;
    private AuthenticationManager authenticationManager;

    public boolean autoLogin(Authentication auth){
        try {
            if (auth!=null && auth.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(auth);
                return true;
            } else {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }catch(AuthenticationException ex){
            logger.error("Tokenlogin error ", ex);
        }
        return false;
    }

    public  boolean autoLogin(String loginname, String password, HttpServletRequest request){
        Authentication auth=getAthTokenByUsernamePwd(loginname, password, request);
        return autoLogin(auth);
    }

    public  boolean autoLogin(String username){
        Authentication auth = getAthTokenByUsername(username);
        return autoLogin(auth);
    }

    private  Authentication  getAthTokenByUsername(String loginname){
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginname);
        UsernamePasswordAuthenticationToken usertoken = new UsernamePasswordAuthenticationToken(loginname, userDetails.getPassword(),userDetails.getAuthorities());
        return usertoken;
    }

    private  Authentication getAthTokenByUsernamePwd(String loginname, String password, HttpServletRequest request ){
        AuthenticationDetailsSource<HttpServletRequest,?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginname, password);
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
        return authenticationManager.authenticate(authRequest);
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


}