package com.mishu.cgwy.utils;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.stock.wrapper.StockOutOrderWrapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linsen on 16/3/11.
 *
 * 根据坐标计算线路
 */
public class LineUtils {

    public static void lineSortByOrder(double[] start, List<StockOutOrderWrapper> orderList , List<StockOutOrderWrapper> rsList){

        double rs = 0.0d;
        StockOutOrderWrapper tmpOrder = null;
        for(int i=0; i<orderList.size(); i++){
            StockOutOrderWrapper orderGroup = orderList.get(i);
            double mLat = orderGroup.getRestaurant().getAddress().getWgs84Point().getLatitude();
            double mLon = orderGroup.getRestaurant().getAddress().getWgs84Point().getLongitude();

            double tmp = distance(start, new double[]{mLat,mLon});
            if(rs == 0 || rs > tmp){
                rs = tmp;
                tmpOrder = orderGroup;
            }
        }
        rsList.add(tmpOrder);
        if(orderList.size() > 1){
            orderList.remove(tmpOrder);
            double _mLat = tmpOrder.getRestaurant().getAddress().getWgs84Point().getLatitude();
            double _mLon = tmpOrder.getRestaurant().getAddress().getWgs84Point().getLongitude();
            lineSortByOrder(new double[]{_mLat, _mLon}, orderList, rsList);
        }
    }

    /**
     * start : [lat,lon]
     * latAndLon : [lat,lon],[lat,lon],[lat,lon] ...
     * */
    public static void lineSort(double[] start , List<double[]> latAndLon, List<double[]> rsLatAndLon){

        double rs = 0.0d;
        double[] tmpLatLon = null;
        for(int i=0;i<latAndLon.size();i++){
            System.out.println("size : " + latAndLon.size());
            double tmp = distance(start, latAndLon.get(i));
            if(rs == 0 || rs > tmp){
                rs = tmp;
                tmpLatLon = latAndLon.get(i);
            }
        }
        rsLatAndLon.add(tmpLatLon);
        if(latAndLon.size() > 1){
            latAndLon.remove(tmpLatLon);
            lineSort(tmpLatLon, latAndLon ,rsLatAndLon);
        }
    }

    public static double distance(double[] start , double[] end){

        double pk = 180 / 3.14169;
        double startLat = start[0] / pk;
        double startLon = start[1] / pk;
        double endLat = end[0] / pk;
        double endLon = end[1] / pk;
        double t1 = Math.cos(startLat) * Math.cos(startLon) * Math.cos(endLat) * Math.cos(endLon);
        double t2 = Math.cos(startLat) * Math.sin(startLon) * Math.cos(endLat) * Math.sin(endLon);
        double t3 = Math.sin(startLat) * Math.sin(endLat);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    public static void main(String[] args){

//        double[] start = {37.480563,121.467113};
//        double[] end = {37.480591,121.467926};
//        System.out.println(distance(start,end));


        double[] start = {40.071847,116.354826}; //龙旗广场永辉超市美食城
        List<double[]> list = new ArrayList<double[]>();
        list.add(new double[]{40.017498, 116.422485}); //大屯路北苑k酷广场4层美食广场无名缘米粉
        list.add(new double[]{40.036477, 116.283542}); //圆明园西路沙县小吃（百旺商城南侧）
        list.add(new double[]{40.039239, 116.425584}); //北苑卜峰莲花4层美食城

        List<double[]> res = new ArrayList<double[]>();
        lineSort(start, list, res);
        System.out.println(res);
        for(int i=0;i<res.size();i++){
            double[] a = (double[])res.get(i);

            System.out.println(a[0] + " | " + a[1]);
        }
    }
}
