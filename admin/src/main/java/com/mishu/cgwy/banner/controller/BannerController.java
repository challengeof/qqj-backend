package com.mishu.cgwy.banner.controller;

import com.mishu.cgwy.banner.service.BannerService;
import com.mishu.cgwy.banner.vo.BannerVo;
import com.mishu.cgwy.banner.vo.PushVo;
import com.mishu.cgwy.push.service.PushMappingService;
import com.mishu.cgwy.utils.weixin.push.PushWeixinUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by bowen on 15-7-28.
 */
@Controller
public class BannerController {

    @Autowired
    private BannerService bannerService;
    @Autowired
    private PushMappingService pushMappingService;

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "api/banner/create",method = RequestMethod.POST)
    @ResponseBody
    public void createBanner(@RequestBody BannerRequest bannerRequest) {

        bannerService.createBanner(bannerRequest);
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "api/banners/{cityId}",method = RequestMethod.GET)
    @ResponseBody
    public List<BannerVo> getBanners(@PathVariable("cityId") Long cityId) {

        return bannerService.getBanners(cityId);
    }


    @RequestMapping(value = "api/banner/{id}",method = RequestMethod.PUT)
    @ResponseBody
    public void updateBanner(@PathVariable("id") Long id,@RequestBody BannerRequest bannerRequest) {

        bannerService.updateBanner(id, bannerRequest);
    }

    @RequestMapping(value = "api/banner/{id}", method = RequestMethod.GET)
    @ResponseBody
    public BannerVo getBanner(@PathVariable("id") Long id) {
        return bannerService.getBanner(id);
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "api/push/create",method = RequestMethod.POST)
    @ResponseBody
    public void createPush(@RequestBody PushRequest pushRequest) {

        bannerService.createPush(pushRequest);
    }

    @RequestMapping(value = "api/push/{id}", method = RequestMethod.GET)
    @ResponseBody
    public PushVo getPush(@PathVariable("id") Long id) {
        return bannerService.getPush(id);
    }

    @RequestMapping(value = "api/pushes", method = RequestMethod.GET)
    @ResponseBody
    public List<PushVo> getPushes() {
        return bannerService.getPushes();
    }

    @RequestMapping(value = "api/push/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void updatePush(@PathVariable("id") Long id, @RequestBody PushRequest request) {
         bannerService.updatePush(id, request);
    }


    @RequestMapping(value = "api/push/wxPushCreate/{cityid}", method = RequestMethod.POST)
    @ResponseBody
    public boolean createWXPush(@PathVariable("cityid") Long cityid , @RequestParam(value = "mediaid")  String mediaId,
                                @RequestParam(value = "isPreview") boolean isPreview, @RequestParam(value = "openid") String openid) {
        PushWeixinUtil pushUtil = new PushWeixinUtil();
        List openIds = null;
        try{
            if(isPreview == true){
                return pushUtil.pushWXPreview(openid,mediaId);
            }else{
                if (cityid != null && cityid != 0) openIds = pushMappingService.getWXPushIds(cityid);
            }
            if(openIds == null) return  false;
            return pushUtil.pushWXMessage(openIds, mediaId);
        }catch (IOException e){
            return false;
        }
    }

    @RequestMapping(value = "api/push/wxPushMediaList", method = RequestMethod.GET)
    @ResponseBody
    public String getWXPushMediaList() {
        try{
            PushWeixinUtil pushUtil = new PushWeixinUtil();
            return pushUtil.getMediaList();
        }catch (IOException e){
            return null;
        }
    }

}
