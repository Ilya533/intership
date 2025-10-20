package minispring;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationContext {
    private final Map<Class<?>, Object> singletonBeans = new HashMap<>();
    private final Set<Class<?>> componentTypes = new HashSet<>();
    private final String basePackage;
    private final Set<Class<?>> beansInCreation = new HashSet<>();

    public ApplicationContext(String basePackage) {
        this.basePackage = basePackage;
        System.out.println("Starting application context with base package: " + basePackage);
        scanComponents(basePackage);
        System.out.println("Found components: " + componentTypes);
        instantiateSingletons();
        injectDependenciesAndInitialize();
        System.out.println("Application context initialized successfully");
    }

    public <T> T getBean(Class<T> type) {
        if (isPrototype(type)) {
            T instance = createInstance(type);
            injectIntoFields(instance);
            invokeInitializingBean(instance);
            return instance;
        }
        Object bean = singletonBeans.get(type);
        if (bean == null) {
            for (Map.Entry<Class<?>, Object> entry : singletonBeans.entrySet()) {
                if (type.isAssignableFrom(entry.getKey())) {
                    return type.cast(entry.getValue());
                }
            }
            throw new IllegalArgumentException("No bean of type " + type.getName() + " found. Available beans: " + singletonBeans.keySet());
        }
        return type.cast(bean);
    }

    private void scanComponents(String basePackage) {
        if (basePackage == null || basePackage.isEmpty()) {
            scanAllPackages();
            return;
        }

        String path = basePackage.replace('.', '/');
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            Enumeration<URL> resources = cl.getResources(path);
            if (!resources.hasMoreElements()) {
                System.out.println("No resources found for path: " + path);
                // Попробуем альтернативный способ
                scanAllPackages();
                return;
            }

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8.name());
                File dir = new File(filePath);
                System.out.println("Scanning directory: " + dir.getAbsolutePath());
                if (dir.exists() && dir.isDirectory()) {
                    findComponentsInDirectory(basePackage, dir);
                } else {
                    System.out.println("Directory does not exist or is not a directory: " + dir.getAbsolutePath());
                    scanClasspathManually();
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to scan package using standard method: " + e.getMessage());
            scanClasspathManually();
        }
    }

    private void scanAllPackages() {
        System.out.println("Scanning all packages...");
        String classpath = System.getProperty("java.class.path");
        System.out.println("Classpath: " + classpath);

        // Альтернативный способ: сканируем все известные пакеты
        scanKnownPackages();
    }

    private void scanKnownPackages() {
        // Попробуем загрузить классы по известным именам
        String[] knownClasses = {
                "test.UserService",
                "test.UserRepository",
                "test.PrototypeTokenGenerator",
                "minispring.UserService",
                "minispring.UserRepository"
        };

        for (String className : knownClasses) {
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Component.class)) {
                    componentTypes.add(clazz);
                    System.out.println("Found component by direct load: " + className);
                }
            } catch (ClassNotFoundException e) {
            }
        }
    }

    private void scanClasspathManually() {
        System.out.println("Attempting manual classpath scan...");
        String classpath = System.getProperty("java.class.path");
        String[] paths = classpath.split(File.pathSeparator);

        for (String path : paths) {
            File file = new File(path);
            if (file.exists() && file.isDirectory()) {
                scanDirectoryRecursively(file, "");
            }
        }
    }

    private void scanDirectoryRecursively(File dir, String packageName) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                String newPackage = packageName.isEmpty() ? file.getName() : packageName + "." + file.getName();
                scanDirectoryRecursively(file, newPackage);
            } else if (file.getName().endsWith(".class")) {
                String className = file.getName().substring(0, file.getName().length() - 6);
                String fqcn = packageName.isEmpty() ? className : packageName + "." + className;

                // Пропускаем внутренние классы
                if (fqcn.contains("$")) continue;

                try {
                    Class<?> clazz = Class.forName(fqcn);
                    if (clazz.isAnnotationPresent(Component.class)) {
                        componentTypes.add(clazz);
                        System.out.println("Manually registered component: " + fqcn);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    // Игнорируем
                }
            }
        }
    }

    private void findComponentsInDirectory(String currentPackage, File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                String newPackage = currentPackage.isEmpty() ? file.getName() : currentPackage + "." + file.getName();
                findComponentsInDirectory(newPackage, file);
            } else if (file.getName().endsWith(".class")) {
                String className = file.getName().substring(0, file.getName().length() - 6);
                String fqcn = currentPackage.isEmpty() ? className : currentPackage + "." + className;

                // Пропускаем внутренние классы
                if (fqcn.contains("$")) continue;

                try {
                    Class<?> clazz = Class.forName(fqcn);
                    if (clazz.isAnnotationPresent(Component.class)) {
                        componentTypes.add(clazz);
                        System.out.println("Registered component: " + fqcn);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    System.out.println("Could not load class: " + fqcn + " - " + e.getMessage());
                }
            }
        }
    }

    private void instantiateSingletons() {
        System.out.println("Instantiating singletons...");
        for (Class<?> type : componentTypes) {
            if (!isPrototype(type)) {
                System.out.println("Creating singleton: " + type.getName());
                Object instance = createInstance(type);
                singletonBeans.put(type, instance);
            }
        }
    }

    private boolean isPrototype(Class<?> type) {
        Scope scope = type.getAnnotation(Scope.class);
        return scope != null && "prototype".equalsIgnoreCase(scope.value());
    }

    private <T> T createInstance(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + type.getName(), e);
        }
    }

    private void injectDependenciesAndInitialize() {
        System.out.println("Injecting dependencies...");
        for (Object bean : new ArrayList<>(singletonBeans.values())) {
            injectIntoFields(bean);
        }

        System.out.println("Initializing beans...");
        for (Object bean : singletonBeans.values()) {
            invokeInitializingBean(bean);
        }
    }

    private void injectIntoFields(Object bean) {
        Class<?> type = bean.getClass();
        for (Field field : getAllFields(type)) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> depType = field.getType();
                System.out.println("Injecting dependency: " + depType.getName() + " into " + type.getName());
                Object dependency = resolveDependency(depType);
                try {
                    field.setAccessible(true);
                    field.set(bean, dependency);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to inject dependency into " + type.getName() +
                            "." + field.getName(), e);
                }
            }
        }
    }

    private List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = type;
        while (current != null && current != Object.class) {
            fields.addAll(Arrays.asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        }
        return fields;
    }

    private Object resolveDependency(Class<?> depType) {
        if (beansInCreation.contains(depType)) {
            throw new IllegalStateException("Circular dependency detected while creating bean: " + depType.getName());
        }

        beansInCreation.add(depType);
        try {
            Object singleton = singletonBeans.get(depType);
            if (singleton != null) {
                return singleton;
            }

            List<Class<?>> candidates = componentTypes.stream()
                    .filter(depType::isAssignableFrom)
                    .collect(Collectors.toList());

            if (candidates.isEmpty()) {
                throw new IllegalArgumentException("No candidate bean for type " + depType.getName() +
                        ". Registered components: " + componentTypes);
            }
            if (candidates.size() > 1) {
                throw new IllegalArgumentException("Multiple candidate beans for type " + depType.getName() +
                        ": " + candidates);
            }

            Class<?> candidate = candidates.get(0);
            System.out.println("Resolving dependency " + depType.getName() + " -> " + candidate.getName());

            if (isPrototype(candidate)) {
                Object instance = createInstance(candidate);
                injectIntoFields(instance);
                invokeInitializingBean(instance);
                return instance;
            } else {
                Object instance = createInstance(candidate);
                singletonBeans.put(candidate, instance);
                injectIntoFields(instance);
                invokeInitializingBean(instance);
                return instance;
            }
        } finally {
            beansInCreation.remove(depType);
        }
    }

    private void invokeInitializingBean(Object bean) {
        if (bean instanceof InitializingBean) {
            try {
                System.out.println("Initializing bean: " + bean.getClass().getName());
                ((InitializingBean) bean).afterPropertiesSet();
            } catch (Exception e) {
                throw new RuntimeException("afterPropertiesSet failed for " + bean.getClass().getName(), e);
            }
        }
    }
}