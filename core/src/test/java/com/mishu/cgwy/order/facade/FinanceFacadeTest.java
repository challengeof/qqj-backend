package com.mishu.cgwy.order.facade;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/application-persist.xml", "/application-context.xml", "/application-security.xml"})
public class FinanceFacadeTest {

//    @Autowired
//    private FinanceFacade financeFacade;
    @Test
    public void testGenerateExcel() throws Exception {
//        financeFacade.generateExcel(DateUtils.parseDate("2015-05-03", new String[]{"yyyy-MM-dd"}), 1L);

    }
}