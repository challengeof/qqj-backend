package com.mishu.cgwy.feedback.controller;

import com.mishu.cgwy.common.facade.FeedbackFacade;
import com.mishu.cgwy.common.service.FeedbackService;
import com.mishu.cgwy.common.service.MediaFileService;
import com.mishu.cgwy.profile.controller.CurrentCustomer;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.domain.Feedback;
import com.mishu.cgwy.profile.domain.FeedbackType;
import com.mishu.cgwy.profile.dto.FeedbackRequest;
import com.mishu.cgwy.profile.dto.FeedbackResponse;
import com.mishu.cgwy.profile.dto.SuggestionPostData;
import com.mishu.cgwy.profile.wrapper.FeedbackWrapper;
import com.mishu.cgwy.profile.wrapper.SuggestionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * User: xudong
 * Date: 7/13/15
 * Time: 6:12 PM
 */
@Controller
public class FeedbackController {
    private static Logger logger = LoggerFactory.getLogger(FeedbackController.class);
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private FeedbackFacade feedbackFacade;
    @Autowired
    private MediaFileService mediaFileService;

    @RequestMapping(value = "/api/v2/feedback", method = RequestMethod.POST)
    @ResponseBody
    public FeedbackResponse feedback(@CurrentCustomer Customer customer, @RequestBody FeedbackRequest feedbackRequest) {

        Feedback feedback = new Feedback();
        feedback.setFeedbackDescription(feedbackRequest.getFeedbackDescription());
        feedback.setCustomer(customer);
        feedback.setType(FeedbackType.CUSTOMER.getValue());
        if (feedbackRequest.getMediaFileId() != null) {
            feedback.setFile(mediaFileService.getMediaFile(feedbackRequest.getMediaFileId()));
        }
        feedback.setSubmitTime(new Date());
        FeedbackResponse feedbackResponse = new FeedbackResponse();
        feedbackResponse.setFeedback(new FeedbackWrapper(feedbackService.saveFeedback(feedback)));
        return feedbackResponse;
    }

    @RequestMapping(value = "/api/v2/suggestion", method = RequestMethod.POST)
    @ResponseBody
    public SuggestionWrapper saveSuggestion(@RequestBody SuggestionPostData data) {
        return feedbackFacade.saveSuggestion(data);
    }
}
