package com.example.android.workingwithsqldatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.google.android.material.button.MaterialButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private static final int START_YEAR = 1880, END_YEAR = 2010, MAX_RANK = 1000;
    private static final String DATABASE_NAME = "babynames";


    Switch aSwitch;
    EditText editText;
    MaterialButton button;
    LinearLayout linearLayout;
    private GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ImportDB(DATABASE_NAME);

        aSwitch = findViewById(R.id.swch);
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        linearLayout = findViewById(R.id.linearLayout);
        graphView = findViewById(R.id.graphView);

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setYAxisBoundsManual(true);

        graphView.getViewport().setMinX(START_YEAR);
        graphView.getViewport().setMaxX(END_YEAR);

        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(MAX_RANK);

        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);


        button.setOnClickListener(v -> {
            boolean state = aSwitch.isChecked();
            String name = editText.getText().toString();
            String sex;

            if (state) sex = "F";
            else sex = "M";

            PlotBaby(name, sex);
        });

    }

    private void PlotBaby(String name, String sex) {
        SQLiteDatabase database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        String query = "Select year, rank FROM ranks WHERE name='" + name + "' AND sex='" + sex + "';";
        Cursor cr = database.rawQuery(query, null);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

        while (cr.moveToNext()) {
            int year = cr.getInt(cr.getColumnIndex("year"));
            int rank = cr.getInt(cr.getColumnIndex("rank"));
            Log.e("tag", name + ": " + Integer.toString(year) + ": " + Integer.toString(rank));

            series.appendData(new DataPoint(year, MAX_RANK - rank), false, 100);
        }
        cr.close();

        graphView.removeAllSeries();
        graphView.addSeries(series);
    }

    private void ImportDB(String databaseName) {
        SQLiteDatabase db = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);

        int resId = getResources().getIdentifier(databaseName, "raw", getPackageName());
        int lineNo = 0;
        Scanner sc = new Scanner(getResources().openRawResource(resId));

        String query = new String("");

        while (sc.hasNextLine()) {

            String line = sc.nextLine();

            if (line.trim().startsWith("--")) {
            }

            query += line + "\n";

            if (query.trim().endsWith(";")) {
                if (lineNo % 1000 == 0) {
                    Log.e("tag", query);
                }
                db.execSQL(query);
                query = "";
            }
            lineNo++;

        }
    }
}