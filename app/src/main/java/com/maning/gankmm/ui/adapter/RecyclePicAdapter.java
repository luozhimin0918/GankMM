package com.maning.gankmm.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.maning.gankmm.R;
import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.db.CollectDao;
import com.maning.gankmm.utils.DensityUtil;
import com.maning.gankmm.utils.MySnackbar;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by maning on 16/5/17.
 */
public class RecyclePicAdapter extends RecyclerView.Adapter<RecyclePicAdapter.MyViewHolder> {

    private Context context;
    private List<GankEntity> commonDataResults;
    private LayoutInflater layoutInflater;
    private List<Integer> mHeights;
    private int ScreenHeight;

    public RecyclePicAdapter(Context context, List<GankEntity> commonDataResults) {
        this.context = context;
        this.commonDataResults = commonDataResults;
        layoutInflater = LayoutInflater.from(this.context);
        ScreenHeight = DensityUtil.getHeight(context);
        //高度
        mHeights = new ArrayList<>();
        addHeights();
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public void updateDatas(List<GankEntity> commonDataResults) {
        this.commonDataResults = commonDataResults;
        //这里多计算了高度，因为滑动太快的话，这么可能出现计算不及时崩溃现象
        addHeights();
        notifyDataSetChanged();
    }

    public void destroyList() {
        if (mHeights != null) {
            mHeights.clear();
            mHeights = null;
        }
        if (commonDataResults != null) {
            commonDataResults.clear();
            commonDataResults = null;
        }
    }

    public List<GankEntity> getAllDatas() {
        return this.commonDataResults;
    }

    private void addHeights() {
        for (int i = 0; i < commonDataResults.size(); i++) {
            mHeights.add((int) (ScreenHeight * 0.25 + Math.random() * ScreenHeight * 0.25));
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = layoutInflater.inflate(R.layout.item_welfare_staggered, parent, false);

        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        final GankEntity resultsEntity = commonDataResults.get(position);

        String time = resultsEntity.getPublishedAt().split("T")[0];
        viewHolder.tvShowTime.setText(time);

        //图片显示
        String url = resultsEntity.getUrl();

        Glide
                .with(context)
                .load(url)
                .placeholder(R.drawable.pic_gray_bg)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.image);

        //高度
        ViewGroup.LayoutParams layoutParams = viewHolder.rlRoot.getLayoutParams();
        layoutParams.height = mHeights.get(position);

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
                    mOnItemClickLitener.onItemClick(viewHolder.itemView, position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return commonDataResults.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

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

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }
}
