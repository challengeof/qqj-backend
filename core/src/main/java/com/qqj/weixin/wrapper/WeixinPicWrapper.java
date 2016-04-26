package com.qqj.weixin.wrapper;

import com.qqj.weixin.domain.WeixinPic;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by wangguodong on 16/4/26.
 */
@Getter
@Setter
public class WeixinPicWrapper {

    private Long id;

    private String url;

    //个人图片编号
    private Short seq;

    private Date createTime;

    public WeixinPicWrapper(WeixinPic weixinPic) {
        this.id = weixinPic.getId();
        this.url = weixinPic.getUrl();
        this.seq = weixinPic.getSeq();
        this.createTime = weixinPic.getCreateTime();
    }

}
