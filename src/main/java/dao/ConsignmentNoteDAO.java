package dao;

import entities.ConsignmentNote;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public final class ConsignmentNoteDAO implements DAO<ConsignmentNote> {

    private final @NotNull Connection connection;

    public ConsignmentNoteDAO(@NotNull Connection connection) {
        this.connection = connection;
    }

    @Override
    public @NotNull ConsignmentNote get(int number) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM consignment_note cn\n")
            .append("INNER JOIN organization o ON cn.organization_id = o.taxpayer_identification_number\n")
            .append("WHERE number = ?;");
        try (var statement = connection.prepareStatement(sql.toString())) {
            statement.setInt(1, number);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return ConsignmentNote.create(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        throw new IllegalStateException("ConsignmentNote with number " + number + " not found");
    }

    @Override
    public @NotNull List<ConsignmentNote> getAll() {
        final var result = new LinkedList<ConsignmentNote>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM consignment_note cn\n")
            .append("INNER JOIN organization o ON cn.organization_id = o.taxpayer_identification_number;");
        try (var statement = connection.prepareStatement(sql.toString())) {
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(ConsignmentNote.create(resultSet));
                }
                return result;
            }
        } catch (SQLException | IllegalStateException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int save(@NotNull ConsignmentNote entity) {
        try (var statement = connection.prepareStatement("INSERT INTO consignment_note VALUES(?,?,?)")) {
            statement.setInt(1, entity.number());
            statement.setDate(2, entity.datetime());
            statement.setInt(3, entity.organization().taxpayerIdentificationNumber());
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int update(@NotNull ConsignmentNote entityToUpdate, @NotNull ConsignmentNote entityToInsert) {
        try(var statement = connection.prepareStatement("UPDATE consignment_note SET datetime = ?, organization_id = ? WHERE number = ?")) {
            statement.setDate(1, entityToInsert.datetime());
            statement.setInt(2, entityToInsert.organization().taxpayerIdentificationNumber());
            statement.setInt(3, entityToUpdate.number());
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int delete(@NotNull ConsignmentNote entity) {
        int result = 0;
        try(var statement = connection.prepareStatement("DELETE FROM consignment_note WHERE number = ?")) {
            statement.setInt(1, entity.number());
            result = statement.executeUpdate();
            if (result == 0) {
                throw new IllegalStateException("ConsignmentNote with number = " + entity.number() + " not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
