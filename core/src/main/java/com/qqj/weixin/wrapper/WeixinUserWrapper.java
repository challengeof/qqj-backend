package com.qqj.weixin.wrapper;

import com.qqj.weixin.domain.WeixinPic;
import com.qqj.weixin.domain.WeixinUser;
import com.qqj.weixin.enumeration.WeixinUserStatus;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * Created by wangguodong on 16/4/26.
 */
@Setter
@Getter
public class WeixinUserWrapper {

    private Long id;

    private String openId;

    private String nickname;

    private String name;

    private Date birthday;

    private String telephone;

    private WeixinUserStatus status;

    private Date auditTime;

    private List<WeixinPicWrapper> pics = new ArrayList<WeixinPicWrapper>();

    public WeixinUserWrapper(WeixinUser weixinUser) {
        if (weixinUser == null) {
            return;
        }
        this.id = weixinUser.getId();
        this.openId = weixinUser.getOpenId();
        this.nickname = weixinUser.getNickname();
        this.name = weixinUser.getName();
        this.birthday = weixinUser.getBirthday();
        this.telephone = weixinUser.getTelephone();
        this.status = WeixinUserStatus.get(weixinUser.getStatus());
        this.auditTime = weixinUser.getAuditTime();

        if (CollectionUtils.isNotEmpty(weixinUser.getPics())) {
            for (WeixinPic weixinPic : weixinUser.getPics()) {
                pics.add(new WeixinPicWrapper(weixinPic));
            }
            Collections.sort(pics, new Comparator<WeixinPicWrapper>() {
                @Override
                public int compare(WeixinPicWrapper o1, WeixinPicWrapper o2) {
                    return o1.getType().compareTo(o2.getType());
                }
            });
        }

    }
}
