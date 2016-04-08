package com.mishu.cgwy.media.controller;

import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.common.service.MediaFileService;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.vendor.controller.CurrentVendor;
import com.mishu.cgwy.vendor.wrapper.VendorOrderItemWrapper;
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
public class MediaController {
    @Autowired
    private MediaFileService mediaFileService;

    @RequestMapping(value = "/api/media", method = RequestMethod.POST)
    @ResponseBody
    public MediaFile uploadMedia(@RequestParam("file") MultipartFile file) {
        return mediaFileService.saveMediaFile(file);
    }

    @RequestMapping(value = "/vendor-api/media", method = RequestMethod.POST)
    @ResponseBody
    public MediaFile vendorUploadMedia(@RequestParam("file") MultipartFile file) {
        return mediaFileService.saveMediaFile(file);
    }
}
