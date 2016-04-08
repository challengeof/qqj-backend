package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:23 PM
 */
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"depot_id","shelfCode"})},
        indexes = {@Index(name = "SHELF_SHELFCODE_INDEX", columnList = "shelfCode")})
@Data
public class Shelf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    private String name;

    private String area;

    private String row;

    private String number;

    private String shelfCode;
}
