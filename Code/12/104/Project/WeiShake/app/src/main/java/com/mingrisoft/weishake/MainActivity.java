package com.mingrisoft.weishake;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //定义音效
    SoundPool soundPool;
    int soundId;
    //定义振动器
    Vibrator vibrator;
    ShakeListener mShakeListner = null;               //定义传感器事件
    private RelativeLayout imgUp;                   //定义向上动画图片布局
    private RelativeLayout imgDn;                   //定义向下动画图片布局

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取震动器服务
        vibrator = (Vibrator) getApplication().getSystemService(VIBRATOR_SERVICE);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)           //设置音效使用场景
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)    //设置音效类型
                .build();
        soundPool=new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)        //设置音效池属性
                .setMaxStreams(10).build();                 //设置最多可容纳10个音频流
        soundId = soundPool.load(this, R.raw.shake, 1);
        //获取上下动画布局
        imgUp = (RelativeLayout) findViewById(R.id.shakeUp);
        imgDn = (RelativeLayout) findViewById(R.id.shakeDown);
        //实现了摇一摇监听类
        mShakeListner = new ShakeListener(this);
        mShakeListner.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake() {
                startAnim();                        //启动动画
                mShakeListner.stop();             //停止监听
                soundPool.play(soundId, 1, 1, 0, 0, 1);      //播放摇一摇音效
                startVibrato();                     //开始震动
                delayMessage();                     //延迟消息
            }
        });
    }
    //标题栏返回按钮的单击事件
    public void shake_activity_back(View v){
        this.finish();
    }

    //定义震动
    public void startVibrato(){
        vibrator.vibrate(new long[]{500,200,500,200},-1);
    }
    //启动动画
    public void startAnim() {
        //摇一摇图片向上并返回的动画
        AnimationSet animup = new AnimationSet(true);
        TranslateAnimation translateAnimation0 = new TranslateAnimation(    //创建向上动画
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -0.5f);
        translateAnimation0.setDuration(1000);          //设置持续时间为1秒
        TranslateAnimation translateAnimation1 = new TranslateAnimation(     //创建返回动画
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, +0.5f);
        translateAnimation1.setDuration(1000);          //设置持续时间为1秒
        translateAnimation1.setStartOffset(1000);       //向上动画结束后执行返回动画
        animup.addAnimation(translateAnimation0);       //添加向上动画
        animup.addAnimation(translateAnimation1);       //添加向下动画
        imgUp.startAnimation(animup);                   //启动向上并返回动画

        //摇一摇图片向下并返回的动画
        AnimationSet animdn = new AnimationSet(true);
        //创建向下动画
        TranslateAnimation translateAnimationdn0 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, +0.5f);
        translateAnimationdn0.setDuration(1000);          //设置持续时间为1秒
        //创建返回动画
        TranslateAnimation translateAnimationdn1 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -0.5f);
        translateAnimationdn1.setDuration(1000);          //设置持续时间为1秒
        translateAnimationdn1.setStartOffset(1000);       //向下动画结束后执行返回动画
        animdn.addAnimation(translateAnimationdn0);       //添加向下动画
        animdn.addAnimation(translateAnimationdn1);       //添加向上动画
        imgDn.startAnimation(animdn);                   //启动向下并返回动画
    }

    //延迟消息
    private void delayMessage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "抱歉，暂时没有找到\n在同一时刻摇一摇的人。\n再试一次吧！", Toast.LENGTH_SHORT).show();
                vibrator.cancel();              //取消振动器
                mShakeListner.start();          //启动监听器
            }
        }, 2000);
    }

    //当界面重新启动时启动监听器
    @Override
    protected void onRestart() {
        super.onRestart();
        mShakeListner.start();
    }

    //当界面停止时，停止监听器
    @Override
    protected void onStop() {
        super.onStop();
        mShakeListner.stop();
    }

    //当界面销毁时，停止监听器
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mShakeListner != null) {
            mShakeListner.stop();
        }
    }
}
