package com.mishu.cgwy.operating.skipe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.repository.CityRepository;
import com.mishu.cgwy.operating.skipe.constant.SpikeActivityState;
import com.mishu.cgwy.operating.skipe.constant.SpikeState;
import com.mishu.cgwy.operating.skipe.domain.Spike;
import com.mishu.cgwy.operating.skipe.domain.SpikeItem;
import com.mishu.cgwy.operating.skipe.wrapper.SpikeItemWrapper;
import com.mishu.cgwy.operating.skipe.wrapper.SpikeWrapper;
import com.mishu.cgwy.order.wrapper.CartSkuStockOutWrapper;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.utils.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用于在缓存一些数据提高秒杀活动的效率
 * Created by king-ck on 2016/1/13.
 */
@Service
public class SpikeCacheService {
    private static Logger LOG = LoggerFactory.getLogger(SpikeCacheService.class);

    @Autowired
    private SpikeService spikeService;
    @Resource(name = "stringRedisTemplate")
    private ValueOperations<String, String> vOps;
    @Resource(name = "stringRedisTemplate")
    private HashOperations hOps;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CityRepository cityRepository;

    private ObjectMapper objectMapper = new ObjectMapper();


    public final static String S_SEPARATOR=",";
    public final static String SPIKES_NOT_EXPIRE_KEY="$SPIKES_NOT_EXPIRE_%s"; //  参数为cityid ， val 存放这个城市未开始和已开始的 秒杀活动id
    public final static long   SPIKES_NOT_EXPIRE_KEY_EXPIRE=1;//小时

    public final static String SPIKE_KEY="$SPIKE_%s"; //$SPIKE_id
    public final static String SPIKE_VAL_HASHKEY="$SPIKE_VAL"; //
    public final static String SPIKE_ITEMIDS_HASHKEY="$SPIKE_ITEMIDS_%s"; //



    public final static String SPIKEITEM_KEY="$SPIKEITEM_%s"; //

    public final static String SPIKEITEM_CUSTOMER_TAKENUM_KEY="$SPIKEITEM_$CUSTOMER_TAKENUM_%s_%s"; //$CUSTOMER_SPIKEITEM_TAKENUM_id
    public final static long SPIKEITEM_CUSTOMER_TAKENUM_EXPIRE=24;//
//
//    public void removeCacheBySpikeOutOf(List<CartSkuStockOutWrapper> stockOut) {
//        for(CartSkuStockOutWrapper sso : stockOut){
//            this.removeSpikeItemCache(sso.);
//        }
//    }

    public void removeSpikeNotExpire(Long cityId){
        try{
            String key =String.format(SPIKES_NOT_EXPIRE_KEY,cityId);

            redisTemplate.delete(key);
        }catch(Exception ex){
            LOG.error(String.format("removeSpikeNotExpire city:%s",ex));
        }
    }
    public void removeSpikeCache(Long spikeId){
        try {
            String key =String.format(SPIKE_KEY,spikeId);
            redisTemplate.delete(key);
        }catch(Exception ex){
            LOG.error(String.format("removeSpikeCache spikeId:%s",spikeId),ex);
        }
    }


    public void removeItemCacheBySpikeId(Long spikeId){
        try {
            Spike spike = this.spikeService.getSpike(spikeId);

            for(SpikeItem item : spike.getItems()){
                String key =String.format(SPIKEITEM_KEY,item.getId());
                redisTemplate.delete(key);
            }
        }catch(Exception ex){
            LOG.error(String.format("removeSpikeItemCache spikeItemId:%s",spikeId),ex);
        }
    }

    public void removeSpikeItemCache(Long spikeItemId){
        try {
            String key =String.format(SPIKEITEM_KEY,spikeItemId);
            redisTemplate.delete(key);
        }catch(Exception ex){
            LOG.error(String.format("removeSpikeItemCache spikeItemId:%s",spikeItemId),ex);
        }
    }

    public void removeAllSpike() {
        List<City> citys = cityRepository.findAll();
        for(City city : citys){
            this.removeSpikeNotExpire(city.getId());
        }
        List<Spike> spikes = spikeService.getAllSpike();
        for(Spike spike : spikes){
            this.removeSpikeCache(spike.getId());
        }
        List<SpikeItem> items = spikeService.getAllSpikeItem();
        for(SpikeItem item : items){
            this.removeSpikeItemCache(item.getId());
        }
    }

    public List<SpikeWrapper> getSpikes( SpikeActivityState... activityState) {
        List<City> citys = cityRepository.findAll();
        List<SpikeWrapper> spikes =new ArrayList<>();
        for(City city : citys){
            spikes.addAll(this.getSpikes(city,activityState));
        }
        return spikes;
    }

    public List<SpikeWrapper> getSpikes(final City city, SpikeActivityState... activityState) {
        try {
            String key = String.format(SPIKES_NOT_EXPIRE_KEY, city.getId());
            String sIds =RedisUtils.readVal(vOps, SPIKES_NOT_EXPIRE_KEY_EXPIRE, TimeUnit.HOURS, key, new RedisUtils.ValGetter<String>() {
                @Override
                public String getVo() {
                    List<Long> spikes = spikeService.getSpikeIds(city, SpikeActivityState.unStart, SpikeActivityState.process);
                    String spikeIds = StringUtils.join(spikes,S_SEPARATOR);
                    return spikeIds;
                }
            });
            if(StringUtils.isBlank(sIds)){
                return Collections.emptyList();
            }
            String[] spikeIds = sIds.split(S_SEPARATOR);
            List<SpikeWrapper> spikes = new ArrayList<>();
            for(String sid : spikeIds){
                SpikeWrapper spike = this.getSpike(Long.parseLong(sid));
                boolean inState =SpikeActivityState.checkSpikeState(spike,activityState);
                if(inState){
                    spikes.add(spike);
                }
            }
            return spikes;
        } catch (Exception e) {
            LOG.error("getSpikes",e);
        }
        List<Spike> spikes = spikeService.getSpikeList(city,activityState);

        return SpikeWrapper.toWrappers(spikes);
    }


    public SpikeWrapper getSpikeByItemId(Long spikeItemId) {
        SpikeItemWrapper sitem = this.getSpikeItem(spikeItemId);
        return this.getSpike(sitem.getSpikeId());
    }

    public SpikeWrapper getSpike(final Long spikeId){

        String key =String.format(SPIKE_KEY,spikeId);
        try {
            String val =RedisUtils.readHashValForJson(hOps, key, SPIKE_VAL_HASHKEY, new RedisUtils.ValGetter<SpikeWrapper>() {
                @Override
                public SpikeWrapper getVo() {
                    Spike spike = spikeService.getSpike(spikeId);
                    return new SpikeWrapper(spike);
                }
            });
            return objectMapper.readValue(val,SpikeWrapper.class);
        } catch (Exception e) {
            LOG.error(String.format("getSpike spikeId：%s",spikeId),e);
            this.removeSpikeCache(spikeId);
        }

        Spike spike = spikeService.getSpike(spikeId);
        return new SpikeWrapper(spike);
    }

    public List<SpikeItemWrapper> getSpikeItemsBySpikeId(final Long spikeId){
        String key =String.format(SPIKE_KEY,spikeId);
        try {
            String val =RedisUtils.readHashVal(hOps, key, SPIKE_ITEMIDS_HASHKEY, new RedisUtils.ValGetter<String>() {
                @Override
                public String getVo() {
                    List<Long> itemIds = new ArrayList<Long>();
                    Spike spike = spikeService.getSpike(spikeId);
                    List<SpikeItem> items = spike.getItems();
                    for (SpikeItem item : items) {
                        itemIds.add(item.getId());
                    }
                    return StringUtils.join(itemIds, S_SEPARATOR);
                }
            });
            String[] spikeItemIds = val.split(S_SEPARATOR);
            List<SpikeItemWrapper> items = new ArrayList();
            for(String spikeItemId : spikeItemIds){
                SpikeItemWrapper sitem = this.getSpikeItem(Long.parseLong(spikeItemId));
                items.add(sitem);
            }
            return items;
        } catch (Exception e) {
            LOG.error(String.format("getSpikeItems spikeId：%s",spikeId),e);
            this.removeSpikeCache(spikeId);
        }
        List<SpikeItem> items = spikeService.getSpikeItemsBySpikeId(spikeId);
        return SpikeItemWrapper.toWrappers(items);
    }

    public SpikeItemWrapper getSpikeItem(final Long spikeItemId) {
        try {
            String itemKey = String.format(SPIKEITEM_KEY, spikeItemId);
            String val =RedisUtils.readValForJson(vOps, itemKey, new RedisUtils.ValGetter<SpikeItemWrapper>() {
                @Override
                public SpikeItemWrapper getVo() {
                    SpikeItem spikeItem = spikeService.getSpikeItem(spikeItemId);
                    return new SpikeItemWrapper(spikeItem,null);
                }
            });
            return objectMapper.readValue(val,SpikeItemWrapper.class );
        }catch(Exception ex){
            LOG.error(String.format("getSpikeItem spikeItemi的：%s", spikeItemId),ex);
            this.removeSpikeItemCache(spikeItemId);
        }
        SpikeItem spikeItem = spikeService.getSpikeItem(spikeItemId);
        return new SpikeItemWrapper(spikeItem, null);
    }

    public Integer getCustomerSpikeTakeNum(final Customer customer, final Long spikeItemId) {
        try {
            String customerPerKey = String.format(SPIKEITEM_CUSTOMER_TAKENUM_KEY, customer.getId(), spikeItemId);
            String val =RedisUtils.readVal(vOps, SPIKEITEM_CUSTOMER_TAKENUM_EXPIRE, TimeUnit.HOURS, customerPerKey, new RedisUtils.ValGetter<String>() {
                @Override
                public String getVo() {
                    Long num = spikeService.getSpikeCustomerCurrentNum(customer, spikeItemId);
                    return num==null ? null : num.toString();
                }
            });
            return Integer.valueOf(val);
        }catch (Exception ex){
            LOG.error(String.format("getCustomerSpikeTakeNum  customerId:%s, spikeItemId:%s", customer.getId(), spikeItemId),ex);

        }
        Integer num = spikeService.getSpikeCustomerCurrentNum(customer, spikeItemId).intValue();
        return num;
    }

    //递增当前用户活动商品的购买数量
    public void incrLimitByCustomer(Customer customer, Long spikeItemId, Integer quantity) {
        try {
            String customerPerKey = String.format(SPIKEITEM_CUSTOMER_TAKENUM_KEY, customer.getId(), spikeItemId);
            vOps.increment(customerPerKey, quantity);
        }catch(Exception ex){
            LOG.error(String.format("incrLimitByCustomer  customerId:%s, spikeItemId:%s ,quantity:%s", customer.getId(), spikeItemId, quantity),ex);
        }
    }

    //预检查秒杀商品时候剩余库存
    public boolean checkItemSurplus(Integer quantity, Long spikeItemId) {
        SpikeItemWrapper item= this.getSpikeItem(spikeItemId);
        return item.getTakeNum()+quantity<=item.getNum();
    }

    //预检查是否满足根据客户限量
    public boolean checkItemLimitByCustomer(Customer customer, Integer quantity, Long spikeItemId) {
        SpikeItemWrapper spikeItem = this.getSpikeItem(spikeItemId);
        if(spikeItem.getPerMaxNum()==null ){
            return true;
        }
        Integer csPerNum = this.getCustomerSpikeTakeNum(customer, spikeItemId);
        return csPerNum+quantity <= spikeItem.getPerMaxNum();
    }

    public SpikeState getSpikeStateByItemId(Long spikeItemId) {
        SpikeItemWrapper spikeItem = this.getSpikeItem(spikeItemId);
        SpikeWrapper spikeWrapper = this.getSpike(spikeItem.getSpikeId());
        return SpikeState.fromInt(spikeWrapper.getState());
    }



}
