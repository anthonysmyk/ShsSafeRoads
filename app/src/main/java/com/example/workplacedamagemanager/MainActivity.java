package com.example.workplacedamagemanager;

import android.content.Intent;
import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    DatabaseHelper2 myDb2;
    EditText  search;
    Button add;
    Button file, refresh, se;
    private ListView mListView , mListView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        myDb = new DatabaseHelper(this);
        myDb2 = new DatabaseHelper2(this);



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        search = (EditText) findViewById(R.id.editText);

/*        LayoutInflater factory = getLayoutInflater();
         View  view = factory.inflate(R.layout.record, null);*/
        mListView = (ListView)findViewById(R.id.listView);
        mListView2 = (ListView)findViewById(R.id.listView2);

        populateListView();
        ArrayList<Integer> ids = new ArrayList<Integer>();
        final Cursor data = myDb.getAllData();
        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            if (data.getInt(4)!=0)
                ids.add(data.getInt(4));
        }

        final ArrayList<Integer> ides = ids;
        Log.d("SHABBAT FREEDOM" , ids.toString());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzX0lhVQBZnfQURdQllg2RMlFMuBt2DRjUCq3Gp7QmlXsIvM1Ho/exec",

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String[] idees = response.split(",");
                        Log.d("response gotten","oof");
                        Log.d("RIP CODE", ""+response);
                        for(int i=0; i< ides.size(); i++) {
                            Log.d("emp",""+data.moveToFirst());
                            if (data.moveToFirst()) {
                                if (data.getInt(4) == ides.get(i))
                                    myDb.updateName(data.getString(1), data.getInt(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), idees[i], data.getString(6), data.getBlob(7));
                                while (data.moveToNext()) {
                                    //get the value from the database in column 1
                                    //then add it to the ArrayList
                                    Log.d("First", "move");
                                    Log.d("data", "" + data.getInt(4));
                                    if (data.getInt(4) == ides.get(i))
                                        myDb.updateName(data.getString(1), data.getInt(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), idees[i], data.getString(6), data.getBlob(7));
                                }
                            }
                        }
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
                Log.d("sending",ides.toString());
                //here we pass params
                params.put("action","status");
                params.put("ids", ides.toString());

                return params;
            }
        };
        int socketTimeOut = 5000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);

        refresh = (Button)findViewById(R.id.refresh);
        se = (Button)findViewById(R.id.se);


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateListView();
            }
        });

        se.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor data = myDb.getItemID(search.getText().toString());

                populateListViewSearch(data);
            }
        });

    }

    public void populateListViewSearch(Cursor data) {
        Log.d("hi", "populateListView: Displaying data in the ListView.");
        //get the data and append to a list
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            listData.add(data.getString(1));
        }
        //create the list adapter and set the adapter
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);
    }
    public void populateListView() {
        Log.d("hi", "populateListView: Displaying data in the ListView.");

        //get the data and append to a list
        Cursor data = myDb.getAllData();
        Cursor data2 = myDb2.getAllData();

        ArrayList<String> listData = new ArrayList<>();

        ArrayList<String> listData2 = new ArrayList<>();

        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            listData.add(data.getString(1));
        }

        while(data2.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            listData2.add(data2.getString(1));
        }
        //create the list adapter and set the adapter
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        ListAdapter adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData2);

        mListView.setAdapter(adapter);
        mListView2.setAdapter(adapter2);

        //set an onItemClickListener to the ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();
                Cursor data = myDb.getItemID(name); //get the id associated with that name
                int itemID = -1;
                String description = "";
                String dateD = "";
                String dateM= "";
                String dateY= "";
                String severity ="";
                byte[] img=null;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                     description= data.getString(2);
                     dateM = data.getString(3);
                    dateD = data.getString(4);

                    dateY = data.getString(5);

                    severity = data.getString(6);
                    img = data.getBlob(7);


                }
                if(itemID > -1){
                    Intent editScreenIntent = new Intent(view.getContext(), EditDataActivity.class);
                    editScreenIntent.putExtra("id",itemID);
                    editScreenIntent.putExtra("name",name);
                    editScreenIntent.putExtra("description",description);

                    editScreenIntent.putExtra("datem",dateM);
                    editScreenIntent.putExtra("dated",dateD);
                    editScreenIntent.putExtra("datey",dateY);


                    editScreenIntent.putExtra("severity",severity);
                    editScreenIntent.putExtra("image",img);


                    startActivity(editScreenIntent);
                }
                else{
                    Toast.makeText(MainActivity.this,"NO ID ASSOCIATED",Toast.LENGTH_LONG).show();
                }
            }
        });
        mListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String date = adapterView.getItemAtPosition(i).toString();
                Cursor data = myDb2.getItemID(date); //get the id associated with that name
                int itemID = -1;
                String GPS ="" ;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                    GPS = data.getString(2);



                }
                if(itemID > -1){
                    Intent editScreenIntent = new Intent(view.getContext(), ViewAutoDataActivity.class);
                    editScreenIntent.putExtra("id",itemID);

                    editScreenIntent.putExtra("date",date);

                    editScreenIntent.putExtra("GPS",GPS);


                    startActivity(editScreenIntent);
                }
                else{
                    Toast.makeText(MainActivity.this,"NO ID ASSOCIATED",Toast.LENGTH_LONG).show();
                }
            }
        });



    }






}
