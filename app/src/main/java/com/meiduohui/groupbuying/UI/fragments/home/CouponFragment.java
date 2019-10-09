package com.meiduohui.groupbuying.UI.fragments.home;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.login.LoginActivity;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CouponFragment extends Fragment {

    private String TAG = "HomeFragment: ";
    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;

    private Unbinder unbinder;

    @BindView(R.id.unused_tv)                                     // 未使用
    TextView unused_tv;
    @BindView(R.id.unused_v)
    View unused_v;

    @BindView(R.id.used_tv)                                       // 已使用
    TextView used_tv;
    @BindView(R.id.used_v)
    View used_v;

    @BindView(R.id.expired_tv)                                    // 已过期
    TextView expired_tv;
    @BindView(R.id.expired_v)
    View expired_v;

    @BindView(R.id.coupon_item_list)
    PullToRefreshListView coupon_item_list;

    private int page = 1;
    private static final int SEARCH_LESSON_PARAMETER  = 100;        //参数查询
    private static final int SEARCH_LESSON_PULL_UP = 200;           //上拉加载
    private int mSearchType = 100;  // 查询的标志

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_coupon, container, false);
        unbinder = ButterKnife.bind(this,mView);

        initData();
        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();

    }

    private void initData() {
        mContext = getContext();
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
//        initPullToRefresh();
    }


    @OnClick({R.id.unused_rl,R.id.used_rl,R.id.expired_rl})
    public void onTopBarClick(View v) {

        switch (v.getId()) {

            case R.id.unused_rl:

//                    mIsFJ = false;
//
//                    if (mMoreTuiMessageInfos != null)
//                        updataListView(mMoreTuiMessageInfos); //  推荐请求
//                    else {
//                        updateData();     //  推荐请求
//                    }

                break;

            case R.id.used_rl:

//                    mIsFJ = true;
//                    LogUtils.i(TAG + " mMoreFJMessageInfos" + (mMoreFJMessageInfos == null));
//                    if (mMoreFJMessageInfos != null)
//                        updataListView(mMoreFJMessageInfos); //  附近请求
//                    else {
//
//                        mIsMore = false;
//                        mIsFJ = true;
//                        getIndexData();     // 附近请求
//                    }

                break;

            case R.id.expired_rl:

//                    mIsFJ = true;
//                    LogUtils.i(TAG + " mMoreFJMessageInfos" + (mMoreFJMessageInfos == null));
//                    if (mMoreFJMessageInfos != null)
//                        updataListView(mMoreFJMessageInfos); //  附近请求
//                    else {
//
//                        mIsMore = false;
//                        mIsFJ = true;
//                        getIndexData();     // 附近请求
//                    }

                break;
        }

        changeTabItemStyle(v);
    }

    // 初始化列表
    private void initPullListView() {

        setListView();

        coupon_item_list.setMode(PullToRefreshBase.Mode.BOTH);

        coupon_item_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {  //拉动时
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page= 1;

                mSearchType = SEARCH_LESSON_PARAMETER;
//                getLessonData(mSort,mCateId,mKeyword, page); // 下拉刷新搜索
                setViewForResult(true,"");
                LogUtils.i("AllClassFragment: onPullDownToRefresh 下拉" + page + "页");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;

                mSearchType = SEARCH_LESSON_PULL_UP;
//                getLessonData(mSort,mCateId,mKeyword, page); // 上拉加载搜索

                LogUtils.i("AllClassFragment: onPullUpToRefresh 上拉" + page + "页");
            }
        });

        coupon_item_list.setOnItemClickListener(new AdapterView.OnItemClickListener() { //点击item时
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
                    startActivity(new Intent(mContext, LoginActivity.class));
                    return;
                } else {
//                    RecommendLesson.LessonBean lessonBean = mShowList.get(position-1);
//                    LogUtils.i("AllClassFragment: ItemClick position " + position);
//                    Intent intent = new Intent(mContext,LessonActivity2.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("LessonBean",lessonBean);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
                }
            }
        });

        coupon_item_list.setOnScrollListener(new AbsListView.OnScrollListener() {  //列表滑动时
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int tempPos = coupon_item_list.getRefreshableView().getFirstVisiblePosition();

                //                if (tempPos > 0) {
                //                    goTop.setVisibility(View.VISIBLE);
                //                } else {
                //                    goTop.setVisibility(View.GONE);
                //                }

            }
        });

        //        goTop.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                coupon_item_list.getRefreshableView().smoothScrollToPosition(0);//移动到首部
        //            }
        //        });

    }

    //初始化列表控件上下拉的状态
    private void setListView() {

        ILoadingLayout startLabels = coupon_item_list.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新...");// 刷新时
        startLabels.setReleaseLabel("放开刷新");// 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = coupon_item_list.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");// 刚下拉时，显示的提示
        endLabels.setRefreshingLabel("正在载入...");// 刷新时
        endLabels.setReleaseLabel("放开加载更多");// 下来达到一定距离时，显示的提示

        //        headView = LayoutInflater.from(this).inflate(R.layout.headview, null);
        //        listview.getRefreshableView().addHeaderView(headView);//为ListView添加头布局
    }

    // 更新分类
//    private void initLessonCategoryList(List<LessonCategory> LessonCategorys) {
//        LogUtils.i("AllClassFragment: initLessonCategoryList size"  + LessonCategorys.size());
//        adapter2 = new LessonCategoryAdapter2(mContext, LessonCategorys,this);
//
//        final StaggeredGridLayoutManager staggeredGridLayoutManager = new NewStaggeredGridLayoutManager(4, OrientationHelper.VERTICAL);
//        lesson_category_rv.setLayoutManager(staggeredGridLayoutManager);
//        lesson_category_rv.setAdapter(adapter2);
//    }

//    @Override
//    public void onSearch(LessonCategory.SonlistBean sonlistBean) {
//
//        isShowlessonfiltrate(false);
//        lesson_filtrate_tv.setText(sonlistBean.getName());
//
//        mCateId = sonlistBean.getId()+"";
//        mKeyword = "";
//        mSort = "";
//        page= 1;
//
//        mSearchType = SEARCH_LESSON_PARAMETER;
//        getLessonData(mSort,mCateId,mKeyword, page); // 通过id搜索
//    }


    // 根据获取结果显示view
    private void setViewForResult(boolean isSuccess,String msg) {

        if (isSuccess) {
            mView.findViewById(R.id.not_data).setVisibility(View.GONE);

        } else {
            mView.findViewById(R.id.not_data).setVisibility(View.VISIBLE);
            ((TextView) mView.findViewById(R.id.not_data_tv)).setText(msg);
        }
    }

    // 更新课程列表数据
    private void upDataLessonListView() {

        switch (mSearchType) {

            case SEARCH_LESSON_PARAMETER:

//                mShowList.clear();
//                mShowList.addAll(mLessonSearches);
//                LogUtils.i("AllClassFragment: SEARCH_LESSON_FOR_PARAMETER "  + mShowList.size());
//
//                adapter.notifyDataSetChanged();
//                coupon_item_list.getRefreshableView().smoothScrollToPosition(0);//移动到首部
//                coupon_item_list.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        coupon_item_list.onRefreshComplete();
//                    }
//                }, 1000);
                break;

            case SEARCH_LESSON_PULL_UP:

//                mShowList.addAll(mLessonSearches);
//                //                adapter.addLast(mLessonSearches);
//                LogUtils.i("AllClassFragment: SEARCH_LESSON_PULL_UP " + mShowList.size());
//
//                adapter.notifyDataSetChanged();
//                coupon_item_list.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        coupon_item_list.onRefreshComplete();
//                        if (mLessonSearches.size()==0){
//                            ToastUtil.show(mContext,"没有更多结果");
//                        }
//                    }
//                }, 1000);
                break;
        }
    }

    // 设置标题栏颜色
    private void changeTabItemStyle(View view) {

        unused_tv.setTextColor(view.getId() == R.id.unused_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.m_old_price));
        used_tv.setTextColor(view.getId() == R.id.used_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.m_old_price));
        expired_tv.setTextColor(view.getId() == R.id.expired_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.m_old_price));

        unused_v.setVisibility(view.getId() ==  R.id.unused_rl ? View.VISIBLE:View.GONE);
        used_v.setVisibility(view.getId() == R.id.used_rl ? View.VISIBLE:View.GONE);
        expired_v.setVisibility(view.getId() == R.id.expired_rl ? View.VISIBLE:View.GONE);
    }


}
