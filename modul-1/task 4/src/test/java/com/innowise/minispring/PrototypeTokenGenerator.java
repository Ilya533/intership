package com.innowise.minispring;

import java.util.UUID;

@Component
@Scope("prototype")
public class PrototypeTokenGenerator {
    public String newToken() {
        return UUID.randomUUID().toString();
    }
}