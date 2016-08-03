package com.maning.gankmm.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.maning.gankmm.R;
import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.db.CollectDao;
import com.maning.gankmm.utils.DensityUtil;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.utils.MySnackbar;
import com.maning.library.SwitcherView;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by maning on 16/5/17.
 * 首页瀑布流图片展示
 */
public class RecyclePicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<GankEntity> commonDataResults;
    private List<GankEntity> headLines;
    private ArrayList<String> headLinesStrs;
    private LayoutInflater layoutInflater;
    private int screenWidth;

    public RecyclePicAdapter(Context context, List<GankEntity> commonDataResults) {
        this.context = context;
        this.commonDataResults = commonDataResults;
        layoutInflater = LayoutInflater.from(this.context);
        screenWidth = DensityUtil.getWidth(context);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    private void updateHeadLinesStrs() {
        if (headLines != null && headLines.size() > 0) {
            headLinesStrs = new ArrayList<>();
            for (int i = 0; i < headLines.size(); i++) {
                headLinesStrs.add(headLines.get(i).getDesc());
            }
        }
    }

    public void updateDatas(List<GankEntity> commonDataResults) {
        this.commonDataResults = commonDataResults;
        notifyDataSetChanged();
    }

    public void updateHeadLines(List<GankEntity> headLines) {
        this.headLines = headLines;
        updateHeadLinesStrs();
        notifyItemChanged(0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    public void destroyList() {
        if (headLines != null) {
            headLines.clear();
            headLines = null;
        }
        if (commonDataResults != null) {
            commonDataResults.clear();
            commonDataResults = null;
        }
    }

    public List<GankEntity> getAllDatas() {
        return this.commonDataResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (0 == viewType) {
            View inflate = layoutInflater.inflate(R.layout.item_welfare_header, parent, false);
            return new MyViewHolderHeader(inflate);
        } else {
            View inflate = layoutInflater.inflate(R.layout.item_welfare_staggered, parent, false);
            return new MyViewHolder(inflate);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolderHeader) {
            final MyViewHolderHeader viewHolder = (MyViewHolderHeader) holder;
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
            if (headLines != null && headLines.size() > 0) {
                if (viewHolder.tvLoadingHeadLine.getVisibility() == View.VISIBLE) {
                    viewHolder.tvLoadingHeadLine.setVisibility(View.GONE);
                    viewHolder.switcherView.setVisibility(View.VISIBLE);
                    //设置数据源
                    viewHolder.switcherView.setResource(headLinesStrs);
                    KLog.i(headLinesStrs.toString() + "--------------");
                    //开始滚动
                    viewHolder.switcherView.startRolling();

                    viewHolder.switcherView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = viewHolder.switcherView.getCurrentIndex();
                            GankEntity randomGankEntity = headLines.get(index);
                            KLog.i(randomGankEntity.getDesc() + "--------------");
                            IntentUtils.startToWebActivity(context,randomGankEntity.getType(),randomGankEntity.getDesc(),randomGankEntity.getUrl());
                        }
                    });
                }
            } else {
                viewHolder.tvLoadingHeadLine.setVisibility(View.VISIBLE);
                viewHolder.switcherView.setVisibility(View.GONE);
            }
        } else if (holder instanceof MyViewHolder) {
            final MyViewHolder viewHolder = (MyViewHolder) holder;
            final int newPosition = position - 1;
            final GankEntity resultsEntity = commonDataResults.get(newPosition);

            String time = resultsEntity.getPublishedAt().split("T")[0];
            viewHolder.tvShowTime.setText(time);

            //图片显示
            String url = resultsEntity.getUrl();

            if (resultsEntity.getItemHeight() > 0) {
                ViewGroup.LayoutParams layoutParams = viewHolder.rlRoot.getLayoutParams();
                layoutParams.height = resultsEntity.getItemHeight();
            }

            Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .placeholder(R.drawable.pic_gray_bg)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new SimpleTarget<Bitmap>(screenWidth / 2, screenWidth / 2) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            int width = resource.getWidth();
                            int height = resource.getHeight();
                            //计算高宽比
                            int finalHeight = (screenWidth / 2) * height / width;
                            if (resultsEntity.getItemHeight() <= 0) {
                                resultsEntity.setItemHeight(finalHeight);
                                ViewGroup.LayoutParams layoutParams = viewHolder.rlRoot.getLayoutParams();
                                layoutParams.height = resultsEntity.getItemHeight();
                            }

                            viewHolder.image.setImageBitmap(resource);
                        }
                    });

            //查询是否存在收藏
            boolean isCollect = new CollectDao().queryOneCollectByID(resultsEntity.get_id());
            if (isCollect) {
                viewHolder.btnCollect.setLiked(true);
            } else {
                viewHolder.btnCollect.setLiked(false);
            }
            viewHolder.btnCollect.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    boolean insertResult = new CollectDao().insertOneCollect(resultsEntity);
                    if (insertResult) {
                        MySnackbar.makeSnackBarBlack(viewHolder.tvShowTime, "收藏成功");
                    } else {
                        MySnackbar.makeSnackBarRed(viewHolder.tvShowTime, "收藏失败");
                        likeButton.setLiked(false);
                    }
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    boolean deleteResult = new CollectDao().deleteOneCollect(resultsEntity.get_id());
                    if (deleteResult) {
                        MySnackbar.makeSnackBarBlack(viewHolder.tvShowTime, "取消收藏成功");
                    } else {
                        MySnackbar.makeSnackBarRed(viewHolder.tvShowTime, "取消收藏失败");
                        likeButton.setLiked(true);
                    }

                }
            });

            //如果设置了回调，则设置点击事件
            if (mOnItemClickLitener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickLitener.onItemClick(viewHolder.itemView, newPosition);
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        if (commonDataResults != null) {
            return commonDataResults.size() + 1;
        } else {
            return 0;
        }

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.tvShowTime)
        TextView tvShowTime;
        @Bind(R.id.btn_collect)
        LikeButton btnCollect;
        @Bind(R.id.rl_root)
        RelativeLayout rlRoot;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class MyViewHolderHeader extends RecyclerView.ViewHolder {
        @Bind(R.id.switcherView)
        SwitcherView switcherView;

        @Bind(R.id.tv_loading_headline)
        TextView tvLoadingHeadLine;

        public MyViewHolderHeader(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void destroyHeadLines() {
            KLog.i("---------------destroyHeadLines");
            switcherView.destroySwitcher();
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }
}
