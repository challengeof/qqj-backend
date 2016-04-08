package com.mishu.cgwy.stock.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by admin on 2015/9/17.
 */
@Entity
@Data
public class SellReturnReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  String reason;

    @Override
    public String toString() {
        return "SellReturnReason{" +
                "id=" + id +
                ", reason='" + reason + '\'' +
                '}';
    }
}
