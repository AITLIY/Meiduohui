package com.meiduohui.groupbuying.UI.activitys.mine;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.githang.statusbar.StatusBarCompat;
import com.meiduohui.groupbuying.R;

import butterknife.ButterKnife;

public class ShopIntroActivity extends AppCompatActivity {

    private String TAG = "ShopIntroActivity: ";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_intro);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        //    Intent intent = new Intent();
        //    intent.putExtra("quan_id", poiItem);
        //
        //    setResult(RESULT_OK, intent);
        //    finish();
    }

}
