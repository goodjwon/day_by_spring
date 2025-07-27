package com.example.patten.template.before;

import org.testng.annotations.Test;

public class DataProcessorTest {
    @Test
    void testProcessing() {
        CsvDataProcessor csvProcessor = new CsvDataProcessor();
        csvProcessor.process();

        TxtDataProcessor txtProcessor = new TxtDataProcessor();
        txtProcessor.process();
    }
}