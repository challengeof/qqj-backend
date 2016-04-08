package com.mishu.cgwy.common.service;

import com.mishu.cgwy.common.domain.*;
import com.mishu.cgwy.common.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: xudong
 * Date: 2/28/15
 * Time: 2:33 PM
 */
@Service
@Transactional
public class SystemPropertiesService {
    @Autowired
    private SystemPropertiesRepository systemPropertiesRepository;

    @Transactional(readOnly = true)
    public Map<String, String> getAllSystemProperties() {
        final List<SystemProperties> all = systemPropertiesRepository.findAll();

        Map<String, String> map = new HashMap<>();
        for (SystemProperties s : all) {
            map.put(s.getName(), s.getValue());
        }

        return map;
    }
}

