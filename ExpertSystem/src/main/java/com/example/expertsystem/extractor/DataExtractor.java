package com.example.expertsystem.extractor;

import com.example.database.AppDatabase;
import com.example.database.entity.PhoneLocalization;
import com.example.database.entity.PhoneMovement;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class PhoneActivityExtractor {

    private final AppDatabase database;
    private final LocalDate now;

    public PhoneActivityExtractor(AppDatabase database) {
        this.database = database;
        now = LocalDate.now();
    }

    public List<Double> extractMostCommonCoordinates(LocalDate date) {
//        database.phoneLocalizationDao().insert(new PhoneLocalization(999, LocalTime.now(), LocalDate.now(),
//                54.217132, 16.077090, new HomeAddress("ul. Dworcowa 23B", "76-031 Mścice", "Poland")));
        PhoneLocalization mostCommonCoordinatesByDate = database.phoneLocalizationDao()
                .getMostCommonLocationByDate(date).orElseThrow(
                        RuntimeException::new // TODO: jakis lepszy wyjatek tutaj dac
                );

        return Arrays.asList(mostCommonCoordinatesByDate.getLatitude(), mostCommonCoordinatesByDate.getLongitude());
    }

    public Integer extractAmountOfNotedMovements() {
        List<PhoneMovement> allByDate = database.phoneMovementDao().getAllByDate(now);
        int size = allByDate.size();
        return Math.min(size, 4);
    }
}
