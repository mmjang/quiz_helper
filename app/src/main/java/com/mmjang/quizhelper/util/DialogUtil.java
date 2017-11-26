package com.mmjang.quizhelper.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.mmjang.quizhelper.MyApplication;
import com.mmjang.quizhelper.R;

/**
 * Created by Gao on 2017/6/27.
 */

public class DialogUtil {

    public static void showStartAnkiDialog(Context activityContext) {
        new AlertDialog.Builder(activityContext)
                .setMessage(activityContext.getString(R.string.plan_anki_not_started))
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                MyApplication.getAnkiDroid().startAnkiDroid();
                            }
                        })
                .show();
    }
}
