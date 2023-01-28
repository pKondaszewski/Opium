package com.example.expertsystem.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import android.content.Context;

import com.example.database.AppDatabase;
import com.example.database.HomeAddress;
import com.example.database.dao.DailyFeelingsDao;
import com.example.database.dao.DailyQuestionAnswerDao;
import com.example.database.dao.DailyQuestionDao;
import com.example.database.dao.FitbitSpo2DataDao;
import com.example.database.dao.FitbitStepsDataDao;
import com.example.database.dao.PhoneLocalizationDao;
import com.example.database.dao.PhoneMovementDao;
import com.example.database.dao.ProfileDao;
import com.example.database.entity.DailyFeelings;
import com.example.database.entity.DailyQuestion;
import com.example.database.entity.DailyQuestionAnswer;
import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;
import com.example.database.entity.PhoneLocalization;
import com.example.database.entity.Profile;
import com.example.expertsystem.ExpertSystemLevel;
import com.example.expertsystem.ResourceNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DataExtractorTest {
    private final LocalDate now = LocalDate.now();
    private AppDatabase database;
    private Context context;
    private PhoneLocalization phoneLocalization;
    private DataExtractor dataExtractor;
    private DailyFeelingsDao dailyFeelingsDao;
    private DailyQuestionAnswerDao dailyQuestionAnswerDao;
    private PhoneLocalizationDao phoneLocalizationDao;
    private PhoneMovementDao phoneMovementDao;
    private ProfileDao profileDao;
    private DailyQuestionDao dailyQuestionDao;
    private FitbitStepsDataDao fitbitStepsDataDao;
    private FitbitSpo2DataDao fitbitSpO2DataDao;

    @Before
    public void init() {
        initDatabase();
        initData();
    }

    private void initDatabase() {
        context = Mockito.mock(Context.class);
        database = Mockito.mock(AppDatabase.class);
        dataExtractor = new DataExtractor(database);

        phoneLocalizationDao = Mockito.mock(PhoneLocalizationDao.class);
        phoneMovementDao = Mockito.mock(PhoneMovementDao.class);
        profileDao = Mockito.mock(ProfileDao.class);
        dailyFeelingsDao = Mockito.mock(DailyFeelingsDao.class);
        dailyQuestionAnswerDao = Mockito.mock(DailyQuestionAnswerDao.class);
        dailyQuestionDao = Mockito.mock(DailyQuestionDao.class);
        fitbitStepsDataDao = Mockito.mock(FitbitStepsDataDao.class);
        fitbitSpO2DataDao = Mockito.mock(FitbitSpo2DataDao.class);

        when(database.phoneLocalizationDao()).thenReturn(phoneLocalizationDao);
        when(database.profileDao()).thenReturn(profileDao);
        when(database.phoneMovementDao()).thenReturn(phoneMovementDao);
        when(database.dailyFeelingsDao()).thenReturn(dailyFeelingsDao);
        when(database.dailyQuestionAnswerDao()).thenReturn(dailyQuestionAnswerDao);
        when(database.dailyQuestionDao()).thenReturn(dailyQuestionDao);
        when(database.fitbitStepsDataDao()).thenReturn(fitbitStepsDataDao);
        when(database.fitbitSpO2DataDao()).thenReturn(fitbitSpO2DataDao);
    }

    private void initData() {
        phoneLocalization = new PhoneLocalization(1, null, now, 1, 1, null);
    }

    @Test
    public void extractMostCommonCoordinatesReturnPositiveValue() {
        when(phoneLocalizationDao.getMostCommonLocationByDate(now))
                .thenReturn(Optional.of(phoneLocalization));

        List<Double> doubles = dataExtractor.extractMostCommonCoordinates();

        assertEquals(List.of(phoneLocalization.getLatitude(), phoneLocalization.getLongitude()), doubles);
    }

    @Test
    public void extractMostCommonCoordinatesReturnNullValue() {
        when(phoneLocalizationDao.getMostCommonLocationByDate(now))
                .thenReturn(Optional.empty());

        List<Double> doubles = dataExtractor.extractMostCommonCoordinates();

        assertNull(doubles);
    }

    @Test
    public void extractProfileCoordinatesNoHomeAddress() {
        when(database.profileDao().get()).thenReturn(Optional.of(new Profile()));

        assertThrows(ResourceNotFoundException.class,
                () -> dataExtractor.extractProfileCoordinates(context));
    }

    @Test
    public void extractProfileCoordinatesEmptyValues() throws IOException, ResourceNotFoundException {
        Profile profile = Profile.builder()
                .homeAddress(new HomeAddress("", "", ""))
                .build();
        when(database.profileDao().get()).thenReturn(Optional.of(profile));

        List<Double> doubles = dataExtractor.extractProfileCoordinates(context);

        assertEquals(Collections.emptyList(), doubles);
    }

    @Test
    public void extractAmountOfNotedMovementsReturnPositiveValue() {
        when(phoneMovementDao.getCountByDate(now)).thenReturn(3);

        int lowInt = dataExtractor.extractAmountOfNotedMovements(ExpertSystemLevel.LOW);
        int mediumInt = dataExtractor.extractAmountOfNotedMovements(ExpertSystemLevel.MEDIUM);
        int highInt = dataExtractor.extractAmountOfNotedMovements(ExpertSystemLevel.HIGH);

        assertEquals(4, lowInt);
        assertEquals(3, mediumInt);
        assertEquals(2, highInt);
    }

    @Test
    public void extractMoodValueReturnNull() {
        when(database.dailyFeelingsDao().getByDate(now)).thenReturn(Optional.empty());

        Double result = dataExtractor.extractMoodValue();

        assertNull(result);
    }

    @Test
    public void extractMoodValueReturnPositiveValuesWithAilments() {
        DailyFeelings dailyFeelings = new DailyFeelings(1, "great", List.of("cough"),
                null, null, now, null);
        when(database.dailyFeelingsDao().getByDate(now)).thenReturn(Optional.of(dailyFeelings));

        Double greatMoodResult = dataExtractor.extractMoodValue();
        dailyFeelings.setMood("good");
        Double goodMoodResult = dataExtractor.extractMoodValue();
        dailyFeelings.setMood("ok");
        Double okMoodResult = dataExtractor.extractMoodValue();
        dailyFeelings.setMood("bad");
        Double badMoodResult = dataExtractor.extractMoodValue();
        dailyFeelings.setMood("tragic");
        Double tragicMoodResult = dataExtractor.extractMoodValue();

        assertEquals(Double.valueOf(3.8), greatMoodResult);
        assertEquals(Double.valueOf(2.8), goodMoodResult);
        assertEquals(Double.valueOf(1.8), okMoodResult);
        assertEquals(Double.valueOf(0.8), badMoodResult);
        assertEquals(Double.valueOf(0.0), tragicMoodResult);
    }

    @Test
    public void extractAnswerValueReturnNull() {
        when(database.dailyQuestionAnswerDao().getNewestByDate(now)).thenReturn(Optional.empty());

        Double result = dataExtractor.extractAnswerValue();

        assertEquals(Double.valueOf(0.0), result);
    }

    @Test
    public void extractAnswerValueReturnPositiveValue() {
        DailyQuestion dailyQuestion = new DailyQuestion(1, "1+0=?",1);
        DailyQuestionAnswer dailyQuestionAnswer = new DailyQuestionAnswer(1, 1, 1, null, now);
        when(database.dailyQuestionAnswerDao().getNewestByDate(now)).thenReturn(Optional.of(dailyQuestionAnswer));
        when(database.dailyQuestionDao().getById(dailyQuestionAnswer.getDailyQuestionId())).thenReturn(Optional.of(dailyQuestion));

        Double result = dataExtractor.extractAnswerValue();

        assertEquals(Double.valueOf(1.0), result);
    }

    @Test
    public void extractFitbitDataEmptyValues() {
        when(database.fitbitStepsDataDao().getMaxFitbitStepsDataByDate(now)).thenReturn(Optional.empty());
        when(database.fitbitSpO2DataDao().getMaxFitbitSpO2DataByDate(now)).thenReturn(Optional.empty());

        Map<String, Integer> result = dataExtractor.extractFitbitData();

        result.values().forEach(integer -> assertEquals(Integer.valueOf(0), integer));
    }

    @Test
    public void extractFitbitDataPositiveValues() {
        FitbitStepsData fitbitStepsData = new FitbitStepsData(1, now, null, "100");
        FitbitSpO2Data fitbitSpO2Data = new FitbitSpO2Data(1, now, null, "100");
        when(database.fitbitStepsDataDao().getMaxFitbitStepsDataByDate(now)).thenReturn(Optional.of(fitbitStepsData));
        when(database.fitbitSpO2DataDao().getMaxFitbitSpO2DataByDate(now)).thenReturn(Optional.of(fitbitSpO2Data));

        Map<String, Integer> result = dataExtractor.extractFitbitData();

        result.values().forEach(integer -> assertEquals(Integer.valueOf(100), integer));
    }
}
