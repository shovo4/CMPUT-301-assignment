package com.example.idiots;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class Histo extends AppCompatActivity {
    private BarChart barGraph;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histo);
        sqLiteDatabase = this.openOrCreateDatabase("MyData", MODE_PRIVATE, null);

        barGraph = (BarChart) findViewById(R.id.barGraph);
        ArrayList<BarEntry> entries = new ArrayList<>();

        String name = getIntent().getStringExtra("histogram");
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM [" + name+"]", null);


        int roll_data;
        int col_id = c.getColumnIndex("id");
        int col_rolls = c.getColumnIndex("rolls");
        while (c.moveToNext()) {
            roll_data = c.getInt(col_rolls);
            entries.add(new BarEntry(Float.parseFloat(c.getString(col_id)), roll_data));
        }

        BarDataSet bds = new BarDataSet(entries, "rolls");

        ArrayList<BarEntry> show = new ArrayList<>();

        for (int i = 1; i <= 18; i++) {
            show.add(new BarEntry(i, 0));
        }


        BarDataSet barDataSet1 = new BarDataSet(show, "rolls");

        BarData org = new BarData(barDataSet1, bds);
        barGraph.setData(org);
        barGraph.setTouchEnabled(true);
        barGraph.setDragEnabled(true);
        barGraph.setScaleEnabled(true);


    }
}


