package com.homework3.lakshitha.photonotes;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNoteActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private static final int RESULT_CODE_CAMERA = 5;
    private static final int REQUEST_CODE_WRITE_STORAGE = 1;
    private static final int REQUEST_CODE_CAMERA = 2;

    private String imagePath;
    private ImageView mImageView;
    private Bitmap mImageBitmap;
    private String captionContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button captureButton = (Button) findViewById(R.id.captureButton);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkorGrantPermissions();
            }
        });

        mImageView = (ImageView) findViewById(R.id.imageView1);
        mImageBitmap = null;

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText captionText = (EditText) findViewById(R.id.captionContent);
                captionContent = captionText.getText().toString();

                if(captionContent == null || captionContent.length() <= 0) {
                    Toast.makeText(AddNoteActivity.this, "caption can not be blank", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(imagePath == null || imagePath.length() <= 0) {
                    Toast.makeText(AddNoteActivity.this, "image not available..retry", Toast.LENGTH_SHORT).show();
                    return;
                }

                insert(captionContent, imagePath);

                imagePath = null;
                captionContent = null;

                finish();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePath = null;
                captionContent = null;
                mImageView.setImageDrawable(null);
                finish();
            }
        });

        if (savedInstanceState != null) {
            imagePath = savedInstanceState.getString("IMAGE_STATE");
            setPic();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        captionContent = ((EditText) findViewById(R.id.captionContent)).getText().toString();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private File getAlbumStorageDir(String albumName) {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                    ),
                    albumName
            );

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getAlbumStorageDir("PhotoNotes");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imagePath = photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, RESULT_CODE_CAMERA);
            }
        }
    }

    private void checkorGrantPermissions() {
        if (ActivityCompat.checkSelfPermission(AddNoteActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddNoteActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_STORAGE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    // Call back for requested permission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_CODE_WRITE_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(AddNoteActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == Activity.RESULT_OK) {
            setPic();
        } else {
            captionContent = null;
            imagePath = null;
            mImageView.setImageDrawable(null);
        }

        if(captionContent != null)
            ((EditText) findViewById(R.id.captionContent)).setText(captionContent);
        EditText et = (EditText)findViewById(R.id.captionContent);
        et.setSelection(et.getText().length());

    }


    public void setPic() {

		/* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);

        mImageView.setImageBitmap(bitmap);
        mImageView.setVisibility(View.VISIBLE);
    }


    private void insert(String caption,String imagePath) {
        SQLiteDatabase db = new PhotoNotesDBHelper(this).getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(PhotoNotesDBHelper.CAPTION_COLUMN, caption);
        newValues.put(PhotoNotesDBHelper.PHOTO_PATH_COLUMN, imagePath);
        db.insert(PhotoNotesDBHelper.DATABASE_TABLE, null, newValues);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("IMAGE_STATE", imagePath);
        super.onSaveInstanceState(savedInstanceState);
    }


}
