package cn.zhaoshuhao.cniaosshop.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.zhaoshuhao.cniaosshop.R;

/**
 * Created by zsh06
 * Created on 2016/10/20 10:33.
 */

public class NumberAddSubView extends LinearLayout implements View.OnClickListener {
    private LayoutInflater mInflater;

    private Button mBtnAdd;
    private Button mBtnSub;
    private TextView mTvNum;

    private int value = 1;
    private int minValue;
    private int maxValue;

    public NumberAddSubView(Context context) {
        this(context, null);
    }

    public NumberAddSubView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberAddSubView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NumberAddSubView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mInflater = LayoutInflater.from(context);
        initView();
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberAddSubView, defStyleAttr, defStyleRes);
            int value = a.getInt(R.styleable.NumberAddSubView_value, 0);
            setValue(value);
            int minValue = a.getInt(R.styleable.NumberAddSubView_minValue, 0);
            setMinValue(minValue);
            int maxValue = a.getInt(R.styleable.NumberAddSubView_maxValue, 0);
            setMaxValue(maxValue);

            Drawable add = a.getDrawable(R.styleable.NumberAddSubView_btnAddBackground);
            Drawable sub = a.getDrawable(R.styleable.NumberAddSubView_btnSubBackground);
            Drawable text = a.getDrawable(R.styleable.NumberAddSubView_textViewBackground);

            if (add == null) add = getResources().getDrawable(R.drawable.bg_btn_add_sub, null);
            if (sub == null) sub = getResources().getDrawable(R.drawable.bg_btn_add_sub, null);
            if (text == null) text = getResources().getDrawable(R.drawable.bg_text_view_num, null);
            setBtnAddBackground(add);
            setBtnSubBackground(sub);
            setTextViewBackground(text);
            a.recycle();
        }
    }

    private void initView() {
        View view = mInflater.inflate(R.layout.weight_number_add_sub, this, true);
        mBtnAdd = (Button) view.findViewById(R.id.id_btn_add);
        mBtnSub = (Button) view.findViewById(R.id.id_btn_sub);
        mTvNum = (TextView) view.findViewById(R.id.id_tv_num);
        mBtnAdd.setOnClickListener(this);
        mBtnSub.setOnClickListener(this);
    }

    private OnButtonClickListener mOnButtonClickListener;

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        mOnButtonClickListener = onButtonClickListener;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.id_btn_add) {
            addNum();
            if (mOnButtonClickListener != null) {
                mOnButtonClickListener.OnButtonAddListener(v, value);
            }
        } else if (id == R.id.id_btn_sub) {
            subNum();
            if (mOnButtonClickListener != null) {
                mOnButtonClickListener.OnButtonSubListener(v, value);
            }
        }
    }

    private void addNum() {
        if (value < maxValue) {
            value++;
        }
        setTextValue(value);
    }

    private void subNum() {
        if (value > minValue) {
            value--;
        }
        setTextValue(value);
    }

    public interface OnButtonClickListener {
        void OnButtonAddListener(View v, int value);

        void OnButtonSubListener(View v, int value);
    }

    private void setTextValue(int value) {
        mTvNum.setText(value + "");
    }

    public int getValue() {
        String s = mTvNum.getText().toString();
        if (!TextUtils.isEmpty(s)) {
            value = Integer.parseInt(s);
            return value;
        }
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        setTextValue(value);
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setBtnAddBackground(Drawable drawable) {
        mBtnAdd.setBackground(drawable);
    }

    public void setBtnSubBackground(Drawable drawable) {
        mBtnSub.setBackground(drawable);
    }

    public void setTextViewBackground(Drawable drawable) {
        mTvNum.setBackground(drawable);
    }
}
