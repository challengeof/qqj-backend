package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.stock.domain.Transfer;
import com.mishu.cgwy.stock.domain.TransferStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class TransferWrapper {

    private Long id;

    private Long cityId;

    private TransferStatus status;

    private DepotWrapper sourceDepot;

    private DepotWrapper targetDepot;

    private String remark;

    private String creator;

    private Date createDate;

    private String auditor;

    private Date auditDate;

    private List<TransferItemWrapper> items;

    private String opinion;

    public TransferWrapper(Transfer transfer) {
        this.id = transfer.getId();
        this.cityId = transfer.getSourceDepot().getCity().getId();
        this.status = TransferStatus.fromInt(transfer.getStatus());
        this.sourceDepot = new DepotWrapper(transfer.getSourceDepot());
        this.targetDepot = new DepotWrapper(transfer.getTargetDepot());
        this.remark = transfer.getRemark();
        this.creator = transfer.getCreator().getRealname();
        this.createDate = transfer.getCreateDate();
        this.auditor = transfer.getAuditor() == null ? null : transfer.getAuditor().getRealname();
        this.auditDate = transfer.getAuditDate();
        this.opinion = transfer.getOpinion();
    }
}
