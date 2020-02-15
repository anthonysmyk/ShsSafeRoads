package com.example.workplacedamagemanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import android.support.v7.app.AppCompatActivity;

/**
 * Created by User on 2/28/2017.
 */

public class ViewAutoDataActivity extends AppCompatActivity {

    private static final String TAG = "ViewAutoDataActivity";

    private Button btnDelete,btnSend;
    private TextView Dtxt, Gtxt, Ltxt;

   DatabaseHelper2 mDatabaseHelper;


    EditText editName, editDescription, editSeverity,editDateM, editDateD, editDateY;
    Button add;
    private ListView mListView;

    private int selectedID;

    private String selectedDate;
    private String selectedGPS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_auto);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnSend = findViewById(R.id.btnSend);
        Dtxt = (TextView) findViewById(R.id.textDate);
        Ltxt = (TextView) findViewById(R.id.textLink);
        Gtxt = (TextView) findViewById(R.id.textGPS);

        mDatabaseHelper = new DatabaseHelper2(this);

        //get the intent extra from the ListDataActivity
        Intent receivedIntent = getIntent();

        //now get the itemID we passed as an extra
        selectedID = receivedIntent.getIntExtra("id",-1); //NOTE: -1 is just the default value

        //now get the name we passed as an extra

        selectedDate = receivedIntent.getStringExtra("date");
        selectedGPS = receivedIntent.getStringExtra("GPS");

        //set the text to show the current selected name

        Gtxt.setText(selectedGPS);
        Dtxt.setText(selectedDate);
        Ltxt.setText("https://www.google.com/maps/place/" + selectedGPS);



        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseHelper.deleteName(selectedID,selectedDate);
                Dtxt.setText("");
                toastMessage("removed from database");
                Intent editScreenIntent = new Intent(view.getContext(), MainActivity.class);
                startActivity(editScreenIntent);
            }
        });



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToSheet();

            }
        });

    }
    private void addItemToSheet() {
        final ProgressDialog loading = ProgressDialog.show(this,"Adding Item","Please wait");
        final String  GPS = (Gtxt.getText().toString());
        final  String date = (Dtxt.getText().toString());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzX0lhVQBZnfQURdQllg2RMlFMuBt2DRjUCq3Gp7QmlXsIvM1Ho/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(ViewAutoDataActivity.this,response,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                //here we pass params
                params.put("action","addItem2");
                params.put("date", date);
                params.put("GPS", GPS);
                return params;
            }
        };

        int socketTimeOut = 5000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }


    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }


}