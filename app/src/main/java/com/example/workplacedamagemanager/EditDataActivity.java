package com.example.workplacedamagemanager;

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.HashMap;
import java.util.Map;
import android.util.Base64;

/**
 * Created by User on 2/28/2017.
 */

public class EditDataActivity extends AppCompatActivity {

    private static final String TAG = "EditDataActivity";

    private Button btnSave,btnDelete,btnImage,btnSend;
    private EditText Ntxt, Dtxt, DMtxt, Stxt, Statustxt;
    private ImageView Itxt;

   DatabaseHelper mDatabaseHelper;


    EditText editName, editDescription, editSeverity,editDateM, editDateD, editDateY;
    Button add;
    private ListView mListView;

    private String selectedName;
    private int selectedID;
    private String selectedDateM;
    private String selectedSeverity;
    private String selectedDescription;
    private String selectedStatus;
    private byte[] selectedImage;

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_data_layout);
        getSupportActionBar().hide();
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnImage = findViewById(R.id.button2);
        btnSend = findViewById(R.id.btnSend);
        Ntxt = (EditText) findViewById(R.id.editable_item);
        Dtxt = (EditText) findViewById(R.id.editText_d);
        Itxt = (ImageView) findViewById(R.id.imageView);

        DMtxt = (EditText) findViewById(R.id.editText_dM);
        Statustxt = (EditText) findViewById(R.id.editable_item2);


        Stxt = (EditText) findViewById(R.id.editText_s);

        mDatabaseHelper = new DatabaseHelper(this);

        //get the intent extra from the ListDataActivity
        Intent receivedIntent = getIntent();

        //now get the itemID we passed as an extra
        selectedID = receivedIntent.getIntExtra("id",-1); //NOTE: -1 is just the default value

        //now get the name we passed as an extra
        selectedName = receivedIntent.getStringExtra("name");
        selectedDescription = receivedIntent.getStringExtra("description");
        selectedDateM = receivedIntent.getStringExtra("datem");
        selectedSeverity = receivedIntent.getStringExtra("severity");
        selectedStatus = receivedIntent.getStringExtra("datey");
        selectedImage = receivedIntent.getByteArrayExtra("image");
        //set the text to show the current selected name
        Ntxt.setText(selectedName);
        Stxt.setText((selectedSeverity));
        DMtxt.setText(selectedDateM);
        Dtxt.setText(selectedDescription);
        Statustxt.setText(selectedStatus);
        Statustxt.setEnabled(false);

        Bitmap bitmap = BitmapFactory.decodeByteArray(selectedImage,0,selectedImage.length);
        bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
        Itxt.setImageBitmap(bitmap);
        Itxt.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(Ntxt.getText())&& !TextUtils.isEmpty(Stxt.getText())&& !TextUtils.isEmpty(DMtxt.getText()) &&  !TextUtils.isEmpty(Dtxt.getText())&& hasImage(Itxt)){
                    if (!(Stxt.getText().toString()).equals("No Metadata")) {
                    String name = Ntxt.getText().toString();
            String description = Dtxt.getText().toString();

                String severity = (Stxt.getText().toString());
                String dateM = DMtxt.getText().toString();
                byte[] img = selectedImage;
              //  if(!name.equals("") && !description.equals("") && img != null && !Integer.toString(severity).equals("") && !Integer.toString(date).equals("")){

                    mDatabaseHelper.updateName(name,selectedID,selectedName, description, dateM, "", "", severity,selectedImage);
                    Intent editScreenIntent = new Intent(view.getContext(), MainActivity.class);
                    startActivity(editScreenIntent);
                }
                else{
                    toastMessage("Please use a picture with metadata or allow location priveliges");
                }
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
                toastMessage("removed from database");
                Intent editScreenIntent = new Intent(view.getContext(), MainActivity.class);
                startActivity(editScreenIntent);
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(Ntxt.getText())&& !TextUtils.isEmpty(Stxt.getText())&& !TextUtils.isEmpty(DMtxt.getText())&& !TextUtils.isEmpty(Dtxt.getText())&& hasImage(Itxt)){
                    if (!(Stxt.getText().toString()).equals("No Metadata")) {
                        addItemToSheet();
                    }
                    else{
                        toastMessage("Please use a picture with metadata or allow location priveliges");
                    }
                }else{
                    toastMessage("You must fill all fields");
                }

            }
        });

    }
    private void addItemToSheet() {
        Intent receivedIntent = getIntent();
        mDatabaseHelper = new DatabaseHelper(this);
        selectedID = receivedIntent.getIntExtra("id",-1); //NOTE: -1 is just the default value

        //now get the name we passed as an extra
        selectedName = receivedIntent.getStringExtra("name");
        final ProgressDialog loading = ProgressDialog.show(this,"Adding Item","Please wait");
       final String name = Ntxt.getText().toString();
        final String description = Dtxt.getText().toString();
        final String severity = Stxt.getText().toString();
        final String dateM = DMtxt.getText().toString();
        selectedImage = receivedIntent.getByteArrayExtra("image");
        final byte[] img = selectedImage;
        final String encodedImage = Base64.encodeToString(img, Base64.DEFAULT);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzX0lhVQBZnfQURdQllg2RMlFMuBt2DRjUCq3Gp7QmlXsIvM1Ho/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(EditDataActivity.this,"Success",Toast.LENGTH_LONG).show();
                        mDatabaseHelper.updateName(name,selectedID,selectedName, description, dateM, response, "", severity,selectedImage);
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
                params.put("action","addItem");
                params.put("name",name);
                params.put("description",description);
                params.put("img",encodedImage);
                params.put("coords",severity);
                params.put("dates",dateM);

                return params;
            }
        };

        int socketTimeOut = 5000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }
    void showExif(Uri photoUri){
        if(photoUri != null){

            ParcelFileDescriptor parcelFileDescriptor = null;

            /*
            How to convert the Uri to FileDescriptor, refer to the example in the document:
            https://developer.android.com/guide/topics/providers/document-provider.html
             */
            try {
                parcelFileDescriptor = getContentResolver().openFileDescriptor(photoUri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

                /*
                ExifInterface (FileDescriptor fileDescriptor) added in API level 24
                 */
                ExifInterface exifInterface = new ExifInterface(fileDescriptor);
                String datetime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                String longlat = "";
                if (longitude == null || latitude == null){
                    longitude = "";
                    latitude = "";
                    Stxt.setText("No Metadata");
                    Stxt.setTextColor(Color.RED);
                }

                else if (longitude != null && latitude != null) {
                    int first = latitude.indexOf('/');
                    int second = latitude.indexOf('/', first + 1);
                    int third = latitude.indexOf('/', second + 1);
                    latitude = "" + (Double.valueOf(latitude.substring(0, first)) + Double.valueOf(latitude.substring(first + 3, second)) / 60 + Double.valueOf(latitude.substring(second + 3, third)) / (3600 * 100));
                    first = longitude.indexOf('/');
                    second = longitude.indexOf('/', first + 1);
                    third = longitude.indexOf('/', second + 1);
                    longitude = "" + -1 * (Double.valueOf(longitude.substring(0, first)) + Double.valueOf(longitude.substring(first + 3, second)) / 60 + Double.valueOf(longitude.substring(second + 3, third)) / (3600 * 100));
                    longlat = Math.round(Double.valueOf(latitude) * 1000000) / 1000000.0 + ", " + Math.round(Double.valueOf(longitude) * 1000000) / 1000000.0;
                    //Log.d("IGAOO",exif);
                    Stxt.setText(longlat);
                    Stxt.setTextColor(Color.BLACK);
                    longitude = "";
                    latitude = "";
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            }

            String strPhotoPath = photoUri.getPath();

        }else{
            Toast.makeText(getApplicationContext(),
                    "photoUri == null",
                    Toast.LENGTH_LONG).show();
        }
    };
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && (requestCode == 1)) {
//TODO: action
            Uri uri = data.getData();
            InputStream inputStream = null;
            showExif(uri);
            try {

                inputStream = getContentResolver().openInputStream(uri);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            Bitmap bitmap;
            bitmap = BitmapFactory.decodeStream(inputStream);
            selectedImage = getBitmapAsByteArray(bitmap);

            ImageView Itxt = (ImageView) findViewById(R.id.imageView);
            /*boolean supported = inputStream.markSupported();
            if(supported){
            inputStream.reset();
            }*/
            bitmap = BitmapFactory.decodeByteArray(selectedImage,0,selectedImage.length);
            Itxt.setImageBitmap(bitmap);


        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }


}