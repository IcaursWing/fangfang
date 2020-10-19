package com.lobot.lobotcontrol.component;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.AnimatorSet.Builder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.lobot.lobotcontrol.uitls.ColorUtil;

public class CircularProgressView
        extends View {
    private static final float INDETERMINANT_MIN_SWEEP = 15.0F;
    private float actualProgress;
    private int animDuration;
    private int animSteps;
    private boolean autoStartAnimation;
    private RectF bounds;
    private int color;
    private float currentProgress;
    private AnimatorSet indeterminateAnimator;
    private float indeterminateRotateOffset;
    private float indeterminateSweep;
    private boolean isIndeterminate;
    private float maxProgress;
    private Paint paint;
    private ValueAnimator progressAnimator;
    private int size = 0;
    private float startAngle;
    private ValueAnimator startAngleRotate;
    private int thickness;

    public CircularProgressView(Context paramContext) {
        super(paramContext);
        init(null, 0);
    }

    public CircularProgressView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init(paramAttributeSet, 0);
    }

    public CircularProgressView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramAttributeSet, paramInt);
    }

    private AnimatorSet createIndeterminateAnimator(float paramFloat) {
        final float f1 = 360.0F * (this.animSteps - 1) / this.animSteps + 15.0F;
        final float f2 = -90.0F + (f1 - 15.0F) * paramFloat;
        ValueAnimator localValueAnimator1 = ValueAnimator.ofFloat(new float[]{15.0F, f1});
        localValueAnimator1.setDuration(this.animDuration / this.animSteps / 2);
        localValueAnimator1.setInterpolator(new DecelerateInterpolator(1.0F));
        localValueAnimator1.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator) {
                CircularProgressView.inflate(CircularProgressView.this, ((Float) paramAnonymousValueAnimator.getAnimatedValue()).floatValue(), null);
                CircularProgressView.this.invalidate();

                float value=(float)paramAnonymousValueAnimator.getAnimatedValue();

            }
        });

        float f3 = paramFloat * 720.0F / this.animSteps;
        float f4 = (0.5F + paramFloat) * 720.0F;
        ValueAnimator localValueAnimator2 = ValueAnimator.ofFloat(new float[]{f3, f4 / this.animSteps});
        localValueAnimator2.setDuration(this.animDuration / this.animSteps / 2);
        localValueAnimator2.setInterpolator(new LinearInterpolator());
        localValueAnimator2.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator) {
                CircularProgressView.access$302(CircularProgressView.this, ((Float) paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
            }
        });
        ValueAnimator localValueAnimator3 = ValueAnimator.ofFloat(new float[]{f2, f2 + f1 - 15.0F});
        localValueAnimator3.setDuration(this.animDuration / this.animSteps / 2);
        localValueAnimator3.setInterpolator(new DecelerateInterpolator(1.0F));
        localValueAnimator3.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator) {
                CircularProgressView.access$102(CircularProgressView.this, ((Float) paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
                CircularProgressView.access$202(CircularProgressView.this, f1 - CircularProgressView.this.startAngle + f2);
                CircularProgressView.this.invalidate();
            }
        });
        ValueAnimator localValueAnimator4 = ValueAnimator.ofFloat(new float[]{f4 / this.animSteps, (paramFloat + 1.0F) * 720.0F / this.animSteps});
        localValueAnimator4.setDuration(this.animDuration / this.animSteps / 2);
        localValueAnimator4.setInterpolator(new LinearInterpolator());
        localValueAnimator4.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator) {
                CircularProgressView.access$302(CircularProgressView.this, ((Float) paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
            }
        });
        AnimatorSet localAnimatorSet = new AnimatorSet();
        localAnimatorSet.play(localValueAnimator1).with(localValueAnimator2);
        localAnimatorSet.play(localValueAnimator3).with(localValueAnimator4).after(localValueAnimator2);
        return localAnimatorSet;
    }

    private void initAttributes(AttributeSet paramAttributeSet, int paramInt) {
        paramAttributeSet = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.CircularProgressView, paramInt, 0);
        this.currentProgress = paramAttributeSet.getFloat(6, 0.0F);
        this.maxProgress = paramAttributeSet.getFloat(5, 100.0F);
        this.thickness = paramAttributeSet.getDimensionPixelSize(7, 4);
        this.isIndeterminate = paramAttributeSet.getBoolean(4, false);
        this.autoStartAnimation = paramAttributeSet.getBoolean(0, true);
        this.color = paramAttributeSet.getColor(3, ColorUtil.getPrimaryColor(getContext()));
        this.animDuration = paramAttributeSet.getInteger(1, 4000);
        this.animSteps = paramAttributeSet.getInteger(2, 3);
        paramAttributeSet.recycle();
    }

    private void updateBounds() {
        int i = getPaddingLeft();
        int j = getPaddingTop();
        this.bounds.set(this.thickness + i, this.thickness + j, this.size - i - this.thickness, this.size - j - this.thickness);
    }

    private void updatePaint() {
        this.paint.setColor(this.color);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeWidth(this.thickness);
        this.paint.setStrokeCap(Cap.BUTT);
    }

    public int getColor() {
        return this.color;
    }

    public float getMaxProgress() {
        return this.maxProgress;
    }

    public float getProgress() {
        return this.currentProgress;
    }

    public int getThickness() {
        return this.thickness;
    }

    protected void init(AttributeSet paramAttributeSet, int paramInt) {
        getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.CircularProgressView, paramInt, 0).recycle();
        initAttributes(paramAttributeSet, paramInt);
        this.paint = new Paint(1);
        updatePaint();
        this.bounds = new RectF();
        if (this.autoStartAnimation) {
            startAnimation();
        }
    }

    public boolean isIndeterminate() {
        return this.isIndeterminate;
    }

    protected void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        if (isInEditMode()) {
        }
        for (float f = this.currentProgress; ; f = this.actualProgress) {
            f /= this.maxProgress;
            break;
        }
        if (!this.isIndeterminate) {
            paramCanvas.drawArc(this.bounds, this.startAngle, f * 360.0F, false, this.paint);
        } else {
            paramCanvas.drawArc(this.bounds, this.startAngle + this.indeterminateRotateOffset, this.indeterminateSweep, false, this.paint);
        }
    }

    protected void onMeasure(int paramInt1, int paramInt2) {
        super.onMeasure(paramInt1, paramInt2);
        int i = getPaddingLeft() + getPaddingRight();
        int j = getPaddingTop() + getPaddingBottom();
        paramInt2 = getMeasuredWidth() - i;
        paramInt1 = getMeasuredHeight() - j;
        if (paramInt2 < paramInt1) {
            paramInt1 = paramInt2;
        }
        this.size = paramInt1;
        setMeasuredDimension(this.size + i, this.size + j);
    }

    protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
        if (paramInt1 >= paramInt2) {
            paramInt1 = paramInt2;
        }
        this.size = paramInt1;
        updateBounds();
    }

    public void resetAnimation() {
        if ((this.startAngleRotate != null) && (this.startAngleRotate.isRunning())) {
            this.startAngleRotate.cancel();
        }
        if ((this.progressAnimator != null) && (this.progressAnimator.isRunning())) {
            this.progressAnimator.cancel();
        }
        if ((this.indeterminateAnimator != null) && (this.indeterminateAnimator.isRunning())) {
            this.indeterminateAnimator.cancel();
        }
        boolean bool = this.isIndeterminate;
        int i = 0;
        if (!bool) {
            this.startAngle = -90.0F;
            this.startAngleRotate = ValueAnimator.ofFloat(new float[]{-90.0F, 270.0F});
            this.startAngleRotate.setDuration(5000L);
            this.startAngleRotate.setInterpolator(new DecelerateInterpolator(2.0F));
            this.startAngleRotate.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator) {
                    CircularProgressView.access$102(CircularProgressView.this, ((Float) paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
                    CircularProgressView.this.invalidate();
                }
            });
            this.startAngleRotate.start();
            this.actualProgress = 0.0F;
            this.progressAnimator = ValueAnimator.ofFloat(new float[]{this.actualProgress, this.currentProgress});
            this.progressAnimator.setDuration(500L);
            this.progressAnimator.setInterpolator(new LinearInterpolator());
            this.progressAnimator.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator) {
                    CircularProgressView.access$002(CircularProgressView.this, ((Float) paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
                    CircularProgressView.this.invalidate();
                }
            });
            this.progressAnimator.start();
        } else {
            this.startAngle = -90.0F;
            this.indeterminateSweep = 15.0F;
            this.indeterminateAnimator = new AnimatorSet();
            AnimatorSet localAnimatorSet;
            for (Object localObject = null; i < this.animSteps; localObject = localAnimatorSet) {
                localAnimatorSet = createIndeterminateAnimator(i);
                Builder localBuilder = this.indeterminateAnimator.play(localAnimatorSet);
                if (localObject != null) {
                    localBuilder.after((Animator) localObject);
                }
                i++;
            }
            this.indeterminateAnimator.addListener(new AnimatorListenerAdapter() {
                boolean wasCancelled = false;

                public void onAnimationCancel(Animator paramAnonymousAnimator) {
                    this.wasCancelled = true;
                }

                public void onAnimationEnd(Animator paramAnonymousAnimator) {
                    if (!this.wasCancelled) {
                        CircularProgressView.this.resetAnimation();
                    }
                }
            });
            this.indeterminateAnimator.start();
        }
    }

    public void setColor(int paramInt) {
        this.color = paramInt;
        updatePaint();
        invalidate();
    }

    public void setIndeterminate(boolean paramBoolean) {
        int i;
        if (this.isIndeterminate == paramBoolean) {
            i = 1;
        } else {
            i = 0;
        }
        this.isIndeterminate = paramBoolean;
        if (i != 0) {
            resetAnimation();
        }
    }

    public void setMaxProgress(float paramFloat) {
        this.maxProgress = paramFloat;
        invalidate();
    }

    public void setProgress(float paramFloat) {
        this.currentProgress = paramFloat;
        if (!this.isIndeterminate) {
            if ((this.progressAnimator != null) && (this.progressAnimator.isRunning())) {
                this.progressAnimator.cancel();
            }
            this.progressAnimator = ValueAnimator.ofFloat(new float[]{this.actualProgress, paramFloat});
            this.progressAnimator.setDuration(500L);
            this.progressAnimator.setInterpolator(new LinearInterpolator());
            this.progressAnimator.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator) {
                    CircularProgressView.access$002(CircularProgressView.this, ((Float) paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
                    CircularProgressView.this.invalidate();
                }
            });
            this.progressAnimator.start();
        }
        invalidate();
    }

    public void setThickness(int paramInt) {
        this.thickness = paramInt;
        updatePaint();
        updateBounds();
        invalidate();
    }

    public void startAnimation() {
        resetAnimation();
    }
}
