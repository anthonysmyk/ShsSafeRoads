package com.example.workplacedamagemanager;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Switch;
import android.view.View;
import android.widget.Button;
import android.graphics.Color;
import android.widget.CompoundButton;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;


public class RealMainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    public static Date lastDate = parseDate("2019-9-11");
    public static Boolean toggle = false;
    public static Boolean logged = false;
    DatabaseHelper myDb;

    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;



    Switch switch1;
    Button togg;

    public static String username;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        myDb = new DatabaseHelper(this);
        //getActionBar().hide();
        setContentView(R.layout.main);
        getSupportActionBar().hide();

        chronometer = (Chronometer)findViewById(R.id.chronometer);
        chronometer.bringToFront();
        chronometer.setFormat("00:00:00");
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";
                cArg.setText(hh+":"+mm+":"+ss);
            }
        });
        chronometer.setBase(SystemClock.elapsedRealtime());
        //chronometer.setFormat("Time: %s");
        /*chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if ((SystemClock.elapsedRealtime() - chronometer.getBase()) >= 10000) {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    Toast.makeText(RealMainActivity.this, "Bing!", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        togg = (Button)findViewById(R.id.swatch);
        if (toggle) {
            togg.setBackgroundResource(R.drawable.stopwatch1);
        }
        else {
            togg.setBackgroundResource(R.drawable.stopwatch);
        }
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item2:
                Intent intent1 = new Intent(this, Record.class);
                this.startActivity(intent1);
                return true;
            case R.id.item4:
                Intent intent2 = new Intent(this, MainActivity.class);
                this.startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean b) {
        if(switch1.isChecked())
        {
            toggle=true;
            togg.setBackgroundColor(Color.GREEN);
            startService(new Intent(this, ShakeService.class));

        }else{
            toggle=false;
            togg.setBackgroundColor(Color.RED);
            stopService(new Intent(this, ShakeService.class));
        }
    }
    public void filereport(View view) {
        Intent intent1 = new Intent(this, Record.class);
        this.startActivity(intent1);
    }
    public void database(View view) {
        Intent intent1 = new Intent(this, MainActivity.class);
        this.startActivity(intent1);
    }
    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public void startChronometer() {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }

    public void pauseChronometer() {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }

    public void resetChronometer() {
        String hours = chronometer.getText().toString();
        chronometer.setBase(SystemClock.elapsedRealtime());
        String currentTime = Calendar.getInstance().getTime().toString();
        int x =Integer.parseInt(currentTime.substring(currentTime.indexOf(":")-2,currentTime.indexOf(":")-1));
        if ( x>18 || x<6)
        {
            myDb.insertData(currentTime, "Night", Integer.toString(60*Integer.parseInt(hours.substring(0,2))+Integer.parseInt(hours.substring(3,5))), "","", username, "Local Road");
        }
        else{
            myDb.insertData(currentTime, "Day", Integer.toString(60*Integer.parseInt(hours.substring(0,2))+Integer.parseInt(hours.substring(3,5))), "","", username, "Local Road");

        }
        Toast.makeText(RealMainActivity.this,"Recorded",Toast.LENGTH_LONG).show();
        pauseChronometer();
        pauseOffset = 0;
    }

    public void toggle(View view) {
        toggle = !toggle;

        if (toggle) {
            togg.setBackgroundResource(R.drawable.stopwatch1);
            startChronometer();

        } else {
            togg.setBackgroundResource(R.drawable.stopwatch);
            resetChronometer();
        }
    }

}
