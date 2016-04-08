package com.mishu.cgwy.profile.dto;

import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.profile.domain.Feedback;
import com.mishu.cgwy.profile.wrapper.FeedbackWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by bowen on 15-5-26.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeedbackResponse extends RestError{
    private FeedbackWrapper feedback;
}
