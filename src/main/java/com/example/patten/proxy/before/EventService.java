package com.example.patten.proxy.before;

public class EventService {
    public void processEvent(String eventName) {
        long startTime = System.currentTimeMillis(); // 부가 기능

        // 핵심 비즈니스 로직
        System.out.println("이벤트 처리 시작: " + eventName);
        try {
            Thread.sleep(1000); // 1초간의 복잡한 처리 시뮬레이션
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("이벤트 처리 완료.");
        // 핵심 비즈니스 로직 끝

        long endTime = System.currentTimeMillis(); // 부가 기능
        System.out.println("== 실행 시간: " + (endTime - startTime) + "ms =="); // 부가 기능
    }
}