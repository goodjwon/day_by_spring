package com.example.patten.proxy.before;

import org.junit.jupiter.api.Test;

public class MixedConcernTest {
    @Test
    void testEventService() {
        System.out.println("\n👎 Before: 프록시 패턴을 사용하지 않은 코드");
        EventService eventService = new EventService();
        eventService.processEvent("신규 회원 가입");
    }
}