package com.mishu.cgwy.antispam.facade;

import com.mishu.cgwy.admin.wrapper.AdminUserWrapper;
import com.mishu.cgwy.order.wrapper.OrderWrapper;
import com.mishu.cgwy.profile.wrapper.RestaurantWrapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: xudong
 * Date: 8/26/15
 * Time: 5:13 PM
 */
public class AntiSpamMain {
    public static void main(String[] args) throws ParseException, IOException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"/application-persist.xml",
                "/application-context.xml", "/application-security.xml", "/application-message.xml",
                "/application-search.xml"});


        final AntiSpamFacade bean = applicationContext.getBean(AntiSpamFacade.class);
        final Map<AdminUserWrapper, Map<RestaurantWrapper, List<OrderWrapper>>> suspectFakeRestaurant = bean
                .findSuspectFakeRestaurant(DateUtils.parseDate(args[0], new String[]{"yyyy-MM-dd"}));
//        final Map<RestaurantWrapper, List<OrderWrapper>> suspectDealer = bean.findSuspectDealer(DateUtils.parseDate
//                (args[0], new String[]{"yyyy-MM-dd"}));

        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("template/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-8");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);


        List<OrderWrapper> suspectFakeOrder = new ArrayList<>();

        for (Map<RestaurantWrapper, List<OrderWrapper>> map : suspectFakeRestaurant.values()) {
            for (List<OrderWrapper> l : map.values()) {
                suspectFakeOrder.addAll(l);
            }
        }

//        List<OrderWrapper> suspectDealerOrder = new ArrayList<>();

//        for (List<OrderWrapper> l : suspectDealer.values()) {
//            suspectDealerOrder.addAll(l);
//        }


        Context context = new Context();

        context.setVariable("suspectFakeOrder", suspectFakeOrder);

//        context.setVariable("suspectDealerOrder", suspectDealerOrder);


        final FileOutputStream suspectFakeRestaurantOutput = new FileOutputStream("suspectFakeRestaurant" + args[0] +
                ".html");
        IOUtils.write(templateEngine.process("fakeRestaurant", context), suspectFakeRestaurantOutput);
        suspectFakeRestaurantOutput.close();

//        final FileOutputStream suspectDealerOutput = new FileOutputStream("suspectDealer" + args[0] +
//                ".html");
//        IOUtils.write(templateEngine.process("dealer", context), suspectDealerOutput);
//        suspectDealerOutput.close();



        applicationContext.close();

    }
}
