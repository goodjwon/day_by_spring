package com.example.patten.adapter.before;

import com.example.patten.adapter.DataProcessor;

// 기존에 있던 XML 처리기 - 우리 시스템 표준 인터페이스를 구현
public class XmlDataProcessor implements DataProcessor {
    @Override
    public void processData() {
        System.out.println("기존 방식으로 XML 데이터를 처리합니다.");
    }
}
