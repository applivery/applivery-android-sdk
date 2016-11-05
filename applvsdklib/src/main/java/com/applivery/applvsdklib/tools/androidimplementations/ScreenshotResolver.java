package com.applivery.applvsdklib.tools.androidimplementations;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.R;

/**
 * Created by Andres Hernandez on 5/11/16.
 */
public class ScreenshotResolver {

  private static final String[] FIELDS = new String[] {
      MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA,
      MediaStore.Images.Media.DATE_ADDED
  };
  private static final String SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";
  private static final long DEFAULT_DETECT_WINDOW_SECONDS = 10;

  private Context applicationContext;

  public ScreenshotResolver(Context applicationContext) {
    this.applicationContext = applicationContext;
  }

  public String resolvePathFrom(Uri uri) {
    String path = "";

    if (!uri.toString().startsWith(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())) {
      return path;
    }

    Cursor cursor = null;
    cursor = applicationContext.getContentResolver().query(uri, FIELDS, null, null, SORT_ORDER);

    if (cursor == null || !cursor.moveToFirst()) {
      return path;
    }

    String tempPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
    long dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
    long currentTime = System.currentTimeMillis();

    if (matchPath(tempPath) && matchTime(currentTime, dateAdded)) {
      path = tempPath;
    }

    cursor.close();

    return path;
  }

  public Bitmap resolveBitmapFrom(String path) {
    try {
      Bitmap bitmap = BitmapFactory.decodeFile(path);
      return bitmap;
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
