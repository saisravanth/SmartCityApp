package com.example.bharti.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    int numOfCrimes[] = {25, 10, 11, 35, 45};
    String cityNames[] = {"San Jose", "San Francisco", "Santa Clara", "Sacramento", "Los Angeles"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        makeAPieChart();

    }

    private void makeAPieChart() {
        List<PieEntry> listOfPieEntries = new ArrayList<PieEntry>();
        for (int i = 0; i < numOfCrimes.length; i++) {
            listOfPieEntries.add(new PieEntry(numOfCrimes[i], cityNames[i]));
        }

        PieDataSet dataSet = new PieDataSet(listOfPieEntries, "Crimes in CA");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        PieData data = new PieData(dataSet);

        PieChart chart = (PieChart) findViewById(R.id.pieChart);
        chart.setData(data);
        Description desc = new Description();
        chart.setDescription(desc);

        chart.setTouchEnabled(true);
        chart.setRotationEnabled(true);
        chart.setFocusable(true);

        chart.invalidate();
    }
}
