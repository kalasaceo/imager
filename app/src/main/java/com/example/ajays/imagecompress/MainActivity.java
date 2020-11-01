package com.example.ajays.imagecompress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iceteck.silicompressorr.SiliCompressor;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Random;

import id.zelory.compressor.Compressor;
import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;
//import top.zibin.luban.CompressionPredicate;
//import top.zibin.luban.Luban;
//import top.zibin.luban.OnCompressListener;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static int N_RETURNS = 0;

    private ImageView actualImageView;
    private ImageView compressedImageView;
    private TextView actualSizeTextView;
    private TextView compressedSizeTextView;
    private File actualImage;
    private File compressedImage;

    long tStart, tEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actualImageView = (ImageView) findViewById(R.id.actual_image);
        compressedImageView = (ImageView) findViewById(R.id.compressed_image);
        actualSizeTextView = (TextView) findViewById(R.id.actual_size);
        compressedSizeTextView = (TextView) findViewById(R.id.compressed_size);

        actualImageView.setBackgroundColor(getRandomColor());
        clearImage();
    }

    public void chooseImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void compressImageAL(View view){
        if (actualImage == null){
            showError("Please choose an image!");
        } else {

            Luban.compress(MainActivity.this,actualImage)
                    .putGear(Luban.FIRST_GEAR)
//                    .setMaxSize(650)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .launch(new OnCompressListener() {
                        @Override
                        public void onStart() {
                            tStart = System.currentTimeMillis();
                        }

                        @Override
                        public void onSuccess(File file) {
                            compressedImage = file;
//                            tEnd = System.currentTimeMillis();
                            setCompressedImage();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        }
    }

//    public void compressImageAL(View view){
//        if (actualImage == null){
//            showError("Please choose an image!");
//        } else {
//            tStart = System.currentTimeMillis();
//            Luban.with(this)
//                    .load(actualImage)
//                    .ignoreBy(80)
//                    .setFocusAlpha(false)
//                    .putGear(5)
//                    .setTargetDir(getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).
//                            getAbsolutePath())
//                    .filter(new CompressionPredicate() {
//                        @Override
//                        public boolean apply(String path) {
//                            return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
//                        }
//                    })
//                    .setCompressListener(new OnCompressListener() {
//                        @Override
//                        public void onStart() { }
//
//                        @Override
//                        public void onSuccess(File file) {
//                            compressedImage = file;
//                            tEnd = System.currentTimeMillis();
//                            setCompressedImage();
//                        }
//
//                        @Override
//                        public void onError(Throwable e) { }
//                    }).launch();
//        }
//    }

    public void compressImageSili(View view) throws IOException {
        if (actualImage == null) {
            showError("Please choose an image!");
        } else {
            tStart = System.currentTimeMillis();
            Bitmap bitmap = SiliCompressor.with(this).getCompressBitmap(actualImage.getAbsolutePath());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bytes);
            compressedImage = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).
                    getAbsolutePath() + "/file0.jpg");
            try {
                FileOutputStream fo = new FileOutputStream(compressedImage);
                fo.write(bytes.toByteArray());
                fo.flush();
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            tEnd = System.currentTimeMillis();

            setCompressedImage();
        }
    }

    @SuppressLint("CheckResult")
    public void compressImageC(View view) throws IOException {
        if (actualImage == null) {
            showError("Please choose an image!");
        } else {
            tStart = System.currentTimeMillis();
            compressedImage = new Compressor(MainActivity.this)
                    .setMaxWidth(650)
                    .setQuality(50)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setDestinationDirectoryPath(getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).
                            getAbsolutePath())
                    .compressToFile(actualImage);
//            tEnd = System.currentTimeMillis();

            setCompressedImage();
        }
    }

    public void compressImageLT(View view){
        if (actualImage == null) {
            showError("Please choose an image!");
        }else{
            tStart = System.currentTimeMillis();
            getResizedImage(MainActivity.this,actualImage);
//            tEnd = System.currentTimeMillis();
            setCompressedImage();
        }
    }

    private void getResizedImage(Context context, File inputImage) {
        Integer imageMaxSize = 650;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();

            // setting inJustDecodeBounds to true will not find bitmap but can give image dimensions first
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(String.valueOf(inputImage.getAbsolutePath()), options);
            if (options.outWidth > imageMaxSize) {
                // if image is big, we find inSampleSize and find corresponding bitmap after setting inJustDecodeBounds to false
                options.inSampleSize = options.outWidth / imageMaxSize;
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(inputImage.getAbsolutePath()), options);
                compressedImage = resizeImage(bitmap);
            } else {
                compressedImage = inputImage;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File resizeImage(Bitmap bitmap) {
        File resizedImage = null;

        try {
            int inWidth = bitmap.getWidth();
            int inHeight = bitmap.getHeight();
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, inWidth, inHeight, false);
            resizedImage = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).
                    getAbsolutePath() + "/resize" + 0 + ".jpg");
            OutputStream fOut = new BufferedOutputStream(new FileOutputStream(resizedImage));
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
            fOut.flush();
            fOut.close();
            bitmap.recycle();
            scaledBitmap.recycle();
            // cannot delete resized file, need to get the file while uploading
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resizedImage;
    }

    private void setCompressedImage() {
        compressedImageView.setImageBitmap(BitmapFactory.decodeFile(compressedImage.getAbsolutePath()));
        compressedSizeTextView.setText(String.format("Size : %s", getReadableFileSize(compressedImage.length())));
        tEnd = System.currentTimeMillis();

        Toast.makeText(this, "Compressed image save in " + compressedImage.getAbsolutePath(), Toast.LENGTH_LONG).show();
        Log.d("Compressor", "Compressed image save in " + compressedImage.getAbsolutePath());
        Log.d("Compressor","Time Elapsed: " + (tEnd-tStart));
        Toast.makeText(this, "Time Elapsed: " + (tEnd-tStart), Toast.LENGTH_LONG).show();
    }

    public void compClick(View view){
        Intent intent = new Intent(MainActivity.this,
                FullScreenActivity.class);
        intent.putExtra("imagepath",compressedImage.getAbsolutePath());
        MainActivity.this.startActivity(intent);
    }

    private void clearImage() {
        actualImageView.setBackgroundColor(getRandomColor());
        compressedImageView.setImageDrawable(null);
        compressedImageView.setBackgroundColor(getRandomColor());
        compressedSizeTextView.setText("Size : -");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                showError("Failed to open picture!");
                return;
            }
            try {
                actualImage = FileUtil.from(this, data.getData());
                actualImageView.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                actualSizeTextView.setText(String.format("Size : %s", getReadableFileSize(actualImage.length())));
                clearImage();
            } catch (IOException e) {
                showError("Failed to read picture data!");
                e.printStackTrace();
            }
        }
    }

    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private int getRandomColor() {
        Random rand = new Random();
        return Color.argb(100, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
