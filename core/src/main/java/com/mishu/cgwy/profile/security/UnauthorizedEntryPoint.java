package com.mishu.cgwy.profile.security;

import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.utils.RenderUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * {@link org.springframework.security.web.AuthenticationEntryPoint} that rejects all requests with an unauthorized error message.
 *
 * @author xudong
 */
public class UnauthorizedEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

}