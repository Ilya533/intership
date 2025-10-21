import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private OrderItem item(String name, int qty, double price, Category cat) {
        OrderItem it = new OrderItem();
        it.setProductName(name);
        it.setQuantity(qty);
        it.setPrice(price);
        it.setCategory(cat);
        return it;
    }

    private Customer customer(String id, String name, String city) {
        Customer c = new Customer();
        c.setCustomerId(id);
        c.setName(name);
        c.setCity(city);
        c.setEmail(name + "@example.com");
        c.setRegisteredAt(LocalDateTime.now().minusDays(10));
        c.setAge(30);
        return c;
    }

    private Order order(String id, Customer c, OrderStatus status, OrderItem... items) {
        Order o = new Order();
        o.setOrderId(id);
        o.setCustomer(c);
        o.setOrderDate(LocalDateTime.now());
        o.setStatus(status);
        o.setItems(Arrays.asList(items));
        return o;
    }

    @Test
    @DisplayName("Уникальные города: нормальный случай")
    void testUniqueCities() {
        Customer c1 = customer("C1", "Alice", "Minsk");
        Customer c2 = customer("C2", "Bob", "Gomel");
        Customer c3 = customer("C3", "Carl", "Minsk");

        Order o1 = order("O1", c1, OrderStatus.NEW);
        Order o2 = order("O2", c2, OrderStatus.NEW);
        Order o3 = order("O3", c3, OrderStatus.NEW);

        Set<String> cities = Task.uniqueCities(Arrays.asList(o1, o2, o3));
        assertEquals(2, cities.size());
        assertTrue(cities.contains("Minsk"));
        assertTrue(cities.contains("Gomel"));
    }

    @Test
    @DisplayName("Уникальные города: пустой список")
    void testUniqueCities_EmptyList() {
        Set<String> cities = Task.uniqueCities(Collections.emptyList());
        assertTrue(cities.isEmpty());
    }

    @Test
    @DisplayName("Уникальные города: null список")
    void testUniqueCities_NullList() {
        Set<String> cities = Task.uniqueCities(null);
        assertTrue(cities.isEmpty());
    }

    @Test
    @DisplayName("Уникальные города: клиенты с null городом")
    void testUniqueCities_NullCity() {
        Customer c1 = customer("C1", "Alice", null);
        Customer c2 = customer("C2", "Bob", "Gomel");

        Order o1 = order("O1", c1, OrderStatus.NEW);
        Order o2 = order("O2", c2, OrderStatus.NEW);

        Set<String> cities = Task.uniqueCities(Arrays.asList(o1, o2));
        assertEquals(1, cities.size());
        assertTrue(cities.contains("Gomel"));
    }

    @Test
    @DisplayName("Общий доход: доставленные заказы")
    void testTotalIncomeForCompletedOrders() {
        Customer c = customer("C1", "Alice", "Minsk");
        OrderItem it1 = item("Book", 2, 10.0, Category.BOOKS);
        OrderItem it2 = item("Pen", 1, 2.5, Category.BOOKS);

        Order delivered = order("O1", c, OrderStatus.DELIVERED, it1, it2);
        Order cancelled = order("O2", c, OrderStatus.CANCELLED, item("Phone", 1, 200.0, Category.ELECTRONICS));

        double total = Task.totalIncomeForCompletedOrders(Arrays.asList(delivered, cancelled));
        assertEquals(22.5, total, 1e-9);
    }

    @Test
    @DisplayName("Общий доход: нет доставленных заказов")
    void testTotalIncomeForCompletedOrders_NoDelivered() {
        Customer c = customer("C1", "Alice", "Minsk");
        Order newOrder = order("O1", c, OrderStatus.NEW, item("Phone", 1, 200.0, Category.ELECTRONICS));
        Order cancelled = order("O2", c, OrderStatus.CANCELLED, item("Book", 1, 10.0, Category.BOOKS));

        double total = Task.totalIncomeForCompletedOrders(Arrays.asList(newOrder, cancelled));
        assertEquals(0.0, total, 1e-9);
    }

    @Test
    @DisplayName("Общий доход: пустой список")
    void testTotalIncomeForCompletedOrders_EmptyList() {
        double total = Task.totalIncomeForCompletedOrders(Collections.emptyList());
        assertEquals(0.0, total, 1e-9);
    }

    @Test
    @DisplayName("Общий доход: null список")
    void testTotalIncomeForCompletedOrders_NullList() {
        double total = Task.totalIncomeForCompletedOrders(null);
        assertEquals(0.0, total, 1e-9);
    }

    @Test
    @DisplayName("Самый популярный продукт: нормальный случай")
    void testMostPopularProductBySales() {
        Customer c = customer("C1", "Alice", "Minsk");
        OrderItem a1 = item("iPhone", 1, 500.0, Category.ELECTRONICS);
        OrderItem a2 = item("iPhone", 2, 500.0, Category.ELECTRONICS);
        OrderItem b1 = item("Book", 5, 10.0, Category.BOOKS);

        Order o1 = order("O1", c, OrderStatus.DELIVERED, a1);
        Order o2 = order("O2", c, OrderStatus.DELIVERED, a2);
        Order o3 = order("O3", c, OrderStatus.DELIVERED, b1);

        Optional<String> popular = Task.mostPopularProductBySales(Arrays.asList(o1, o2, o3));
        assertTrue(popular.isPresent());
        assertEquals("Book", popular.get());
    }

    @Test
    @DisplayName("Самый популярный продукт: пустой список")
    void testMostPopularProductBySales_EmptyList() {
        Optional<String> popular = Task.mostPopularProductBySales(Collections.emptyList());
        assertFalse(popular.isPresent());
    }

    @Test
    @DisplayName("Самый популярный продукт: null список")
    void testMostPopularProductBySales_NullList() {
        Optional<String> popular = Task.mostPopularProductBySales(null);
        assertFalse(popular.isPresent());
    }

    @Test
    @DisplayName("Самый популярный продукт: заказы без товаров")
    void testMostPopularProductBySales_EmptyItems() {
        Customer c = customer("C1", "Alice", "Minsk");
        Order o1 = order("O1", c, OrderStatus.DELIVERED);
        Order o2 = order("O2", c, OrderStatus.DELIVERED);

        Optional<String> popular = Task.mostPopularProductBySales(Arrays.asList(o1, o2));
        assertFalse(popular.isPresent());
    }

    @Test
    @DisplayName("Средний чек: доставленные заказы")
    void testAverageCheckForDeliveredOrders() {
        Customer c = customer("C1", "Alice", "Minsk");
        OrderItem it1 = item("A", 1, 100.0, Category.ELECTRONICS);
        OrderItem it2 = item("B", 2, 50.0, Category.HOME);

        Order d1 = order("O1", c, OrderStatus.DELIVERED, it1);
        Order d2 = order("O2", c, OrderStatus.DELIVERED, it2);
        Order newOrder = order("O3", c, OrderStatus.NEW, item("C", 1, 100.0, Category.TOYS));

        OptionalDouble avg = Task.averageCheckForDeliveredOrders(Arrays.asList(d1, d2, newOrder));
        assertTrue(avg.isPresent());
        assertEquals(100.0, avg.getAsDouble(), 1e-9);
    }

    @Test
    @DisplayName("Средний чек: нет доставленных заказов")
    void testAverageCheckForDeliveredOrders_NoDelivered() {
        Customer c = customer("C1", "Alice", "Minsk");
        Order newOrder = order("O1", c, OrderStatus.NEW, item("A", 1, 100.0, Category.ELECTRONICS));
        Order cancelled = order("O2", c, OrderStatus.CANCELLED, item("B", 1, 50.0, Category.HOME));

        OptionalDouble avg = Task.averageCheckForDeliveredOrders(Arrays.asList(newOrder, cancelled));
        assertFalse(avg.isPresent());
    }

    @Test
    @DisplayName("Средний чек: пустой список")
    void testAverageCheckForDeliveredOrders_EmptyList() {
        OptionalDouble avg = Task.averageCheckForDeliveredOrders(Collections.emptyList());
        assertFalse(avg.isPresent());
    }

    @Test
    @DisplayName("Средний чек: null список")
    void testAverageCheckForDeliveredOrders_NullList() {
        OptionalDouble avg = Task.averageCheckForDeliveredOrders(null);
        assertFalse(avg.isPresent());
    }

    @Test
    @DisplayName("Клиенты с >5 заказами: нормальный случай")
    void testCustomersWithMoreThanFiveOrders() {
        Customer c1 = customer("C1", "Alice", "Minsk");
        Customer c2 = customer("C2", "Bob", "Grodno");
        Customer c3 = customer("C3", "Carol", "Vitebsk");

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 6; i++) orders.add(order("A" + i, c1, OrderStatus.NEW));
        for (int i = 0; i < 5; i++) orders.add(order("B" + i, c2, OrderStatus.NEW));
        for (int i = 0; i < 7; i++) orders.add(order("C" + i, c3, OrderStatus.NEW));

        Set<Customer> result = Task.customersWithMoreThanNOrders(orders, 5);
        assertEquals(2, result.size());
        Set<String> ids = new HashSet<>();
        result.forEach(c -> ids.add(c.getCustomerId()));
        assertTrue(ids.contains("C1"));
        assertTrue(ids.contains("C3"));
        assertFalse(ids.contains("C2"));
    }

    @Test
    @DisplayName("Клиенты с >N заказами: пустой список")
    void testCustomersWithMoreThanNOrders_EmptyList() {
        Set<Customer> result = Task.customersWithMoreThanNOrders(Collections.emptyList(), 5);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Клиенты с >N заказами: null список")
    void testCustomersWithMoreThanNOrders_NullList() {
        Set<Customer> result = Task.customersWithMoreThanNOrders(null, 5);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Клиенты с >N заказами: нет подходящих клиентов")
    void testCustomersWithMoreThanNOrders_NoMatches() {
        Customer c1 = customer("C1", "Alice", "Minsk");
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 3; i++) orders.add(order("A" + i, c1, OrderStatus.NEW));

        Set<Customer> result = Task.customersWithMoreThanNOrders(orders, 5);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Сумма заказа: нормальный случай")
    void testOrderTotalHelper() {
        OrderItem i1 = item("X", 2, 12.5, Category.HOME);
        OrderItem i2 = item("Y", 1, 5.0, Category.BEAUTY);
        Order o = order("T1", customer("C1", "Name", "City"), OrderStatus.DELIVERED, i1, i2);
        assertEquals(30.0, Task.orderTotal(o), 1e-9);
    }

    @Test
    @DisplayName("Сумма заказа: пустой список товаров")
    void testOrderTotal_EmptyItems() {
        Order o = order("T1", customer("C1", "Name", "City"), OrderStatus.DELIVERED);
        assertEquals(0.0, Task.orderTotal(o), 1e-9);
    }

    @Test
    @DisplayName("Сумма заказа: null список товаров")
    void testOrderTotal_NullItems() {
        Order o = new Order();
        o.setItems(null);
        assertEquals(0.0, Task.orderTotal(o), 1e-9);
    }

    @Test
    @DisplayName("Сумма заказа: null заказ")
    void testOrderTotal_NullOrder() {
        assertEquals(0.0, Task.orderTotal(null), 1e-9);
    }

    @Test
    @DisplayName("Сумма заказа: товары с нулевой ценой")
    void testOrderTotal_ZeroPrice() {
        OrderItem i1 = item("X", 2, 0.0, Category.HOME);
        OrderItem i2 = item("Y", 1, 0.0, Category.BEAUTY);
        Order o = order("T1", customer("C1", "Name", "City"), OrderStatus.DELIVERED, i1, i2);
        assertEquals(0.0, Task.orderTotal(o), 1e-9);
    }
}