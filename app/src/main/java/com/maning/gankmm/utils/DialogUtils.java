package com.maning.gankmm.utils;

import android.content.Context;
import android.view.View;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by maning on 16/6/22.
 */
public class DialogUtils {

    public static MaterialDialog initDialog(final Context context, String title, String content, String positiveBtnText, String negativeBtnText, final OnDialogClickListener onDialogClickListener) {
        final MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.setTitle(title);
        materialDialog.setMessage(content);
        materialDialog.setPositiveButton(positiveBtnText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                if (onDialogClickListener != null) {
                    onDialogClickListener.onConfirm();
                }
            }
        });
        materialDialog.setNegativeButton(negativeBtnText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                if (onDialogClickListener != null) {
                    onDialogClickListener.onCancel();
                }
            }
        });
        return materialDialog;
    }

    public interface OnDialogClickListener {

        void onConfirm();

        void onCancel();
    }

}
