package com.example.op.fragment.fitbit.markers;

import android.content.Context;
import android.widget.TextView;

import com.example.op.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

public class MonthsMarkerView extends MarkerView {

    private final TextView dateTv;

    public MonthsMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        dateTv = findViewById(R.id.text_view_date);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        dateTv.setText(String.valueOf((int) e.getY()));
        super.refreshContent(e, highlight);
    }
}
