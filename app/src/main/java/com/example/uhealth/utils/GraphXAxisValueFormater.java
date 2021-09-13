package com.example.uhealth.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GraphXAxisValueFormater implements IAxisValueFormatter{

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        String dateAsText = new SimpleDateFormat("yyyy-MM-dd")
                .format(new Date((long) (value* 1000L)));
        return dateAsText;
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
