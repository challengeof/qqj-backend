package com.mishu.cgwy.coupon.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.operating.skipe.constant.SpikeActivityState;
import com.mishu.cgwy.operating.skipe.controller.SpikeAddRequest;
import com.mishu.cgwy.operating.skipe.controller.SpikeListRequest;
import com.mishu.cgwy.operating.skipe.controller.SpikeModifyRequest;
import com.mishu.cgwy.operating.skipe.facade.SpikeFacade;
import com.mishu.cgwy.operating.skipe.wrapper.SpikeWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
public class SpikeController {


    @Autowired
    private SpikeFacade spikeFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/api/spike/activeState/query", method = RequestMethod.GET)
    @ResponseBody
    public SpikeActivityState[] queryActiveState(@CurrentAdminUser AdminUser adminUser) throws Exception {

        return SpikeActivityState.values();
    }


    @RequestMapping(value = "/api/spike/modify", method = RequestMethod.PUT)
    @ResponseBody
    public void modify(@RequestBody SpikeModifyRequest request, @CurrentAdminUser AdminUser adminUser) throws Exception {
        spikeFacade.modify(request,adminUser);
    }

    @RequestMapping(value = "/api/spike/add", method = RequestMethod.POST)
    @ResponseBody
    public void add(@RequestBody SpikeAddRequest request, @CurrentAdminUser AdminUser adminUser) throws Exception {
        spikeFacade.add(request,adminUser);
    }

    @RequestMapping(value = "/api/spike/query", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SpikeWrapper> queryList(SpikeListRequest request){

        QueryResponse<SpikeWrapper> result = spikeFacade.getSpikePage(request);

        return result;
    }

    @RequestMapping(value = "/api/spike/query/{id}", method = RequestMethod.GET)
    @ResponseBody
    public SpikeWrapper queryItem(@PathVariable("id") Long id){

        SpikeWrapper content = spikeFacade.getSpike(id);
        return content;
    }

    @RequestMapping(value = "/api/spike/state/change", method = RequestMethod.POST)
    @ResponseBody
    public SpikeWrapper spikeStateChange(@RequestParam("id") Long id, @RequestParam("state") Integer state){

        spikeFacade.updateSpikeState(id, state);

        SpikeWrapper spike = spikeFacade.getSpike(id);

        return spike;
    }


}