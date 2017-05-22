package com.example.utmpapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class ChangeUserIDDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_iddialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangeUserIDDialogActivity.this);
        builder.setMessage("We strongly recommend you to change your User ID, to avoid theft of ID. Would you like to change the ID?")
                .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        ChangeUserIDDialogFragment dialogFragment = new ChangeUserIDDialogFragment();
                        dialogFragment.show(getFragmentManager(), "ChangeUserIDDialogFragment");
                    }
                })
                .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .create()
                .show();
    }
}
