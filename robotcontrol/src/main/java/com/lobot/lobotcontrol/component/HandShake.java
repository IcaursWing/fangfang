package com.lobot.lobotcontrol.component;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import com.lobot.lobotcontrol.R.styleable;

public class HandShake
        extends View
{
    private static final String TAG = "HandShake";
    private Bitmap ballBgBmp;
    private Bitmap ballBmp;
    private Point ballCenter;
    private int ballRadius;
    private Rect bgDest;
    private Point center;
    private int currentDirection;
    private Rect dest;
    private DirectionListener directionListener;
    private boolean isTouching;
    private Vibrator mVibrator;
    private Paint paint;
    private int radius;
    private int ringColor;
    private float ringWidth;
    private final Object syncLock = new Object();

    public HandShake(Context paramContext)
    {
        super(paramContext);
        init();
    }

    public HandShake(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
        init();
        setAttribute(paramContext, paramAttributeSet, 0, 0);
    }

    public HandShake(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
        super(paramContext, paramAttributeSet, paramInt);
        init();
        setAttribute(paramContext, paramAttributeSet, paramInt, 0);
    }

    @TargetApi(21)
    public HandShake(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
    {
        super(paramContext, paramAttributeSet, paramInt1, paramInt2);
        init();
        setAttribute(paramContext, paramAttributeSet, paramInt1, paramInt2);
    }

    private double distance(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
        return Math.sqrt(Math.pow(paramFloat1 - paramFloat3, 2.0D) + Math.pow(paramFloat2 - paramFloat4, 2.0D));
    }

    private int getDirection(Point paramPoint)
    {
        Point[] arrayOfPoint = new Point[4];
        Point localPoint = new Point(this.center.x, this.center.y - this.radius);
        int i = 0;
        arrayOfPoint[0] = localPoint;
        localPoint = new Point(this.center.x + this.radius, this.center.y);
        int j = 1;
        arrayOfPoint[1] = localPoint;
        arrayOfPoint[2] = new Point(this.center.x, this.center.y + this.radius);
        arrayOfPoint[3] = new Point(this.center.x - this.radius, this.center.y);
        while (j < arrayOfPoint.length)
        {
            int k = i;
            if (distance(arrayOfPoint[j].x, arrayOfPoint[j].y, paramPoint.x, paramPoint.y) < distance(arrayOfPoint[i].x, arrayOfPoint[i].y, paramPoint.x, paramPoint.y)) {
                k = j;
            }
            j++;
            i = k;
        }
        return i;
    }

    private void init()
    {
        this.isTouching = false;
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.dest = new Rect(0, 0, 0, 0);
        this.bgDest = new Rect(0, 0, 0, 0);
        this.center = new Point();
        this.ballCenter = new Point();
        this.mVibrator = ((Vibrator)getContext().getSystemService("vibrator"));
        this.currentDirection = -1;
    }

    private void playVibrator()
    {
        if (this.mVibrator != null) {
            this.mVibrator.vibrate(30L);
        }
    }

    private void setBallRect(float paramFloat1, float paramFloat2)
    {
        double d1 = distance(this.center.x, this.center.y, paramFloat1, paramFloat2);
        double d2 = this.radius / d1;
        float f1 = paramFloat1;
        float f2 = paramFloat2;
        if (d2 < 1.0D)
        {
            double d3 = paramFloat1;
            double d4 = 1.0D - d2;
            f1 = (float)(d3 * d2 + this.center.x * d4);
            f2 = (float)(paramFloat2 * d2 + d4 * this.center.y);
        }
        this.ballCenter.x = ((int)f1);
        this.ballCenter.y = ((int)f2);
        this.dest.left = ((int)(f1 - this.ballRadius));
        this.dest.top = ((int)(f2 - this.ballRadius));
        this.dest.right = ((int)(f1 + this.ballRadius));
        this.dest.bottom = ((int)(f2 + this.ballRadius));
        if (d1 > this.radius - this.ballRadius / 2) {
            synchronized (this.syncLock)
            {
                int i = getDirection(this.ballCenter);
                if (this.currentDirection != i)
                {
                    this.currentDirection = i;
                    if (this.directionListener != null) {
                        this.directionListener.onDirection(this.currentDirection);
                    }
                    playVibrator();
                }
            }
        }
        if ((d1 != 0.0D) && (this.currentDirection != -2))
        {
            this.currentDirection = -2;
            if (this.directionListener != null) {
                this.directionListener.onDirection(this.currentDirection);
            }
        }
    }

    private void stop()
    {
        try
        {
            setBallRect(this.center.x, this.center.y);
            if (this.currentDirection != -1)
            {
                this.currentDirection = -1;
                if (this.directionListener != null) {
                    this.directionListener.onDirection(this.currentDirection);
                }
            }
            return;
        }
        finally
        {
            localObject = finally;
            throw ((Throwable)localObject);
        }
    }

    public float dpToPx(float paramFloat, Resources paramResources)
    {
        return TypedValue.applyDimension(1, paramFloat, paramResources.getDisplayMetrics());
    }

    public DirectionListener getDirectionListener()
    {
        return this.directionListener;
    }

    protected void onDraw(Canvas paramCanvas)
    {
        super.onDraw(paramCanvas);
        if (this.ballBgBmp == null)
        {
            this.paint.setStyle(Style.STROKE);
            this.paint.setColor(this.ringColor);
            this.paint.setStrokeWidth(this.ringWidth);
            paramCanvas.drawCircle(this.center.x, this.center.y, this.radius, this.paint);
        }
        else
        {
            paramCanvas.drawBitmap(this.ballBgBmp, null, this.bgDest, null);
        }
        if (this.ballBmp != null) {
            paramCanvas.drawBitmap(this.ballBmp, null, this.dest, null);
        }
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        paramInt3 -= paramInt1;
        paramInt2 = paramInt4 - paramInt2;
        if (paramInt3 < paramInt2) {
            paramInt1 = paramInt3;
        } else {
            paramInt1 = paramInt2;
        }
        this.ballRadius = (paramInt1 / 8);
        this.radius = (paramInt1 / 2 - this.ballRadius);
        this.center.x = (paramInt3 / 2);
        this.center.y = (paramInt2 / 2);
        paramInt1 = (int)dpToPx(2.0F, getResources());
        this.bgDest.top = (this.center.y - this.radius - paramInt1);
        this.bgDest.right = (this.center.x + this.radius + paramInt1);
        this.bgDest.bottom = (this.center.y + this.radius + paramInt1);
        this.bgDest.left = (this.center.x - this.radius - paramInt1);
        if (paramBoolean) {
            setBallRect(this.center.x, this.center.y);
        }
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
        switch (paramMotionEvent.getAction())
        {
            default:
                if (this.isTouching) {
                    this.isTouching = false;
                }
                break;
            case 2:
                if (!this.isTouching) {
                    this.isTouching = true;
                }
                setBallRect(paramMotionEvent.getX(), paramMotionEvent.getY());
                break;
            case 1:
                if (this.isTouching) {
                    this.isTouching = false;
                }
                stop();
                break;
            case 0:
                if (this.isTouching) {
                    break label112;
                }
                this.isTouching = true;
                break;
        }
        stop();
        label112:
        invalidate();
        return true;
    }

    protected void setAttribute(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
    {
        paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.HandShake, paramInt1, paramInt2);
        paramAttributeSet = paramContext.getDrawable(1);
        if (paramAttributeSet != null) {
            this.ballBmp = ((BitmapDrawable)paramAttributeSet).getBitmap();
        }
        paramAttributeSet = paramContext.getDrawable(0);
        if (paramAttributeSet != null) {
            this.ballBgBmp = ((BitmapDrawable)paramAttributeSet).getBitmap();
        }
        this.ringWidth = paramContext.getDimension(3, dpToPx(1.0F, getResources()));
        this.ringColor = paramContext.getColor(2, Color.argb(200, 250, 250, 250));
        paramContext.recycle();
    }

    public void setDirectionListener(DirectionListener paramDirectionListener)
    {
        this.directionListener = paramDirectionListener;
    }

    public static abstract interface DirectionListener
    {
        public static final int DIREACTION_DOWN = 2;
        public static final int DIREACTION_INITIAL = -1;
        public static final int DIREACTION_LEFT = 3;
        public static final int DIREACTION_NONE = -2;
        public static final int DIREACTION_RIGHT = 1;
        public static final int DIREACTION_UP = 0;

        public abstract void onDirection(int paramInt);
    }
}
