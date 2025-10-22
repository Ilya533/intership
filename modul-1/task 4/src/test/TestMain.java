package test;

import minispring.ApplicationContext;

public class TestMain {
    public static void main(String[] args) {
        ApplicationContext ctx = new ApplicationContext("test");

        UserService userService = ctx.getBean(UserService.class);
        System.out.println(userService.getGreeting(42));

        PrototypeTokenGenerator t1 = ctx.getBean(PrototypeTokenGenerator.class);
        PrototypeTokenGenerator t2 = ctx.getBean(PrototypeTokenGenerator.class);
        System.out.println("Different instances? " + (t1 != t2));
        System.out.println("Tokens: " + t1.newToken() + " | " + t2.newToken());
    }
}