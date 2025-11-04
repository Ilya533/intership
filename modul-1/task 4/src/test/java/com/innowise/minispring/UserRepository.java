package com.innowise.minispring;

@Component
public class UserRepository implements InitializingBean {
    private boolean initialized;

    @Override
    public void afterPropertiesSet() {
        initialized = true;
        System.out.println("UserRepository initialized = " + initialized);
    }

    public String findUsernameById(long id) {
        return "user-" + id;
    }
}