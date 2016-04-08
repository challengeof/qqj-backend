package com.mishu.cgwy.version.controller.legacy;

import com.mishu.cgwy.common.dto.VersionUpdateResponse;
import com.mishu.cgwy.common.facade.VersionFacade;
import com.mishu.cgwy.version.controller.legacy.pojo.VersionUpdateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by kaicheng on 4/16/15.
 * Change by linsen on 1/11/16.
 */
@Controller
public class VersionController {

    @Autowired
    private VersionFacade versionFacade;

    @RequestMapping(value = "/api/legacy/version/check/update", method = RequestMethod.POST)
    @ResponseBody
    public VersionUpdateResponse checkVersionUpdate(@RequestBody VersionUpdateRequest request){
        Integer versionCode = request.getVersionCode();
        VersionUpdateResponse response = versionFacade.checkForUpdate(versionCode);
        return  response;

    }
}
