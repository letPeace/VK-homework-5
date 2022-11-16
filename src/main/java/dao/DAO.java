package dao;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DAO<T> {

    @NotNull
    T get(int id);

    @NotNull
    List<T> getAll();

    int save(@NotNull T entity);

    int update(@NotNull T entityToUpdate, @NotNull T entityToInsert);

    int delete(@NotNull T entity);

}
