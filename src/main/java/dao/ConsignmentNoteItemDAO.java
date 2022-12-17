package dao;

import entities.ConsignmentNoteItem;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public final class ConsignmentNoteItemDAO implements DAO<ConsignmentNoteItem> {

    private final @NotNull Connection connection;

    public ConsignmentNoteItemDAO(@NotNull Connection connection) {
        this.connection = connection;
    }

    @Override
    public @NotNull ConsignmentNoteItem get(int id) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM consignment_note_item cni\n")
            .append("INNER JOIN product p ON cni.product_id = p.code\n")
            .append("INNER JOIN consignment_note cn ON cni.consignment_note_id = cn.number AND cni.id = ?\n")
            .append("INNER JOIN organization o ON cn.organization_id = o.taxpayer_identification_number;");
        try (var statement = connection.prepareStatement(sql.toString())) {
            statement.setInt(1, id);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return ConsignmentNoteItem.create(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new IllegalStateException("ConsignmentNoteItem with id " + id + " not found");
    }

    @Override
    public @NotNull List<ConsignmentNoteItem> getAll() {
        final var result = new LinkedList<ConsignmentNoteItem>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM consignment_note_item cni\n")
            .append("INNER JOIN product p ON cni.product_id = p.code\n")
            .append("INNER JOIN consignment_note cn ON cni.consignment_note_id = cn.number\n")
            .append("INNER JOIN organization o ON cn.organization_id = o.taxpayer_identification_number;");
        try (var statement = connection.prepareStatement(sql.toString())) {
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(ConsignmentNoteItem.create(resultSet));
                }
                return result;
            }
        } catch (SQLException | IllegalStateException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int save(@NotNull ConsignmentNoteItem entity) {
        try (var statement = connection.prepareStatement("INSERT INTO consignment_note_item (product_id, price, quantity, consignment_note_id) VALUES(?,?,?,?)")) {
            statement.setInt(1, entity.product().code());
            statement.setInt(2, entity.price());
            statement.setInt(3, entity.quantity());
            statement.setInt(4, entity.consignmentNote().number());
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int update(@NotNull ConsignmentNoteItem entityToUpdate, @NotNull ConsignmentNoteItem entityToInsert) {
        try(var statement = connection.prepareStatement("UPDATE consignment_note_item SET product_id = ?, price = ?, quantity = ?, consignment_note_id = ? WHERE id = ?")) {
            statement.setInt(1, entityToInsert.product().code());
            statement.setInt(2, entityToInsert.price());
            statement.setInt(3, entityToInsert.quantity());
            statement.setInt(4, entityToInsert.consignmentNote().number());
            statement.setInt(5, entityToUpdate.id());
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int delete(@NotNull ConsignmentNoteItem entity) {
        int result = 0;
        try(var statement = connection.prepareStatement("DELETE FROM consignment_note_item WHERE id = ?")) {
            statement.setInt(1, entity.id());
            result = statement.executeUpdate();
            if (result == 0) {
                throw new IllegalStateException("ConsignmentNoteItem with id = " + entity.id() + " not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
