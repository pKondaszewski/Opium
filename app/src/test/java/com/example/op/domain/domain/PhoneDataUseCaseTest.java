package com.example.op.domain.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.graphics.Color;

import com.example.op.domain.PhoneDataUseCase;
import com.github.mikephil.charting.data.PieData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

public class PhoneDataUseCaseTest {
    private Context context;
    private String contextString;
    private PhoneDataUseCase phoneDataUseCase;

    @Before
    public void init() {
        phoneDataUseCase = new PhoneDataUseCase();

        contextString = "contextString";
        context = Mockito.mock(Context.class);
        when(context.getString(anyInt())).thenReturn(contextString);
    }

    @Test
    public void getPhoneQuestionAnswersPieDataPositiveMultipleResults() {
        PieData phoneQuestionAnswersPieData = phoneDataUseCase.getPhoneQuestionAnswersPieData(context, 1, 1);
        List<Integer> colors = phoneQuestionAnswersPieData.getDataSet().getColors();

        assertNotNull(phoneQuestionAnswersPieData);
        assertEquals(2, (int) phoneQuestionAnswersPieData.getYValueSum());
        assertEquals(2, colors.size());
        assertTrue(colors.contains(Color.RED));
        assertTrue(colors.contains(Color.GREEN));
    }

    @Test
    public void getPhoneQuestionAnswersPieDataPositiveSingleResult() {
        PieData phoneQuestionAnswersPieData = phoneDataUseCase.getPhoneQuestionAnswersPieData(context, 1, 0);
        List<Integer> colors = phoneQuestionAnswersPieData.getDataSet().getColors();

        assertNotNull(phoneQuestionAnswersPieData);
        assertEquals(1, (int) phoneQuestionAnswersPieData.getYValueSum());
        assertEquals(1, colors.size());
        assertFalse(colors.contains(Color.RED));
        assertTrue(colors.contains(Color.GREEN));
    }

    @Test
    public void getPhoneQuestionAnswersPieDataEmptyResult() {
        PieData phoneQuestionAnswersPieData = phoneDataUseCase.getPhoneQuestionAnswersPieData(context, null, null);
        List<Integer> colors = phoneQuestionAnswersPieData.getDataSet().getColors();

        assertNotNull(phoneQuestionAnswersPieData);
        assertEquals(0, (int) phoneQuestionAnswersPieData.getYValueSum());
        assertEquals(0, colors.size());
        assertFalse(colors.contains(Color.RED));
        assertFalse(colors.contains(Color.GREEN));
    }
}
