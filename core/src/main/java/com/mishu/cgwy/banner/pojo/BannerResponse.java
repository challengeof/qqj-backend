package com.mishu.cgwy.banner.pojo;

import com.mishu.cgwy.banner.dto.BannerUrl;
import com.mishu.cgwy.banner.dto.Message;
import com.mishu.cgwy.error.RestError;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowen on 15-5-25.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BannerResponse extends RestError {

    private List<BannerUrl> banner = new ArrayList<>();
    private Message welcomeContent;
    private String shoppingTip = "";
}
