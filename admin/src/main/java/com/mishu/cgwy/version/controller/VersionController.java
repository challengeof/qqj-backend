package com.mishu.cgwy.version.controller;

import com.mishu.cgwy.common.controller.VersionQueryRequest;
import com.mishu.cgwy.common.controller.VersionUpdateData;
import com.mishu.cgwy.common.dto.VersionWrapper;
import com.mishu.cgwy.common.facade.VersionFacade;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by xiao1zhao2 on 16/1/13.
 */
@Controller
public class VersionController {

    @Autowired
    private VersionFacade versionFacade;

    @RequestMapping(value = "/api/version/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<VersionWrapper> getVersionList(VersionQueryRequest request) {
        return versionFacade.getVersionList(request);
    }

    @RequestMapping(value = "/api/version/update", method = RequestMethod.POST)
    @ResponseBody
    public VersionWrapper updateVersion(@RequestBody VersionUpdateData versionUpdateData) {
        return versionFacade.updateVersion(versionUpdateData);
    }

    @RequestMapping(value = "/api/version/{id}", method = RequestMethod.GET)
    @ResponseBody
    public VersionWrapper getVersionById(@PathVariable("id") Long id) {
        return versionFacade.getVersionById(id);
    }
}
