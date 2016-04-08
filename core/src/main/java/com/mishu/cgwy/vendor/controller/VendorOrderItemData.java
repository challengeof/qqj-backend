package com.mishu.cgwy.vendor.controller;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class VendorOrderItemData {

    Long[] ids;

    Long depotId;

}
