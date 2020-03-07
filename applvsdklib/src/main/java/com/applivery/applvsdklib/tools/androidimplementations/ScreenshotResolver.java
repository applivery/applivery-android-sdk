package com.applivery.applvsdklib.tools.androidimplementations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.R;
import com.applivery.applvsdklib.domain.download.permissions.ReadScreenshotFolderPermission;
import com.applivery.applvsdklib.tools.permissions.PermissionChecker;
import com.applivery.applvsdklib.tools.permissions.UserPermissionRequestResponseListener;

/**
 * Created by Andres Hernandez on 5/11/16.
 */
public class ScreenshotResolver {

  private static final long DEFAULT_DETECT_WINDOW_SECONDS = 10;

  private final Context applicationContext;
  private final FileResolver fileResolver;
  private PermissionChecker permissionRequestExecutor;
  private final ReadScreenshotFolderPermission readScreenshotFolderPermission;
  private long currentTime = 0;

  public ScreenshotResolver(Context applicationContext, FileResolver fileResolver) {
    this.applicationContext = applicationContext;
    this.fileResolver = fileResolver;
    readScreenshotFolderPermission = new ReadScreenshotFolderPermission();
  }

  public void setupPermissionRequest() {
    permissionRequestExecutor = AppliverySdk.getPermissionRequestManager();
  }

  public void screenshotWasTaken() {
    currentTime = System.currentTimeMillis() / 1000;
  }

  public void resolvePathFrom(Uri uri) {
    if (permissionRequestExecutor == null) {
      AppliverySdk.Logger.log(
          "PermissionRequestExecutor must be initialized before reading external storage");
      return;
    }

    if (!permissionRequestExecutor.isGranted(readScreenshotFolderPermission)) {
      askForPermission(uri);
    } else {
      resolvePath(uri);
    }
  }

  private void askForPermission(final Uri uri) {
    permissionRequestExecutor.askForPermission(readScreenshotFolderPermission, new UserPermissionRequestResponseListener() {
      @Override public void onPermissionAllowed(boolean permissionAllowed) {
        if (permissionAllowed) {
          resolvePath(uri);
        }
      }
    }, AppliverySdk.getCurrentActivity());
  }

  private void resolvePath(Uri uri) {
    String path = null;

    if (!uri.toString().startsWith(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())) {
      return;
    }

    ScreenShotPathResolver pathResolver = ScreenShotInfoPathResolverFactory.get(applicationContext);
    ScreenShotPathInfo pathInfo = pathResolver.resolvePath(uri);

    if(pathInfo != null
            && matchPath(pathInfo.getPath())
            && matchTime(currentTime, pathInfo.getAddedAt())){
      path = pathInfo.getPath();
    }

    currentTime = 0;
    fileResolver.pathResolved(path);
  }

  public Bitmap resolveBitmapFrom(String path) {
    try {
      return BitmapFactory.decodeFile(path);
    } catch (Throwable e) {
      AppliverySdk.Logger.log(e.getMessage());
      return BitmapFactory.decodeResource(null, R.drawable.applivery_icon);
    }
  }

  private boolean matchPath(String path) {
    return path.toLowerCase().contains("screenshot") || path.contains("截屏") || path.contains("截图");
  }

  private boolean matchTime(long currentTime, long dateAdded) {
    return Math.abs(currentTime - dateAdded) <= DEFAULT_DETECT_WINDOW_SECONDS;
  }
}
