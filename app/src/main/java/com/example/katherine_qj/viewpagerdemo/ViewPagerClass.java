package com.example.katherine_qj.viewpagerdemo;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Katherine-qj on 2016/11/20.
 */

public class ViewPagerClass extends RelativeLayout implements Runnable {
    /*定义初始变量*/
    /*定义关于ViewPager初始变量*/
    private ViewPager viewPager;
    private List<View> views;
    /*定义关于小圆点的初始变量*/
    private LinearLayout viewDots;
    private List<ImageView> dots;
    private int dotsSpacing = 2;
    private int dotsRedio = 15;
    /*定于关于轮播的初始变量*/
    private int position = 0;
    private boolean isContinue = true;
    private boolean isAlive = true;
    private  int gravity = Gravity.RIGHT;
    private  int changeTime = 1500;



    public ViewPagerClass(Context context) {
        this(context, null);
    }

    public ViewPagerClass(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerClass(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViewPager();
        initLinearLayout();
    }
    /*初始化viewPager*/
    public void initViewPager() {
        viewPager = new ViewPager(getContext());
        LayoutParams viewPagerLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(viewPager,viewPagerLp);
    }
   /* 初始化LinearLayout*/
    public void initLinearLayout(){
        viewDots = new LinearLayout(getContext());
        LayoutParams viewDotsLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        viewDotsLp.addRule(ALIGN_PARENT_BOTTOM);
        viewDotsLp.bottomMargin = dpTopx(5);
        viewDots.setGravity(gravity);
        addView(viewDots,viewDotsLp);
    }
    /*为viewPager构造适配器*/
    class ViewPagerAdapter extends PagerAdapter{
    /*以下四个方法是必须被重载的*/
        @Override
        public int getCount() {
           if (views==null){
               return 0;
           }
            return views.size();
        }
         /*
        该函数用来判断instantiateItem(ViewGroup, int)函数所返回来的Key
        与一个页面视图是否是代表的同一个视图(即它俩是否是对应的，对应的表示同一个View)
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
        /*为即将展示页做操作*/
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (views.get(position).getParent()!=null){
                ((ViewGroup)views.get(position).getParent()).removeView(views.get(position));
            }
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }
    }


    Handler pagerHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            viewPager.setCurrentItem(msg.what);
           /* setCurrentItem(int index)方法主要用来制定初始化的页面。
            例如加入3个页面通过setCurrentItem(0)制定第一个页面为当前页面*/
            super.handleMessage(msg);
        }
    };
    /*根据图片数量生成圆点*/
    private void addDots(int size){
        dots = new ArrayList<ImageView>();
        for (int i = 0; i < size; i++) {
            ImageView dot = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpTopx(dotsRedio), dpTopx(dotsRedio));
            params.setMargins(dpTopx(dotsSpacing), 0, dpTopx(dotsSpacing), 0);
            dot.setLayoutParams(params);
            if (i == 0) {
                dot.setBackgroundResource(R.drawable.dot_focused);
            } else {
                dot.setBackgroundResource(R.drawable.dot_normal);
            }
            dots.add(dot);
            viewDots.addView(dot);
        }
    }
    public void setViewPagerViews(List<View> views){
        this.views = views;
        addDots(views.size());
        viewPager.setAdapter(new ViewPagerAdapter());
        /*滑动的时候要改变圆点的颜色*/
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int positiona) {
             position = positiona;
                for (int i = 0; i < dots.size(); i++) {
                    if (position == i) {
                        dots.get(i).setBackgroundResource(
                                R.drawable.dot_focused);
                    } else {
                        dots.get(i)
                                .setBackgroundResource(R.drawable.dot_normal);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        isContinue = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        isContinue = true;
                        break;
                    default:
                        isContinue = true;
                        break;
                }
                return false;
            }
        });
        new Thread(this).start();


    }

    @Override
    public void run() {
        while (isAlive) {
            if (isContinue) {
                pagerHandler.sendEmptyMessage(position);
                position = (position + 1) % views.size();
                try {
                    Thread.sleep(changeTime);
                } catch (InterruptedException e) {
                }
            }
        }
    }
    private int dpTopx(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    /*当调到其他页面时 调用stop停止线程*/
    public void stop() {
        isAlive = false;
    }
}
