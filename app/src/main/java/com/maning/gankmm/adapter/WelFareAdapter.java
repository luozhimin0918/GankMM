package com.maning.gankmm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.maning.gankmm.R;
import com.maning.gankmm.bean.PublicData;
import com.maning.gankmm.db.CollectDao;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by maning on 16/3/3.
 * 测试用的Adapter
 */
public class WelFareAdapter extends BaseAdapter {

    private Context context;
    private List<PublicData.ResultsEntity> commonDataResults;
    private LayoutInflater layoutInflater;

    public WelFareAdapter(Context context, List<PublicData.ResultsEntity> commonDataResults) {
        this.context = context;
        this.commonDataResults = commonDataResults;
        layoutInflater = LayoutInflater.from(this.context);
    }

    public void updateList(List<PublicData.ResultsEntity> commonDataResults){
        this.commonDataResults = commonDataResults;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return commonDataResults.size();
    }

    @Override
    public Object getItem(int position) {
        return commonDataResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_welfare, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final PublicData.ResultsEntity resultsEntity = commonDataResults.get(position);

        viewHolder.tvShowTime.setText(resultsEntity.getCreatedAt().split("T")[0]);
        viewHolder.tvShowWho.setText("图：" + resultsEntity.getWho());
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

        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_common.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.tvShowWho)
        TextView tvShowWho;
        @Bind(R.id.tvShowTime)
        TextView tvShowTime;
        @Bind(R.id.btn_collect)
        LikeButton btnCollect;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
