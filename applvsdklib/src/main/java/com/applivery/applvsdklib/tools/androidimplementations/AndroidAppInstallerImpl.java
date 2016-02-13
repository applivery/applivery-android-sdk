package com.applivery.applvsdklib.tools.androidimplementations;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.applivery.applvsdklib.domain.download.app.AppInstaller;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/1/16.
 */
public class AndroidAppInstallerImpl implements AppInstaller {

  private final Context context;

  public AndroidAppInstallerImpl(Context context) {
    this.context = context;
  }

  @Override public void installApp(String path) {
    Intent promptInstall = new Intent(Intent.ACTION_VIEW)
        .setDataAndType(Uri.parse("file:///" + path), "application/vnd.android.package-archive");
    promptInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(promptInstall);
  }
}
