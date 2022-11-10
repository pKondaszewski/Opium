package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.database.entity.FitbitAccessToken;

import java.util.List;
import java.util.Optional;

@Dao
public interface FitbitAccessTokenDao extends CrudDao<FitbitAccessToken> {
    @Query("SELECT * FROM FitbitAccessToken")
    List<FitbitAccessToken> getAll();

    @Query("SELECT * FROM FitbitAccessToken ORDER BY id DESC LIMIT 1;")
    Optional<FitbitAccessToken> getNewestAccessToken();
}
