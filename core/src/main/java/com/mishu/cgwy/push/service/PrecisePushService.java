package com.mishu.cgwy.push.service;

import com.baidu.yun.push.exception.PushClientException;
import com.baidu.yun.push.exception.PushServerException;
import com.mishu.cgwy.push.request.PrecisePushRequest;
import com.mishu.cgwy.profile.repository.RestaurantRepository;
import com.mishu.cgwy.push.domain.PushMapping;
import com.mishu.cgwy.push.repository.PushMappingRepository;
import com.mishu.cgwy.utils.baidu.push.PushBaiduUtil;
import com.mishu.cgwy.utils.baidu.push.PushMsgModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiao1zhao2 on 16/2/15.
 */
@Service
public class PrecisePushService {

    @Autowired
    private PushMappingRepository pushMappingRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    public void precisePush(PrecisePushRequest request) throws PushClientException, PushServerException {

        List<String> androidChannelIds = new ArrayList<>();
        List<String> iosChannelIds = new ArrayList<>();
        for (Long restaurantId : request.getRestaurantIds()) {
            for (PushMapping pushMapping : pushMappingRepository.findByCustomerId(restaurantRepository.findById(restaurantId).getCustomer().getId())) {
                if ("android".equals(pushMapping.getPlatform())) {
                    androidChannelIds.add(pushMapping.getBaiduChannelId());
                } else if ("ios".equals(pushMapping.getPlatform())) {
                    iosChannelIds.add(pushMapping.getBaiduChannelId());
                }
            }
        }

        PushMsgModel androidMsg = new PushMsgModel();
        androidMsg.setDeviceType(PushMsgModel.DEVICE_TYPE_ANDROID);
        androidMsg.setChannelIds(androidChannelIds.toArray(new String[androidChannelIds.size()]));
        androidMsg.setTitle(request.getTitle());
        androidMsg.setDescription(request.getMessage());

        PushMsgModel iosMsg = new PushMsgModel();
        iosMsg.setDeviceType(PushMsgModel.DEVICE_TYPE_IOS);
        iosMsg.setChannelIds(iosChannelIds.toArray(new String[iosChannelIds.size()]));
        iosMsg.setTitle(request.getTitle());
        iosMsg.setDescription(request.getMessage());

        PushBaiduUtil util = new PushBaiduUtil();
        util.pushBatchMsgByAndroid(androidMsg);
        util.pushBatchMsgByIos(iosMsg);
    }
}
