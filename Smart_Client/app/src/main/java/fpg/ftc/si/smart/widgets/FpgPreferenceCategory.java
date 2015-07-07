/*
 * Copyright (c) 2014.
 * This Project and its content is copyright of ftc
 * All rights reserved.
 */

package fpg.ftc.si.smart.widgets;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import fpg.ftc.si.smart.R;


/**
 * Created by MarlinJoe on 2014/3/20.
 */
public class FpgPreferenceCategory extends PreferenceCategory {
    public FpgPreferenceCategory(Context context) {
        super(context);
    }

    public FpgPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FpgPreferenceCategory(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView titleView = (TextView) view.findViewById(android.R.id.title);
        //titleView.setTextSize(getContext().getResources().getDimension(R.dimen.text_size_medium));
    }
}
