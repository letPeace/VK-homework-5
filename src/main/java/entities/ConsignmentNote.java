package entities;

import org.jetbrains.annotations.NotNull;
import utils.Date;

import java.sql.ResultSet;
import java.sql.SQLException;

public record ConsignmentNote(int number, @NotNull Date datetime, @NotNull Organization organization) {
    public static ConsignmentNote create(@NotNull ResultSet resultSet) throws SQLException {
        return new ConsignmentNote(
                resultSet.getInt("number"),
                new Date(resultSet.getDate("datetime").getTime()),
                Organization.create(resultSet)
        );
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsignmentNote that = (ConsignmentNote) o;
        return number == that.number;
    }
    @Override
    public int hashCode() {return number;}
}
