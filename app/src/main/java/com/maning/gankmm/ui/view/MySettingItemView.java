package com.maning.gankmm.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.maning.gankmm.R;

/**
 * Created by maning on 16/6/17.
 *  设置界面的组合控件
 */
public class MySettingItemView extends FrameLayout {

    private Context context;

    //声明的控件
    private TextView tv_title;  //标题
    private TextView tv_right;  //右侧文字
    private ImageView iv_red_dot;   //小红点

    private String title;
    private String rightText;
    private boolean isShowRedDot;

    public MySettingItemView(Context context) {
        this(context, null);
    }

    public MySettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;

        //获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingView);
        //遍历拿到自定义属性
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int index = typedArray.getIndex(i);
            switch (index) {
                case R.styleable.SettingView_setting_title:
                    title = typedArray.getString(R.styleable.SettingView_setting_title);
                    break;
                case R.styleable.SettingView_setting_red_dot:
                    isShowRedDot = typedArray.getBoolean(R.styleable.SettingView_setting_red_dot, false);
                    break;
                case R.styleable.SettingView_setting_right_text:
                    rightText = typedArray.getString(R.styleable.SettingView_setting_right_text);
                    break;
            }
        }
        //销毁
        typedArray.recycle();

        //初始化View
        initView();

    }

    private void initView() {

        View.inflate(context, R.layout.setting_item_view, this);
        tv_title = (TextView) findViewById(R.id.setting_tv_title);
        tv_right = (TextView) findViewById(R.id.setting_tv_right);
        iv_red_dot = (ImageView) findViewById(R.id.setting_iv_red);

        //赋值
        tv_title.setText(title);
        setRedDot(isShowRedDot);
        setRightText(rightText);

    }

    public void setRedDot(boolean flag) {
        if (flag) {
            iv_red_dot.setVisibility(View.VISIBLE);
        } else {
            iv_red_dot.setVisibility(View.GONE);
        }
    }

    public void setRightText(String rightText) {
        if (TextUtils.isEmpty(rightText)) {
            tv_right.setVisibility(View.GONE);
        } else {
            tv_right.setVisibility(View.VISIBLE);
            tv_right.setText(rightText);
        }
    }

    public String getRightText() {
        return tv_right.getText().toString();
    }

}
