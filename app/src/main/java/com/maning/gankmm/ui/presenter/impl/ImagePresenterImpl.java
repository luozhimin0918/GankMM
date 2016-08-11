package com.maning.gankmm.ui.presenter.impl;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;

import com.maning.gankmm.R;
import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.constant.Constants;
import com.maning.gankmm.ui.iView.IImageView;
import com.maning.gankmm.ui.presenter.IImagePresenter;
import com.maning.gankmm.utils.BitmapUtils;

import java.io.IOException;

/**
 * Created by maning on 16/6/21.
 */
public class ImagePresenterImpl extends BasePresenterImpl<IImageView> implements IImagePresenter {

    private Context context;

    public ImagePresenterImpl(Context context, IImageView iImageView) {
        this.context = context;
        attachView(iImageView);
    }

    @Override
    public void saveImage() {
        if(mView == null){
            return;
        }
        //显示dialog
        mView.showBaseProgressDialog("正在保存...");
        final Bitmap bitmap = mView.getCurrentImageViewBitmap();
        if (bitmap == null) {
            mView.showBasesProgressError(context.getResources().getString(R.string.gank_hint_save_pic_fail));
            return;
        }
        //save Image
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean saveBitmapToSD = BitmapUtils.saveBitmapToSD(bitmap, Constants.BasePath, System.currentTimeMillis() + ".jpg", true);
                MyApplication.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if(mView == null){
                            return;
                        }
                        if (saveBitmapToSD) {
                            mView.showBasesProgressSuccess("保存成功，保存目录：" + Constants.BasePath);
                        } else {
                            mView.showBasesProgressError(context.getString(R.string.gank_hint_save_pic_fail));
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void setWallpaper() {
        if(mView == null){
            return;
        }
        mView.showBaseProgressDialog("正在设置壁纸...");
        final Bitmap bitmap = mView.getCurrentImageViewBitmap();
        if (bitmap == null) {
            mView.showBasesProgressError("设置失败");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = false;
                WallpaperManager manager = WallpaperManager.getInstance(context);
                try {
                    manager.setBitmap(bitmap);
                    flag = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    flag = false;
                } finally {
                    if(mView != null){
                        if (flag) {
                            MyApplication.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    mView.showBasesProgressSuccess("设置成功");
                                }
                            });
                        } else {
                            MyApplication.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    mView.showBasesProgressError("设置失败");
                                }
                            });
                        }
                    }

                }
            }
        }).start();
    }

}
