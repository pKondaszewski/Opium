package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.database.entity.Profile;

import java.util.Optional;

@Dao
public interface ProfileDao extends CrudDao<Profile> {
    @Query("SELECT * FROM Profile WHERE id = 1")
    Optional<Profile> get();
}
