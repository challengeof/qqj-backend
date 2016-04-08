package com.mishu.cgwy.profile.wrapper;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.common.wrapper.MediaFileWrapper;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.inventory.vo.VendorVo;
import com.mishu.cgwy.profile.domain.Feedback;
import com.mishu.cgwy.profile.domain.FeedbackStatus;
import com.mishu.cgwy.profile.domain.FeedbackType;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 7/13/15
 * Time: 8:19 PM
 */
@Data
public class FeedbackWrapper {
    private Long id;
    private String feedbackDescription;
    private MediaFileWrapper file;
    private CustomerWrapper customer;
    private Date submitTime;
    private FeedbackStatus status;
    private Date updateTime;
    private FeedbackType type;
    private VendorVo vendor;

    public FeedbackWrapper() {

    }

    public FeedbackWrapper(Feedback feedback) {
        id = feedback.getId();
        feedbackDescription = feedback.getFeedbackDescription();
        file = feedback.getFile() != null? new MediaFileWrapper(feedback.getFile()) : null;
        customer = feedback.getCustomer() != null ? new CustomerWrapper(feedback.getCustomer()) : null;
        submitTime = feedback.getSubmitTime();
        status = FeedbackStatus.fromInt(feedback.getStatus());
        updateTime = feedback.getUpdateTime();
        type= FeedbackType.get(feedback.getType());

        Vendor feedbackVendor = feedback.getVendor();
        if (feedbackVendor != null) {
            vendor = new VendorVo();
            vendor.setName(feedbackVendor.getName());
        }
    }


    public static List<FeedbackWrapper> toWrappers(List<Feedback> content) {
        Collection<FeedbackWrapper> feedbacks = Collections2.transform(content, new Function<Feedback, FeedbackWrapper>() {
            @Override
            public FeedbackWrapper apply(Feedback input) {
                return new FeedbackWrapper(input);
            }
        });
        return new ArrayList<>(feedbacks);
    }
}
