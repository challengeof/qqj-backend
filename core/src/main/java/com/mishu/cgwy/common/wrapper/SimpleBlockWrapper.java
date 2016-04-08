package com.mishu.cgwy.common.wrapper;

import com.mishu.cgwy.common.domain.Block;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xingdong on 15/8/5.
 */
@Data
public class SimpleBlockWrapper {

    private Long id;

    private String name;

    private boolean active;

    private List<PointWrapper> points = new ArrayList<>();

    public SimpleBlockWrapper(){}

    public SimpleBlockWrapper(Block block) {
        this.id = block.getId();
        this.name = block.getName();
        this.active = block.isActive();

        if(StringUtils.isNotBlank(block.getPointStr())){
            String[]arr = block.getPointStr().trim().split(";");
            for(String s:arr){
                String[]datas = s.split(",");
                points.add(new PointWrapper(datas[0],datas[1]));
            }
        }
    }
}
