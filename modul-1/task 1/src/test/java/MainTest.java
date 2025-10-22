import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

import task.LinkList;
import task.Node;

class MainTest {
    private LinkList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new LinkList<>();
    }

    @Test
    @DisplayName("size() should return 0 for empty list")
    void size_EmptyList_ReturnsZero() {
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("size() should return correct size after additions")
    void size_AfterAdditions_ReturnsCorrectSize() {
        list.addFirst(1);
        list.addLast(2);
        list.add(1, 3);

        assertEquals(3, list.size());
    }

    @Test
    @DisplayName("addFirst() should add element to empty list")
    void addFirst_EmptyList_AddsElement() {
        list.addFirst(10);

        assertEquals(1, list.size());
        assertEquals(10, list.getFirst());
        assertEquals(10, list.getLast());
    }

    @Test
    @DisplayName("addFirst() should add multiple elements correctly")
    void addFirst_MultipleElements_AddsInCorrectOrder() {
        list.addFirst(3);
        list.addFirst(2);
        list.addFirst(1);

        assertEquals(3, list.size());
        assertEquals(1, list.getFirst());
        assertEquals(3, list.getLast());
    }

    @Test
    @DisplayName("addLast() should add element to empty list")
    void addLast_EmptyList_AddsElement() {
        list.addLast(10);

        assertEquals(1, list.size());
        assertEquals(10, list.getFirst());
        assertEquals(10, list.getLast());
    }

    @Test
    @DisplayName("addLast() should add multiple elements correctly")
    void addLast_MultipleElements_AddsInCorrectOrder() {
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);

        assertEquals(3, list.size());
        assertEquals(1, list.getFirst());
        assertEquals(3, list.getLast());
    }

    @Test
    @DisplayName("add(index, element) should add at beginning")
    void add_AtIndexZero_AddsAtBeginning() {
        list.addLast(2);
        list.add(0, 1);

        assertEquals(2, list.size());
        assertEquals(1, list.getFirst());
    }

    @Test
    @DisplayName("add(index, element) should add at end when index equals size")
    void add_AtEnd_AddsAtEnd() {
        list.addLast(1);
        list.add(1, 2);

        assertEquals(2, list.size());
        assertEquals(2, list.getLast());
    }

    @Test
    @DisplayName("add(index, element) should add in middle")
    void add_InMiddle_AddsCorrectly() {
        list.addLast(1);
        list.addLast(3);
        list.add(1, 2);

        assertEquals(3, list.size());
    }

    @Test
    @DisplayName("getFirst() should return first element")
    void getFirst_NonEmptyList_ReturnsFirstElement() {
        list.addFirst(1);
        list.addLast(2);

        assertEquals(1, list.getFirst());
    }

    @Test
    @DisplayName("getLast() should return last element")
    void getLast_NonEmptyList_ReturnsLastElement() {
        list.addFirst(1);
        list.addLast(2);

        assertEquals(2, list.getLast());
    }

    @Test
    @DisplayName("removeFirst() should remove and return first element")
    void removeFirst_NonEmptyList_RemovesAndReturnsFirst() {
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);

        Node<Integer> removed = list.removeFirst();

        assertEquals(1, removed.data);
        assertEquals(2, list.size());
        assertEquals(2, list.getFirst());
    }

    @Test
    @DisplayName("removeFirst() on single element list should make list empty")
    void removeFirst_SingleElement_MakesListEmpty() {
        list.addFirst(1);

        Node<Integer> removed = list.removeFirst();

        assertEquals(1, removed.data);
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("removeFirst() on empty list should throw exception")
    void removeFirst_EmptyList_ThrowsException() {
        assertThrows(NoSuchElementException.class, () -> list.removeFirst());
    }

    @Test
    @DisplayName("removeLast() should remove and return last element")
    void removeLast_NonEmptyList_RemovesAndReturnsLast() {
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);

        Node<Integer> removed = list.removeLast();

        assertEquals(3, removed.data);
        assertEquals(2, list.size());
        assertEquals(2, list.getLast());
    }

    @Test
    @DisplayName("removeLast() on single element list should make list empty")
    void removeLast_SingleElement_MakesListEmpty() {
        list.addFirst(1);

        Node<Integer> removed = list.removeLast();

        assertEquals(1, removed.data);
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("removeLast() on empty list should throw exception")
    void removeLast_EmptyList_ThrowsException() {
        assertThrows(NoSuchElementException.class, () -> list.removeLast());
    }

    @Test
    @DisplayName("remove(index) should remove element at specified index")
    void remove_ValidIndex_RemovesElement() {
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);

        Node<Integer> removed = list.remove(1);

        assertEquals(2, removed.data);
        assertEquals(2, list.size());
        assertEquals(1, list.getFirst());
        assertEquals(3, list.getLast());
    }

    @Test
    @DisplayName("remove(index) at beginning should work correctly")
    void remove_AtIndexZero_RemovesFirstElement() {
        list.addLast(1);
        list.addLast(2);

        Node<Integer> removed = list.remove(0);

        assertEquals(1, removed.data);
        assertEquals(1, list.size());
        assertEquals(2, list.getFirst());
    }

    @Test
    @DisplayName("remove(index) at end should work correctly")
    void remove_AtLastIndex_RemovesLastElement() {
        list.addLast(1);
        list.addLast(2);

        Node<Integer> removed = list.remove(1);

        assertEquals(2, removed.data);
        assertEquals(1, list.size());
        assertEquals(1, list.getLast());
    }

    @Test
    @DisplayName("remove(index) with invalid index should throw exception")
    void remove_InvalidIndex_ThrowsException() {
        list.addLast(1);

        assertThrows(NoSuchElementException.class, () -> list.remove(-1));
        assertThrows(NoSuchElementException.class, () -> list.remove(2));
    }

    @Test
    @DisplayName("Complex scenario: multiple operations")
    void complexScenario_MultipleOperations_WorksCorrectly() {
        // Start with empty list
        assertEquals(0, list.size());

        // Add elements
        list.addFirst(10);
        list.addLast(30);
        list.add(1, 20);

        // Verify size and order
        assertEquals(3, list.size());
        assertEquals(10, list.getFirst());
        assertEquals(30, list.getLast());

        // Remove from middle
        Node<Integer> removed = list.remove(1);
        assertEquals(20, removed.data);
        assertEquals(2, list.size());

        // Remove first and last
        list.removeFirst();
        list.removeLast();

        // Should be empty
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("Node constructor and data access")
    void nodeConstructor_StoresDataCorrectly() {
        Node<Integer> node = new Node<>(42, null, null);

        assertEquals(42, node.data);
        assertNull(node.next);
        assertNull(node.prev);
    }
}