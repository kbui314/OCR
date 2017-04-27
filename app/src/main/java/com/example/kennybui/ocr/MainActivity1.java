package com.example.kennybui.ocr;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.jar.Manifest;
import java.util.zip.GZIPInputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

public class MainActivity1 extends AppCompatActivity {
    public static final String PACKAGE_NAME = "com.example.kennybui.ocr;";
    public File DATA_PATH;
    public String data_path;
    public String d_p;
    public FileInputStream fis;
    Bitmap bmap;
    String pPath;

    // You should have the trained data file in assets folder
    // You can get them at:
    // https://github.com/tesseract-ocr/tessdata
    public static final String lang = "eng";

    private static final String TAG = "MainActivity1.java";
    private static int RESULT_LOAD_IMAGE = 1;

    protected Button _button, importBtn;
    // protected ImageView _image;
    protected EditText _field;
    protected String _path;
    protected boolean _taken;

    protected static final String PHOTO_TAKEN = "photo_taken";
    Uri uri;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        DATA_PATH = new File(this.getFilesDir(), "tessdata");
        DATA_PATH.mkdirs();

        data_path = DATA_PATH.toString();
        d_p = this.getFilesDir().toString();
        Toast.makeText(MainActivity1.this, data_path, Toast.LENGTH_LONG).show();
        loadAsset();
        boolean permitted = isExternalStorageWritable();
        String bool;
        if (permitted)
            bool = "True";
        else
            bool = "False";
        pPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+ "/Camera/20170426_124033.jpg";

        Toast.makeText(MainActivity1.this, "Here:" + pPath, Toast.LENGTH_LONG).show();




        // lang.traineddata file with the app (in assets folder)
        // You can get them at:
        // http://code.google.com/p/tesseract-ocr/downloads/list
        // This area needs work and optimization



        // _image = (ImageView) findViewById(R.id.image);
        _field = (EditText) findViewById(R.id.field);
        _button = (Button) findViewById(R.id.button);
        importBtn = (Button) findViewById(R.id.importBtn);
        _button.setOnClickListener(new ButtonClickHandler());
        importBtn.setOnClickListener(new GalleryClickHandler());

        // _path = DATA_PATH + "/ocr.jpg";
        //Toast.makeText(MainActivity.this, _path, Toast.LENGTH_LONG).show();
    }

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {

            Log.v(TAG, "Starting Camera app");
            startCameraActivity();
        }
    }
    public class GalleryClickHandler implements View.OnClickListener {
        public void onClick(View view) {

            Log.v(TAG, "Opening Gallery");
            startGalleryActivity();
        }
    }

    protected void startCameraActivity() {
        _path = null;
        File imgPath = new File(getFilesDir(), "Images");
        //String imgPath = "user/0/csc3380.project.ocr_app/files/";
        File file = new File(imgPath + File.separator + "ocr.jpg");
        file.getParentFile().mkdirs();
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        Uri outputFileUri = FileProvider.getUriForFile(this, "com.example.kennybui.ocr.fileprovider", file);

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        _path = imgPath.toString() + File.separator + "ocr.jpg";
        Toast.makeText(MainActivity1.this, _path, Toast.LENGTH_LONG).show();


        startActivityForResult(intent, 2);
    }
    protected void startGalleryActivity() {

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "resultCode: " + resultCode);

        if (requestCode == 2 && resultCode == -1) {

            onPhotoTaken();
        }
        else if (requestCode == RESULT_LOAD_IMAGE) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            //String pPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+ "/Camera/20170426_124033.jpg";
            //Toast.makeText(this,"here:" + pPath,Toast.LENGTH_LONG).show();
            Bitmap b_map = BitmapFactory.decodeFile(picturePath);
            ImageView imgView = (ImageView) findViewById(R.id.imageView);
            imgView.setImageBitmap(b_map);
            imgView.setVisibility(View.VISIBLE);
            _path = null;
            _path = pPath;
            //File imgPath = new File(getFilesDir(), "Images");
            //String imgPath = "user/0/csc3380.project.ocr_app/files/";
            //File file = new File(imgPath + File.separator + "ocr.jpg");
          // bmap = null;
           // try{
           //     bmap = BitmapFactory.decde
           // }catch (IOException e) {
           //     e.printStackTrace();
           // }
/*            File gallery_pic = new File(_path);
            ArrayList<Byte> bytes = new ArrayList<Byte>();
            int b = 0;
            try {
                fis = new FileInputStream(gallery_pic);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                while ((b = fis.read())!=  -1) {
                    bytes.add((byte)b);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] info = new byte[bytes.size()];
            for (int i = 0; i <bytes.size();i++){
                info[i] = bytes.get(i);
            }

*/
            onPhotoTaken();

        }
        else if(requestCode == 3){
            onPhotoTaken();
        }
        else {
            Log.v(TAG, "User cancelled");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(MainActivity1.PHOTO_TAKEN, _taken);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState()");
        if (savedInstanceState.getBoolean(MainActivity1.PHOTO_TAKEN)) {
            onPhotoTaken();
        }
    }

    private void cropImage(){
        try{
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(uri,"image/*");

            cropIntent.putExtra("crop","true");
            cropIntent.putExtra("outputX",180);
            cropIntent.putExtra("outputY",180);
            cropIntent.putExtra("aspectX",3);
            cropIntent.putExtra("aspectY",4);
            cropIntent.putExtra("scaleUpIfNeeded",true);
            cropIntent.putExtra("return-data",true);

            startActivityForResult(cropIntent,3);
        }
        catch(ActivityNotFoundException ex)
        {
            ex.printStackTrace();
        }
    }
    protected void onPhotoTaken() {
        _taken = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

        try {
            ExifInterface exif = new ExifInterface(_path);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v(TAG, "Orient: " + exifOrientation);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }

            // Convert to ARGB_8888, required by tess
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        } catch (IOException e) {
            Log.e(TAG, "Couldn't correct orientation: " + e.toString());
        }

        // _image.setImageBitmap( bitmap );

        Log.v(TAG, "Before baseApi");
        //cropImage(bitmap);
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(d_p, lang);
        baseApi.setImage(bitmap);

        String recognizedText = baseApi.getUTF8Text();

        baseApi.end();

        // You now have the text in recognizedText var, you can do anything with it.
        // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
        // so that garbage doesn't make it to the display.

        Log.v(TAG, "OCRED TEXT: " + recognizedText);

        if (lang.equalsIgnoreCase("eng")) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }

        recognizedText = recognizedText.trim();

        if (recognizedText.length() != 0) {
            _field.setText(_field.getText().toString().length() == 0 ? recognizedText : _field.getText() + " " + recognizedText);
            _field.setSelection(_field.getText().toString().length());
        }

        // Cycle done.
    }

    public void loadAsset() {
        File assetPath = new File(getFilesDir(), "tessdata");
        assetPath.getParentFile().mkdirs();
        File myAss = new File(assetPath + File.separator + "eng.traineddata");
        //Toast.makeText(this, "You made it here", Toast.LENGTH_LONG).show();
        //Toast.makeText(this, myAss.toString(), Toast.LENGTH_LONG).show();


        myAss.getParentFile().mkdirs();
        if (!myAss.exists()){
            try {
                Toast.makeText(this, "No Ass", Toast.LENGTH_LONG).show();
                myAss.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //if (!myAss.exists()) {
            try {
                //Toast.makeText(this, "Checkpoint reached", Toast.LENGTH_LONG).show();

                //AssetManager assetManager = getAssets();
                AssetManager assetManager = this.getAssets();


                InputStream in = getAssets().open("tessdata" + File.separator + lang + ".traineddata");
                OutputStream out = new FileOutputStream(assetPath + File.separator + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
       // }

    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }


}