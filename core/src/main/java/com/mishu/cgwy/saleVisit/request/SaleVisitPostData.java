package com.mishu.cgwy.saleVisit.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by apple on 15/8/13.
 */
@Data
public class SaleVisitPostData {

    private Long saleVisitId;
    private Long restaurantId;
    private Date visitTime;
    private Integer visitStage;
    private List<Integer> visitPurposes = new ArrayList<>();
    private List<Integer> intentionProductions = new ArrayList<>();
    private List<Integer> visitTroubles = new ArrayList<>();
    private List<Integer> visitSolutions = new ArrayList<>();
    private Date nextVisitTime;
    private Integer nextVisitStage;
    private String remark;

}
