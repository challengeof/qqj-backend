//package com.mishu.cgwy.task.wrapper;
//
//import com.mishu.cgwy.admin.domain.AdminUser;
//import com.mishu.cgwy.common.wrapper.SimpleCityWrapper;
//import com.mishu.cgwy.product.wrapper.SkuWrapper;
//import com.mishu.cgwy.task.domain.Task;
//import com.mishu.cgwy.task.enumeration.TaskStatus;
//import com.mishu.cgwy.task.enumeration.TaskType;
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.*;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//@Getter
//@Setter
//public class TaskWrapper {
//    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    private static BigDecimal convert = new BigDecimal(1000);
//
//    private Long id;
//
//    private String submitUser;
//
//    private String submitDate;
//
//    private BigDecimal timeCost;//in seconds
//
//    private TaskStatus status;
//
//    private TaskType type;
//
//    private String description;
//
//    public TaskWrapper(Task task) {
//        this.id = task.getId();
//        this.submitUser = task.getSubmitUser().getRealname();
//        this.submitDate = df.format(task.getSubmitDate());
//        this.timeCost = new BigDecimal(task.getTimeCost()).divide(convert).setScale(2, RoundingMode.HALF_UP);
//        this.status = TaskStatus.get(task.getStatus());
//        this.type = TaskType.get(task.getType());
//        this.description = task.getDescription();
//    }
//}
