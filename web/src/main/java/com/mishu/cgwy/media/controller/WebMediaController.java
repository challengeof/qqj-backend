package com.mishu.cgwy.media.controller;

import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.common.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * User: xudong
 * Date: 4/12/15
 * Time: 9:36 PM
 */
@Controller
public class WebMediaController {
    @Autowired
    private MediaFileService mediaFileService;

    @RequestMapping(value = "/api/v2/media", method = RequestMethod.POST)
    @ResponseBody
    public MediaFile uploadMedia(@RequestParam("file") MultipartFile file) {
        return mediaFileService.saveMediaFile(file);
    }
}
