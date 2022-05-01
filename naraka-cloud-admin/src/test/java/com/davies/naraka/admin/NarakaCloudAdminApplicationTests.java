package com.davies.naraka.admin;

import com.davies.naraka.admin.domain.entity.Order;
import com.davies.naraka.admin.service.IOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

@SpringBootTest
@ActiveProfiles("dev")
public class NarakaCloudAdminApplicationTests {

    @Autowired
    private IOrderService orderService;

    @Test
    public void contextLoads() {
        Order order = new Order();
        order.setPaymentType(1);
        order.setCreatedTime(new Date());
        order.setCreatedBy("root");
        orderService.save(order);
    }

}
