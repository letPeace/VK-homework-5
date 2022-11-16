package dao;

import database.JDBCCredentials;
import database.MigrationsInitializer;
import entities.ConsignmentNote;
import entities.ConsignmentNoteItem;
import entities.Organization;
import entities.Product;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Date;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

class ConsignmentNoteItemDAOTest {

    private static final @NotNull Connection connection;
    private static final @NotNull ConsignmentNoteItemDAO consignmentNoteItemDAO;

    static {
        try {
            connection = JDBCCredentials.getDefaultConnection();
            consignmentNoteItemDAO = new ConsignmentNoteItemDAO(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void initializeDB(){
        MigrationsInitializer.initialize();
    }

    @Test
    void get() {
        final var product = new Product(1, "prod1");
        final var org = new Organization(1, "org1", 11);
        final var con = new ConsignmentNote(1, new Date(1), org);
        final var conItem = new ConsignmentNoteItem(1, product, 1, 1, con);
        Assertions.assertEquals(conItem, consignmentNoteItemDAO.get(1));
    }

    @Test
    void getAll() {
        var org1 = new Organization(1, "org1", 11);
        var org2 = new Organization(2, "org2", 22);
        var con1 = new ConsignmentNote(1, new Date(1), org1);
        var con2 = new ConsignmentNote(2, new Date(120000000), org2);
        var product1 = new Product(1, "prod1");
        var product2 = new Product(2, "prod2");
        var product3 = new Product(3, "prod3");
        var conItem1 = new ConsignmentNoteItem(1, product1, 1, 1, con1);
        var conItem2 = new ConsignmentNoteItem(2, product2, 2, 2, con1);
        var conItem3 = new ConsignmentNoteItem(3, product2, 4, 3, con2);
        var conItem4 = new ConsignmentNoteItem(4, product3, 3, 3, con2);
        final List<ConsignmentNoteItem> list = new LinkedList<>(List.of(conItem1, conItem2, conItem3, conItem4));
        Assertions.assertEquals(list, consignmentNoteItemDAO.getAll());
    }

    @Test
    void save() {
        final var product = new Product(1, "prod1");
        final var org = new Organization(1, "org1", 11);
        final var con = new ConsignmentNote(1, new Date(1), org);
        final var conItem = new ConsignmentNoteItem(5, product, 5, 5, con);
        Assertions.assertEquals(1, consignmentNoteItemDAO.save(conItem));
    }

    @Test
    void update() {
        final var product = new Product(1, "prod1");
        final var org = new Organization(1, "org1", 11);
        final var con = new ConsignmentNote(1, new Date(1), org);
        final var conItemToUpdate = new ConsignmentNoteItem(1, product, 1, 1, con);
        final var conItemToInsert = new ConsignmentNoteItem(1, product, 2, 2, con);
        Assertions.assertEquals(1, consignmentNoteItemDAO.update(conItemToUpdate, conItemToInsert));
    }

    @Test
    void delete() {
        final var product = new Product(1, "prod1");
        final var org = new Organization(1, "org1", 11);
        final var con = new ConsignmentNote(1, new Date(1), org);
        final var conItem = new ConsignmentNoteItem(1, product, 1, 1, con);
        Assertions.assertEquals(1, consignmentNoteItemDAO.delete(conItem));
        Assertions.assertThrows(IllegalStateException.class, () -> consignmentNoteItemDAO.delete(conItem));
    }
}