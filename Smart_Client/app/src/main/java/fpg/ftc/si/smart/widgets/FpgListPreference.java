/*
 * Copyright (c) 2014.
 * This Project and its content is copyright of ftc
 * All rights reserved.
 */

package fpg.ftc.si.smart.widgets;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import fpg.ftc.si.smart.R;


/**
 * Created by MarlinJoe on 2014/3/20.
 */
public class FpgListPreference extends ListPreference {

    public FpgListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FpgListPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView titleView = (TextView) view.findViewById(android.R.id.title);
        titleView.setTextSize(getContext().getResources().getDimension(R.dimen.text_size_medium));
        TextView summary = (TextView) view.findViewById(android.R.id.summary);
        summary.setTextSize(getContext().getResources().getDimension(R.dimen.text_size_small));
    }
}
