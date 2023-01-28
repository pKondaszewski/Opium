package com.example.op.activity.analyze.marker;

import android.content.Context;
import android.widget.TextView;

import com.example.op.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;

public class PieDataMarkerView extends MarkerView {
    private final TextView labelTv, valueTv;

    public PieDataMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        labelTv = findViewById(R.id.text_view_label);
        valueTv = findViewById(R.id.text_view_value);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        PieEntry e1 = (PieEntry) e;
        labelTv.setText(e1.getLabel());
        valueTv.setText(String.valueOf((int) e1.getValue()));
        super.refreshContent(e, highlight);
    }
}
