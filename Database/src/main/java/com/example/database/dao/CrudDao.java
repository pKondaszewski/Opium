package com.example.database.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

public interface CrudDao<T> {
    @Insert
    void insert(T t);

    @Update
    void update(T t);

    @Delete
    void delete(T t);
}
