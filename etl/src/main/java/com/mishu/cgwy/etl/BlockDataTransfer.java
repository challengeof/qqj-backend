package com.mishu.cgwy.etl;

import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.common.wrapper.PointWrapper;
import com.mishu.cgwy.common.wrapper.SimpleBlockWrapper;
import com.mishu.cgwy.profile.domain.Wgs84Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by xingdong on 15/8/14.
 */
@Service
public class BlockDataTransfer {

    Map<Long,List<Long>> maps = new HashMap<>();

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    private org.slf4j.Logger logger = LoggerFactory.getLogger(BlockDateTransfer.class);

    @Transactional
    public void getAllRestaurantPoints(Long cityId){
        int count = 0;
        int index = 0;
        List<Map<String, Object>>  lists = jdbcTemplate.queryForList("select c.id as customerId,c.admin_user_id as adminUserId,r.id as restaurantId," +
                "r.longitude,r.latitude from restaurant r left outer join customer " +
                "c on r.customer_id = c.id  where r.status=2 and c.city_id = " + cityId);
        Iterator<Map<String,Object>> mapIterator = lists.iterator();
        while(mapIterator.hasNext()){
            Map<String,Object> obj = mapIterator.next();
            Long customerId = Long.parseLong(obj.get("customerId").toString());
            Long restaurantId = Long .parseLong(obj.get("restaurantId").toString());
            Long adminUserId = null;
            try {
                adminUserId = Long.parseLong(obj.get("adminUserId").toString());
            }catch(Exception e){
                System.out.println("customerId:" + customerId + "\t");
            }
            Double longitude = null;
            Double latitude = null;
            Wgs84Point wgs84Point = new Wgs84Point();
            if(obj.get("longitude") != null && obj.get("latitude") != null) {
                longitude = Double.parseDouble(obj.get("longitude").toString());
                latitude = Double.parseDouble(obj.get("latitude").toString());
                wgs84Point.setLongitude(longitude);
                wgs84Point.setLatitude(latitude);

            }else{
                //没有经纬度的 TODO

                count++;
                continue;
            }

            List<Block> blocks = getAllBlocks(cityId);
            boolean flag = false;
            for(Block block:blocks){
                if(isExist2(wgs84Point,block)){
                    flag = true;
                    jdbcTemplate.update("update customer set block_id = "+block.getId()+" where id = "+customerId);
                    System.out.println("customer_id:" + customerId + "\t" + "block_id:" + block.getId());
                    break;
                }
            }

            if(!flag){
                List<Long> datas = maps.get(adminUserId);
                if(datas == null){
                    datas = new ArrayList<>();
                    datas.add(restaurantId);
                }else{
                    datas.add(restaurantId);
                }
                maps.put(adminUserId,datas);
                index++;
            }
        }

        Set<Long> keys = maps.keySet();
        Iterator<Long> iterator = keys.iterator();
        while(iterator.hasNext()){
            Long data =  iterator.next();
            System.out.println(data+":"+maps.get(data));
        }
        System.out.println("没有经纬度的人数:"+count);
        System.out.println("用户不在当前所分配的区块当中人数:" + index);
    }

    public List<Block> getAllBlocks(Long cityId){
        List<Block> blocks = new ArrayList<>();

        List<Map<String, Object>> list = jdbcTemplate.queryForList("select id, name, active, city_id, warehouse_id, point_str from block where id >99");
        for (int i = 0; i < list.size(); i ++) {
            Block block = new Block();
            Map<String, Object> map = list.get(i);
            block.setId(Long.parseLong(map.get("id").toString()));
            block.setName(map.get("name").toString());
            block.setActive(Boolean.getBoolean(map.get("active").toString()));
            block.setPointStr(map.get("point_str")!= null ? map.get("point_str").toString(): null);
            blocks.add(block);
        }
        return blocks;
    }


    public List<Wgs84Point> getPointsBlock(Block block){

        SimpleBlockWrapper blockWrapper = new SimpleBlockWrapper(block);
        List<PointWrapper> pointWrappers = blockWrapper.getPoints();
        List<Wgs84Point> points = new ArrayList<>();
        for(PointWrapper wrapper:pointWrappers){
            Wgs84Point wgs84Point = new Wgs84Point();
            try {
                Double lon =  Double.parseDouble(wrapper.getLongitude());
                wgs84Point.setLongitude(lon);
                Double lat =  Double.parseDouble(wrapper.getLatitude());
                wgs84Point.setLatitude(lat);
                points.add(wgs84Point);
            }catch(Exception e){
//                System.out.println(block.getId()+":"+wrapper.getLatitude()+","+wrapper.getLongitude());
            }
        }
        return points;
    }
    public boolean isExist(Wgs84Point test, Block block) {
        List<Wgs84Point> points =  getPointsBlock(block);
        int pointCount = points.size();
        int nCross = 0;
        for (int i = 0; i < pointCount; i ++) {
            Wgs84Point p1 = points.get(i);
            Wgs84Point p2 = points.get((i + 1) % pointCount);
            /*Wgs84Point p2 = null;
            for (int j = 0; j < pointCount; ++j) {
                p2 = points.get(j);
            }*/

            // 求解 y=p.y 与 p1 p2 的交点
            if ( p1.getLatitude() == p2.getLatitude() ) {   // p1p2 与 y=p0.y平行
                continue;
            }
            if ( test.getLatitude() < Math.min(p1.getLatitude(), p2.getLatitude()) ) { // 交点在p1p2延长线上
                continue;
            }
            if ( test.getLatitude() >= Math.max(p1.getLatitude(), p2.getLatitude()) ) { // 交点在p1p2延长线上
                continue;
            }
            // 求交点的 X 坐标
            double x = (test.getLatitude() - p1.getLatitude()) * (p2.getLongitude() - p1.getLongitude())
                    / (p2.getLatitude() - p1.getLongitude()) + p1.getLongitude();
            if ( x > test.getLongitude() ) { // 只统计单边交点
                nCross++;
            }

        }
        return (nCross%2==1);
    }


    public boolean isExist2(Wgs84Point p, Block block) {
        List<Wgs84Point> points =  getPointsBlock(block);

        double widthMax = 0;
        double lengthMax = 0;
        for (int i = 0; i< points.size(); i++) {
            widthMax = Math.max(widthMax, points.get(i).getLatitude());
            lengthMax = Math.max(lengthMax, points.get(i).getLongitude());
        }
        if (p.getLatitude() > widthMax || p.getLongitude() > lengthMax) {
            return false;
        }


        int pointCount = points.size();
        int nCross = 0;
        for (int i = 0; i < pointCount; i ++) {
            Wgs84Point p1 = points.get(i);
            Wgs84Point p2 = points.get((i + 1) % pointCount);


            if (p1.getLatitude() == p2.getLatitude())             //如果线段本身就是平行线，则看待测点是否在线上
            {
                if (p.getLatitude() == p1.getLatitude() && p.getLongitude() >= Math.min(p1.getLongitude(), p2.getLongitude()) && p.getLongitude() <= Math.max(p1.getLongitude(), p2.getLongitude())) {
                    return true;
                }
                continue;
            }
            if (p.getLatitude() < Math.min(p1.getLatitude(), p2.getLatitude()) || p.getLatitude() > Math.max(p1.getLatitude(), p2.getLatitude()))  //一定没有交点的情况
                continue;
            double x = (p.getLatitude() - p1.getLatitude()) * (p2.getLongitude() - p1.getLongitude()) / (p2.getLatitude() - p1.getLatitude()) + p1.getLongitude();            //几何知识
            if (x > p.getLongitude())        //有交点
                nCross++;
            else if (x == p.getLongitude())     //在线上
                return true;

        }

        return (nCross%2==1);
    }
}
