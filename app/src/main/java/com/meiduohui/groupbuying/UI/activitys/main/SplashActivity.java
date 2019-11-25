package com.meiduohui.groupbuying.UI.activitys.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.commons.CommonParameters;


public class SplashActivity extends AppCompatActivity {

    private Context mContext;
    private static final int NEED_JUMP = 100;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case NEED_JUMP:

                    break;

            }
        }
    };

    private class SplashTask implements Runnable {

        @Override
        public void run() {
            LogUtils.d("SplashActivity : start HomepageActivity");

            Intent intent = new Intent(mContext, HomepageActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mContext = this;
        ImageView bg = findViewById(R.id.splash_bg);
        Glide.with(this).load(R.drawable.icon_bg_splash).into(bg);

        Intent intent = getIntent();

        if (intent != null) {

            String scheme = intent.getScheme();
            String url = intent.getDataString();

            LogUtils.i("share_jump scheme " + scheme);
            LogUtils.i("share_jump url " + url);

            if (CommonParameters.SCHEME.equals(scheme)) {

                if (!TextUtils.isEmpty(url)) {

                    GlobalParameterApplication.isNeedJump = true;
                    Intent intent1 = new Intent(mContext, HomepageActivity.class);
                    intent1.putExtra("share_url", url);
                    startActivity(intent1);
                    finish();
                    mHandler.sendEmptyMessageDelayed(NEED_JUMP, 0);
                    return;
                }
            }
        }

        mHandler.postDelayed(new SplashTask(), 2000);
    }
}
