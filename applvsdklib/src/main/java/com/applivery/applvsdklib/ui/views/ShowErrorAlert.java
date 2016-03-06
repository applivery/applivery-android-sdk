package com.applivery.applvsdklib.ui.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.R;
import com.applivery.applvsdklib.domain.model.ErrorObject;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 3/1/16.
 */
public class ShowErrorAlert {
  public void showError(ErrorObject error) {
    if (AppliverySdk.isContextAvailable()) {
      AlertDialog.Builder builder = new AlertDialog.Builder(AppliverySdk.getCurrentActivity());
      builder.setTitle(R.string.appliveryError)
          .setCancelable(true)
          .setMessage(error.getMessage())
          .setPositiveButton((R.string.appliveryAcceptButton),
              new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              })
          .show();
    } else {
      Toast.makeText(AppliverySdk.getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG)
          .show();
    }
  }
}
