package com.example.op.fragment.fitbit.markers;

import android.content.Context;
import android.widget.TextView;

import com.example.op.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.time.LocalDate;
import java.time.Year;

public class DaysMarkerView extends MarkerView {

    private final TextView dateTv, valueTv;

    public DaysMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        dateTv = findViewById(R.id.text_view_date);
        valueTv = findViewById(R.id.text_view_value);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        LocalDate date = Year.of(LocalDate.now().getYear()).atDay((int) e.getX());
        String finalDate = String.format("%d.%d", date.getDayOfMonth(), date.getMonth().getValue());
        dateTv.setText(finalDate);
        valueTv.setText(String.valueOf((int) e.getY()));
        super.refreshContent(e, highlight);
    }
}
