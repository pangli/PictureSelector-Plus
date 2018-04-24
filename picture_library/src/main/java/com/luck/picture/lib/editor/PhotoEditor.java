package com.luck.picture.lib.editor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by pangli on 2018/4/11 11:33
 * 备注：   图片涂鸦构造器
 */

public class PhotoEditor {

    private static final String TAG = PhotoEditor.class.getSimpleName();
    private PhotoEditorView parentView;
    private BrushDrawingView brushDrawingView;
    private Context context;

    private PhotoEditor(Builder builder) {
        this.context = builder.context;
        this.parentView = builder.parentView;
        this.brushDrawingView = builder.brushDrawingView;
    }

    public void setBrushDrawingMode(boolean brushDrawingMode) {
        if (brushDrawingView != null) {
            brushDrawingView.setBrushDrawingMode(brushDrawingMode);
        }
    }

    public Boolean getBrushDrawableMode() {
        return brushDrawingView != null && brushDrawingView.getBrushDrawingMode();
    }

    public void setBrushSize(float size) {
        if (brushDrawingView != null) {
            brushDrawingView.setBrushSize(size);
        }
    }

    public void setOpacity(@IntRange(from = 0, to = 100) int opacity) {
        if (brushDrawingView != null) {
            opacity = (int) ((opacity / 100.0) * 255.0);
            brushDrawingView.setOpacity(opacity);
        }
    }


    public void setPaintColor(@ColorInt int color) {
        if (brushDrawingView != null) {
            brushDrawingView.setBrushColor(color);
        }
    }

    public boolean undo() {
        return brushDrawingView != null && brushDrawingView.undo();
    }

    public void clearBrushAllViews() {
        if (brushDrawingView != null) {
            brushDrawingView.clearAll();
        }
    }

    public interface OnSaveListener {
        void onStart();

        void onSuccess(String imagePath);

        void onFailure(Boolean success);
    }

    @SuppressLint("StaticFieldLeak")
    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void saveImage(@NonNull final String imagePath, @NonNull final OnSaveListener onSaveListener) {
        new AsyncTask<String, String, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                onSaveListener.onStart();
            }

            @SuppressLint("MissingPermission")
            @Override
            protected Boolean doInBackground(String... strings) {
                // Create a media file name
                File file = new File(imagePath);
                try {
                    FileOutputStream out = new FileOutputStream(file, false);
                    if (parentView != null) {
                        Bitmap drawingCache = parentView.getResultBitmap();
                        drawingCache.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    }
                    out.flush();
                    out.close();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    onSaveListener.onFailure(false);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                if (success) {
                    clearBrushAllViews();
                    onSaveListener.onSuccess(imagePath);
                } else {
                    onSaveListener.onFailure(success);
                }
            }

        }.execute();
    }

    /**
     * Check if any changes made need to save
     *
     * @return true is nothing is there to change
     */
    public boolean isCacheEmpty() {
        if (brushDrawingView != null) {
            return brushDrawingView.isCacheEmpty();
        } else {
            return true;
        }
    }


    public static class Builder {

        private Context context;
        private PhotoEditorView parentView;
        private BrushDrawingView brushDrawingView;

        public Builder(Context context, PhotoEditorView photoEditorView) {
            this.context = context;
            parentView = photoEditorView;
            brushDrawingView = photoEditorView.getBrushDrawingView();
        }

        public PhotoEditor build() {
            return new PhotoEditor(this);
        }
    }

}
