package com.qqj.download.controller;

import com.qqj.download.DownloadUtils;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: guodong
 */
@Controller
public class DownloadController {
    @RequestMapping(value = "/api/download", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> downloadApk(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return DownloadUtils.downloadApk(request, response);
    }
}
