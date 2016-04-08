package com.mishu.cgwy.push.service;

import com.mishu.cgwy.push.domain.PushMapping;
import com.mishu.cgwy.push.repository.PushMappingRepository;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * User: xudong
 * Date: 6/28/15
 * Time: 9:35 PM
 */
@Service
public class PushMappingService {
    @Autowired
    private PushMappingRepository pushMappingRepository;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public PushMapping savePushMapping(Long customerId, String baiduChannelId, String platform ) {
        final List<PushMapping> list = pushMappingRepository.findByBaiduChannelId(baiduChannelId);
        if (list.isEmpty()) {
            PushMapping pushMapping = new PushMapping();
            pushMapping.setBaiduChannelId(baiduChannelId);
            pushMapping.setCustomerId(customerId);
            pushMapping.setPlatform(platform);
            return pushMappingRepository.save(pushMapping);
        } else {
            PushMapping pushMapping = list.get(0);
            pushMapping.setCustomerId(customerId);
            pushMapping.setPlatform(platform);
            return pushMappingRepository.save(pushMapping);
        }
    }

    @Transactional(readOnly = true)
    public List<PushMapping> findByCustomerId(Long id) {
        return pushMappingRepository.findByCustomerId(id);
    }


    @Transactional(readOnly = true)
    public List<Object[]> getWXPushIds(Long cityid){
        String sql = "select p.baidu_channel_id  from push_mapping p left join customer c on p.customer_id = c.id " +
                "where  c.city_id = " + cityid +
                " and p.platform='weixin' group by baidu_channel_id  ";

        Query sqlQuery = entityManager.createNativeQuery(sql);
        List<Object[]> result = sqlQuery.getResultList();
        if(result == null || result.size() == 0)
            return null;
        return result;
    }


    private static void showResponseResult(HttpResponse response)
    {
        if (null == response)
        {
            return;
        }

        HttpEntity httpEntity = response.getEntity();
        try
        {
            InputStream inputStream = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));
            String result = "";
            String line = "";
            while (null != (line = reader.readLine()))
            {
                result += line;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
