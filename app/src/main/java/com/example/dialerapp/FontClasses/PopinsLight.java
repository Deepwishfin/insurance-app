package com.example.dialerapp.FontClasses;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class PopinsLight extends TextView {

    public PopinsLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PopinsLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PopinsLight(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),
                "Roboto-Light.ttf");
        setTypeface(typeface, 1);

    }
}