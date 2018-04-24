package com.luck.picture.lib.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * Created by pangli on 2018/4/11 11:38
 * 备注：   图片涂鸦容器
 */

public class PhotoEditorView extends RelativeLayout {

    private ImageView mImgSource;
    private BrushDrawingView mBrushDrawingView;
    private static final int imgSrcId = 1, brushSrcId = 2;

    public PhotoEditorView(Context context) {
        super(context);
        init();
    }

    public PhotoEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotoEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PhotoEditorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        //Setup image attributes
        mImgSource = new ImageView(getContext());
        mImgSource.setId(imgSrcId);
        mImgSource.setAdjustViewBounds(true);
        LayoutParams imgSrcParam = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgSrcParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        //Setup brush view
        mBrushDrawingView = new BrushDrawingView(getContext());
        mBrushDrawingView.setVisibility(GONE);
        mBrushDrawingView.setId(brushSrcId);
        //Align brush to the size of image view
        LayoutParams brushParam = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        brushParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        brushParam.addRule(RelativeLayout.ALIGN_TOP, imgSrcId);
        brushParam.addRule(RelativeLayout.ALIGN_BOTTOM, imgSrcId);


        //Add image source
        addView(mImgSource, imgSrcParam);
        //Add brush view
        addView(mBrushDrawingView, brushParam);
    }

    /**
     * Source image which you want to edit
     *
     * @return source ImageView
     */
    public ImageView getImageView() {
        return mImgSource;
    }

    BrushDrawingView getBrushDrawingView() {
        return mBrushDrawingView;
    }

    /**
     * 返回最终Bitmap
     *
     * @return
     */
    public Bitmap getResultBitmap() {
        BitmapDrawable drawable = (BitmapDrawable) mImgSource.getDrawable();
        Bitmap imageViewBitmap = drawable.getBitmap();
        RectF clipRect = new RectF();
        clipRect.top = mImgSource.getY();
        clipRect.left = mImgSource.getX();
        clipRect.bottom = mImgSource.getHeight();
        clipRect.right = mImgSource.getWidth();
        PointF srcSize = new PointF();
        srcSize.x = imageViewBitmap.getWidth();
        srcSize.y = imageViewBitmap.getHeight();
        Bitmap bitmap = mBrushDrawingView.getBrushResultImage(clipRect, srcSize);
        Bitmap resultBitmap = Bitmap.createBitmap(imageViewBitmap.getWidth(), imageViewBitmap.getHeight(), Bitmap.Config
                .ARGB_4444);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(imageViewBitmap, 0, 0, null);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return resultBitmap;
    }
}
