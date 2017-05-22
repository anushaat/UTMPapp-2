package com.example.utmpapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.utmpapp.RegisterActivity.LOGIN_PREF;
import static com.example.utmpapp.RegisterActivity.USERID_PREF;

public class ChangeUserIDDialogFragment extends DialogFragment {


  //  protected ChangeUserIDDialogActivity mActivity;

/*    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ChangeUserIDDialogActivity) activity;
    }*/

    SharedPreferences sharedPreferences;
    private static String CREDENTIALS = "Credentials";


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_change_userid, null);
        final EditText mNewUserIDView = (EditText) view.findViewById(R.id.new_userid);
        //String final mNewUserID = mNewUserIDView.getText().toString();

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                // Set Dialog Icon
                .setIcon(R.mipmap.edit)
                // Set Dialog Title
                .setTitle("Update UserID")
                // Positive button
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(mNewUserIDView.getText().toString()) && isUserIDValid(mNewUserIDView.getText().toString())) {
                            sharedPreferences = getActivity().getSharedPreferences(CREDENTIALS, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(USERID_PREF, mNewUserIDView.getText().toString());
                            editor.putBoolean(LOGIN_PREF,false);
                            Log.v("Entered String", mNewUserIDView.getText().toString());
                            editor.apply();
                            Intent intent = new Intent(getActivity(),LoginActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            showValidationErrorToast();
                        }
                    }
                })

                // Negative Button
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,	int which) {
                        // Do something
                        dismiss();
                    }
                }).create();
    }

    private boolean isUserIDValid(String newUserID)
    {
        return newUserID.length() > 0 && newUserID.length() <= 10;
    }
    private void showValidationErrorToast() {
        Toast.makeText(getActivity(), getActivity().getString(R.string.Toast_Validation), Toast.LENGTH_SHORT).show();
    }
}
