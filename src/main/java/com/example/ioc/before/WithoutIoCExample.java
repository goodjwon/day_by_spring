package com.example.ioc.before;

class Beef {
    public String getName() {
        return "소고기";
    }
}

class Onion {
    public String getName() {
        return "양파";
    }
}

class Salt {
    public String getName() {
        return "소금";
    }
}

class ChefWithoutIoC {
    private Beef beef;
    private Onion onion;
    private Salt salt;

    public ChefWithoutIoC() {
        // Chef가 직접 모든 재료를 준비함 (제어권이 Chef에게 있음)
        this.beef = new Beef();
        this.onion = new Onion();
        this.salt = new Salt();
        System.out.println("👨‍🍳 Chef가 직접 재료를 준비했습니다.");
    }

    public void cook() {
        System.out.println("🍳 " + beef.getName() + ", " + onion.getName() +
                ", " + salt.getName() + "으로 요리를 시작합니다.");
    }
}

public class WithoutIoCExample {
    public static void main(String[] args) {
        System.out.println("=== IoC 없는 경우 실행 ===");
        ChefWithoutIoC chef = new ChefWithoutIoC();
        chef.cook();
    }
}
