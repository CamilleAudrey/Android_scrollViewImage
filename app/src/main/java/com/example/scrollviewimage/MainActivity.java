package com.example.scrollviewimage;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

public class MainActivity extends Activity {

    private ScrollView scrollView;
    private ImageView img;

    // 记录首次按下位置
    private float mFirstPosition = 0;
    // 是否正在放大
    private Boolean mScaling = false;

    private DisplayMetrics metric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取屏幕宽高
        metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        //获取控件
        scrollView = (ScrollView) findViewById(R.id.scollview);
        img = (ImageView) findViewById(R.id.img);

        //设置图片初始大小  这里我设为满屏的16:9
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) img.getLayoutParams();
        lp.width = metric.widthPixels;
        lp.height = metric.widthPixels * 9 / 16;
        img.setLayoutParams(lp);

        //监听滚动事件
        scrollView.setOnTouchListener(new OnTouchListener() {
            @SuppressLint({ "ClickableViewAccessibility", "NewApi" })
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) img.getLayoutParams();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        //手指离开后恢复图片
                        mScaling = false;
                        replyImage();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!mScaling) {
                            if (scrollView.getScrollY() == 0) {
                                mFirstPosition = event.getY();// 滚动到顶部时记录位置，否则正常返回
                            } else {
                                break;
                            }
                        }
                        int distance = (int) ((event.getY() - mFirstPosition) * 0.6); // 滚动距离乘以一个系数
                        if (distance < 0) { // 当前位置比记录位置要小，正常返回
                            break;
                        }

                        // 处理放大
                        mScaling = true;
                        lp.width = metric.widthPixels + distance;
                        lp.height = (metric.widthPixels + distance) * 9 / 16;
                        img.setLayoutParams(lp);
                        return true; // 返回true表示已经完成触摸事件，不再处理
                }
                return false;
            }
        });

    }

    // 回弹动画 (使用了属性动画)
    @SuppressLint("NewApi")
    public void replyImage() {
        final ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) img.getLayoutParams();
        final float w = img.getLayoutParams().width;// 图片当前宽度
        final float h = img.getLayoutParams().height;// 图片当前高度
        final float newW = metric.widthPixels;// 图片原宽度
        final float newH = metric.widthPixels * 9 / 16;// 图片原高度

        // 设置动画
        ValueAnimator anim = ObjectAnimator.ofFloat(0.0F, 1.0F).setDuration(200);

        anim.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                lp.width = (int) (w - (w - newW) * cVal);
                lp.height = (int) (h - (h - newH) * cVal);
                img.setLayoutParams(lp);
            }
        });
        anim.start();

    }
}

