package dao;

import entities.Organization;
import entities.Product;
import org.jetbrains.annotations.NotNull;
import utils.Date;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public final class OrganizationDAO implements DAO<Organization> {

    private final @NotNull Connection connection;

    public OrganizationDAO(@NotNull Connection connection) {
        this.connection = connection;
    }

    public @NotNull Map<Organization, Integer> getOrganizationsSuppliedProduct(int code, int limit){
        final Product product = getProduct(code);
        final var result = new LinkedHashMap<Organization, Integer>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT SUM(quantity) quantity_sum, organization.* FROM public.consignment_note_item\n")
            .append("INNER JOIN public.consignment_note ON consignment_note_item.consignment_note_id = consignment_note.number AND product_id = ?\n")
            .append("INNER JOIN public.organization ON consignment_note.organization_id = organization.taxpayer_identification_number\n")
            .append("GROUP BY product_id, taxpayer_identification_number\n")
            .append("ORDER BY quantity_sum DESC\n")
            .append("LIMIT ?;");
        try (var statement = connection.prepareStatement(sql.toString())) {
            statement.setInt(1, product.code());
            statement.setInt(2, limit);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.put(Organization.create(resultSet), resultSet.getInt("quantity_sum"));
                }
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private @NotNull Product getProduct(int code){
        return new ProductDAO(connection).get(code);
    }

    private @NotNull Map<Product, Integer> getProductsMap(@NotNull Map<Integer, Integer> originalMap){
        var map = new HashMap<Product, Integer>();
        ProductDAO productDAO = new ProductDAO(connection);
        for(Map.Entry<Integer, Integer> entry : originalMap.entrySet()){
            map.put(productDAO.get(entry.getKey()), entry.getValue());
        }
        return map;
    }

    public @NotNull Map<Product, List<Organization>> getOrganizationsSuppliedProductsMoreQuantity(@NotNull Map<Integer, Integer> originalMap){
        final var map = getProductsMap(originalMap);
        final var result = new LinkedHashMap<Product, List<Organization>>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT product.code code, product.name product_name, SUM(quantity) quantity_sum, organization.* FROM public.consignment_note_item\n")
            .append("INNER JOIN public.product ON consignment_note_item.product_id = product.code\n")
            .append("INNER JOIN public.consignment_note ON consignment_note_item.consignment_note_id = consignment_note.number\n")
            .append("INNER JOIN public.organization ON consignment_note.organization_id = organization.taxpayer_identification_number\n")
            .append("GROUP BY product.code, taxpayer_identification_number\n")
            .append("HAVING\n");
        map
            .entrySet()
            .stream()
            .map(entry -> "product.code = ? AND SUM(quantity) > ? OR\n")
            .forEach(sql::append);
        sql
            .delete(sql.length()-3, sql.length())
            .append("ORDER BY quantity_sum DESC;");
        try (var statement = connection.prepareStatement(sql.toString())) {
            int intPlace = 1;
            for(Map.Entry<Product, Integer> entry : map.entrySet()){
                statement.setInt(intPlace++, entry.getKey().code());
                statement.setInt(intPlace++, entry.getValue());
            }
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = Product.create(resultSet, "code", "product_name");
                    List<Organization> list = result.getOrDefault(product, new LinkedList<>());
                    list.add(Organization.create(resultSet));
                    result.put(product, list);
                }
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public @NotNull Map<Organization, List<Product>> getOrganizationsWithSuppliedProducts(@NotNull Date start, @NotNull Date end){
        final var result = new HashMap<Organization, List<Product>>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT product.code code, product.name product_name, organization.* FROM public.consignment_note_item\n")
            .append("INNER JOIN public.product ON consignment_note_item.product_id = product.code\n")
            .append("INNER JOIN public.consignment_note ON consignment_note_item.consignment_note_id = consignment_note.number\n")
            .append("AND datetime >= ? AND datetime <= ?\n")
            .append("RIGHT JOIN public.organization ON consignment_note.organization_id = organization.taxpayer_identification_number\n")
            .append("GROUP BY product.code, organization.taxpayer_identification_number;");
        try (var statement = connection.prepareStatement(sql.toString())) {
            statement.setDate(1, start);
            statement.setDate(2, end);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Organization organization = Organization.create(resultSet);
                    int code = resultSet.getInt("code");
                    List<Product> list = result.getOrDefault(organization, new LinkedList<>());
                    if(!resultSet.wasNull()){
                        Product product = Product.create(resultSet, "code", "product_name");
                        list.add(product);
                    }
                    result.put(organization, list);
                }
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public @NotNull Organization get(int taxpayerIdentificationNumber) {
        try (var statement = connection.prepareStatement("SELECT * FROM organization WHERE taxpayer_identification_number = ?")) {
            statement.setInt(1, taxpayerIdentificationNumber);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Organization.create(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new IllegalStateException("Organization with taxpayerIdentificationNumber " + taxpayerIdentificationNumber + " not found");
    }

    @Override
    public @NotNull List<Organization> getAll() {
        final var result = new LinkedList<Organization>();
        try (var statement = connection.prepareStatement("SELECT * FROM organization")) {
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(Organization.create(resultSet));
                }
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int save(@NotNull Organization entity) {
        try (var statement = connection.prepareStatement("INSERT INTO organization VALUES(?,?,?)")) {
            statement.setInt(1, entity.taxpayerIdentificationNumber());
            statement.setString(2, entity.name());
            statement.setInt(3, entity.paymentAccount());
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int update(@NotNull Organization entityToUpdate, @NotNull Organization entityToInsert) {
        try(var statement = connection.prepareStatement("UPDATE organization SET name = ?, payment_account = ? WHERE taxpayer_identification_number = ?")) {
            statement.setString(1, entityToInsert.name());
            statement.setInt(2, entityToInsert.paymentAccount());
            statement.setInt(3, entityToUpdate.taxpayerIdentificationNumber());
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int delete(@NotNull Organization entity) {
        int result = 0;
        try(var statement = connection.prepareStatement("DELETE FROM organization WHERE taxpayer_identification_number = ?")) {
            statement.setInt(1, entity.taxpayerIdentificationNumber());
            result = statement.executeUpdate();
            if (result == 0) {
                throw new IllegalStateException("Organization with taxpayer_identification_number = " + entity.taxpayerIdentificationNumber() + " not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
