package task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private OrderItem createItem(String name, int qty, double price, Category cat) {
        OrderItem item = new OrderItem();
        item.setProductName(name);
        item.setQuantity(qty);
        item.setPrice(price);
        item.setCategory(cat);
        return item;
    }

    private Customer createCustomer(String id, String name, String city) {
        Customer customer = new Customer();
        customer.setCustomerId(id);
        customer.setName(name);
        customer.setCity(city);
        customer.setEmail(name + "@example.com");
        customer.setRegisteredAt(LocalDateTime.now().minusDays(10));
        customer.setAge(30);
        return customer;
    }

    private Order createOrder(String id, Customer customer, OrderStatus status, List<OrderItem> items) {
        Order order = new Order();
        order.setOrderId(id);
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(status);
        order.setItems(items);
        return order;
    }

    private Order createOrder(String id, Customer customer, OrderStatus status, OrderItem... items) {
        return createOrder(id, customer, status, Arrays.asList(items));
    }

    @Test
    @DisplayName("Unique cities: normal case")
    void testUniqueCities() {
        Customer c1 = createCustomer("C1", "Alice", "Minsk");
        Customer c2 = createCustomer("C2", "Bob", "Gomel");
        Customer c3 = createCustomer("C3", "Carl", "Minsk");

        Order o1 = createOrder("O1", c1, OrderStatus.NEW);
        Order o2 = createOrder("O2", c2, OrderStatus.NEW);
        Order o3 = createOrder("O3", c3, OrderStatus.NEW);

        Set<String> cities = Task.uniqueCities(Arrays.asList(o1, o2, o3));
        assertEquals(2, cities.size());
        assertTrue(cities.contains("Minsk"));
        assertTrue(cities.contains("Gomel"));
    }

    @Test
    @DisplayName("Unique cities: empty list")
    void testUniqueCities_EmptyList() {
        Set<String> cities = Task.uniqueCities(Collections.emptyList());
        assertTrue(cities.isEmpty());
    }

    @Test
    @DisplayName("Unique cities: null list")
    void testUniqueCities_NullList() {
        Set<String> cities = Task.uniqueCities(null);
        assertTrue(cities.isEmpty());
    }

    @Test
    @DisplayName("Unique cities: customers with null city")
    void testUniqueCities_NullCity() {
        Customer c1 = createCustomer("C1", "Alice", null);
        Customer c2 = createCustomer("C2", "Bob", "Gomel");

        Order o1 = createOrder("O1", c1, OrderStatus.NEW);
        Order o2 = createOrder("O2", c2, OrderStatus.NEW);

        Set<String> cities = Task.uniqueCities(Arrays.asList(o1, o2));
        assertEquals(1, cities.size());
        assertTrue(cities.contains("Gomel"));
    }

    @Test
    @DisplayName("Total income: delivered orders")
    void testTotalIncomeForCompletedOrders() {
        Customer c = createCustomer("C1", "Alice", "Minsk");
        OrderItem it1 = createItem("Book", 2, 10.0, Category.BOOKS);
        OrderItem it2 = createItem("Pen", 1, 2.5, Category.BOOKS);

        Order delivered = createOrder("O1", c, OrderStatus.DELIVERED, it1, it2);
        Order cancelled = createOrder("O2", c, OrderStatus.CANCELLED,
                createItem("Phone", 1, 200.0, Category.ELECTRONICS));

        double total = Task.totalIncomeForCompletedOrders(Arrays.asList(delivered, cancelled));
        assertEquals(22.5, total, 1e-9);
    }

    @Test
    @DisplayName("Total income: no delivered orders")
    void testTotalIncomeForCompletedOrders_NoDelivered() {
        Customer c = createCustomer("C1", "Alice", "Minsk");
        Order newOrder = createOrder("O1", c, OrderStatus.NEW,
                createItem("Phone", 1, 200.0, Category.ELECTRONICS));
        Order cancelled = createOrder("O2", c, OrderStatus.CANCELLED,
                createItem("Book", 1, 10.0, Category.BOOKS));

        double total = Task.totalIncomeForCompletedOrders(Arrays.asList(newOrder, cancelled));
        assertEquals(0.0, total, 1e-9);
    }

    @Test
    @DisplayName("Total income: empty list")
    void testTotalIncomeForCompletedOrders_EmptyList() {
        double total = Task.totalIncomeForCompletedOrders(Collections.emptyList());
        assertEquals(0.0, total, 1e-9);
    }

    @Test
    @DisplayName("Total income: null list")
    void testTotalIncomeForCompletedOrders_NullList() {
        double total = Task.totalIncomeForCompletedOrders(null);
        assertEquals(0.0, total, 1e-9);
    }

    @Test
    @DisplayName("Most popular product: normal case")
    void testMostPopularProductBySales() {
        Customer c = createCustomer("C1", "Alice", "Minsk");
        OrderItem a1 = createItem("iPhone", 1, 500.0, Category.ELECTRONICS);
        OrderItem a2 = createItem("iPhone", 2, 500.0, Category.ELECTRONICS);
        OrderItem b1 = createItem("Book", 5, 10.0, Category.BOOKS);

        Order o1 = createOrder("O1", c, OrderStatus.DELIVERED, a1);
        Order o2 = createOrder("O2", c, OrderStatus.DELIVERED, a2);
        Order o3 = createOrder("O3", c, OrderStatus.DELIVERED, b1);

        Optional<String> popular = Task.mostPopularProductBySales(Arrays.asList(o1, o2, o3));
        assertTrue(popular.isPresent());
        assertEquals("Book", popular.get());
    }

    @Test
    @DisplayName("Most popular product: empty list")
    void testMostPopularProductBySales_EmptyList() {
        Optional<String> popular = Task.mostPopularProductBySales(Collections.emptyList());
        assertFalse(popular.isPresent());
    }

    @Test
    @DisplayName("Most popular product: null list")
    void testMostPopularProductBySales_NullList() {
        Optional<String> popular = Task.mostPopularProductBySales(null);
        assertFalse(popular.isPresent());
    }

    @Test
    @DisplayName("Most popular product: orders without items")
    void testMostPopularProductBySales_EmptyItems() {
        Customer c = createCustomer("C1", "Alice", "Minsk");
        Order o1 = createOrder("O1", c, OrderStatus.DELIVERED, Collections.emptyList());
        Order o2 = createOrder("O2", c, OrderStatus.DELIVERED, Collections.emptyList());

        Optional<String> popular = Task.mostPopularProductBySales(Arrays.asList(o1, o2));
        assertFalse(popular.isPresent());
    }

    @Test
    @DisplayName("Average check: delivered orders")
    void testAverageCheckForDeliveredOrders() {
        Customer c = createCustomer("C1", "Alice", "Minsk");
        OrderItem it1 = createItem("A", 1, 100.0, Category.ELECTRONICS);
        OrderItem it2 = createItem("B", 2, 50.0, Category.HOME);

        Order d1 = createOrder("O1", c, OrderStatus.DELIVERED, it1);
        Order d2 = createOrder("O2", c, OrderStatus.DELIVERED, it2);
        Order newOrder = createOrder("O3", c, OrderStatus.NEW,
                createItem("C", 1, 100.0, Category.TOYS));

        OptionalDouble avg = Task.averageCheckForDeliveredOrders(Arrays.asList(d1, d2, newOrder));
        assertTrue(avg.isPresent());
        assertEquals(100.0, avg.getAsDouble(), 1e-9);
    }

    @Test
    @DisplayName("Average check: no delivered orders")
    void testAverageCheckForDeliveredOrders_NoDelivered() {
        Customer c = createCustomer("C1", "Alice", "Minsk");
        Order newOrder = createOrder("O1", c, OrderStatus.NEW,
                createItem("A", 1, 100.0, Category.ELECTRONICS));
        Order cancelled = createOrder("O2", c, OrderStatus.CANCELLED,
                createItem("B", 1, 50.0, Category.HOME));

        OptionalDouble avg = Task.averageCheckForDeliveredOrders(Arrays.asList(newOrder, cancelled));
        assertFalse(avg.isPresent());
    }

    @Test
    @DisplayName("Average check: empty list")
    void testAverageCheckForDeliveredOrders_EmptyList() {
        OptionalDouble avg = Task.averageCheckForDeliveredOrders(Collections.emptyList());
        assertFalse(avg.isPresent());
    }

    @Test
    @DisplayName("Average check: null list")
    void testAverageCheckForDeliveredOrders_NullList() {
        OptionalDouble avg = Task.averageCheckForDeliveredOrders(null);
        assertFalse(avg.isPresent());
    }

    @Test
    @DisplayName("Customers with >5 orders: normal case")
    void testCustomersWithMoreThanFiveOrders() {
        Customer c1 = createCustomer("C1", "Alice", "Minsk");
        Customer c2 = createCustomer("C2", "Bob", "Grodno");
        Customer c3 = createCustomer("C3", "Carol", "Vitebsk");

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 6; i++) orders.add(createOrder("A" + i, c1, OrderStatus.NEW));
        for (int i = 0; i < 5; i++) orders.add(createOrder("B" + i, c2, OrderStatus.NEW));
        for (int i = 0; i < 7; i++) orders.add(createOrder("C" + i, c3, OrderStatus.NEW));

        Set<Customer> result = Task.customersWithMoreThanNOrders(orders, 5);
        assertEquals(2, result.size());
        Set<String> ids = new HashSet<>();
        for (Customer customer : result) {
            ids.add(customer.getCustomerId());
        }
        assertTrue(ids.contains("C1"));
        assertTrue(ids.contains("C3"));
        assertFalse(ids.contains("C2"));
    }

    @Test
    @DisplayName("Customers with >N orders: empty list")
    void testCustomersWithMoreThanNOrders_EmptyList() {
        Set<Customer> result = Task.customersWithMoreThanNOrders(Collections.emptyList(), 5);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Customers with >N orders: null list")
    void testCustomersWithMoreThanNOrders_NullList() {
        Set<Customer> result = Task.customersWithMoreThanNOrders(null, 5);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Customers with >N orders: no matching customers")
    void testCustomersWithMoreThanNOrders_NoMatches() {
        Customer c1 = createCustomer("C1", "Alice", "Minsk");
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 3; i++) orders.add(createOrder("A" + i, c1, OrderStatus.NEW));

        Set<Customer> result = Task.customersWithMoreThanNOrders(orders, 5);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Order total: normal case")
    void testOrderTotalHelper() {
        OrderItem i1 = createItem("X", 2, 12.5, Category.HOME);
        OrderItem i2 = createItem("Y", 1, 5.0, Category.BEAUTY);
        Order o = createOrder("T1", createCustomer("C1", "Name", "City"), OrderStatus.DELIVERED, i1, i2);
        assertEquals(30.0, Task.orderTotal(o), 1e-9);
    }

    @Test
    @DisplayName("Order total: empty items list")
    void testOrderTotal_EmptyItems() {
        Order o = createOrder("T1", createCustomer("C1", "Name", "City"), OrderStatus.DELIVERED, Collections.emptyList());
        assertEquals(0.0, Task.orderTotal(o), 1e-9);
    }

    @Test
    @DisplayName("Order total: null items list")
    void testOrderTotal_NullItems() {
        Order o = new Order();
        o.setItems(null);
        assertEquals(0.0, Task.orderTotal(o), 1e-9);
    }

    @Test
    @DisplayName("Order total: null order")
    void testOrderTotal_NullOrder() {
        assertEquals(0.0, Task.orderTotal(null), 1e-9);
    }

    @Test
    @DisplayName("Order total: items with zero price")
    void testOrderTotal_ZeroPrice() {
        OrderItem i1 = createItem("X", 2, 0.0, Category.HOME);
        OrderItem i2 = createItem("Y", 1, 0.0, Category.BEAUTY);
        Order o = createOrder("T1", createCustomer("C1", "Name", "City"), OrderStatus.DELIVERED, i1, i2);
        assertEquals(0.0, Task.orderTotal(o), 1e-9);
    }
}