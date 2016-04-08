
package com.mishu.cgwy.organization.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.facade.LocationFacade;
import com.mishu.cgwy.common.wrapper.BlockWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Created by xingdong on 15/7/6.
 */
@Controller
public class BlockController {
    @Autowired
    private LocationFacade locationFacade;



    @RequestMapping(value = "/api/block", method = RequestMethod.GET)
    @ResponseBody
    public BlockQueryResponse getBlocks(BlockQueryRequest request, @CurrentAdminUser AdminUser adminUser){
        return locationFacade.getBlocks(request,adminUser);
    }

    @RequestMapping(value = "/api/block/{id}",method = RequestMethod.GET)
    @ResponseBody
    public BlockWrapper getBlockById(@PathVariable Long id) {
        return locationFacade.getBlockById(id);
    }

    @RequestMapping(value = "/api/block/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public BlockWrapper updateBlock(@PathVariable(value = "id")Long id,@RequestBody UpdateBlockQueryRequest request,@CurrentAdminUser AdminUser adminUser) {
        return locationFacade.updateBlock(id, request, adminUser);
    }
    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/api/block", method = RequestMethod.POST)
    @ResponseBody
    public BlockWrapper createBlock(@RequestBody AddBlockRequest request,@CurrentAdminUser AdminUser adminUser) {
        return locationFacade.addBlock(request, adminUser);
    }

    @RequestMapping(value = "/api/block/warehouse/{id}",method = RequestMethod.GET)
    @ResponseBody
    public List<BlockWrapper> getBlockByWarehouseId(@PathVariable("id") Long warehouseId, @CurrentAdminUser AdminUser adminUser) {
        return locationFacade.getBlockByWarehouseId(warehouseId, adminUser);
    }

}
