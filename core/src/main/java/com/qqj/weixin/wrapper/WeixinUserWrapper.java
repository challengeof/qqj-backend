package com.qqj.weixin.wrapper;

import com.qqj.weixin.domain.WeixinPic;
import com.qqj.weixin.domain.WeixinUser;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangguodong on 16/4/26.
 */
@Setter
@Getter
public class WeixinUserWrapper {

    private Long id;

    private String openId;

    private String nickname;

    private List<WeixinPicWrapper> pics = new ArrayList<WeixinPicWrapper>();

    public WeixinUserWrapper(WeixinUser weixinUser) {
        this.id = weixinUser.getId();
        this.openId = weixinUser.getOpenId();
        this.nickname = weixinUser.getNickname();

        if (CollectionUtils.isNotEmpty(weixinUser.getPics())) {
            for (WeixinPic weixinPic : weixinUser.getPics()) {
                pics.add(new WeixinPicWrapper(weixinPic));
            }
        }

    }
}
