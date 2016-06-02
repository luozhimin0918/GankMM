package com.maning.gankmm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.maning.gankmm.R;
import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.db.CollectDao;

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

    public RecyclePicAdapter(Context context, List<GankEntity> commonDataResults) {
        this.context = context;
        this.commonDataResults = commonDataResults;
        layoutInflater = LayoutInflater.from(this.context);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public void updateDatas(List<GankEntity> commonDataResults){
        this.commonDataResults = commonDataResults;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = layoutInflater.inflate(R.layout.item_welfare, parent, false);

        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        final GankEntity resultsEntity = commonDataResults.get(position);

        viewHolder.tvShowTime.setText(resultsEntity.getCreatedAt().split("T")[0]);
        viewHolder.tvShowWho.setText("来自：" + resultsEntity.getWho());
        //图片显示
        String url = resultsEntity.getUrl();
        Glide
                .with(context)
                .load(url)
                .placeholder(R.drawable.pic_gray_bg)
                .error(R.drawable.pic_gray_bg)
                .centerCrop()
                .into(viewHolder.image);

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
                    Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "收藏失败", Toast.LENGTH_SHORT).show();
                    likeButton.setLiked(false);
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                boolean deleteResult = new CollectDao().deleteOneCollect(resultsEntity.get_id());
                if (deleteResult) {
                    Toast.makeText(context, "取消收藏成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "取消收藏失败", Toast.LENGTH_SHORT).show();
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
        @Bind(R.id.tvShowWho)
        TextView tvShowWho;
        @Bind(R.id.tvShowTime)
        TextView tvShowTime;
        @Bind(R.id.btn_collect)
        LikeButton btnCollect;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }
}
