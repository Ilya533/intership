package com.innowise.minispring;

public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
}
