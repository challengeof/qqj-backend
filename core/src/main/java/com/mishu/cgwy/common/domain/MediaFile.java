package com.mishu.cgwy.common.domain;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * User: xudong
 * Date: 3/18/15
 * Time: 10:27 PM
 */
@Entity
@Data
public class MediaFile {
    // TODO
    public static final String default7NiuDomain = "http://7xijms.com1.z0.glb.clouddn.com/";
    // TODO
    public static final String defaultLocalDomain = "http://www.canguanwuyou.cn";

    public final static String DEFAULT_IMAGE = "default";

    public static final String defaultPhoto = "默认";

    public static final String noPhoto = "无";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String localPath;

    private String qiNiuHash;

    public static String getUrl(String localPath, String qiNiuHash){
        if (StringUtils.isNotBlank(qiNiuHash)) {
            return default7NiuDomain + qiNiuHash;
        } else if (StringUtils.isNotBlank(localPath)) {
            return defaultLocalDomain + localPath;
        } else {
            return null;
        }
    }

    public String getUrl() {
        return getUrl(localPath,qiNiuHash);
    }
}
