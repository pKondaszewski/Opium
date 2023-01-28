package com.example.op.domain.utils;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;

import android.content.Context;

import com.example.op.utils.Translation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

public class TranslationTest {
    private Context context;
    private String value;

    @Before
    public void init() {
        context = Mockito.mock(Context.class);
        value = "value";
    }

    @Test
    public void translateGender() {
        Mockito.when(context.getString(anyInt())).thenReturn(value);
        Translation translation = new Translation(context);

        String result = translation.translateGender("Other");

        assertEquals(value, result);
    }

    @Test
    public void translateMood() {
        Mockito.when(context.getString(anyInt())).thenReturn(value);
        Translation translation = new Translation(context);

        String result = translation.translateMood("tragic");

        assertEquals(value, result);
    }

    @Test
    public void translateAilments() {
        Mockito.when(context.getString(anyInt())).thenReturn(value);
        Translation translation = new Translation(context);

        List<String> translatedAilments = translation.translateAilments(List.of("cough", "flu", "fever"));

        translatedAilments.forEach(s -> assertEquals("value", s));
    }
}
