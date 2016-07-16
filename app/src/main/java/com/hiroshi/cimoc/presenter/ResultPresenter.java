package com.hiroshi.cimoc.presenter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hiroshi.cimoc.core.Kami;
import com.hiroshi.cimoc.core.base.Manga;
import com.hiroshi.cimoc.model.Comic;
import com.hiroshi.cimoc.ui.activity.DetailActivity;
import com.hiroshi.cimoc.ui.activity.ResultActivity;
import com.hiroshi.cimoc.ui.adapter.BaseAdapter;
import com.hiroshi.cimoc.utils.EventMessage;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by Hiroshi on 2016/7/4.
 */
public class ResultPresenter extends BasePresenter {

    private ResultActivity mResultActivity;

    private Manga mManga;
    private String keyword;
    private int page;
    private boolean isLoading;

    public ResultPresenter(ResultActivity activity, int source, String keyword) {
        this.mResultActivity = activity;
        this.mManga = Kami.getMangaById(source);
        this.keyword = keyword;
        this.page = 1;
        this.isLoading = false;
    }

    public RecyclerView.OnScrollListener getScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastItem = mResultActivity.findLastItemPosition();
                int itemCount = mResultActivity.getItemCount();
                if (lastItem >= itemCount - 4 && dy > 0) {
                    if (!isLoading) {
                        isLoading = true;
                        mManga.search(keyword, page++);
                    }
                }
            }
        };
    }

    public BaseAdapter.OnItemClickListener getItemClickListener() {
        return new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Comic comic = mResultActivity.getItem(position);
                Intent intent = DetailActivity.createIntent(mResultActivity, comic.getSource(), comic.getPath());
                mResultActivity.startActivity(intent);
            }
        };
    }

    public void initManga() {
        mManga.search(keyword, page++);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMessage msg) {
        switch (msg.getType()) {
            case EventMessage.SEARCH_SUCCESS:
                List list = (List<Comic>) msg.getData();
                if (!list.isEmpty()) {
                    mResultActivity.addAll(list);
                    mResultActivity.hideProgressBar();
                    isLoading = false;
                }
                break;
        }
    }

}