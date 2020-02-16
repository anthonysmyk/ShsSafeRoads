package com.example.workplacedamagemanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.media.ExifInterface;
import android.os.ParcelFileDescriptor;
import java.io.FileDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
public class Record extends AppCompatActivity {

public static final int PICK_IMAGE = 1;
    EditText editName, editDescription, editcoords,editDateM;
    Button add,img;
    DatabaseHelper myDb;
    byte[] imgByte = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DatabaseHelper(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.record);
        getSupportActionBar().hide();

        add = findViewById(R.id.button);
        editDateM = (EditText)findViewById(R.id.editText_dM);
        editDescription = (EditText)findViewById(R.id.editText_d);
        editName = (EditText)findViewById(R.id.editText_n);
        editcoords = (EditText)findViewById(R.id.editText_s);
        img = findViewById(R.id.button2);
        add.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
                    AddData();
            }
        });


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

            }
        });

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
                    editcoords.setText("No Metadata");
                    editcoords.setTextColor(Color.RED);
                    editDateM.setText("No Metadata");
                    editDateM.setTextColor(Color.RED);
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
                    editcoords.setText(longlat);
                    editcoords.setTextColor(Color.BLACK);
                    editDateM.setText(datetime);
                    editDateM.setTextColor(Color.BLACK);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && (requestCode == PICK_IMAGE)) {
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
            imgByte = getBitmapAsByteArray(bitmap);
            /*try {
                Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(imgByte));
                for (Directory directory : metadata.getDirectories()) {
                    for (Tag tag : directory.getTags()) {
                        Log.d("PAY ATTENTION TO THIS ", tag.toString());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ImageProcessingException e) {
                e.printStackTrace();
            }*/
                      ImageView Itxt = (ImageView) findViewById(R.id.imageView);
            bitmap = BitmapFactory.decodeByteArray(imgByte,0,imgByte.length);
            bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
            Itxt.setImageBitmap(bitmap);
            Itxt.setScaleType(ImageView.ScaleType.CENTER_INSIDE);


        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, outputStream);
        return outputStream.toByteArray();
    }

    public  void AddData()
    {
        if(!TextUtils.isEmpty(editName.getText())&& (!TextUtils.isEmpty(editcoords.getText()))&& !TextUtils.isEmpty(editDateM.getText())&& !TextUtils.isEmpty(editDescription.getText())&& imgByte != null) {
            if (!(editcoords.getText().toString()).equals("No Metadata")) {

                boolean isInserted = myDb.insertData(
                        editName.getText().toString(),
                        editDescription.getText().toString(),
                        editcoords.getText().toString(),
                        editDateM.getText().toString(), "", "", imgByte);

                if (isInserted) {
                    Toast.makeText(this, "Data inserted", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Data not inserted", Toast.LENGTH_LONG).show();
                }
            }
        else{
            Toast.makeText(this, "Please use a picture with metadata or allow location priveliges", Toast.LENGTH_LONG).show();
            }
        }

        else{
            Toast.makeText(this, "You must fill all fields", Toast.LENGTH_LONG).show();
        }


    }








}
