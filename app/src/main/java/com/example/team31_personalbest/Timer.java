import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class Timer extends AppCompatActivity {
    /*
    private TextView timeDisplay;
    private final const int SECS_PER_HOUR = 3600;
    private final const int SECS_PER_MIN = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_to_ten);
        timeDisplay = findViewById(R.id.timeDisplay);
        Clock clock = new Clock();
        clock.execute();
    }




    private class Clock extends AsyncTask<String, String, String> {
        // Holds the number of seconds since beginning
        private long time;

        private int hours;
        private int minutes;
        private int seconds;

        @Override
        protected void doInBackground(String... params) {
            try {
                int t = Integer.parseInt(params[0]) * 1000;
                Thread.sleep(t);
                time++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            time = 0;
        }

        @Override
        protected void onProgressUpdate()
        {
            updateTime();

            timeDisplay.setText();
        }

        public long getTime() {
            return time;
        }

        public void updateTime() {
            int temp = time;
            int hours = time/SECS_PER_HOUR;
            temp = time % SECS_PER_HOUR;
            int minutes = temp/SECS_PER_MIN;
            temp = temp % SECS_PER_MIN;
            int seconds = temp;
        }
    }
    */
}