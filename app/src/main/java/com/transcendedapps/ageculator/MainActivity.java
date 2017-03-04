package com.transcendedapps.ageculator;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;
    private LinearLayout resultContainer;
    private LinearLayout horoContainer;
    private TextView tvDay;
    private TextView tvPrompt;
    private TextView tvMonth;
    private TextView tvYear;
    private TextView tvAgeYearOut;
    private TextView tvAgeMonthOut;
    private TextView tvAgeDayOut;
    private TextView tvErrorOut;
    private TextView tvNextMonths;
    private TextView tvNextDays;
    private TextView tvAgeHeader;
    private TextView tvnextHeader;
    private TextView tvHoro;
    private EditText etDayIn;
    private EditText etMonthIn;
    private EditText etYearIn;
    private ImageView horoIcon;
    private RadioGroup rgCheck;
    private int radioCheck;

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
        tvAgeHeader = (TextView) findViewById(R.id.tvAgeDisplay);
        tvnextHeader = (TextView) findViewById(R.id.tvnextBDDisplay);
        tvAgeYearOut = (TextView) findViewById(R.id.tvAgeYear);
        tvAgeMonthOut = (TextView) findViewById(R.id.tvAgeMonth);
        tvAgeDayOut = (TextView) findViewById(R.id.tvAgeDay);
        tvErrorOut = (TextView) findViewById(R.id.tvErrorMSG);
        tvNextMonths = (TextView) findViewById(R.id.tvNextMonths);
        tvNextDays = (TextView) findViewById(R.id.tvNextDays);
        tvHoro      = (TextView) findViewById(R.id.tvHoro);
        tvPrompt      = (TextView) findViewById(R.id.tvPrompt);
        etDayIn = (EditText) findViewById(R.id.etDay);
        etMonthIn = (EditText) findViewById(R.id.etMonth);
        etYearIn = (EditText) findViewById(R.id.etYear);
        resultContainer = (LinearLayout) findViewById(R.id.resultContainer);
        horoContainer = (LinearLayout) findViewById(R.id.horoResult);
        horoIcon = (ImageView) findViewById(R.id.horoIcon);
        rgCheck = (RadioGroup) findViewById(R.id.rgCheck);

        //Setting visibility and text actions
        resultContainer.setVisibility(View.INVISIBLE);
        horoContainer.setVisibility(View.INVISIBLE);
        rgCheck.setVisibility(View.VISIBLE);
        tvErrorOut.setTextSize(14);
        etDayIn.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        etMonthIn.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        etYearIn.setImeOptions(EditorInfo.IME_ACTION_DONE);

        //what radio checked
        radioCheck = 1;
        rgCheck.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                radioCheck = checkedId;
                if (radioCheck==R.id.rd1) {
                    radioCheck = 1;
                    tvPrompt.setText("Birth Date");
                } else if (radioCheck ==R.id.rd2) {
                    radioCheck = 2;
                    tvPrompt.setText("Event Date");
                }
            }
        });



        //primary execution
        textHandlers();
        setToday();



    }

    public void btCalculate(View view) {
        tvErrorOut.setTextColor(Color.RED);
        tvErrorOut.setTextSize(14);


        resultContainer.setVisibility(View.INVISIBLE);
        horoContainer.setVisibility(View.INVISIBLE);
        rgCheck.setVisibility(View.INVISIBLE);


        if (radioCheck == 1) {
            if (calculateAgeCheck()) {
                Toast.makeText(this, "Calculation finished", Toast.LENGTH_SHORT).show();

                calculateRemain();
                horoscopeSelect();
                resultContainer.setVisibility(View.VISIBLE);
                horoContainer.setVisibility(View.VISIBLE);
            }
        } else if (radioCheck == 2) {
            if (calculateAgeCheck()) {
                tvAgeHeader.setText("Passed");
                tvnextHeader.setText("Next event");
                Toast.makeText(this, "Calculation finished", Toast.LENGTH_SHORT).show();

                calculateRemain();
                resultContainer.setVisibility(View.VISIBLE);
            }
        }

        //clear focus and set the focus on a 0*0 view
        (findViewById(R.id.dummy_id)).requestFocus();


        //Hides keyboard if open
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


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
            bDay.set(Integer.parseInt(etYearIn.getText().toString()),
                    Integer.parseInt(etMonthIn.getText().toString()) - 1,
                    Integer.parseInt(etDayIn.getText().toString()));


            int years = now.get(Calendar.YEAR) - bDay.get(Calendar.YEAR);
            int months = now.get(Calendar.MONTH) - bDay.get(Calendar.MONTH);
            int days = now.get(Calendar.DAY_OF_MONTH) - bDay.get(Calendar.DAY_OF_MONTH);


            //If entered year > now.year
            if (Integer.parseInt(etYearIn.getText().toString()) > now.get(Calendar.YEAR)) {
                tvErrorOut.setText("Invalid Year");
                return (false);
            //must be less than 150 year
            } else if ((radioCheck==1 && years>150)) {
                tvErrorOut.setText("Vampire detected");
                return (false);
            //if month entered < 0 (or) > 12 (or) bd time > now time and month > now.month
            } else if ((Integer.parseInt(etMonthIn.getText().toString()) - 1 < 0 || Integer.parseInt(etMonthIn.getText().toString()) - 1 > 11)|| (bDay.getTimeInMillis()>now.getTimeInMillis() && Integer.parseInt(etMonthIn.getText().toString())-1> now.get(Calendar.MONTH))) {
                tvErrorOut.setText("Invalid Month");
                return (false);
                //If day < 1 (or) bd.month entered doesn't equal month set on bd.calender then the bd.days entered were > max days @ this month (or) bd time > now time and days > now.days
            } else if ((Integer.parseInt(etDayIn.getText().toString()) < 1) || (Integer.parseInt(etMonthIn.getText().toString()) != bDay.get(Calendar.MONTH) + 1) || ((bDay.getTimeInMillis())>now.getTimeInMillis() && Integer.parseInt(etDayIn.getText().toString())> now.get(Calendar.DAY_OF_MONTH))) {
                tvErrorOut.setText("Invalid Day");
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

            //if all is good --> sets the text views to the age
            tvAgeYearOut.setText(String.valueOf(years));
            tvAgeMonthOut.setText(String.valueOf(months));
            tvAgeDayOut.setText(String.valueOf(days));
            tvErrorOut.setText("");
            return (true);

        } catch (Exception e) {

            tvErrorOut.setText("Error");
            Log.d("try",e.toString());
            return false;
        }

    }

    public void calculateRemain (){
        int remMonthToBd = bDay.get(Calendar.MONTH) - now.get(Calendar.MONTH);


        int remDaysToBD = (bDay.get(Calendar.DAY_OF_MONTH) - now.get(Calendar.DAY_OF_MONTH));
        if (remDaysToBD < 0) {
            remDaysToBD += now.getActualMaximum(Calendar.DAY_OF_MONTH);
            remMonthToBd--;
        }
        if (remMonthToBd<0) {remMonthToBd+=12;}

        if (rgCheck.getCheckedRadioButtonId() == R.id.rd1) {
            if (calculateAgeCheck()) {
                if (remDaysToBD == 0 && remMonthToBd == 0) {
                    tvErrorOut.setText("Happy Birthday");
                    tvErrorOut.setTextSize(18);
                    tvErrorOut.setTextColor(Color.argb(255,0,204,255));
                    tvNextMonths.setText(String.valueOf(12));
                    tvNextDays.setText(String.valueOf(0));
                } else {
                    tvNextMonths.setText(String.valueOf(remMonthToBd));
                    tvNextDays.setText(String.valueOf(remDaysToBD));
                }
            }
        } else if (rgCheck.getCheckedRadioButtonId() == R.id.rd2) {
            if (remDaysToBD == 0 && remMonthToBd == 0) {
                tvErrorOut.setText("Happy Anniversary");
                tvErrorOut.setTextSize(18);
                tvErrorOut.setTextColor(Color.argb(255,0,204,255));
                tvNextMonths.setText(String.valueOf(12));
                tvNextDays.setText(String.valueOf(0));
            } else {
                tvNextMonths.setText(String.valueOf(remMonthToBd));
                tvNextDays.setText(String.valueOf(remDaysToBD));
            }
            }


    }

    public void horoscopeSelect() {

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

        int[] horoSet = {
                R.drawable.h1,
                R.drawable.h2,
                R.drawable.h3,
                R.drawable.h4,
                R.drawable.h5,
                R.drawable.h6,
                R.drawable.h7,
                R.drawable.h8,
                R.drawable.h9,
                R.drawable.h10,
                R.drawable.h11,
                R.drawable.h12,
        };

        if (bDayToInt < signStartDates[0] || bDayToInt >= signStartDates[11]) {
            horoIcon.setImageResource(horoSet[11]);
            tvHoro.setText(signs[11]);
        } else {

            for (int i = 0; i < signStartDates.length; i++) {
                if (bDayToInt >= signStartDates[i] && bDayToInt < signStartDates[i + 1]) {

                    horoIcon.setImageResource(horoSet[i]);
                    tvHoro.setText(signs[i]);
                }
            }

        }
    }

    public void datePicker(View view) {
        int day = now.get(Calendar.DAY_OF_MONTH);
        if (!etDayIn.getText().toString().isEmpty()){
            day = Integer.parseInt(etDayIn.getText().toString());
        }

        int month = now.get(Calendar.MONTH);
        if (!etMonthIn.getText().toString().isEmpty()){
            month = Integer.parseInt(etMonthIn.getText().toString())-1;
        }

        int year = now.get(Calendar.YEAR);
        if (!etYearIn.getText().toString().isEmpty()){
            year = Integer.parseInt(etYearIn.getText().toString());
        }

        DatePickerDialog picker = new DatePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etDayIn.setText(dayOfMonth+"");
                etMonthIn.setText((month+1)+"");
                etYearIn.setText(year+"");
            }
        },year,month,day);

        picker.show();

    }

    private void setToday(){
        tvDay.setText(now.get(Calendar.DAY_OF_MONTH)+"");
        tvMonth.setText((now.get(Calendar.MONTH)+1)+"");
        tvYear.setText(now.get(Calendar.YEAR)+"");
    }

    private void textHandlers(){

        //auto next for day field
        etDayIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if ( (etDayIn.getText().toString().length()==2) || (etDayIn.getText().toString().length()==1 && Integer.parseInt(etDayIn.getText().toString())>3) ) {
                    etMonthIn.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //auto next for month field
        etMonthIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if ( (etMonthIn.getText().toString().length()==2) || (etMonthIn.getText().toString().length()==1 && Integer.parseInt(etMonthIn.getText().toString())>1) ) {
                    etYearIn.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //hides keyboard if 4 digits exit in year field
        etYearIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if ( (etYearIn.getText().toString().length()==4) ) {
                    etYearIn.clearFocus();
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //hides keyboard and resets focus on touching outside edittexts
        findViewById(R.id.activity_main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                (findViewById(R.id.dummy_id)).requestFocus();
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });

    }



}
