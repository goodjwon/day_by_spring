package com.example.ioc.before;

class ChefWithIoC {
    private Beef beef;
    private Onion onion;
    private Salt salt;

    // 생성자를 통해 외부에서 재료들을 주입받음 (제어권이 외부에 있음)
    public ChefWithIoC(Beef beef, Onion onion, Salt salt) {
        this.beef = beef;
        this.onion = onion;
        this.salt = salt;
        System.out.println("👨‍🍳 Chef가 준비된 재료를 받았습니다.");
    }

    public void cook() {
        System.out.println("🍳 " + beef.getName() + ", " + onion.getName() +
                ", " + salt.getName() + "으로 요리를 시작합니다.");
    }
}

// IoC 컨테이너 역할을 하는 KitchenManager
class KitchenManager {

    public ChefWithIoC createChef() {
        System.out.println("🏪 KitchenManager가 재료들을 준비합니다...");

        // KitchenManager가 모든 재료를 준비
        Beef beef = new Beef();
        Onion onion = new Onion();
        Salt salt = new Salt();

        System.out.println("📦 재료 준비 완료!");

        // Chef에게 준비된 재료들을 전달 (의존성 주입)
        return new ChefWithIoC(beef, onion, salt);
    }
}

public class ManualIoCExample {
    public static void main(String[] args) {
        System.out.println("=== 수동 IoC 적용 실행 ===");
        KitchenManager manager = new KitchenManager();
        ChefWithIoC chef = manager.createChef();
        chef.cook();
    }
}