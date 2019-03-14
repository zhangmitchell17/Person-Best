//package com.example.team31_personalbest_ms2v2;
//
//import android.content.SharedPreferences;
//import android.graphics.Color;
//
//import com.github.mikephil.charting.charts.BarChart;
//import com.github.mikephil.charting.data.BarData;
//import com.github.mikephil.charting.data.BarDataSet;
//import com.github.mikephil.charting.data.BarEntry;
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.robolectric.Robolectric;
//import org.robolectric.RobolectricTestRunner;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static android.content.Context.MODE_PRIVATE;
//import static com.example.team31_personalbest_ms2v2.Constants.*;
//import static org.junit.Assert.assertEquals;
//
//@RunWith(RobolectricTestRunner.class)
//public class ProgressActivityTest {
//    ProgressActivity progressActivity;
//    private BarChart bc;
//    private ProgressChart pc;
//    private List<Integer> unplanned;
//    private List<Integer> planned;
//    private String[] bottomAxisLabels;
//
//
//    @Before
//    public void setup() {
//        progressActivity = Robolectric.setupActivity(ProgressActivity.class);
//        bc = progressActivity.findViewById(R.id.barChartFriends);
//        unplanned = new ArrayList<>();
//        planned = new ArrayList<>();
//        bottomAxisLabels = new String[DAYS_PER_WEEK];
//        // populating data lists
//        for(int i = 1; i <= DAYS_PER_WEEK; i++) {
//            unplanned.add(i*1000);
//            planned.add(200);
//            bottomAxisLabels[i-1] = "12/" + Integer.toString(i);
//        }
//        //create a barchart with some arbitrary data
//        pc = new ProgressChart(bc, unplanned, planned, unplanned.size(), bottomAxisLabels);
//    }
//
//    // Tests if data from barchart has been correctly added and is being correctly displayed
//    @Test
//    public void testData() {
//        // TODO have it get data from barchart and ensure that it displays properly
//        assertEquals(bc.getBarData(), createData()/*TODO create a barData obj from unplanned and planned*/);
//        assertEquals(bc.getDefaultValueFormatter(), new IndexAxisValueFormatter(bottomAxisLabels));
//    }
//
//    public BarData createData() {
//        List<BarEntry> barEntries = new ArrayList<>();
//
//        // populating barEntries with values from planned and unplanned
//        for(int i = 0; i < unplanned.size(); i++) {
//            barEntries.add(new BarEntry(i, new float[]{planned.get(i), unplanned.get(i)}));
//        }
//
//        // making dataset from set labeled "steps"
//        BarDataSet set = new BarDataSet(barEntries, "Steps");
//        // labels for the chart legend
//        set.setStackLabels(new String[]{PLANNED_STEPS_STR, UNPLANNED_STEPS_STR});
//        // colors for the chart legend
//        set.setColors(Color.parseColor(PASTEL_GREEN), Color.parseColor(PASTEL_BLUE));
//
//        BarData data = new BarData(set);
//        return data;
//    }
//}
