package cn.zhaoshuhao.cniaosshop.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import cn.zhaoshuhao.cniaosshop.R;

/**
 * Created by zsh06 on 2016/9/21.
 */

public class CNToolbar extends Toolbar {

    private static final String TAG = "CNToolbar";

    private LayoutInflater mInflater;
    private View mView;
    private EditText mEtSearch;
    private TextView mTvTitle;
    private Button mBtnRight;
    private ImageButton mIbNavigation;
    private View.OnClickListener mRightButtonClickListener;


    public CNToolbar(Context context) {
        this(context, null);
    }

    public CNToolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CNToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes
                    (attrs, R.styleable.CNToolbar, defStyleAttr, 0);
            final Drawable nacIcon = a.getDrawable(R.styleable.CNToolbar_rightButtonIcon);
            if (nacIcon != null) {
                setRightButtonIcon(nacIcon);
            }

            String rbText = a.getString(R.styleable.CNToolbar_rightButtonText);
            if (!TextUtils.isEmpty(rbText)) {
                setRightButtonText(rbText);
            }

            boolean isShowSearchView = a.getBoolean(R.styleable.CNToolbar_isShowSearchView, false);
            if (isShowSearchView) {
                showSearchView();
                hideTitle();
                hideRightBtn();
            }
            a.recycle();
        }
    }

    private void initView() {
        if (mView == null) {
            mInflater = LayoutInflater.from(getContext());
            mView = mInflater.inflate(R.layout.toolbar, null);

            mBtnRight = (Button) mView.findViewById(R.id.id_btn_right);
            mTvTitle = (TextView) mView.findViewById(R.id.id_tv_title);
            mEtSearch = (EditText) mView.findViewById(R.id.id_et_search);
            mIbNavigation = (ImageButton) mView.findViewById(R.id.id_ib_navigation);

            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
            addView(mView, lp);

//            mView.removeAllViews();
//            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
//            addView(mTvTitle, lp);
//
//            lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
//            addView(mBtnRight, lp);
//
//            lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
//            addView(mEtSearch, lp);
        }
    }

    @Override
    public void setNavigationIcon(@Nullable Drawable icon) {
        if (mIbNavigation != null) {
            showNavigationButton();
            mIbNavigation.setImageDrawable(icon);
            mIbNavigation.getBackground().setAlpha(0);
        }
    }

    @Override
    public void setNavigationOnClickListener(OnClickListener listener) {
        if (mIbNavigation != null)
            mIbNavigation.setOnClickListener(listener);
    }

    public void showNavigationButton() {
        if (mIbNavigation != null) mIbNavigation.setVisibility(VISIBLE);
    }

    public void hideNavigationButton() {
        if (mIbNavigation != null) mIbNavigation.setVisibility(GONE);
    }

    @Override
    public void setTitle(@StringRes int resId) {
        setTitle(getContext().getText(resId));
    }

    @Override
    public void setTitle(CharSequence title) {
        initView();
        if (mTvTitle != null) {
            showTitle();
            mTvTitle.setText(title);
        }
    }

    public void showSearchView() {
        if (mEtSearch != null) {
            mEtSearch.setVisibility(VISIBLE);
        }
    }

    public void hideSearchView() {
        if (mEtSearch != null) {
            mEtSearch.setVisibility(GONE);
        }
    }

    public void showTitle() {
        if (mTvTitle != null) {
            mTvTitle.setVisibility(VISIBLE);
        }
    }

    public void hideTitle() {
        if (mTvTitle != null) {
            mTvTitle.setVisibility(GONE);
        }
    }

    public void showRightBtn() {
        if (mBtnRight != null) {
            mBtnRight.setVisibility(VISIBLE);
        }
    }

    public void hideRightBtn() {
        if (mBtnRight != null) {
            mBtnRight.setVisibility(GONE);
        }
    }

    public void setRightButtonText(String s) {
        if (mBtnRight != null) {
            showRightBtn();
            mBtnRight.setText(s);
        }
    }

    public void setRightButtonIcon(Drawable rightButtonIcon) {
        if (mBtnRight != null) {
            showRightBtn();
            mBtnRight.setBackground(rightButtonIcon);
        }
    }

    public Button getRightButton() {
        return mBtnRight;
    }

    public void setOnRightButtonOnClickListener(OnClickListener li) {
        if (mBtnRight != null && mRightButtonClickListener == null) {
            mRightButtonClickListener = li;
            mBtnRight.setOnClickListener(li);
        }
    }
}
