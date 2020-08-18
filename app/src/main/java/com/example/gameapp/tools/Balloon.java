package com.example.gameapp.tools;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.example.gameapp.R;


public class Balloon extends androidx.appcompat.widget.AppCompatImageView implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
    private ValueAnimator mAnimator;
    private BalloonListener mListener;
    private boolean mPopped;


    public Balloon(Context context) {
        super(context);
    }

    public Balloon(Context context,int color,int rawHeight) {
        super(context);
        mListener = (BalloonListener) context;
        this.setImageResource(R.drawable.balloon);
        this.setColorFilter(color);
        int rawWidth=rawHeight;

        int dpHeight=PixelHelper.pixelsToDp(rawHeight,context);
        int dpWidth=PixelHelper.pixelsToDp(rawWidth,context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dpWidth,dpWidth);
        setLayoutParams(params);
    }


    public void releaseBalloon(int screenHieght,int duration)
    {
        mAnimator = new ValueAnimator();
        mAnimator.setDuration(duration);
        mAnimator.setFloatValues(screenHieght,0f);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setTarget(this);
        mAnimator.addUpdateListener(this);
        mAnimator.addListener(this);
        mAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

        setY((Float) animation.getAnimatedValue());
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if(!mPopped)
        {
            mListener.popBalloon(this,false);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mPopped && event.getAction()==MotionEvent.ACTION_DOWN)
        {
            mListener.popBalloon(this,true);
            mPopped=true;
            mAnimator.cancel();
        }
        return super.onTouchEvent(event);
    }

    public void setPopped(boolean popped)
    {
        mPopped = popped;
        if(popped)
        {
            mAnimator.cancel();
        }
    }

    public interface BalloonListener
    {
        void popBalloon(Balloon balloon,boolean userTouch);

    }
}
