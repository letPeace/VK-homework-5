package dao;

import database.JDBCCredentials;
import database.JDBCCredentialsTest;
import database.MigrationsInitializerTest;
import entities.Product;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Date;
import utils.Pair;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

class ProductDAOTest {

    private static final @NotNull Connection connection;
    private static final @NotNull ProductDAO productDAO;

    static {
        try {
            connection = JDBCCredentialsTest.getDefaultConnection();
            productDAO = new ProductDAO(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void initializeDB(){
        MigrationsInitializerTest.initialize();
    }

    @Test
    void getProductQuantityAndTotalWithDate() {
        final var product1 = new Product(1, "prod1");
        final var product2 = new Product(2, "prod2");
        final var product3 = new Product(3, "prod3");
        final var pair1 = new Pair<>(1, 1);
        final var pair2 = new Pair<>(2, 2);
        final var pair3 = new Pair<>(3, 4);
        final var pair4 = new Pair<>(3, 3);
        final Map<Product, Pair<Integer, Integer>> productsMap1 = new HashMap<>();
        productsMap1.put(product1, pair1);
        productsMap1.put(product2, pair2);
        final Map<Product, Pair<Integer, Integer>> productsMap2 = new HashMap<>();
        productsMap2.put(product2, pair3);
        productsMap2.put(product3, pair4);
        final Date start = new Date(1);
        final Date end = new Date(120000000);
        final Map<Date, Map<Product, Pair<Integer, Integer>>> map = new LinkedHashMap<>();
        map.put(start, productsMap1);
        map.put(end, productsMap2);
        Assertions.assertEquals(map, productDAO.getProductQuantityAndTotalWithDate(start, end));
    }

    @Test
    void getProductQuantityAndTotal() {
        final var map = new HashMap<Product, Double>();
        final var product1 = new Product(1, "prod1");
        final var product2 = new Product(2, "prod2");
        final var product3 = new Product(3, "prod3");
        map.put(product1, 1d);
        map.put(product2, 3d);
        map.put(product3, 3d);
        final Date start = new Date(1);
        final Date end = new Date(120000000);
        Assertions.assertEquals(map, productDAO.getProductQuantityAndTotal(start, end));
    }

    @Test
    void get() {
        final var product1 = new Product(1, "prod1");
        Assertions.assertEquals(product1, productDAO.get(1));
    }

    @Test
    void getAll() {
        final var product1 = new Product(1, "prod1");
        final var product2 = new Product(2, "prod2");
        final var product3 = new Product(3, "prod3");
        final List<Product> list = new LinkedList<>(List.of(product1, product2, product3));
        Assertions.assertEquals(list, productDAO.getAll());
    }

    @Test
    void save() {
        final var product = new Product(4, "prod4");
        Assertions.assertEquals(1, productDAO.save(product));
    }

    @Test
    void update() {
        final var productToUpdate = new Product(1, "prod1");
        final var productToInsert = new Product(1, "prod1Updated");
        Assertions.assertEquals(1, productDAO.update(productToUpdate, productToInsert));
    }

    @Test
    void delete() {
        final var product = new Product(1, "prod1");
        Assertions.assertEquals(1, productDAO.delete(product));
        Assertions.assertThrows(IllegalStateException.class, () -> productDAO.delete(product));
    }
}