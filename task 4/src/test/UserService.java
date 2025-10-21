package test;

import minispring.*;

@Component
public class UserService implements InitializingBean {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void afterPropertiesSet() {
        System.out.println("UserService wired with UserRepository = " + (userRepository != null));
    }

    public String getGreeting(long id) {
        return "Hello, " + userRepository.findUsernameById(id);
    }
}
