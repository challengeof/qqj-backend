package com.qqj.barcode.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by bowen on 16/4/26.
 */
@Entity
@Getter
@Setter
public class BarcodeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "barcode_id")
    private Barcode barcode;

    private String bagCode;
}
