package com.example.katherine_qj.viewpagerdemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPagerClass viewPager;
    private List<View> views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPagerClass)findViewById(R.id.viewPager);
        views = new ArrayList<>();
        ImageView imageView1 = new ImageView(getApplicationContext()) ;
        ImageView imageView2 = new ImageView(getApplicationContext());
        ImageView imageView3 = new ImageView(getApplicationContext());
        ImageView imageView4 = new ImageView(getApplicationContext());
        imageView1.setBackgroundResource(R.drawable.lunboa);
        views.add(imageView1);
        imageView2.setBackgroundResource(R.drawable.lunboc);
        views.add(imageView2);
        imageView3.setBackgroundResource(R.drawable.lunbob);
        views.add(imageView3);
        imageView4.setBackgroundResource(R.drawable.lunbod);
        views.add(imageView4);
        viewPager.setViewPagerViews(views);
    }
}
