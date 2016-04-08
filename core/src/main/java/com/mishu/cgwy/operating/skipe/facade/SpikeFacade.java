package com.mishu.cgwy.operating.skipe.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.repository.CityRepository;
import com.mishu.cgwy.error.ErrorCode;
import com.mishu.cgwy.error.SpikeCanNotModifyException;
import com.mishu.cgwy.error.SpikeInvalidException;
import com.mishu.cgwy.operating.skipe.constant.SpikeActivityState;
import com.mishu.cgwy.operating.skipe.controller.SpikeAddRequest;
import com.mishu.cgwy.operating.skipe.controller.SpikeItemAddRequest;
import com.mishu.cgwy.operating.skipe.controller.SpikeListRequest;
import com.mishu.cgwy.operating.skipe.controller.SpikeModifyRequest;
import com.mishu.cgwy.operating.skipe.domain.Spike;
import com.mishu.cgwy.operating.skipe.domain.SpikeItem;
import com.mishu.cgwy.operating.skipe.service.SpikeCacheService;
import com.mishu.cgwy.operating.skipe.service.SpikeService;
import com.mishu.cgwy.operating.skipe.util.SpikeUtil;
import com.mishu.cgwy.operating.skipe.wrapper.SpikeItemWrapper;
import com.mishu.cgwy.operating.skipe.wrapper.SpikeWrapper;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by king-ck on 2016/1/7.
 */
@Lazy
@Component
public class SpikeFacade {

    @Autowired
    private SpikeService spikeService;

    @Autowired
    private SpikeCacheService spikeCacheService;

    @Autowired
    private CityRepository cityRepository;

    @Transactional(rollbackFor = Exception.class)
    public void modify(SpikeModifyRequest request, AdminUser operater){
        Spike spike = spikeService.getSpike(request.getSpikeId());
        if(!SpikeUtil.checkCanModify(spike)){
            throw new SpikeCanNotModifyException();
        }
        spike.setLastModify(new Date());
        spike.setLastModifyOperater(operater);
        spike.setDescription(request.getDescription());

        SpikeItem[] items = SpikeItemAddRequest.toSpikeItem(request.getItems());
        try {
            spikeService.addOrModify(spike, items);
        }finally {
            spikeCacheService.removeItemCacheBySpikeId(spike.getId());
            spikeCacheService.removeSpikeCache(spike.getId());
            spikeCacheService.removeSpikeNotExpire(spike.getCity().getId());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void add(SpikeAddRequest request, AdminUser user) throws Exception {
        Spike spike = request.toSpike(request, user);
        try {
            SpikeItem[] items = SpikeItemAddRequest.toSpikeItem(request.getItems());

            spikeService.addOrModify(spike, items);
        }finally{
            spikeCacheService.removeSpikeNotExpire(spike.getCity().getId());
        }
    }

    @Transactional(readOnly = true)
    public QueryResponse<SpikeWrapper> getSpikePage(SpikeListRequest request){
        Page<Spike> spikePage =  spikeService.getSpikePage(request);

        QueryResponse<SpikeWrapper> result = new QueryResponse<>(SpikeWrapper.toWrappers(spikePage.getContent()));
        result.setPage(request.getPage());
        result.setPageSize(request.getPageSize());
        result.setTotal(spikePage.getTotalElements());
        return result;
    }

    @Transactional(readOnly = true)
    public SpikeWrapper getSpike(Long id) {
        Spike spike = spikeService.getSpike(id);
        return new SpikeWrapper(spike,spike.getItems());
    }

    public void updateSpikeState(Long id, Integer state) {
        try {
            spikeService.updateSpikeState(id, state);
        }finally {
            Spike spike = spikeService.getSpike(id);
            spikeCacheService.removeItemCacheBySpikeId(spike.getId());
            spikeCacheService.removeSpikeCache(id);
            spikeCacheService.removeSpikeNotExpire(spike.getCity().getId());
        }
    }

    public List<SpikeWrapper> findSpikeFirstCache(Customer customer, SpikeActivityState... progress) {

        List<Spike> spikes = spikeService.getSpikeList(customer.getCity(), progress);
        List<SpikeWrapper> spikeWrappers = SpikeWrapper.toWrappers(spikes);
        return spikeWrappers;
    }

    public List<SpikeItemWrapper> findSpikeItemsFirstCache(Long spikeId) {
        Spike spike = spikeService.getSpike(spikeId);
        return SpikeItemWrapper.toWrappers(spike.getItems());
    }

    public List<SpikeWrapper> findCacheSpikeByState(Long cityId,SpikeActivityState... activityStates) {

        City city = cityRepository.getOne(cityId);

        return spikeCacheService.getSpikes( city, activityStates);
    }


    public void spikeValidity(Long spikeId){
        SpikeWrapper spike = this.spikeCacheService.getSpike(spikeId);
        if(SpikeActivityState.checkSpikeState(spike, SpikeActivityState.invalid)){
            throw new SpikeInvalidException(ErrorCode.SpikeInvalid);
        }
        if(SpikeActivityState.checkSpikeState(spike, SpikeActivityState.end)){
            throw new SpikeInvalidException(ErrorCode.SpikeEnd);
        }
    }

    public boolean spikeInArea(Customer customer, Long spikeItemId ){
        SpikeWrapper spike = this.spikeCacheService.getSpikeByItemId(spikeItemId);
        if(customer.getCity().getId() == spike.getCity().getId()){
            return true;
        }
        return false;
    }

    public boolean spikeIsProcess(Long spikeItemId){
        SpikeWrapper spike = this.spikeCacheService.getSpikeByItemId(spikeItemId);
        SpikeActivityState saState = SpikeActivityState.parseSpikeActivity(spike);
        if(saState==SpikeActivityState.process){
            return true;
        }
        return false;
    }

    public List<SpikeItemWrapper> findCacheSpikeItems(Long spikeId) {
        List<SpikeItemWrapper> spikeItems = spikeCacheService.getSpikeItemsBySpikeId(spikeId);
        return spikeItems;
    }

    public List<SpikeItemWrapper> findCacheSpikeItems(Long... spikeItemIds) {
        List<SpikeItemWrapper> items =new ArrayList<>();
        for(Long spikeItemId : spikeItemIds) {
            SpikeItemWrapper spikeItem = spikeCacheService.getSpikeItem(spikeItemId);
            if(null!=spikeItem){
                items.add(spikeItem);
            }
        }
        return items;
    }

    public int increaseTakeNum(Long spikeItemId, int quantity) {

        int result =spikeService.increaseTakeNum(spikeItemId,quantity);
        if(result>0){
            spikeCacheService.removeSpikeItemCache(spikeItemId);
        }
        return result;
    }

    public List<SpikeItemWrapper> findSpikeItems(Long[] spikeItemIds) {

        List<SpikeItem> spikeItems = spikeService.getSpikeItems(spikeItemIds);
        return SpikeItemWrapper.toWrappers(spikeItems);
    }

    public SpikeItemWrapper findSpikeItem(Long spikeItemId) {
        SpikeItem spikeItem = spikeService.getSpikeItem(spikeItemId);
        return new SpikeItemWrapper(spikeItem,SpikeActivityState.parseSpikeActivity(spikeItem.getSpike()));
    }



}
