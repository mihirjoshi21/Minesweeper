package com.conem.app.assignment2.screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;

import com.conem.app.assignment2.R;

public class DialogFragmentLicense extends DialogFragment {

    private Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = View.inflate(mActivity, R.layout.dialog_licenses, null);
        WebView webView = v.findViewById(R.id.web_license);
        webView.loadUrl("file:///android_asset/www/licenses.html");
        AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                .setView(v).create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return alertDialog;
    }

}