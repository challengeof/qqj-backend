package com.qqj.barcode.controller;

import com.qqj.admin.domain.AdminUser;
import com.qqj.barcode.domain.Barcode;
import com.qqj.barcode.dto.BarcodeRequest;
import com.qqj.barcode.dto.QueryBarcodeRequest;
import com.qqj.barcode.service.BarcodeService;
import com.qqj.barcode.vo.BarcodeItemVo;
import com.qqj.barcode.vo.BarcodeVo;
import com.qqj.org.controller.CurrentAdminUser;
import com.qqj.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by bowen on 16/4/26.
 */
@Controller
@RequestMapping(value = "/api/barcode")
public class BarcodeController {

    @Autowired
    private BarcodeService barcodeService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public void createBarcode(@CurrentAdminUser AdminUser operator, @RequestBody BarcodeRequest request) {

        barcodeService.createBarcode(operator, request);
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<BarcodeVo> findBarcode(QueryBarcodeRequest request) {

        return barcodeService.findBarcode(request);
    }

    @RequestMapping(value = "/query-item", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<BarcodeItemVo> findBarcodeItem(QueryBarcodeRequest request) {

        return barcodeService.findBarcodeItem(request);
    }
}
