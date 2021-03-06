package xyzs.hy.com.xyzs.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import xyzs.hy.com.xyzs.DetailActivity;
import xyzs.hy.com.xyzs.DetailNoActivity;
import xyzs.hy.com.xyzs.R;
import xyzs.hy.com.xyzs.adapter.FoundAdapter;
import xyzs.hy.com.xyzs.entity.Found;

/**
 * 寻物全部碎片
 */
public class FoundFragment extends Fragment {
    private ArrayList<Found> mFoundDatas;//数据源
    private RecyclerView mRecycleView;//列表控件
    private SwipeRefreshLayout mSwipeRefreshLayout;//下拉刷新控件
    private FoundAdapter mFoundAdapter;//适配器
    private View mFoundView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //引入布局，固定用法，详情百度fragment用法
        mFoundView = inflater.inflate(R.layout.fragment_found, container, false);
        //设置SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) mFoundView.findViewById(R.id.SwipeRefreshLayout_found);
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        initData();
        initView();
        return mFoundView;
    }

    /**
     * 初始化视图
     */

    private void initView() {
        mRecycleView = (RecyclerView) mFoundView.findViewById(R.id.recyclerview_found);
        mFoundAdapter = new FoundAdapter(getActivity(), mFoundDatas);
        mRecycleView.setAdapter(mFoundAdapter);
        //设置布局管理
        LinearLayoutManager lin = new LinearLayoutManager(getActivity());
        mRecycleView.setLayoutManager(lin);
        refreshDatas();
        mFoundAdapter.setmOnItemClickListener(new FoundAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent;
                //根据标识，跳转不同详情页
                if (mFoundDatas.get(position).getStatus() == 0) {
                    intent = new Intent(getActivity(), DetailNoActivity.class);
                    intent.putExtra("name", mFoundDatas.get(position).getPublisher().getUsername());
                    intent.putExtra("time", mFoundDatas.get(position).getUpdatedAt());
                    intent.putExtra("title", mFoundDatas.get(position).getTitle());
                    intent.putExtra("describe", mFoundDatas.get(position).getDescribe());
                    intent.putExtra("phone", mFoundDatas.get(position).getPhone());
                    intent.putExtra("headURL", mFoundDatas.get(position).getPublisher().getHeadSculpture());
                    startActivity(intent);
                } else {
                    intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("name", mFoundDatas.get(position).getPublisher().getUsername());
                    intent.putExtra("time", mFoundDatas.get(position).getUpdatedAt());
                    intent.putExtra("title", mFoundDatas.get(position).getTitle());
                    intent.putExtra("describe", mFoundDatas.get(position).getDescribe());
                    intent.putExtra("phone", mFoundDatas.get(position).getPhone());
                    intent.putExtra("headURL", mFoundDatas.get(position).getPublisher().getHeadSculpture());
                    intent.putExtra("url", mFoundDatas.get(position).getImageURL());
                    startActivity(intent);

                }
            }

            @Override
            public boolean OnItemLongClick(int position) {
                return false;
            }
        });

    }

    /**
     * 初始化数据
     */
    private void initData() {
        mFoundDatas = new ArrayList<Found>();
        BmobQuery<Found> query = new BmobQuery<Found>();
        query.include("publisher");
        query.order("-updatedAt");
        query.findObjects(getActivity(), new FindListener<Found>() {
            @Override
            public void onSuccess(List<Found> object) {
                mFoundDatas.addAll(object);
                initView();
                refreshDatas();
            }

            @Override
            public void onError(int code, String msg) {
            }
        });
    }

    //下拉刷新
    private void refreshDatas() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BmobQuery<Found> query = new BmobQuery<Found>();
                        query.include("publisher");
                        query.order("-updatedAt");
                        if (getActivity() != null) {
                            query.findObjects(getActivity(), new FindListener<Found>() {
                                @Override
                                public void onSuccess(List<Found> object) {
                                    //添加新的数据
                                    mFoundAdapter.addItem(object);

                                }

                                @Override
                                public void onError(int code, String msg) {
                                    Toast.makeText(getActivity(), code + msg, Toast.LENGTH_LONG).show();
                                }
                            });
                            Toast.makeText(getActivity(), "刷新成功", Toast.LENGTH_LONG).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });
    }
}
