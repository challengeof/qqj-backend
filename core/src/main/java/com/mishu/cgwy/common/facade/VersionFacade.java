package com.mishu.cgwy.common.facade;

import com.mishu.cgwy.common.controller.VersionQueryRequest;
import com.mishu.cgwy.common.controller.VersionUpdateData;
import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.common.domain.Version;
import com.mishu.cgwy.common.dto.VersionUpdateResponse;
import com.mishu.cgwy.common.dto.VersionWrapper;
import com.mishu.cgwy.common.service.MediaFileService;
import com.mishu.cgwy.common.service.VersionService;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaicheng on 4/16/15.
 * Change by linsen on 1/11/16.
 */
@Service
public class VersionFacade {

    @Autowired
    private VersionService versionService;
    @Autowired
    private MediaFileService mediaFileService;

    @Transactional
    public VersionUpdateResponse checkForUpdate(Integer versionCode) {
        VersionUpdateResponse response = new VersionUpdateResponse();
        List<Version> versions = versionService.getVersionByVersionCode(versionCode);//获取所有高于当前版本Array
        for (Version version : versions) {
            //如果有高于当前版本并且是强制更新的,就视为任何一个版本为强制更新
            if (version.getType().equals(1)) {
                response.setForceUpdate(true);
            }
            response.setVersionCode(version.getVersionCode());
            response.setVersionName(version.getVersionName());
            response.setComment(version.getComment());
            response.setUrl(version.getFile().getUrl());
        }
        return response;
    }

    @Transactional
    public VersionWrapper updateVersion(VersionUpdateData versionUpdateData) {

        Version version = versionUpdateData.getId() != null ? versionService.getVersionById(versionUpdateData.getId()) : new Version();
        version.setVersionCode(versionUpdateData.getVersionCode());
        version.setVersionName(versionUpdateData.getVersionName());
        version.setComment(versionUpdateData.getComment());
        version.setForceUpdate(versionUpdateData.getForceUpdate());
        version.setType(versionUpdateData.getType());
        MediaFile mediaFile = mediaFileService.getMediaFile(versionUpdateData.getFilePath()) != null ? mediaFileService.getMediaFile(versionUpdateData.getFilePath()) : new MediaFile();
        mediaFile.setQiNiuHash(versionUpdateData.getFilePath());
        version.setFile(mediaFileService.saveMediaFile(mediaFile));
        return new VersionWrapper(versionService.updateVersion(version));
    }

    @Transactional(readOnly = true)
    public QueryResponse<VersionWrapper> getVersionList(VersionQueryRequest request) {

        QueryResponse<VersionWrapper> response = new QueryResponse<>();
        List<VersionWrapper> list = new ArrayList<>();
        Page<Version> page = versionService.getVersionList(request);
        for (Version version : page.getContent()) {
            list.add(new VersionWrapper(version));
        }
        response.setContent(list);
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        return response;
    }

    @Transactional(readOnly = true)
    public VersionWrapper getVersionById(Long id) {
        return new VersionWrapper(versionService.getVersionById(id));
    }
}
