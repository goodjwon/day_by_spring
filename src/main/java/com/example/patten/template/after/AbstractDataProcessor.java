package com.example.patten.template.after;

public abstract class AbstractDataProcessor {

    // 이것이 바로 '템플릿 메서드'입니다. 알고리즘의 전체 흐름을 제어합니다.
    public final void process() {
        System.out.println("--- " + getClass().getSimpleName() + " 실행 ---");
        // 1. 데이터 조회 (공통 로직)
        String data = selectData();

        // 2. 데이터 가공 (하위 클래스에 위임)
        String processedData = transformData(data);

        // 3. 파일 저장 (공통 로직)
        saveData(processedData);
        System.out.println("----------------------------------\n");
    }

    // 공통 로직은 private으로 구현하여 하위 클래스가 수정하지 못하게 막을 수 있습니다.
    private String selectData() {
        System.out.println("[공통] 데이터베이스에서 데이터를 조회합니다.");
        return "id,name,role";
    }

    private void saveData(String data) {
        System.out.println("[공통] 변환된 데이터를 파일에 씁니다: " + data);
        String fileName = getFileName(); // 파일 이름은 하위 클래스에서 결정
        System.out.println("[저장] " + fileName + " 파일이 성공적으로 생성되었습니다.");
    }

    // 하위 클래스가 반드시 구현해야 하는 '추상 메서드'
    protected abstract String transformData(String data);
    protected abstract String getFileName();
}