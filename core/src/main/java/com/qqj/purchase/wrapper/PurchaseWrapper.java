package com.qqj.purchase.wrapper;

import com.qqj.org.wrapper.CustomerWrapper;
import com.qqj.org.wrapper.TeamWrapper;
import com.qqj.purchase.domain.Purchase;
import com.qqj.purchase.domain.PurchaseItem;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wangguodong on 16/4/12.
 */
@Setter
@Getter
public class PurchaseWrapper {
    private Long id;

    private TeamWrapper team;

    private Date createTime;

    private CustomerWrapper directLeader;

    private List<PurchaseItemWrapper> purchaseItems = new ArrayList<PurchaseItemWrapper>();

    public PurchaseWrapper(Purchase purchase) {
        this.id = purchase.getId();
        this.team = new TeamWrapper(purchase.getTeam());
        this.directLeader = new CustomerWrapper(purchase.getDirectLeader());
        this.createTime = purchase.getCreateTime();

        for (PurchaseItem item : purchase.getPurchaseItems()) {
            purchaseItems.add(new PurchaseItemWrapper(item));
        }
    }
}
