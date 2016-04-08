package com.mishu.cgwy.admin.facade;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

public class AdminUserFacadeTest {

    @Test
    public void testRegister() throws Exception {

    }

    @Test
    public void testUpdatePassword() throws Exception {
    	
    }
    
    @Test
    public void calcuatePassword() {
    	String username = "yuhongmei";
		String password = "wawj808";
		
		String str = username + password + "mirror";
		System.out.println(str);
		str = DigestUtils.md5Hex(str);
		System.out.println(str);
    }
}