package com.example.op.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.op.database.entity.PhoneLocalization;

import java.util.List;
import java.util.Optional;

@Dao
public interface PhoneLocalizationDao extends CrudDao<PhoneLocalization> {
    @Query("SELECT * FROM PhoneLocalization")
    List<PhoneLocalization> getAll();

    @Query("SELECT * FROM PhoneLocalization ORDER BY id DESC LIMIT 1")
    Optional<PhoneLocalization> getNewestLocation();
}
