package com.mishu.cgwy.common.facade;

import com.mishu.cgwy.common.controller.FeedBackListRequest;
import com.mishu.cgwy.common.service.FeedbackService;
import com.mishu.cgwy.profile.domain.Feedback;
import com.mishu.cgwy.profile.domain.FeedbackStatus;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.domain.Suggestion;
import com.mishu.cgwy.profile.dto.SuggestionPostData;
import com.mishu.cgwy.profile.repository.RestaurantRepository;
import com.mishu.cgwy.profile.vo.FeedbackShowVo;
import com.mishu.cgwy.profile.wrapper.SuggestionWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by king-ck on 2016/1/25.
 */
@Service
public class FeedbackFacade {

    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    public QueryResponse<FeedbackShowVo> getFeedBacks(FeedBackListRequest request) {
        Page<FeedbackShowVo> feedbacks = feedbackService.findFeedBacks(request);
        QueryResponse<FeedbackShowVo> res = new QueryResponse<>();
        res.setContent(feedbacks.getContent());
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(feedbacks.getTotalElements());
        return res;
    }

    public FeedbackShowVo getFeedBack(Long feedBackId) {

        Feedback feedback = feedbackService.getOne(feedBackId);
        FeedbackShowVo fbsVo = new FeedbackShowVo();
        fbsVo.setId(feedback.getId());
        fbsVo.setFeedbackDescription(feedback.getFeedbackDescription());

        if (feedback.getFile() != null) {
            fbsVo.setMediaFileurl(feedback.getFile().getUrl());
        }
        fbsVo.setCustomerUsername(feedback.getCustomer().getUsername());

        List<Restaurant> restaurants = feedback.getCustomer().getRestaurant();
        fbsVo.setRestaurantId(restaurants == null || restaurants.size() == 0 ? null : restaurants.get(0).getId());
        fbsVo.setRestaurantName(restaurants == null || restaurants.size() == 0 ? null : restaurants.get(0).getName());
        fbsVo.setReceiver(restaurants == null || restaurants.size() == 0 ? null : restaurants.get(0).getReceiver());
        fbsVo.setTelephone(restaurants == null || restaurants.size() == 0 ? null : restaurants.get(0).getTelephone());
        fbsVo.setSubmitTime(feedback.getSubmitTime());
        FeedbackStatus status = FeedbackStatus.fromInt(feedback.getStatus());
        fbsVo.setStatus(status);
        return fbsVo;
    }

    @Transactional
    public SuggestionWrapper saveSuggestion(SuggestionPostData data) {

        Suggestion suggestion = new Suggestion();
        suggestion.setRemark(data.getRemark());
        suggestion.setCreateTime(new Date());
        suggestion.setRestaurant(restaurantRepository.getOne(data.getRestaurantId()));
        return new SuggestionWrapper(feedbackService.saveSuggestion(suggestion));
    }

    @Transactional(readOnly = true)
    public QueryResponse<SuggestionWrapper> getSuggestionList(FeedBackListRequest request) {

        List<SuggestionWrapper> list = new ArrayList<>();
        for (Suggestion suggestion : feedbackService.getSuggestionList(request)) {
            list.add(new SuggestionWrapper(suggestion));
        }
        QueryResponse<SuggestionWrapper> res = new QueryResponse<>();
        res.setContent(list);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        return res;
    }

}
