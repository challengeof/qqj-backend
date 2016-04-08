package com.mishu.cgwy.conf.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.conf.controller.SaveConfRequest;
import com.mishu.cgwy.conf.domain.Conf;
import com.mishu.cgwy.conf.repository.ConfRepository;
import com.mishu.cgwy.profile.domain.Feedback;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bowen on 15-5-26.
 */
@Service
public class ConfService {

    @Autowired
    private ConfRepository confRepository;

    private static String EMPTY_JSON = "{}";

    private ObjectMapper objectMapper = new ObjectMapper();

    public Conf getOne(Long id) {
        return confRepository.getOne(id);
    }

    public Conf findByName(String name) {
        List<Conf> confList = confRepository.findByName(name);
        return CollectionUtils.isNotEmpty(confList) ? confList.get(0) : null;
    }

    public String getValue(Conf conf) {
        return conf == null ? EMPTY_JSON : conf.getValue();
    }

    public Map<String, String> getConfMap(Conf conf) throws Exception {
        return objectMapper.readValue(getValue(conf), Map.class);
    }

    public Map<String, String> getConfMap(String name) throws Exception {
        return objectMapper.readValue(getValue(findByName(name)), Map.class);
    }

    @Transactional
    public void save(SaveConfRequest saveConfRequest) throws Exception {

        String name = saveConfRequest.getName();
        String key = saveConfRequest.getKey();
        String value = saveConfRequest.getValue();
        Conf conf = findByName(name);
        Map confMap = getConfMap(conf);
        confMap.put(key, value);
        conf.setValue(JSONObject.valueToString(confMap));

        confRepository.save(conf);
    }
}
