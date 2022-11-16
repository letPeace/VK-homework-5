package entities;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public record ConsignmentNoteItem(int id, @NotNull Product product, int price, int quantity, @NotNull ConsignmentNote consignmentNote) {
    public static ConsignmentNoteItem create(@NotNull ResultSet resultSet) throws SQLException {
        return new ConsignmentNoteItem(
                resultSet.getInt("id"),
                Product.create(resultSet),
                resultSet.getInt("price"),
                resultSet.getInt("quantity"),
                ConsignmentNote.create(resultSet)
        );
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsignmentNoteItem that = (ConsignmentNoteItem) o;
        return id == that.id;
    }
    @Override
    public int hashCode() {return id;}
}
