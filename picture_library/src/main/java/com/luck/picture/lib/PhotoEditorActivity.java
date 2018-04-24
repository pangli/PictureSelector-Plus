package com.luck.picture.lib;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.editor.ColorRadioGroup;
import com.luck.picture.lib.editor.PhotoEditor;
import com.luck.picture.lib.editor.PhotoEditorView;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;


/**
 * Created by pangli on 2018/4/10.
 * 备注：图片编辑
 */

public class PhotoEditorActivity extends AppCompatActivity {
    public static final String TYPE = "type";
    public static final int TYPE_ALBUM = 1;
    public static final int TYPE_OTHER = 2;
    public static final String EXTRA_EDITOR_MEDIA = "extra_editor_media";
    public static final String RESULT_EDITOR_MEDIA = "result_editor_media";
    public static final String EXTRA_IMAGE_PATH = "extra_image_path";
    public static final String RESULT_IMAGE_PATH = "result_image_path";
    public static final int REQUESTCODE = 102;
    public static final int RESULTCODE = 100;
    private static String storagePath = "";
    private static final File parentPath = Environment.getExternalStorageDirectory();
    private static String EDITOR_PHOTO_NAME = "Editor";
    private Context mContext;
    private PhotoEditorView photoEditorView;
    private TextView btnCancel;
    private TextView btnComplete;
    private ColorRadioGroup crgColors;
    private ImageButton btnUndo;
    private PhotoEditor photoEditor;
    private String url;
    private String savePath;
    private ProgressDialog mProgressDialog;
    private Intent mIntent;
    private LocalMedia localMedial;
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_editor_activity);
        mContext = this;
        btnUndo = findViewById(R.id.btn_undo);
        crgColors = findViewById(R.id.crg_colors);
        btnComplete = findViewById(R.id.btn_complete);
        btnCancel = findViewById(R.id.btn_cancel);
        photoEditorView = findViewById(R.id.photo_editor_view);
        initWidget();
        bindListener();
        startInvoke();
    }

    public void initWidget() {
        photoEditor = new PhotoEditor.Builder(this, photoEditorView).build();
        photoEditor.setBrushDrawingMode(true);
        crgColors.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                photoEditor.setPaintColor(crgColors.getCheckColor());
            }
        });
        //编辑保存地址
        savePath = saveEditorPhotoJpgPath();
    }

    private void bindListener() {
        btnCancel.setOnClickListener(listener);
        btnUndo.setOnClickListener(listener);
        btnComplete.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.btn_cancel) {
                finish();
            } else if (i == R.id.btn_undo) {
                photoEditor.undo();
            } else if (i == R.id.btn_complete) {
                if (photoEditor.isCacheEmpty()) {
                    finish();
                } else {
                    saveImage();
                }
            }
        }
    };

    public void startInvoke() {
        mIntent = getIntent();
        if (mIntent != null) {
            type = mIntent.getIntExtra(TYPE, 0);
            if (type == TYPE_ALBUM) {
                localMedial = mIntent.getParcelableExtra(EXTRA_EDITOR_MEDIA);
                if (localMedial != null) {
                    url = localMedial.getPath();
                }
            } else {
                url = mIntent.getStringExtra(EXTRA_IMAGE_PATH);
            }
            if (!TextUtils.isEmpty(url)) {
                Glide.with(this).asBitmap().load(url).into(photoEditorView.getImageView());
            } else {
                finish();
            }
        }
    }


    private String initPath() {
        if (storagePath.equals("")) {
            storagePath = parentPath.getAbsolutePath() + File.separator + EDITOR_PHOTO_NAME;
            File file = new File(storagePath);
            if (!file.exists()) {
                file.mkdir();
            }
        }
        return storagePath;
    }

    public String saveEditorPhotoJpgPath() {
        return initPath() + File.separator + "editor_" + System.currentTimeMillis() + ".jpg";
    }


    @SuppressLint("MissingPermission")
    private void saveImage() {
        photoEditor.saveImage(savePath, new PhotoEditor.OnSaveListener() {
            @Override
            public void onStart() {
                showLoading("正在处理中");
            }

            @Override
            public void onSuccess(String imagePath) {
                hideLoading();
                mIntent = new Intent();
                if (type == TYPE_ALBUM) {
                    localMedial.setEditor(true);
                    localMedial.setEditorPath(imagePath);
                    mIntent.putExtra(RESULT_EDITOR_MEDIA, localMedial);
                } else {
                    mIntent.putExtra(RESULT_IMAGE_PATH, imagePath);
                }
                setResult(RESULTCODE, mIntent);
                finish();
            }

            @Override
            public void onFailure(Boolean success) {
                hideLoading();
                Toast.makeText(mContext, "失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    void showLoading(@NonNull String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
