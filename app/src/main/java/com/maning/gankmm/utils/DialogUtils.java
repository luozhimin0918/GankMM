package com.maning.gankmm.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by maning on 16/6/22.
 */
public class DialogUtils {

    public static MaterialDialog showMyDialog(final Context context, String title, String content, String positiveBtnText, String negativeBtnText, final OnDialogClickListener onDialogClickListener) {

        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText(positiveBtnText)
                .negativeText(negativeBtnText)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (onDialogClickListener != null) {
                            onDialogClickListener.onConfirm();
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (onDialogClickListener != null) {
                            onDialogClickListener.onCancel();
                        }
                    }
                })
                .show();
        materialDialog.setCancelable(false);
        return materialDialog;
    }

    public interface OnDialogClickListener {

        void onConfirm();

        void onCancel();
    }

}
