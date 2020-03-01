package com.example.workplacedamagemanager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.os.ParcelFileDescriptor;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.util.Base64;

/**
 * Created by User on 2/28/2017.
 */

public class EditDataActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "EditDataActivity";

    private Button btnSave,btnDelete,btnSend;
    private EditText Ntxt, Dtxt, DMtxt, Stxt;

   DatabaseHelper mDatabaseHelper;


    EditText editName, editDescription,editDateM, editDateD, editDateY, eText;
    Button add;
    private ListView mListView;

    private String selectedName;
    private int selectedID;
    private String selectedDateM,selectedDescription,selectedStatus,selectedRoad;

    DatePickerDialog picker;
    Spinner spinner, spinnerTime;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_data_layout);
        getSupportActionBar().hide();
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnSend = findViewById(R.id.button);
        Ntxt = (EditText) findViewById(R.id.editText_d);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinnerTime = (Spinner) findViewById(R.id.password);

        DMtxt = (EditText) findViewById(R.id.editable_item2);
       // Statustxt = (EditText) findViewById(R.id.editable_item2);

        mDatabaseHelper = new DatabaseHelper(this);

        //get the intent extra from the ListDataActivity
        Intent receivedIntent = getIntent();

        //now get the itemID we passed as an extra
        selectedID = receivedIntent.getIntExtra("id",-1); //NOTE: -1 is just the default value
        //now get the name we passed as an extra
        selectedName = receivedIntent.getStringExtra("name");
        selectedDescription = receivedIntent.getStringExtra("description");
        selectedDateM = receivedIntent.getStringExtra("datem");
        selectedStatus = receivedIntent.getStringExtra("datey");

        selectedRoad = receivedIntent.getStringExtra("road");

        //set the text to show the current selected name
        Ntxt.setText(selectedName);
        DMtxt.setText(selectedDateM);
        Log.d("shabbat",selectedRoad);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(Ntxt.getText()) && !TextUtils.isEmpty(DMtxt.getText()) ){
                    String name = Ntxt.getText().toString();
                     String description = spinnerTime.getSelectedItem().toString() ;
                     String road = spinner.getSelectedItem().toString() ;

                String dateM = DMtxt.getText().toString();
              //  if(!name.equals("") && !description.equals("") && img != null && !Integer.toString(severity).equals("") && !Integer.toString(date).equals("")){

                    mDatabaseHelper.updateName(name,selectedID,selectedName, description, dateM, "", "",road);
                    Intent editScreenIntent = new Intent(view.getContext(), MainActivity.class);
                    startActivity(editScreenIntent);

                }else{
                    toastMessage("You must fill all fields");
                }


            }
        });



        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseHelper.deleteName(selectedID,selectedName);
                Ntxt.setText("");
                toastMessage("Removed from database");
                Intent editScreenIntent = new Intent(view.getContext(), MainActivity.class);
                startActivity(editScreenIntent);
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(Ntxt.getText()) && !TextUtils.isEmpty(DMtxt.getText())){

                        addItemToSheet();
                }else{
                    toastMessage("You must fill all fields");
                }

            }
        });

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Local Road");
        categories.add("Major Road");
        categories.add("Highway");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        // Spinner click listener
        spinnerTime.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories2 = new ArrayList<String>();
        categories2.add("Day");
        categories2.add("Night");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories2);

        // Drop down layout style - list view with radio button
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerTime.setAdapter(dataAdapter2);
        if(selectedRoad.equals("Local Road") )
            spinner.setSelection(0);
        else if (selectedRoad.equals("Major Road"))
            spinner.setSelection(1);
        else
            spinner.setSelection(2);
        if (selectedDescription.equals("Day"))
            spinnerTime.setSelection(0);
        else
            spinnerTime.setSelection(1);
        eText = (EditText) findViewById(R.id.editText_d);
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(EditDataActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                eText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        if (position != 0) {
            String item = parent.getItemAtPosition(position).toString();

            // Showing selected spinner item
        }
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
    private void addItemToSheet() {
        Intent receivedIntent = getIntent();
        mDatabaseHelper = new DatabaseHelper(this);
        selectedID = receivedIntent.getIntExtra("id",-1); //NOTE: -1 is just the default value
        //now get the name we passed as an extra
        selectedName = receivedIntent.getStringExtra("name");
        final ProgressDialog loading = ProgressDialog.show(this,"Adding Item","Please wait");
       final String name = Ntxt.getText().toString();
         final String description = spinnerTime.getSelectedItem().toString() ;
        final String road = spinner.getSelectedItem().toString() ;

        final String dateM = DMtxt.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbwPpZvWH3q8Mo_ML5832K9m6zn_E9X-JIhA0WjtZADQ4dp1HYk/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(EditDataActivity.this,"Success",Toast.LENGTH_LONG).show();
                        mDatabaseHelper.updateName(name,selectedID,selectedName, description, dateM, response, "",road);
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
                params.put("action","addhours");
                params.put("description",description);
                params.put("username", RealMainActivity.username);
                params.put("dates", name);
                params.put("hours",dateM);
                params.put("road",road);


                return params;
            }
        };

        int socketTimeOut = 5000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && (requestCode == 1)) {
//TODO: action
            Uri uri = data.getData();
            InputStream inputStream = null;
            try {

                inputStream = getContentResolver().openInputStream(uri);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }



        }
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }


}