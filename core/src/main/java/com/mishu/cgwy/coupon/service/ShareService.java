package com.mishu.cgwy.coupon.service;

import com.mishu.cgwy.coupon.constant.ShareTypeEnum;
import com.mishu.cgwy.coupon.domain.Share;
import com.mishu.cgwy.coupon.repository.ShareRepository;
import com.mishu.cgwy.coupon.vo.ReqShareIdVo;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.service.CustomerService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by wangguodong on 15/7/28.
 */
@Service
public class ShareService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    ShareRepository shareRepository;
    @Autowired
    CustomerService customerService;


    /**
     * 解析一下传过来的积分分享id    格式： shareid_类型  或者  shareid
     * @param reqShareId
     * @return
     */
    public ReqShareIdVo parseReqShareId(String reqShareId){
        if(StringUtils.isBlank(reqShareId)){
            return null;
        }
        String[] shareInfo = reqShareId.split("[_]");
        ReqShareIdVo rsiv = new ReqShareIdVo();
        rsiv.setShareId(Long.parseLong(shareInfo[0]));
        if(shareInfo.length>1){
            rsiv.setShareType(ShareTypeEnum.findShareType(shareInfo[1]));
        }else{
            rsiv.setShareType(ShareTypeEnum.coupon);
        }
        return rsiv;
    }

    public Share findShare(Customer registrant, Boolean couponSended, ShareTypeEnum shareType) {
        List<Share> shares = shareRepository.findByRegistrantAndCouponSendedAndShareType(registrant, couponSended, shareType.val);
        return CollectionUtils.isEmpty(shares) ? null : shares.get(0);
    }

    public Share findShare(Long registrantCustomerId, ShareTypeEnum shareType) {
        Customer customer = new Customer();
        customer.setId(registrantCustomerId);
        List<Share> shares = shareRepository.findByRegistrantAndShareType(customer, shareType.val);
        return CollectionUtils.isEmpty(shares) ? null : shares.get(0);
    }

    public void saveShareInfo(Customer customer, Long sharerId, ShareTypeEnum shareType) {
        if(customer==null || sharerId==null || shareType==null){
            logger.error(String.format("saveShareInfo param error %s,%s,%s",customer.getId(),sharerId,shareType));
            return;
        }
        Share share = new Share();
        share.setCouponSended(Boolean.FALSE);
        share.setCreatedTime(new Date());
        share.setReference(customerService.getCustomerById(sharerId));
        share.setRegistrant(customer);
        share.setShareType(shareType.val); // 增加 分享注册的类型
        shareRepository.save(share);
    }


}
