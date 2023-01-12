package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.MapInfo;
import androidx.room.Query;

import com.example.database.HomeAddress;
import com.example.database.entity.PhoneLocalization;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Dao
public interface PhoneLocalizationDao extends CrudDao<PhoneLocalization> {
    @Query("SELECT * FROM PhoneLocalization")
    List<PhoneLocalization> getAll();

    @Query("SELECT * FROM PhoneLocalization WHERE localizationCheckDate = :date")
    List<PhoneLocalization> getAllByDate(LocalDate date);

    @Query("SELECT * FROM PhoneLocalization ORDER BY id DESC LIMIT 1")
    Optional<PhoneLocalization> getNewestLocation();

    @Query("SELECT * FROM PhoneLocalization WHERE localizationCheckDate = :date ORDER BY id DESC LIMIT 1")
    Optional<PhoneLocalization> getMostCommonLocationByDate(LocalDate date);

    @MapInfo(keyColumn = "homeAddress", valueColumn = "count")
    @Query("SELECT homeAddress, COUNT(*) AS count FROM PhoneLocalization WHERE localizationCheckDate = :date " +
           "GROUP BY homeAddress ORDER BY COUNT(homeAddress) DESC LIMIT 5")
    Map<HomeAddress, String> getMostFrequentLocationAndCountToday(LocalDate date);

    @MapInfo(keyColumn = "homeAddress", valueColumn = "count")
    @Query("SELECT homeAddress, COUNT(*) AS count FROM PhoneLocalization " +
            "GROUP BY homeAddress ORDER BY COUNT(homeAddress) DESC LIMIT 5")
    Map<HomeAddress, String> getMostFrequentLocationAndCountAllTime();
}
