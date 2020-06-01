package com.example.dialerapp.FontClasses;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class PoppinsRegular extends TextView {

    public PoppinsRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PoppinsRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PoppinsRegular(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),
                "Roboto-Regular.ttf");
        setTypeface(typeface, 1);

    }
}