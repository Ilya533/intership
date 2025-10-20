//You have a list of Orders (List<Order>) received by the online store.
//You need to analyze this list using StreamAPI to collect different business metrics
//Metrics
//List of unique cities where orders came from
//Total income for all completed orders
//The most popular product by sales
//Average check for successfully delivered orders
//Customers who have more than 5 orders
//
//All metrics should be covered with unit tests using JUnit 5
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Main {
    private static final Random random = new Random();
    private static final String[] CITIES = generateCities(50);
    private static final String[] PRODUCTS = {
            "iPhone", "Samsung TV", "Nike Shoes", "Adidas Jacket", "Java Book",
            "Python Guide", "Sofa", "Dining Table", "Lipstick", "Perfume",
            "Lego Set", "Barbie Doll", "Laptop", "Headphones", "Watch",
            "Desk Lamp", "Novel", "Textbook", "T-Shirt", "Jeans"
    };

    public static void main(String[] args) {
        List<Customer> customers = generateCustomers(400);
        List<Order> orders = generateOrders(1000, customers);

        System.out.println("Сгенерировано клиентов: " + customers.size());
        System.out.println("Сгенерировано заказов: " + orders.size());

        Map<String, Long> ordersPerCustomer = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCustomer().getCustomerId(),
                        Collectors.counting()
                ));


    }

    private static List<Customer> generateCustomers(int count) {
        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Customer customer = new Customer();
            customer.setCustomerId("CUST_" + i);
            customer.setName("Customer_" + i);
            customer.setEmail("customer" + i + "@example.com");
            customer.setRegisteredAt(LocalDateTime.now().minusDays(random.nextInt(365)));
            customer.setAge(18 + random.nextInt(50));
            customer.setCity(CITIES[random.nextInt(CITIES.length)]);
            customers.add(customer);
        }
        return customers;
    }

    private static List<Order> generateOrders(int count, List<Customer> customers) {
        List<Order> orders = new ArrayList<>();

        // Сначала создадим по 5+ заказов для части клиентов
        int customersWithMultipleOrders = (int) (customers.size() * 0.3); // 30% клиентов
        for (int i = 0; i < customersWithMultipleOrders; i++) {
            Customer customer = customers.get(i);
            int ordersCount = 5 + random.nextInt(6); // 5-10 заказов
            for (int j = 0; j < ordersCount && orders.size() < count; j++) {
                orders.add(createOrder(orders.size() + 1, customer));
            }
        }

        while (orders.size() < count) {
            Customer randomCustomer = customers.get(random.nextInt(customers.size()));
            orders.add(createOrder(orders.size() + 1, randomCustomer));
        }

        return orders;
    }

    private static Order createOrder(int orderId, Customer customer) {
        Order order = new Order();
        order.setOrderId("ORDER_" + orderId);
        order.setOrderDate(LocalDateTime.now().minusDays(random.nextInt(30)));
        order.setCustomer(customer);
        order.setItems(generateOrderItems());
        order.setStatus(generateRandomStatus());
        return order;
    }

    private static List<OrderItem> generateOrderItems() {
        int itemCount = 1 + random.nextInt(3); // 1-3 товара в заказе
        List<OrderItem> items = new ArrayList<>();

        Set<String> usedProducts = new HashSet<>();
        for (int i = 0; i < itemCount; i++) {
            OrderItem item = new OrderItem();

            String product;
            do {
                product = PRODUCTS[random.nextInt(PRODUCTS.length)];
            } while (!usedProducts.add(product));

            item.setProductName(product);
            item.setQuantity(1 + random.nextInt(3));
            item.setPrice(10 + random.nextDouble() * 490);
            item.setCategory(Category.values()[random.nextInt(Category.values().length)]);
            items.add(item);
        }

        return items;
    }

    private static OrderStatus generateRandomStatus() {
        OrderStatus[] statuses = OrderStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }

    private static String[] generateCities(int count) {
        String[] cities = new String[count];
        for (int i = 0; i < count; i++) {
            cities[i] = "City_" + (i + 1);
        }
        return cities;
    }
}