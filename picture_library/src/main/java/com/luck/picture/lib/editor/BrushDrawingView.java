package com.luck.picture.lib.editor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pangli on 2018/4/11 11:39
 * 备注：图片涂鸦path
 */

public class BrushDrawingView extends View {

    private float mBrushSize = 15;
    private int mOpacity = 255;

    private List<LinePath> mLinePaths = new ArrayList<>();
    private Paint mDrawPaint;

    private Canvas mDrawCanvas;
    private boolean mBrushDrawMode;

    private Path mPath;
    private float mTouchX, mTouchY;
    private static final float TOUCH_TOLERANCE = 4;


    public BrushDrawingView(Context context) {
        this(context, null);
    }

    public BrushDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupBrushDrawing();
    }

    public BrushDrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupBrushDrawing();
    }

    void setupBrushDrawing() {
        //Caution: This line is to disable hardware acceleration to make eraser feature work properly
        setLayerType(LAYER_TYPE_HARDWARE, null);
        mDrawPaint = new Paint();
        mPath = new Path();
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setDither(true);
        mDrawPaint.setColor(Color.RED);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawPaint.setStrokeWidth(mBrushSize);
        mDrawPaint.setAlpha(mOpacity);
        mDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
        this.setVisibility(View.GONE);
    }

    private void refreshBrushDrawing() {
        mBrushDrawMode = true;
        mPath = new Path();
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setDither(true);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawPaint.setStrokeWidth(mBrushSize);
        mDrawPaint.setAlpha(mOpacity);
        mDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
    }

    void setBrushDrawingMode(boolean brushDrawMode) {
        this.mBrushDrawMode = brushDrawMode;
        if (brushDrawMode) {
            this.setVisibility(View.VISIBLE);
            refreshBrushDrawing();
        }
    }

    void setOpacity(@IntRange(from = 0, to = 255) int opacity) {
        this.mOpacity = opacity;
        setBrushDrawingMode(true);
    }

    boolean getBrushDrawingMode() {
        return mBrushDrawMode;
    }

    boolean isCacheEmpty() {
        return mLinePaths.isEmpty();
    }

    void setBrushSize(float size) {
        mBrushSize = size;
        setBrushDrawingMode(true);
    }

    void setBrushColor(@ColorInt int color) {
        mDrawPaint.setColor(color);
        setBrushDrawingMode(true);
    }

    float getBrushSize() {
        return mBrushSize;
    }

    int getBrushColor() {
        return mDrawPaint.getColor();
    }

    void clearAll() {
        mLinePaths.clear();
        if (mDrawCanvas != null) {
            mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
        invalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            Bitmap canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
            mDrawCanvas = new Canvas(canvasBitmap);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (LinePath linePath : mLinePaths) {
            canvas.drawPath(linePath.getDrawPath(), linePath.getDrawPaint());
        }
        canvas.drawPath(mPath, mDrawPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (mBrushDrawMode) {
            float touchX = event.getX();
            float touchY = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchStart(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchMove(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    touchUp();
                    break;
            }
            invalidate();
            return true;
        } else {
            return false;
        }
    }

    public Bitmap getBrushResultImage(RectF clipRect, PointF srcSize) {
        Bitmap drawBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(drawBitmap);
        canvas.drawColor(Color.TRANSPARENT);
        for (int i = 0; i < mLinePaths.size(); i++) {
            LinePath linePath = mLinePaths.get(i);
            canvas.drawPath(linePath.getDrawPath(), linePath.getDrawPaint());
        }
        Bitmap clipBitmap = Bitmap.createBitmap(drawBitmap, 0, 0, (int) clipRect.right, (int) clipRect.bottom, null,
                false);
        Bitmap resultBitmap = Bitmap.createScaledBitmap(clipBitmap, (int) srcSize.x, (int) srcSize.y, true);
        drawBitmap.recycle();
        clipBitmap.recycle();
        return resultBitmap;
    }


    private class LinePath {
        private Paint mDrawPaint;
        private Path mDrawPath;

        LinePath(Path drawPath, Paint drawPaints) {
            mDrawPaint = new Paint(drawPaints);
            mDrawPath = new Path(drawPath);
        }

        Paint getDrawPaint() {
            return mDrawPaint;
        }

        Path getDrawPath() {
            return mDrawPath;
        }
    }

    boolean undo() {
        if (mLinePaths.size() > 0) {
            mLinePaths.remove(mLinePaths.size() - 1);
            invalidate();
        }
        return mLinePaths.size() != 0;
    }


    private void touchStart(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mTouchX = x;
        mTouchY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mTouchX);
        float dy = Math.abs(y - mTouchY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mTouchX, mTouchY, (x + mTouchX) / 2, (y + mTouchY) / 2);
            mTouchX = x;
            mTouchY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mTouchX, mTouchY);
        // Commit the path to our offscreen
        mDrawCanvas.drawPath(mPath, mDrawPaint);
        // kill this so we don't double draw
        mLinePaths.add(new LinePath(mPath, mDrawPaint));
        mPath = new Path();
    }
}