package com.mishu.cgwy.saleVisit.wrapper;

import com.mishu.cgwy.profile.constants.RestaurantActiveType;
import com.mishu.cgwy.saleVisit.constants.*;
import com.mishu.cgwy.saleVisit.domain.SaleVisit;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by apple on 15/8/13.
 */
@Data
public class SaleVisitWrapper {

    private Long id;
    private String visitorName;
    private Date visitTime;
    private Long restaurantId;
    private String restaurantName;
    private String receiverName;
    private String restaurantActiveType;

    private String visitStage;
    private String visitPurposes;
    private String intentionProductions;
    private String visitTroubles;
    private String sellerName;
    private String visitSolutions;
    private String remark;
    private Date createTime;

    public SaleVisitWrapper() {
    }

    public SaleVisitWrapper(SaleVisit saleVisit) {

        id = saleVisit.getId();
        visitorName = saleVisit.getCreator().getRealname();
        visitTime = saleVisit.getVisitTime();
        restaurantId = saleVisit.getRestaurant().getId();
        restaurantName = saleVisit.getRestaurant().getName();
        receiverName = saleVisit.getRestaurant().getReceiver();

        restaurantActiveType = saleVisit.getRestaurant().getActiveType() == null ? "" : RestaurantActiveType.fromInt(saleVisit.getRestaurant().getActiveType()).detail;
        visitStage = saleVisit.getVisitStage()==null ? ""
                : SaleVisitStage.fromInt(saleVisit.getVisitStage()).getName();



        List<String> temp = new ArrayList<>();
        if (saleVisit.getVisitPurposes() != null && !"".equals(saleVisit.getVisitPurposes())) {
            for (String i : saleVisit.getVisitPurposes().split(",")) {
                temp.add(SaleVisitPurpose.fromInt(Integer.parseInt(i)).getName());
            }
            visitPurposes = temp.toString();
            temp.clear();
        }
        if (saleVisit.getIntentionProductions() != null && !"".equals(saleVisit.getIntentionProductions())) {
            for (String i : saleVisit.getIntentionProductions().split(",")) {
                temp.add(SaleVisitIntentionProduction.fromInt(Integer.parseInt(i)).getName());
            }
            intentionProductions = temp.toString();
            temp.clear();
        }
        if (saleVisit.getVisitTroubles() != null && !"".equals(saleVisit.getVisitTroubles())) {
            for (String i : saleVisit.getVisitTroubles().split(",")) {
                temp.add(SaleVisitTrouble.fromInt(Integer.parseInt(i)).getName());
            }
            visitTroubles = temp.toString();
            temp.clear();
        }
        if (saleVisit.getVisitSolutions() != null && !"".equals(saleVisit.getVisitSolutions())) {
            for (String i : saleVisit.getVisitSolutions().split(",")) {
                temp.add(SaleVisitSolution.fromInt(Integer.parseInt(i)).getName());
            }
            visitSolutions = temp.toString();
            temp.clear();
        }
        sellerName = saleVisit.getRestaurant().getCustomer() == null ? "" : saleVisit.getRestaurant().getCustomer().getAdminUser().getRealname();
        remark = saleVisit.getRemark();
        createTime = saleVisit.getCreateTime();
    }

}
