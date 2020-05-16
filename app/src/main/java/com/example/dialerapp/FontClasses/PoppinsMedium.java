package com.example.dialerapp.FontClasses;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class PoppinsMedium extends TextView {

    public PoppinsMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PoppinsMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PoppinsMedium(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),
                "Poppins-Medium.ttf");
        setTypeface(typeface, 1);

    }
}