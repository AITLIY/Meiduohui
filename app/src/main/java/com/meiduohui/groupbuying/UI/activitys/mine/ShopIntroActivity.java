package com.meiduohui.groupbuying.UI.activitys.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.meiduohui.groupbuying.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopIntroActivity extends AppCompatActivity {

    private String TAG = "ShopIntroActivity: ";
    private Context mContext;

    @BindView(R.id.et_intro)
    EditText mEtIntro;
    @BindView(R.id.tv_length)
    TextView mTvLength;

    private String mShopIntro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_intro);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        initData();
    }

    private void initData() {

        Intent intent = getIntent();
        if (intent != null) {
            mShopIntro = intent.getStringExtra("mIntro");
            mEtIntro.setText(mShopIntro);
            mTvLength.setText(mShopIntro.length()+"/"+200);
        }

        mEtIntro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: s " + s);
                mShopIntro = mEtIntro.getText().toString();
                mTvLength.setText(s.length()+"/"+200);
                if (s.length() > 200)
                    mEtIntro.setError("输入超长");
            }
        });

    }

    @OnClick({R.id.iv_back, R.id.tv_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_save:

                Intent intent = new Intent();
                intent.putExtra("intro", mShopIntro);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
