package com.mishu.cgwy.push.service;

import com.mishu.cgwy.push.domain.DailyPush;
import com.mishu.cgwy.push.repository.DailyPushRepository;
import com.mishu.cgwy.utils.jiguang.push.JPushMsgModel;
import com.mishu.cgwy.utils.jiguang.push.JPushUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xiao1zhao2 on 16/2/15.
 */
@Service
public class DailyPushService {

    @Autowired
    private DailyPushRepository dailyPushRepository;

    public DailyPush updateDailyPush(DailyPush dailyPush) {
        return dailyPushRepository.save(dailyPush);
    }

    public DailyPush getDailyPushById(Long id) {
        return dailyPushRepository.getOne(id);
    }

    public DailyPush getDailyPushByTag(String tag) {
        return dailyPushRepository.findByTag(tag);
    }

    public void deleteDailyPushById(Long id) {
        dailyPushRepository.delete(id);
    }

    public List<DailyPush> getDailyPushList() {
        return dailyPushRepository.findAll();
    }

    public void dailyPush() {

        JPushUtil pushUtil = new JPushUtil();
        for (DailyPush push : getDailyPushList()) {
            JPushMsgModel msg = new JPushMsgModel();
            msg.setMsg(push.getMessage());
            msg.setTags(new String[]{push.getTag()});
            pushUtil.pushByTags(msg);
        }
    }

}
