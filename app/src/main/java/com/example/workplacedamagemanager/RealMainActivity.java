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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RealMainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    public static Date lastDate = parseDate("2019-9-11");

    public static Boolean toggle = false;
    Switch switch1;
    Button togg;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setContentView(R.layout.main);
        togg = (Button)findViewById(R.id.detect);
        if (toggle) {
            togg.setBackgroundResource(R.drawable.ic_power_button1);
        }
        else {
            togg.setBackgroundResource(R.drawable.ic_power_button);
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
    public void toggle(View view) {
        toggle = !toggle;

        if (toggle) {
            togg.setBackgroundResource(R.drawable.ic_power_button1);
            startService(new Intent(this, ShakeService.class));


        } else {
            togg.setBackgroundResource(R.drawable.ic_power_button);
            stopService(new Intent(this, ShakeService.class));
        }
    }

}
