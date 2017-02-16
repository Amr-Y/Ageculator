package com.transcendedapps.ageculator;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;
    private TextView tvDay;
    private TextView tvMonth;
    private TextView tvYear;
    private TextView tvAge;
    private TextView tvNextBD;
    private TextView tvHoro;
    private EditText etDay;
    private EditText etMonth;
    private EditText etYear;
    private TextView tvAgeDisplay;
    private TextView tvnextBDDisplay;
    private TextView tvHoroDsplay;
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
        tvDay       = (TextView) findViewById(R.id.tvDay);
        tvMonth     = (TextView) findViewById(R.id.tvMonth);
        tvYear      = (TextView) findViewById(R.id.tvYear);
        tvAge       = (TextView) findViewById(R.id.tvAge);
        tvNextBD    = (TextView) findViewById(R.id.tvNextBD);
        tvHoro      = (TextView) findViewById(R.id.tvHoro);
        etDay       = (EditText) findViewById(R.id.etDay);
        etMonth     = (EditText) findViewById(R.id.etMonth);
        etYear      = (EditText) findViewById(R.id.etYear);
        tvAgeDisplay = (TextView) findViewById(R.id.tvAgeDisplay);
        tvnextBDDisplay = (TextView) findViewById(R.id.tvnextBDDisplay);
        tvHoroDsplay = (TextView) findViewById(R.id.tvHoroDsplay);

        //setPopUps();

        setToday();



    }

    public void btCalculate(View view) {
        Toast.makeText(this, "Calculation finished", Toast.LENGTH_SHORT).show();
        if (calculateAgeCheck()) {
            tvAgeDisplay.setVisibility(View.VISIBLE);
            tvnextBDDisplay.setVisibility(View.VISIBLE);
            tvHoroDsplay.setVisibility(View.VISIBLE);
            tvNextBD.setVisibility(View.VISIBLE);
            tvHoro.setVisibility(View.VISIBLE);
        }
        //tvAge.setText(calculateAge());
        tvNextBD.setText(calculateRemain());
        tvHoro.setText(horoscopeSelect());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(MainActivity.this);
        menuInflater.inflate(R.menu.clearmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.clear) {
            Toast.makeText(this, "restarted", Toast.LENGTH_SHORT).show();
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(intent);

            return true;
        }
        return false;
    }

    public boolean calculateAgeCheck() {
        try {
            bDay.set(Integer.parseInt(etYear.getText().toString()),
                    Integer.parseInt(etMonth.getText().toString()) - 1,
                    Integer.parseInt(etDay.getText().toString()));


            int years = now.get(Calendar.YEAR) - bDay.get(Calendar.YEAR);
            int months = now.get(Calendar.MONTH) - bDay.get(Calendar.MONTH);
            int days = now.get(Calendar.DAY_OF_MONTH) - bDay.get(Calendar.DAY_OF_MONTH);


            if (Integer.parseInt(etYear.getText().toString()) > now.get(Calendar.YEAR)) {
                tvAge.setText("Invalid Year");
                return (false);
            } else if (Integer.parseInt(etMonth.getText().toString()) - 1 < 0 || Integer.parseInt(etMonth.getText().toString()) - 1 > 11) {
                tvAge.setText("Invalid Month");
                return (false);
                //If month entered doesn't equal month set on calender then the days entered were > max days @ this month
            } else if (Integer.parseInt(etDay.getText().toString()) < 1 || Integer.parseInt(etMonth.getText().toString()) != bDay.get(Calendar.MONTH) + 1) {
                tvAge.setText("Invalid Day");
                return (false);
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

            tvAge.setText(years + " years" + ", " + months + " months" + ", " + days + " days");
            return (true);

        } catch (Exception e) {

            tvAge.setText("Error");
            return false;
        }

    }

    public String calculateRemain (){
        int remMonthToBd = bDay.get(Calendar.MONTH) - now.get(Calendar.MONTH);
        if (remMonthToBd<0) {remMonthToBd+=12;}

        int remDaysToBD = (bDay.get(Calendar.DAY_OF_MONTH) - now.get(Calendar.DAY_OF_MONTH));
        if (remDaysToBD < 0) {
            remDaysToBD += now.getActualMaximum(Calendar.DAY_OF_MONTH);
            remMonthToBd--;
        } else if (remDaysToBD == 0 && remMonthToBd == 0) {
            return "Happy birthday";
        }
        return (remMonthToBd+" Months, "+remDaysToBD+" days");
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

    public void datePicker(View view) {
        DatePickerDialog picker = new DatePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etDay.setText(dayOfMonth+"");
                etMonth.setText((month+1)+"");
                etYear.setText(year+"");
            }
        },now.get(Calendar.YEAR),now.get(Calendar.MONTH)+1,now.get(Calendar.DAY_OF_MONTH));

        picker.show();

    }

    private void setPopUps() {
        //Array and adapter for days
        final String[] DAYS = new String[31];
        for (int i = 0; i< DAYS.length; i++){
            DAYS[i] = String.valueOf(i+1);
        }
        final ArrayAdapter<String> adapterDays = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item,DAYS);

        //Array and adapter for Months
        final String[] MONTHS = new String[12];
        for (int i = 0; i< MONTHS.length; i++){
            MONTHS[i] = String.valueOf(i+1);
        }
        final ArrayAdapter<String> adapterMonths = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item,MONTHS);

        //Array and adapter for Years
        final String[] YEARS = new String[120];
        for (int i = 0; i< YEARS.length; i++){
            YEARS[i] = String.valueOf(i+1900);
        }
        final ArrayAdapter<String> adapterYears = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item,YEARS);

        etDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final ListPopupWindow lpw = new ListPopupWindow(MainActivity.this);
                lpw.setAdapter(adapterDays);
                lpw.setAnchorView(etDay);
                lpw.setHeight(700);

                lpw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        etDay.setText(String.valueOf(DAYS[position]));
                        lpw.dismiss();
                    }
                });
                lpw.show();
            }
        });
        etDay.setImeOptions(EditorInfo.IME_ACTION_NEXT);



        etMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ListPopupWindow lpw = new ListPopupWindow(MainActivity.this);
                lpw.setAdapter(adapterMonths);
                lpw.setAnchorView(etMonth);
                lpw.setHeight(700);

                lpw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        etMonth.setText(String.valueOf(MONTHS[position]));
                        lpw.dismiss();
                    }
                });
                lpw.show();
            }
        });

        etYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ListPopupWindow lpw = new ListPopupWindow(MainActivity.this);
                lpw.setAdapter(adapterYears);
                lpw.setAnchorView(etYear);
                lpw.setHeight(700);

                lpw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        etYear.setText(String.valueOf(YEARS[position]));
                        lpw.dismiss();
                    }
                });
                lpw.show();
            }
        });
    }

    private void setToday(){
        tvDay.setText(now.get(Calendar.DAY_OF_MONTH)+"");
        tvMonth.setText((now.get(Calendar.MONTH)+1)+"");
        tvYear.setText(now.get(Calendar.YEAR)+"");
    }
}
