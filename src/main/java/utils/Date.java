package utils;

public class Date extends java.sql.Date {
    public Date(long date) {
        super(date);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Date date = (Date) o;
        return toString().equals(date.toString());
    }
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
