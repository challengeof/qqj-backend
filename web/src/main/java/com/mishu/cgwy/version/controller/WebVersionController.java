package com.mishu.cgwy.version.controller;

import com.mishu.cgwy.common.dto.VersionUpdateResponse;
import com.mishu.cgwy.common.facade.VersionFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by kaicheng on 4/16/15.
 */
@Controller
public class WebVersionController {

    @Autowired
    private VersionFacade versionFacade;

    @RequestMapping(value = "/api/v2/version/update", method = RequestMethod.GET)
    @ResponseBody
    public VersionUpdateResponse checkVersionUpdate(@RequestParam("versionCode") Integer versionCode) {
        VersionUpdateResponse response = versionFacade.checkForUpdate(versionCode);
        return response;
    }
}
