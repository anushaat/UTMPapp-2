package com.example.utmpapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddGeofenceFragment extends DialogFragment {

  // region Properties

  private ViewHolder viewHolder;
  private String[] storeLatitude = {
          " ",
          "37.955840",
          "37.955418",
          "37.954243",
          "37.955526",
          "37.954409"
  };
  private String[] storeLongitude = {
          " ",
          "-91.774114",
          "-91.773476",
          "-91.774241",
          "-91.771959",
          "-91.769522"
  };
  private String geoFenceRadius = "0.05";

  private ViewHolder getViewHolder() {
    return viewHolder;
  }

  AddGeofenceFragmentListener listener;
  public void setListener(AddGeofenceFragmentListener listener) {
    this.listener = listener;
  }

  // endregion

  // region Overrides

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    LayoutInflater inflater = getActivity().getLayoutInflater();
    final View view = inflater.inflate(R.layout.dialog_add_geofence, null);

    viewHolder = new ViewHolder();
    viewHolder.populate(view);

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setView(view)
            .setPositiveButton(R.string.Add, null)
            .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                AddGeofenceFragment.this.getDialog().cancel();

                if (listener != null) {
                  listener.onDialogNegativeClick(AddGeofenceFragment.this);
                }
              }
            });

    final AlertDialog dialog = builder.create();

    // Create an ArrayAdapter using the string array and a default spinner layout
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
            R.array.stores_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
    viewHolder.spinner.setAdapter(adapter);

    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
      @Override
      public void onShow(DialogInterface dialogInterface) {
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        viewHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

          public void onItemSelected(AdapterView<?> parentView,
                                     View selectedItemView, int position, long id) {

            int pos = viewHolder.spinner.getSelectedItemPosition();
            //Toast.makeText(getActivity(),"latitude is " + storeLatitude[pos],Toast.LENGTH_LONG).show();
            if (pos > 0) {
              viewHolder.latitudeEditText.setText(storeLatitude[pos]);
              viewHolder.longitudeEditText.setText(storeLongitude[pos]);
              viewHolder.radiusEditText.setText(geoFenceRadius);
            }
            else {
              viewHolder.latitudeEditText.setText("");
              viewHolder.longitudeEditText.setText("");
              viewHolder.radiusEditText.setText("");
            }
          }

          public void onNothingSelected(AdapterView<?> arg0) {// do nothing
          }

        });

        positiveButton.setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View view) {
            if (dataIsValid()) {
              NamedGeofence geofence = new NamedGeofence();
              geofence.name = getViewHolder().spinner.getSelectedItem().toString();
              geofence.latitude = Double.parseDouble(getViewHolder().latitudeEditText.getText().toString());
              geofence.longitude = Double.parseDouble(getViewHolder().longitudeEditText.getText().toString());
              geofence.radius = Float.parseFloat(getViewHolder().radiusEditText.getText().toString()) * 1000.0f;

              if (listener != null) {
                listener.onDialogPositiveClick(AddGeofenceFragment.this, geofence);
                dialog.dismiss();
              }
            } else {
              showValidationErrorToast();
            }
          }

        });

      }
    });

    return dialog;
  }

  // endregion

  // region Private

  private boolean dataIsValid() {
    boolean validData = true;

    //String name = getViewHolder().nameEditText.getText().toString();
    String latitudeString = getViewHolder().latitudeEditText.getText().toString();
    String longitudeString = getViewHolder().longitudeEditText.getText().toString();
    String radiusString = getViewHolder().radiusEditText.getText().toString();

    if (TextUtils.isEmpty(latitudeString)
            || TextUtils.isEmpty(longitudeString) || TextUtils.isEmpty(radiusString)) {
      validData = false;
    } else {
      double latitude = Double.parseDouble(latitudeString);
      double longitude = Double.parseDouble(longitudeString);
      float radius = Float.parseFloat(radiusString);
      if ((latitude < Constants.Geometry.MinLatitude || latitude > Constants.Geometry.MaxLatitude)
              || (longitude < Constants.Geometry.MinLongitude || longitude > Constants.Geometry.MaxLongitude)
              || (radius < Constants.Geometry.MinRadius || radius > Constants.Geometry.MaxRadius)) {
        validData = false;
      }
    }

    return validData;
  }

  private void showValidationErrorToast() {
    Toast.makeText(getActivity(), getActivity().getString(R.string.Toast_Validation), Toast.LENGTH_SHORT).show();
  }

  // endregion

  // region Interfaces

  public interface AddGeofenceFragmentListener {
    void onDialogPositiveClick(DialogFragment dialog, NamedGeofence geofence);
    void onDialogNegativeClick(DialogFragment dialog);
  }

  // endregion

  // region Inner classes

  static class ViewHolder {
    Spinner spinner;
    EditText latitudeEditText;
    EditText longitudeEditText;
    EditText radiusEditText;

    public void populate(View v) {
      latitudeEditText = (EditText) v.findViewById(R.id.fragment_add_geofence_latitude);
      longitudeEditText = (EditText) v.findViewById(R.id.fragment_add_geofence_longitude);
      radiusEditText = (EditText) v.findViewById(R.id.fragment_add_geofence_radius);

      latitudeEditText.setHint(String.format(v.getResources().getString(R.string.Hint_Latitude), Constants.Geometry.MinLatitude, Constants.Geometry.MaxLatitude));
      longitudeEditText.setHint(String.format(v.getResources().getString(R.string.Hint_Longitude), Constants.Geometry.MinLongitude, Constants.Geometry.MaxLongitude));
      radiusEditText.setHint(String.format(v.getResources().getString(R.string.Hint_Radius), Constants.Geometry.MinRadius, Constants.Geometry.MaxRadius));

      latitudeEditText.setEnabled(false);
      longitudeEditText.setEnabled(false);
      radiusEditText.setEnabled(false);

      spinner = (Spinner) v.findViewById(R.id.fragment_store_name);

    }
  }

  // endregion
}