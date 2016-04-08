package com.mishu.cgwy.common.wrapper;

import com.mishu.cgwy.common.domain.MediaFile;
import lombok.Data;

/**
 * User: xudong
 * Date: 4/13/15
 * Time: 4:26 PM
 */
@Data
public class MediaFileWrapper {
    private Long id;
    private String url;

    public MediaFileWrapper() {

    }

    public MediaFileWrapper(MediaFile mediaFile) {
        this.id = mediaFile.getId();
        this.url = mediaFile.getUrl();
    }

}
