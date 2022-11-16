package entities;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public record Product(int code, @NotNull String name) {
    public static Product create(ResultSet resultSet) throws SQLException {
        return create(resultSet, "code", "name");
    }
    public static Product create(ResultSet resultSet, String... columnLabels) throws SQLException {
        return new Product(
                resultSet.getInt(columnLabels[0]),
                resultSet.getString(columnLabels[1])
        );
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return code == product.code;
    }
    @Override
    public int hashCode() {return code;}
}
