package com.applivery.applvsdklib.tools.permissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.Toast;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 15/1/16.
 */
public class PermissionsUIViews {

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  public static void showRationaleView(final RationaleResponse rationaleResponse, Context context,
      int rationaleTitleStringId, int rationaleMessageStringId) {
    new AlertDialog.Builder(context).setTitle(rationaleTitleStringId)
        .setMessage(rationaleMessageStringId)
        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            rationaleResponse.cancelPermissionRequest();
          }
        }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        rationaleResponse.continuePermissionRequest();
      }
    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
      @Override public void onDismiss(DialogInterface dialog) {
        rationaleResponse.cancelPermissionRequest();
      }
    }).show();
  }

  public static void showPermissionToast(Context context, int stringId){
    Toast.makeText(context, stringId, Toast.LENGTH_LONG).show();
  }

  public static ViewGroup getAppContainer(Activity activity) throws NullContainerException{
    if (activity!=null){
      return (ViewGroup) activity.findViewById(android.R.id.content);
    }else{
      throw new NullContainerException("Container is null");
    }
  }
}
