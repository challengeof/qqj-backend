package com.mishu.cgwy.xls;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * User: xudong
 * Date: 4/26/15
 * Time: 11:46 AM
 */
public class JxlsTest {
    @Test
    public void test() throws IOException, InvalidFormatException {
        final File f = new File("/Users/xudong/workspace/mitree/cgwy/core/src/main/resources/template/summary-template.xls");
        final Workbook file = WorkbookFactory.create(f);

        final Workbook stream = WorkbookFactory.create(new FileInputStream(f));



        System.out.println(file);
        System.out.println(stream);


    }


}
