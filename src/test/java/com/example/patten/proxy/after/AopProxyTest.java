package com.example.patten.proxy.after;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootTest(classes = {EventService.class, PerformanceAspect.class})
@EnableAspectJAutoProxy  // 테스트에서는 명시적으로 필요함
public class AopProxyTest {

    @Autowired
    private EventService eventService;

    @Test
    void proxyTest() {
        System.out.println("\n👍 After: Spring AOP (프록시 패턴) 테스트");
        eventService.processEvent("애플리케이션 로그인");
    }
}