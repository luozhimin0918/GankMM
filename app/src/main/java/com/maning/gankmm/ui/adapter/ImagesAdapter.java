package com.maning.gankmm.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.maning.gankmm.R;

import java.util.ArrayList;

/**
 * Created by maning on 16/6/21.
 * 图片浏览
 */
public class ImagesAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<String> mDatas;
    private LayoutInflater layoutInflater;
    private View mCurrentView;

    public ImagesAdapter(Context mContext, ArrayList<String> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        layoutInflater = LayoutInflater.from(this.mContext);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mCurrentView = (View) object;
    }

    public View getPrimaryItem() {
        return mCurrentView;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        final String imageUrl = mDatas.get(position);
        View inflate = layoutInflater.inflate(R.layout.item_show_image, container, false);
        final ImageView imageView = (ImageView) inflate.findViewById(R.id.imageView);
        Glide
                .with(mContext)
                .load(imageUrl)
                .thumbnail(0.2f)
                .into(imageView);
        container.addView(inflate);

        return inflate;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
