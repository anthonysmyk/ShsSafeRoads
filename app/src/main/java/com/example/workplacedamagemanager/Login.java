package com.example.workplacedamagemanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    EditText editName, editPass;
    Button btn;
    String pass;
    String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (RealMainActivity.logged){
            Intent intent = new Intent(getApplicationContext(),RealMainActivity.class);
            startActivity(intent);
        }
        else {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.login);
            getSupportActionBar().hide();

            btn = findViewById(R.id.item2);
            editName = (EditText) findViewById(R.id.username);
            editPass = (EditText) findViewById(R.id.password);

        }
    }


    public void login(View view) {

        pass =  editPass.getText().toString();
        user = editName.getText().toString();
        Intent receivedIntent = getIntent();
        //now get the name we passed as an extra
        final ProgressDialog loading = ProgressDialog.show(this,"Checking...","Please wait");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbwPpZvWH3q8Mo_ML5832K9m6zn_E9X-JIhA0WjtZADQ4dp1HYk/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();

                        if(response.equals("true")){
                            RealMainActivity.username = user;
                            Intent intent = new Intent(getApplicationContext(),RealMainActivity.class);
                            startActivity(intent);
                            RealMainActivity.logged = true;
                        }
                        else{
                            Toast.makeText(Login.this,"Incorrect Username or Password",Toast.LENGTH_LONG).show();
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

                //here we pass params
                params.put("action","login");
                params.put("username", user);
                params.put("password", pass);


                return params;
            }
        };

        int socketTimeOut = 5000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);
    }


}
