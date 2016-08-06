package com.maning.gankmm.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.maning.gankmm.R;
import com.maning.gankmm.bean.CategoryContentBean;

import java.util.List;

/**
 * Created by maning on 16/6/22.
 */
public class RecycleCodesContentAdapter extends RecyclerView.Adapter<RecycleCodesContentAdapter.MyViewHolder> {

    private Context context;
    private final RequestManager glide;
    private List<CategoryContentBean> mDatas;
    private LayoutInflater layoutInflater;

    public RecycleCodesContentAdapter(Context context, List<CategoryContentBean> mDatas,RequestManager glide) {
        this.context = context;
        this.glide = glide;
        this.mDatas = mDatas;
        layoutInflater = LayoutInflater.from(this.context);
    }

    public void setDatas(List<CategoryContentBean> mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = layoutInflater.inflate(R.layout.item_codes_content, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        CategoryContentBean categoryContentBean = mDatas.get(position);

        holder.tvTitle.setText(categoryContentBean.getTitle());
        holder.tvDescription.setText(categoryContentBean.getDescription());
        holder.tvType.setText(categoryContentBean.getType());
        holder.tvOtherInfo.setText(categoryContentBean.getOtherInfo());

        String imageUrl = categoryContentBean.getImageUrl();
        glide
                .load(imageUrl)
                .asBitmap()
                .centerCrop()
                .into(holder.ivShow);

        //如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvDescription;
        TextView tvType;
        TextView tvOtherInfo;
        ImageView ivShow;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivShow = (ImageView) itemView.findViewById(R.id.iv_show);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvOtherInfo = (TextView) itemView.findViewById(R.id.tv_other_info);
            tvType = (TextView) itemView.findViewById(R.id.tv_type);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
        }
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
