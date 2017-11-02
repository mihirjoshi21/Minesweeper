package com.conem.app.assignment2.screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.conem.app.assignment2.R;


public class DialogFragmentAbout extends DialogFragment {

    private Activity mActivity;
    public static final String VERSION = "1.0.0";
    public static final int YEAR = 2017;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = View.inflate(mActivity, R.layout.dialog_about, null);
        ((TextView) v.findViewById(R.id.textview_title)).setText(getString(R.string.title_about,
                VERSION, YEAR));
        return new AlertDialog.Builder(mActivity)
                .setView(v)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name)).create();
    }

}
