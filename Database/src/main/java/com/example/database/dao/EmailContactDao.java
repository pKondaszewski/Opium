package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.database.entity.EmailContact;

import java.util.List;
import java.util.Optional;

@Dao
public interface EmailContactDao extends CrudDao<EmailContact> {
    @Query("SELECT * from EmailContact")
    List<EmailContact> getAll();

    @Query("SELECT * from EmailContact WHERE name = :name AND emailAddress = :emailAddress")
    Optional<EmailContact> findByNameAndEmailAddress(String name, String emailAddress);
}
