package com.meiduohui.groupbuying.UI.activitys.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.githang.statusbar.StatusBarCompat;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.HomepageActivity;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.UserInfoBean;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {


    private String TAG = "SettingActivity: ";
    private Context mContext;
    private UserInfoBean mUserInfoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        initData();
    }

    private void initData() {
        mContext = this;

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            mUserInfoBean = (UserInfoBean) bundle.getSerializable("UserInfoBean");

            LogUtils.i(TAG + "initData UserInfoBean");
        }
    }


    @OnClick({R.id.iv_back, R.id.ll_vip_info, R.id.ll_shop_info, R.id.ll_rec_pwd, R.id.tv_log_out})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.ll_vip_info:
                Intent intent = new Intent(mContext, VipInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("UserInfoBean", mUserInfoBean);
                intent.putExtras(bundle);
                startActivity(intent);

                break;

            case R.id.ll_shop_info:
                Intent intent2 = new Intent(mContext, ShopInfoActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("UserInfoBean", mUserInfoBean);
                intent2.putExtras(bundle2);
                startActivity(intent2);

                break;

            case R.id.ll_rec_pwd:
                startActivity(new Intent(this,ChangePwdActivity.class));
                break;

            case R.id.tv_log_out:
                GlobalParameterApplication.getInstance().setLoginStatus(false);
                GlobalParameterApplication.getInstance().refeshHomeActivity(this);
                break;
        }
    }
}
