package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.MapInfo;
import androidx.room.Query;

import com.example.database.entity.PhoneLocalization;
import com.example.database.entity.PhoneMovement;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Dao
public interface PhoneMovementDao extends CrudDao<PhoneMovement> {
    @Query("SELECT * FROM PhoneMovement")
    List<PhoneMovement> getAll();

    @Query("SELECT * FROM PhoneMovement WHERE dateOfMovement = :date")
    List<PhoneMovement> getAllByDate(LocalDate date);

    @Query("SELECT * FROM PhoneMovement ORDER BY id DESC LIMIT 1;")
    Optional<PhoneMovement> getNewestMovement();

    @MapInfo(keyColumn = "dateOfMovement", valueColumn = "count")
    @Query("SELECT dateOfMovement, COUNT(*) as count FROM PhoneMovement GROUP BY dateOfMovement ORDER BY dateOfMovement ASC LIMIT :dayLimit;")
    Map<LocalDate, Integer> getMovementsCountByEveryDate(int dayLimit);
}
