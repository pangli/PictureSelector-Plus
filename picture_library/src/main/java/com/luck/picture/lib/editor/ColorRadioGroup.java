package com.luck.picture.lib.editor;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RadioGroup;

/**
 * Created by pangli on 2018/4/10 11:14
 * 备注：   颜色RadioGroup
 */

public class ColorRadioGroup extends RadioGroup {

    public ColorRadioGroup(Context context) {
        super(context);
    }

    public ColorRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getCheckColor() {
        int checkedId = getCheckedRadioButtonId();
        ColorRadioButton radio = findViewById(checkedId);
        if (radio != null) {
            return radio.getColor();
        }
        return Color.WHITE;
    }

    public void setCheckColor(int color) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            ColorRadioButton radio = (ColorRadioButton) getChildAt(i);
            if (radio.getColor() == color) {
                radio.setChecked(true);
                break;
            }
        }
    }
}
