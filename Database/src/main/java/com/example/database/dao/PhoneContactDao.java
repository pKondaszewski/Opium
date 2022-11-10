package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.database.entity.PhoneContact;

import java.util.List;
import java.util.Optional;

@Dao
public interface PhoneContactDao extends CrudDao<PhoneContact> {

    @Query("SELECT * from PhoneContact")
    List<PhoneContact> getAll();

    @Query("SELECT * from PhoneContact WHERE name = :name AND phoneNumber = :phoneNumber")
    Optional<PhoneContact> findByNameAndPhoneNumber(String name, String phoneNumber);
}
