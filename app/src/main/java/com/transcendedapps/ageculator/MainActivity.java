package com.transcendedapps.ageculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;
    private TextView tvToday;
    private TextView tvAge;
    private TextView tvNextBD;
    private TextView tvHoro;
    private EditText etDay;
    private EditText etMonth;
    private EditText etYear;
    private GregorianCalendar now = new GregorianCalendar();
    private GregorianCalendar bDay = new GregorianCalendar();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ads banner
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-6253753258388937~5355075808");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //Link Views
        tvToday     = (TextView) findViewById(R.id.tvToday);
        tvAge       = (TextView) findViewById(R.id.tvAge);
        tvNextBD    = (TextView) findViewById(R.id.tvNextBD);
        tvHoro    = (TextView) findViewById(R.id.tvHoro);
        etDay    = (EditText) findViewById(R.id.etDay);
        etMonth    = (EditText) findViewById(R.id.etMonth);
        etYear    = (EditText) findViewById(R.id.etYear);

        //Today date
        tvToday.setText(now.get(Calendar.DAY_OF_MONTH)+" / "+(now.get(Calendar.MONTH)+1)+" / "+now.get(Calendar.YEAR));
    }

    public void btCalculate(View view) {
        Toast.makeText(this, "Calculation finished", Toast.LENGTH_SHORT).show();
        tvAge.setText(calculateAge());
        tvNextBD.setText(calculateRemain());
        tvHoro.setText(horoscopeSelect());

    }

    public String calculateAge() {
        try {
            bDay.set(Integer.parseInt(etYear.getText().toString()),
                    Integer.parseInt(etMonth.getText().toString()) - 1,
                    Integer.parseInt(etDay.getText().toString()));


            int years = now.get(Calendar.YEAR) - bDay.get(Calendar.YEAR);
            int months = now.get(Calendar.MONTH) - bDay.get(Calendar.MONTH);
            int days = now.get(Calendar.DAY_OF_MONTH) - bDay.get(Calendar.DAY_OF_MONTH);


            if (Integer.parseInt(etYear.getText().toString()) > now.get(Calendar.YEAR)) {
                return ("Invalid Year");
            } else if (Integer.parseInt(etMonth.getText().toString()) - 1 < 0 || Integer.parseInt(etMonth.getText().toString()) - 1 > 11) {
                return ("Invalid Month");
                //If month entered doesn't equal month set on calender then the days entered were > max days @ this month
            } else if (Integer.parseInt(etDay.getText().toString()) < 1 || Integer.parseInt(etMonth.getText().toString()) != bDay.get(Calendar.MONTH) + 1) {
                return ("Invalid Day");
            }

            if (days < 0) {
                months--;
                //days = remaining in birth month + elapsed in current month
                days = bDay.getActualMaximum(GregorianCalendar.DATE) - bDay.get(Calendar.DAY_OF_MONTH) + now.get(Calendar.DAY_OF_MONTH);
            }
            if (months < 0) {
                years--;
                months = 12 + months;
            }


            Toast.makeText(this, "Calculated", Toast.LENGTH_SHORT).show();
            return (years + " years" + ", " + months + " months" + ", " + days + " days");

        } catch (Exception e) {

            return "Invalid entry";
        }

    }

    public String calculateRemain (){
        int remDaysToBD = (now.get(Calendar.DAY_OF_YEAR) - bDay.get(Calendar.DAY_OF_YEAR));
        if (remDaysToBD < 0) {
            remDaysToBD += now.getActualMaximum(Calendar.DAY_OF_YEAR);
        } else if (remDaysToBD == 0) {
            return "Happy birthday";
        }
        return (remDaysToBD+"");
    }

    public String horoscopeSelect() {

        int bDayToInt = bDay.get(Calendar.DAY_OF_YEAR);
        String[] signs = {
                "Aquarius",
                "Pisces",
                "Aries",
                "Taurus",
                "Gemini",
                "Cancer",
                "Leo",
                "Virgo",
                "Libra",
                "Scorpio",
                "Sagittarius",
                "Capricorn" };


        int[] signStartDates = {
                new GregorianCalendar(2000, 0, 20).get(Calendar.DAY_OF_YEAR),// Aquarius start date
                new GregorianCalendar(2000, 1, 19).get(Calendar.DAY_OF_YEAR),// Pisces start date
                new GregorianCalendar(2000, 2, 21).get(Calendar.DAY_OF_YEAR), // Aries start date
                new GregorianCalendar(2000, 3, 20).get(Calendar.DAY_OF_YEAR), // Taurus start date
                new GregorianCalendar(2000, 4, 21).get(Calendar.DAY_OF_YEAR), // Gemini start date
                new GregorianCalendar(2000, 5, 21).get(Calendar.DAY_OF_YEAR), // Cancer start date
                new GregorianCalendar(2000, 6, 23).get(Calendar.DAY_OF_YEAR), // Leo start date
                new GregorianCalendar(2000, 7, 23).get(Calendar.DAY_OF_YEAR), // Virgo start date
                new GregorianCalendar(2000, 8, 23).get(Calendar.DAY_OF_YEAR), // Libra start date
                new GregorianCalendar(2000, 9, 23).get(Calendar.DAY_OF_YEAR), // Scorpio start date
                new GregorianCalendar(2000, 10, 22).get(Calendar.DAY_OF_YEAR),// Sagittarius start date
                new GregorianCalendar(2000, 11, 22).get(Calendar.DAY_OF_YEAR),// Capricorn start date
        };

        if (bDayToInt < signStartDates[0] || bDayToInt >= signStartDates[11]) {
            return signs[11];
        } else {

            for (int i = 0; i < signStartDates.length; i++) {
                if (bDayToInt >= signStartDates[i] && bDayToInt < signStartDates[i + 1]) {
                    return signs[i];
                }
            }
            return "not found";
        }
    }

}
