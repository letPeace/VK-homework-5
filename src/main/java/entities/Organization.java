package entities;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public record Organization(int taxpayerIdentificationNumber, @NotNull String name, int paymentAccount) {
    public static Organization create(@NotNull ResultSet resultSet) throws SQLException {
        return new Organization(
                resultSet.getInt("taxpayer_identification_number"),
                resultSet.getString("name"),
                resultSet.getInt("payment_account")
        );
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return taxpayerIdentificationNumber == that.taxpayerIdentificationNumber;
    }
    @Override
    public int hashCode() {return taxpayerIdentificationNumber;}
}
