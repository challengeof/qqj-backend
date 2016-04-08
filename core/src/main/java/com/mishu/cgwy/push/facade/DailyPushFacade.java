package com.mishu.cgwy.push.facade;

import com.mishu.cgwy.error.UserDefinedException;
import com.mishu.cgwy.push.domain.DailyPush;
import com.mishu.cgwy.push.request.DailyPushRequest;
import com.mishu.cgwy.push.service.DailyPushService;
import com.mishu.cgwy.push.wrapper.DailyPushWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowen on 15/9/23.
 */
@Service
public class DailyPushFacade {

    @Autowired
    private DailyPushService dailyPushService;

    @Transactional
    public DailyPushWrapper updateDailyPush(DailyPushRequest request) {

        DailyPush push = dailyPushService.getDailyPushByTag(request.getTag());
        if (push != null && (request.getId() == null || !push.getId().equals(request.getId()))) {
            throw new UserDefinedException("城市标签重复!");
        }
        DailyPush dailyPush = request.getId() != null ? dailyPushService.getDailyPushById(request.getId()) : new DailyPush();
        dailyPush.setTag(request.getTag());
        dailyPush.setMessage(request.getMessage());
        return new DailyPushWrapper(dailyPushService.updateDailyPush(dailyPush));
    }

    @Transactional(readOnly = true)
    public DailyPushWrapper getDailyPushById(Long id) {
        return new DailyPushWrapper(dailyPushService.getDailyPushById(id));
    }

    @Transactional
    public void deleteDailyPushById(Long id) {
        dailyPushService.deleteDailyPushById(id);
    }

    @Transactional(readOnly = true)
    public List<DailyPushWrapper> getBrandList() {

        List<DailyPushWrapper> list = new ArrayList<>();
        for (DailyPush push : dailyPushService.getDailyPushList()) {
            list.add(new DailyPushWrapper(push));
        }
        return list;
    }

}
