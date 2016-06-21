package com.maning.gankmm.ui.iView;

import android.graphics.Bitmap;

/**
 * Created by maning on 16/6/21.
 */
public interface IImageView extends IBaseView{

    void showBasesProgressSuccess(String msg);

    void showBasesProgressError(String msg);

    Bitmap getCurrentImageViewBitmap();

}
