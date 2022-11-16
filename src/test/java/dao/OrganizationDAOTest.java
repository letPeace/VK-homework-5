package dao;

import database.JDBCCredentials;
import database.JDBCCredentialsTest;
import database.MigrationsInitializerTest;
import entities.Organization;
import entities.Product;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Date;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

class OrganizationDAOTest {

    private static final @NotNull Connection connection;
    private static final @NotNull OrganizationDAO organizationDAO;

    static {
        try {
            connection = JDBCCredentialsTest.getDefaultConnection();
            organizationDAO = new OrganizationDAO(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void initializeDB(){
        MigrationsInitializerTest.initialize();
    }

    @Test
    void getOrganizationsSuppliedProduct() {
        final var organization1 = new Organization(1, "org1", 11);
        final var organization2 = new Organization(2, "org2", 22);
        final var map = new LinkedHashMap<Organization, Integer>();
        map.put(organization2, 3);
        map.put(organization1, 2);
        final int code = 2;
        final int limit = 10;
        Assertions.assertEquals(map, organizationDAO.getOrganizationsSuppliedProduct(code, limit));
    }

    @Test
    void getOrganizationsSuppliedProductsMoreQuantity() {
        final var mapArguments = new HashMap<Integer, Integer>();
        mapArguments.put(1, 0);
        mapArguments.put(2, 1);
        mapArguments.put(3, 2);
        final var map = new LinkedHashMap<Product, List<Organization>>();
        final var organization1 = new Organization(1, "org1", 11);
        final var organization2 = new Organization(2, "org2", 22);
        final var product1 = new Product(1, "prod1");
        final var product2 = new Product(2, "prod2");
        final var product3 = new Product(3, "prod3");
        map.put(product3, new LinkedList<>(List.of(organization2)));
        map.put(product2, new LinkedList<>(List.of(organization2, organization1)));
        map.put(product1, new LinkedList<>(List.of(organization1)));
        Assertions.assertEquals(map, organizationDAO.getOrganizationsSuppliedProductsMoreQuantity(mapArguments));
    }

    @Test
    void getOrganizationsWithSuppliedProducts() {
        final Date start = new utils.Date(1);
        final Date end = new Date(120000000);
        final var map = new HashMap<Organization, List<Product>>();
        final var organization1 = new Organization(1, "org1", 11);
        final var organization2 = new Organization(2, "org2", 22);
        final var organization3 = new Organization(3, "org3", 33);
        final var product1 = new Product(1, "prod1");
        final var product2 = new Product(2, "prod2");
        final var product3 = new Product(3, "prod3");
        map.put(organization1, new LinkedList<>(List.of(product1, product2)));
        map.put(organization2, new LinkedList<>(List.of(product3, product2)));
        map.put(organization3, new LinkedList<>());
        Assertions.assertEquals(map, organizationDAO.getOrganizationsWithSuppliedProducts(start, end));
    }

    @Test
    void get() {
        final var organization = new Organization(1, "org1", 11);
        Assertions.assertEquals(organization, organizationDAO.get(1));
    }

    @Test
    void getAll() {
        final var organization1 = new Organization(1, "org1", 11);
        final var organization2 = new Organization(2, "org2", 22);
        final var organization3 = new Organization(3, "org3", 33);
        final List<Organization> list = new LinkedList<>(List.of(organization1, organization2, organization3));
        Assertions.assertEquals(list, organizationDAO.getAll());
    }

    @Test
    void save() {
        final var organization = new Organization(4, "org4", 44);
        Assertions.assertEquals(1, organizationDAO.save(organization));
    }

    @Test
    void update() {
        final var organizationToUpdate = new Organization(1, "org1", 11);
        final var organizationToInsert = new Organization(1, "org1New", 111);
        Assertions.assertEquals(1, organizationDAO.update(organizationToUpdate, organizationToInsert));
    }

    @Test
    void delete() {
        final var organization = new Organization(1, "org1", 11);
        Assertions.assertEquals(1, organizationDAO.delete(organization));
        Assertions.assertThrows(IllegalStateException.class, () -> organizationDAO.delete(organization));
    }
}