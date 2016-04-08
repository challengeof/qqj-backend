package com.mishu.cgwy.common.service;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Region;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/application-persist.xml", "/application-context.xml", "/application-security.xml"})
public class LocationServiceTest {
    @Autowired
    private LocationService locationService;

    @Test
    @Transactional
    @Rollback
    public void testLocations() throws Exception {
        City city = new City();
        city.setName("beijing");

        final City beijing = locationService.saveCity(city);

        Assert.assertNotNull(city.getId());

        Region region1 = new Region();
        region1.setName("shangdi");
        region1.setCity(city);

        region1 = locationService.saveRegion(region1);

        Region region2 = new Region();
        region2.setCity(city);
        region2.setName("qinghe");

        region2 = locationService.saveRegion(region2);

    }
}