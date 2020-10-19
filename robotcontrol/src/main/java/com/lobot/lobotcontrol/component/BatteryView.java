package com.lobot.lobotcontrol.component;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.example.robotcontrol.R;

import java.util.ArrayList;

public class BatteryView
        extends View {
    private Bitmap batBmp;
    private Bitmap batLessBmp;
    private int batNum = 0;
    private Rect bgDest;
    private Point center;
    private Paint paint;
    private Paint paintBat;
    ArrayList<PointBat> pointBats;
    private boolean undefineFlag = true;

    public BatteryView(Context paramContext) {
        super(paramContext);
        init();
    }

    public BatteryView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
        setAttribute(paramContext, paramAttributeSet, 0, 0);
    }

    public BatteryView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init();
        setAttribute(paramContext, paramAttributeSet, paramInt, 0);
    }

    private void init() {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paintBat = new Paint();
        this.paintBat.setAntiAlias(true);
        this.paintBat.setColor(getResources().getColor(R.color.colorTitleBlue));
        this.paintBat.setStyle(Style.STROKE);
        this.paintBat.setStrokeWidth(3.0F);
        this.bgDest = new Rect(0, 0, 0, 0);
        this.center = new Point();
        this.pointBats = new ArrayList();
    }

    protected void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        if ((this.batNum <= 2) && (!this.undefineFlag)) {
            if (this.batLessBmp != null) {
                paramCanvas.drawBitmap(this.batLessBmp, null, this.bgDest, null);
            }
        } else if (this.batBmp != null) {
            paramCanvas.drawBitmap(this.batBmp, null, this.bgDest, null);
        }
        for (int i = 0; (i < this.batNum) && (i < 10); i++) {
            if (this.batNum <= 2) {
                this.paintBat.setColor(getResources().getColor(R.color.colorRed));
            } else {
                this.paintBat.setColor(getResources().getColor(R.color.colorTitleBlue));
            }
            paramCanvas.drawLine(((PointBat) this.pointBats.get(i)).startX, ((PointBat) this.pointBats.get(i)).startY, ((PointBat) this.pointBats.get(i)).endX,
                    ((PointBat) this.pointBats.get(i)).endY, this.paintBat);
        }
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        paramInt3 -= paramInt1;
        paramInt2 = paramInt4 - paramInt2;
        this.center.x = (paramInt3 / 2);
        this.center.y = (paramInt2 / 2);
        Rect rect = this.bgDest;
        paramInt1 = 0;
        rect.left = 0;
        this.bgDest.right = paramInt3;
        this.bgDest.top = 0;
        this.bgDest.bottom = paramInt2;
        while (paramInt1 < 10) {
            float f1 = paramInt3 / 48.0F;
            float f2 = paramInt3 * 11 / 120.0F;
            float f3 = paramInt1;
            float f4 = paramInt2;
            paramInt1++;
            PointBat pointBat = new PointBat(f1 + f3 * f2, f4, f1 + f2 * paramInt1, 0.0F);
            this.pointBats.add(pointBat);
        }
    }

    protected void setAttribute(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
        TypedArray typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.BatteryView, paramInt1, paramInt2);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) typedArray.getDrawable(paramInt1);
        if (paramAttributeSet != null) {
            this.batBmp = bitmapDrawable.getBitmap();
        }
        bitmapDrawable = (BitmapDrawable) typedArray.getDrawable(paramInt2);
        if (paramAttributeSet != null) {
            this.batLessBmp = bitmapDrawable.getBitmap();
        }
        typedArray.recycle();
    }

    public void setCurBat(long paramLong) {
        if (paramLong != -1L) {
            this.undefineFlag = false;
            this.batNum = ((int) (10L - (8400L - paramLong) / 160L));
        } else {
            this.undefineFlag = true;
            this.batNum = 0;
        }
        invalidate();
    }

    public class PointBat {
        public float endX;
        public float endY;
        public float startX;
        public float startY;

        public PointBat() {
            this.startX = 0.0F;
            this.startY = 0.0F;
            this.endX = 0.0F;
            this.endY = 0.0F;
        }

        public PointBat(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
            this.startX = paramFloat1;
            this.startY = paramFloat2;
            this.endX = paramFloat3;
            this.endY = paramFloat4;
        }
    }
}
