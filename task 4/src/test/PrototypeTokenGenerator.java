package test;

import minispring.Component;
import minispring.Scope;

import java.util.UUID;

@Component
@Scope("prototype")
public class PrototypeTokenGenerator {
    public String newToken() {
        return UUID.randomUUID().toString();
    }
}
