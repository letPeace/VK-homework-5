package dao;

import entities.Product;
import org.jetbrains.annotations.NotNull;
import utils.Pair;
import utils.Date;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public final class ProductDAO implements DAO<Product> {

    private final @NotNull Connection connection;

    public ProductDAO(@NotNull Connection connection) {
        this.connection = connection;
    }

    public @NotNull Map<Date, Map<Product, Pair<Integer, Integer>>> getProductQuantityAndTotalWithDate(@NotNull Date start, @NotNull Date end){
        final var result = new LinkedHashMap<Date, Map<Product, Pair<Integer, Integer>>>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT datetime, SUM(quantity) quantity_sum, SUM(price) price_sum, product.* FROM public.consignment_note_item\n")
            .append("INNER JOIN public.consignment_note ON consignment_note_item.consignment_note_id = consignment_note.number\n")
            .append("INNER JOIN public.product ON consignment_note_item.product_id = product.code\n")
            .append("WHERE datetime >= ? AND datetime <= ?\n")
            .append("GROUP BY product.code, datetime\n")
            .append("ORDER BY datetime ASC;");
        try (var statement = connection.prepareStatement(sql.toString())) {
            statement.setDate(1, start);
            statement.setDate(2, end);
            try (var resultSet = statement.executeQuery()) {
                Date currentDate = start;
                while (resultSet.next()) {
                    Date date = new Date(resultSet.getDate("datetime").getTime());
                    if(!date.equals(currentDate)) currentDate = date;
                    Map<Product, Pair<Integer, Integer>> productsMap = result.getOrDefault(currentDate, new HashMap<>());
                    productsMap.put(Product.create(resultSet), new Pair<>(resultSet.getInt("quantity_sum"), resultSet.getInt("price_sum")));
                    result.put(currentDate, productsMap);
                }
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public @NotNull Map<Product, Double> getProductQuantityAndTotal(@NotNull Date start, @NotNull Date end){
        final var result = new HashMap<Product, Double>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) product_count, SUM(price) price_sum, product.* FROM public.consignment_note_item\n")
            .append("INNER JOIN public.consignment_note ON consignment_note_item.consignment_note_id = consignment_note.number\n")
            .append("INNER JOIN public.product ON consignment_note_item.product_id = product.code\n")
            .append("WHERE datetime >= ? AND datetime <= ?\n")
            .append("GROUP BY product.code;");
        try (var statement = connection.prepareStatement(sql.toString())) {
            statement.setDate(1, start);
            statement.setDate(2, end);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = Product.create(resultSet);
                    Double average = (resultSet.getInt("price_sum")*1.0)/resultSet.getInt("product_count");
                    result.put(product, average);
                }
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public @NotNull Product get(int code) {
        try (var statement = connection.prepareStatement("SELECT code, name FROM product WHERE code = ?")) {
            statement.setInt(1, code);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Product.create(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new IllegalStateException("Product with code " + code + " not found");
    }

    @Override
    public @NotNull List<Product> getAll() {
        final var result = new LinkedList<Product>();
        try (var statement = connection.prepareStatement("SELECT * FROM product")) {
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(Product.create(resultSet));
                }
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int save(@NotNull Product entity) {
        try (var statement = connection.prepareStatement("INSERT INTO product (code,name) VALUES(?,?)")) {
            statement.setInt(1, entity.code());
            statement.setString(2, entity.name());
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int update(@NotNull Product entityToUpdate, @NotNull Product entityToInsert) {
        try(var statement = connection.prepareStatement("UPDATE product SET name = ? WHERE code = ?")) {
            int fieldIndex = 1;
            statement.setString(fieldIndex++, entityToInsert.name());
            statement.setInt(fieldIndex, entityToUpdate.code());
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int delete(@NotNull Product entity) {
        int result = 0;
        try (var statement = connection.prepareStatement("DELETE FROM product WHERE code = ?")) {
            statement.setInt(1, entity.code());
            result = statement.executeUpdate();
            if (result == 0) {
                throw new IllegalStateException("Product with code = " + entity.code() + " not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
