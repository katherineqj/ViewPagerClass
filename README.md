# ViewPagerClass
自定义轮播图
**我们在项目中首页经常会有一个轮播图模块**
**是利用viewpager实现的 每次都要重新写一遍所有的逻辑没有复用的效果**
**所以就把这个东西封装好来达到复用的效果使用起来也特别简单**
这篇会先给一个简单使用的例子之后再分析实现的过程  
虽然写的很烂  
看到后面应该会有一点点东西的
吧~


**先看一个效果图：**
![这里写图片描述](http://img.blog.csdn.net/20161121131133690)




好单调~~~~不过能看到
就是个基本的轮播图
和别的控件配合起来就会很好多     
我已经封装好了
先说说怎么用
####**第一步：**


在git上把我写好的这个类下载下来[Git_ViewPagerClass](https://github.com/katherineqj/ViewPagerClass)
git上是一个完整的例子啊



在你的项目里面加入这个类就是这个ViewPagerClass ~~~~我一般就直接复制了因为别的我也不会 
然后项目结构就大概是这样：
![这里写图片描述](http://img.blog.csdn.net/20161121131818816)

####**第二步：**



在你要使用的xml文件中直接使用这个类：
![这里写图片描述](http://img.blog.csdn.net/20161121131932255)


因为我的包名是com.example.katherine_qj.viewpagerdemo
所以控件的名字是这样 
那你的肯定就是你的包名加类名这样子  
我也
不知道我说的清楚不 

大概就是在xml中引用你项目中的这个类~~~
ViewPagerClass


####**第三步：**


![这里写图片描述](http://img.blog.csdn.net/20161121132425570)

很清楚吧 
就是实例化viewPager之后给他暴露出来的setViewPagerViews( )方法中传入一个list集合就好了 
集合里面是你要轮播的图片 底部指示物会根据你添加的图片自动生成相应的个数
~~

**如果你只是想使用一个现成的轮播图 以上就可以了**


**---------------------------------------------------------------**







**以下** 
**来分析一下这个轮播图是如何封装的：**

我想把我大概的思路写一下
首先想一下写一个轮播图需要什么？

**需要装图片** 
**需要小圆点显示播放到哪一张了**
**还需要把viewpager和小圆点联系起来**
**跑的时候图片轮播小圆点也要动**



所以我们先画一张图这样子
![这里写图片描述](http://img.blog.csdn.net/20161121134819435)

**我们第一步就是要把这些小控件组在一起**   
**把他们都放到RelativeLayout中**

**第二步 我们采用线程的方式让他们跑起来**
这样分为两步就清楚很多




这个类的创建思路我先画图表示出来 


是怎么把控件放在一起的
![这里写图片描述](http://img.blog.csdn.net/20161121140444933)


看一下这部分的代码

 - 这是初始化viewpager和小圆点部分  可以看到就是把viewpager和linearLayout放到Relativelayout中

```

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
```
 - 这是我们为Viewpager构造的适配器 
    继承自pagerAdapter重载了四个方法 方法的解释代码中都有
    
```
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
```

 - 根据图片数量生成圆点  圆点的两种样式可以 自己设置放在drawable文件夹下

```
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
```

 - 下面就是我们暴露出来的一个方法  传入一个图片集合我们 在里面通过 设置适配生成圆点  设置滑动事件等把之前的准备联系在一起

```
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
```

 - 最后可以看见有一个线程开始执行的语句  那就是当我们都设置好 了之后就可以通过线程让他轮播起来

```
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

    Handler pagerHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            viewPager.setCurrentItem(msg.what);
           /* setCurrentItem(int index)方法主要用来制定初始化的页面。
            例如加入3个页面通过setCurrentItem(0)制定第一个页面为当前页面*/
            super.handleMessage(msg);
        }
    };
```


以上就是实现过程 
还有一些小细节 

可以看一下我这个类全部的代码

