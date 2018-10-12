package com.hitchtransporter.smart.customViews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;

public class SwipeableTextView extends AppCompatTextView {

    private Context context;

    public SwipeableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SwipeableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SwipeableTextView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    private void init(Context mContext) {
        setSwipeableListener();
    }

    private void setSwipeableListener() {
        this.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeLeft() {
                animate().translationX(-SwipeableTextView.this.getWidth())
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                setVisibility(View.GONE);
                            }
                        });
            }

            @Override
            public void onSwipeRight() {
                animate().translationX(SwipeableTextView.this.getWidth())
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if (getVisibility() == View.VISIBLE) {
            setTranslationX(0);
            setAlpha(1.0f);
        }
    }
}
