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


public class FoundFragment extends Fragment {
    private ArrayList<Found> FoundDatas;
    private RecyclerView mRecycleView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FoundAdapter adapter;
    private View Found;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Found = inflater.inflate(R.layout.fragment_found, container, false);

        FoundDatas = new ArrayList<Found>();
        mSwipeRefreshLayout = (SwipeRefreshLayout) Found.findViewById(R.id.SwipeRefreshLayout_found);
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        refreshDatas();

        if (FoundDatas == null || FoundDatas.size() == 0) {
            getDatas();
        }

        return Found;
    }

    private void setDatas() {
        if (FoundDatas.size() != 0) {
            mRecycleView = (RecyclerView) Found.findViewById(R.id.recyclerview_found);
            adapter = new FoundAdapter(getActivity(), FoundDatas);
            mRecycleView.setAdapter(adapter);
            LinearLayoutManager lin = new LinearLayoutManager(getActivity());
            mRecycleView.setLayoutManager(lin);
            adapter.setmOnItemClickListener(new FoundAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(int position) {
                    Intent intent;
                    if (FoundDatas.get(position).getStatus() == 0) {
                        intent = new Intent(getActivity(), DetailNoActivity.class);
                        intent.putExtra("name", FoundDatas.get(position).getPublisher().getUsername());
                        intent.putExtra("time", FoundDatas.get(position).getUpdatedAt());
                        intent.putExtra("title", FoundDatas.get(position).getTitle());
                        intent.putExtra("describe", FoundDatas.get(position).getDescribe());
                        intent.putExtra("phone", FoundDatas.get(position).getPhone());
                        startActivity(intent);
                    } else {
                        intent = new Intent(getActivity(), DetailActivity.class);
//                        intent.putExtra("head",lostDatas.get(position).g)
                        intent.putExtra("name", FoundDatas.get(position).getPublisher().getUsername());
                        intent.putExtra("time", FoundDatas.get(position).getUpdatedAt());
                        intent.putExtra("title", FoundDatas.get(position).getTitle());
                        intent.putExtra("describe", FoundDatas.get(position).getDescribe());
                        intent.putExtra("phone", FoundDatas.get(position).getPhone());
                        intent.putExtra("url", FoundDatas.get(position).getImageURL());
                        startActivity(intent);

                    }
                }

                @Override
                public boolean OnItemLongClick(int position) {
                    return false;
                }
            });
        }
    }

    private void getDatas() {
        BmobQuery<Found> query = new BmobQuery<Found>();
        query.include("publisher");
        query.order("-updatedAt");
        query.findObjects(getActivity(), new FindListener<Found>() {
            @Override
            public void onSuccess(List<Found> object) {
                FoundDatas.addAll(object);
                setDatas();
            }

            @Override
            public void onError(int code, String msg) {
                Toast.makeText(getActivity(), code + msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void refreshDatas() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refreshDatas();
                        BmobQuery<Found> query = new BmobQuery<Found>();
                        query.include("publisher");
                        query.order("-updatedAt");
                        query.findObjects(getActivity(), new FindListener<Found>() {
                            @Override
                            public void onSuccess(List<Found> object) {
                                FoundDatas.addAll(object);
                                setDatas();
                            }

                            @Override
                            public void onError(int code, String msg) {
                                Toast.makeText(getActivity(), code + msg, Toast.LENGTH_LONG).show();
                            }
                        });
                        Toast.makeText(getActivity(), "刷新成功", Toast.LENGTH_LONG).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }
}
