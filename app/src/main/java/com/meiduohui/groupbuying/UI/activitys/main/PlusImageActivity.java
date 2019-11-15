package com.meiduohui.groupbuying.UI.activitys.main;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.adapter.ImgViewPagerAdapter;
import com.meiduohui.groupbuying.commons.CommonParameters;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlusImageActivity extends AppCompatActivity {

    @BindView(R.id.viewPager)   //展示图片的ViewPager
    ViewPager mViewPager;
    @BindView(R.id.activity_image)
    LinearLayout mActivityImage;
    @BindView(R.id.position_tv)
    TextView mPositionTv; //图片的位置，第几张图片

    private ArrayList<String> imgList; //图片的数据源
    private int mPosition; //第几张图片
    private ImgViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plus_image);
        ButterKnife.bind(this);

        imgList = getIntent().getStringArrayListExtra(CommonParameters.IMG_LIST);
        mPosition = getIntent().getIntExtra(CommonParameters.POSITION, 0);
        initView();
    }

    private void initView() {

        mViewPager.addOnPageChangeListener(new PageChangeListener());

        mAdapter = new ImgViewPagerAdapter(this, imgList);
        mViewPager.setAdapter(mAdapter);
        mPositionTv.setText(mPosition + 1 + "/" + imgList.size());
        mViewPager.setCurrentItem(mPosition);
    }


    class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mPosition = position;
            mPositionTv.setText(position + 1 + "/" + imgList.size());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}
