package com.maning.gankmm.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.maning.gankmm.R;
import com.maning.gankmm.bean.CategoryTitleBean;

import java.util.List;

/**
 * Created by maning on 16/6/22.
 */
public class RecycleCodesTitleAdapter extends RecyclerView.Adapter<RecycleCodesTitleAdapter.MyViewHolder> {

    private Context context;
    private List<CategoryTitleBean> mDatas;
    private LayoutInflater layoutInflater;
    private String type = "全部代码";

    public RecycleCodesTitleAdapter(Context context, List<CategoryTitleBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = layoutInflater.inflate(R.layout.item_codes_title, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tvTitle.setText(mDatas.get(position).getTitle());

        if (type.equals(holder.tvTitle.getText().toString())) {
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.white));
            holder.tvTitle.setBackgroundColor(context.getResources().getColor(R.color.mainColoe));
        } else {
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.textBlack));
            holder.tvTitle.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        //如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(holder.itemView, position);
                    type = mDatas.get(position).getTitle();
                    notifyDataSetChanged();
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

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
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
