package com.example.dialerapp.FontClasses;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class PopinsSemibold extends TextView {

    public PopinsSemibold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PopinsSemibold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PopinsSemibold(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),
                "Poppins-SemiBold.ttf");
        setTypeface(typeface, 1);

    }
}
