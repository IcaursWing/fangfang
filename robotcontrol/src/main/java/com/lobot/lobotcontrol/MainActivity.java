package com.lobot.lobotcontrol;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateInterpolator;

import com.example.robotcontrol.R;

public class MainActivity
        extends Activity
        implements AnimatorUpdateListener, AnimatorListener {
    private float alpha;
    private ObjectAnimator mAnimatorAlpha;
    private boolean startFlag = false;

    public float getAlpha() {
        return this.alpha;
    }

    public void onAnimationCancel(Animator paramAnimator) {
    }

    public void onAnimationEnd(Animator paramAnimator) {
        startActivity(new Intent(this, NormalModeActivity.class));
    }

    public void onAnimationRepeat(Animator paramAnimator) {
    }

    public void onAnimationStart(Animator paramAnimator) {
    }

    public void onAnimationUpdate(ValueAnimator paramValueAnimator) {
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);
        SysApplication.getInstance().addActivity(this);
        this.mAnimatorAlpha = ObjectAnimator.ofFloat(this, "alpha", new float[]{1.0F, 0.2F});
        this.mAnimatorAlpha.addUpdateListener(this);
        this.mAnimatorAlpha.addListener(this);
        this.mAnimatorAlpha.setDuration(2000L);
        this.mAnimatorAlpha.setInterpolator(new AccelerateInterpolator());
        this.mAnimatorAlpha.start();
    }

    public void onResume() {
        super.onResume();
        if (!this.startFlag) {
            this.startFlag = true;
        } else {
            startActivity(new Intent(this, NormalModeActivity.class));
        }
    }

    public void setAlpha(float paramFloat) {
        this.alpha = paramFloat;
    }
}
