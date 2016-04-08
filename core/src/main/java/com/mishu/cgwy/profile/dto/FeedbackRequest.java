package com.mishu.cgwy.profile.dto;

import lombok.Data;

/**
 * Created by bowen on 15-5-26.
 */
@Data
public class FeedbackRequest {
    private String feedbackDescription;
    private Long mediaFileId;
}
