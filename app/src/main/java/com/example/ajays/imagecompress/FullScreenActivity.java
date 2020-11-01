package com.example.ajays.imagecompress;

import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.ViewGroup;
import android.widget.ImageView;

import static android.view.WindowManager.*;

public class FullScreenActivity extends AppCompatActivity {
    private ImageView imageView;
    private String fullScreenInd;

    private String image_path;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

//        Toolbar tb = (Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(tb);

        imageView = findViewById(R.id.imageView);
        image_path = getIntent().getStringExtra("imagepath");

        imageView.setImageBitmap(BitmapFactory.decodeFile(image_path));

        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
                    LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();

            imageView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            imageView.setAdjustViewBounds(false);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

    }
}
