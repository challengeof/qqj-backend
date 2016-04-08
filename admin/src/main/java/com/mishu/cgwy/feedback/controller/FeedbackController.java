package com.mishu.cgwy.feedback.controller;

import com.mishu.cgwy.common.controller.FeedBackListRequest;
import com.mishu.cgwy.common.facade.FeedbackFacade;
import com.mishu.cgwy.common.service.FeedbackService;
import com.mishu.cgwy.common.service.MediaFileService;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.profile.domain.Feedback;
import com.mishu.cgwy.profile.domain.FeedbackStatus;
import com.mishu.cgwy.profile.domain.FeedbackType;
import com.mishu.cgwy.profile.dto.FeedbackRequest;
import com.mishu.cgwy.profile.dto.FeedbackResponse;
import com.mishu.cgwy.profile.vo.FeedbackShowVo;
import com.mishu.cgwy.profile.wrapper.FeedbackWrapper;
import com.mishu.cgwy.profile.wrapper.SuggestionWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.vendor.controller.CurrentVendor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: guodong
 * Date: 12/18/15
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

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }


    @RequestMapping(value = "/vendor-api/feedback", method = RequestMethod.POST)
    @ResponseBody
    public FeedbackResponse vendorFeedback(@CurrentVendor Vendor vendor, @RequestBody FeedbackRequest feedbackRequest) {

        Feedback feedback = new Feedback();
        feedback.setFeedbackDescription(feedbackRequest.getFeedbackDescription());
        feedback.setVendor(vendor);
        feedback.setType(FeedbackType.VENDOR.getValue());
        if (feedbackRequest.getMediaFileId() != null) {
            feedback.setFile(mediaFileService.getMediaFile(feedbackRequest.getMediaFileId()));
        }
        feedback.setSubmitTime(new Date());
        FeedbackResponse feedbackResponse = new FeedbackResponse();
        feedbackResponse.setFeedback(new FeedbackWrapper(feedbackService.saveFeedback(feedback)));
        return feedbackResponse;
    }


    @RequestMapping(value = "/api/feedback/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportFeedback(FeedBackListRequest request) throws Exception {
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        QueryResponse<FeedbackShowVo> result = feedbackFacade.getFeedBacks(request);
        return this.feedbackService.exportFeedBackInfo(request, result.getContent(), "feedback-list.xls", FeedbackService.FEEDBACK_LIST);
    }

    @RequestMapping(value = "/api/feedback/{feedbackId}", method = RequestMethod.GET)
    @ResponseBody
    public FeedbackShowVo feedBackQuery(@PathVariable("feedbackId") Long feedBackId) {
        return feedbackFacade.getFeedBack(feedBackId);
    }

    @RequestMapping(value = "/api/feedback/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<FeedbackShowVo> feedBackList(FeedBackListRequest feedBackListRequest) {
        return feedbackFacade.getFeedBacks(feedBackListRequest);
    }


    @RequestMapping(value = "/api/feedback/status", method = RequestMethod.GET)
    @ResponseBody
    public FeedbackStatus[] getFeedBackStatus() {
        return FeedbackStatus.values();
    }

    @RequestMapping(value = "/api/feedback/type", method = RequestMethod.GET)
    @ResponseBody
    public FeedbackType[] getFeedBackType() {
        return FeedbackType.values();
    }

    @RequestMapping(value = "/api/suggestion/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SuggestionWrapper> getSuggestionLIst(FeedBackListRequest request) {
        return feedbackFacade.getSuggestionList(request);
    }
}
