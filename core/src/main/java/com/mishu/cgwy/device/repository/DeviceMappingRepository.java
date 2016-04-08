package com.mishu.cgwy.device.repository;

import com.mishu.cgwy.device.domain.DeviceMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceMappingRepository extends JpaRepository<DeviceMapping, Long> {

    public List<DeviceMapping> findByDeviceId(String deviceId);


}
