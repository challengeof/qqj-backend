package com.qqj.download;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.io.File;

/**
 * Created by wangguodong on 16/4/14.
 */
public class DownloadUtils {
    public static HttpEntity<byte[]> downloadApk() throws Exception {
        byte[] readFileToByteArray = FileUtils.readFileToByteArray(new File("/root/app/com.mirror.cgwy_2.1.3_338.apk"));
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Disposition", "attachment; filename=com.mirror.cgwy_2.1.3_338.apk");
        header.setContentLength(readFileToByteArray.length);
        return new HttpEntity<byte[]>(readFileToByteArray, header);
    }
}
