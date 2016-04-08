package com.mishu.cgwy.device;

import com.mishu.cgwy.device.repository.DeviceMappingRepository;
import com.mishu.cgwy.device.domain.DeviceMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: xudong
 * Date: 6/28/15
 * Time: 9:35 PM
 */
@Service
public class DeviceMappingService {
    @Autowired
    private DeviceMappingRepository deviceMappingRepository;

    @Transactional
    public DeviceMapping saveDeviceMapping(Long customerId, String platform, String deviceId) {
        final List<DeviceMapping> list = deviceMappingRepository.findByDeviceId(deviceId);
        if (list.isEmpty()) {
            DeviceMapping pushMapping = new DeviceMapping();
            pushMapping.setDeviceId(deviceId);
            pushMapping.setPlatform(platform);
            pushMapping.setCustomerId(customerId);
            return deviceMappingRepository.save(pushMapping);
        } else {
            DeviceMapping pushMapping = list.get(0);
            pushMapping.setCustomerId(customerId);
            pushMapping.setPlatform(platform);
            return deviceMappingRepository.save(pushMapping);
        }
    }
}
