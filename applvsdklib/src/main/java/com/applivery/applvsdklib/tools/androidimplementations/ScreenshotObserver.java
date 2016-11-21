package com.applivery.applvsdklib.tools.androidimplementations;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.ui.model.ScreenCapture;

/**
 * Created by Andres Hernandez on 5/11/16.
 */
public class ScreenshotObserver implements FileResolver {

  private static ScreenshotObserver screenshotObserver;

  private boolean isEnabled;
  private Context applicationContext;
  private HandlerThread handlerThread;
  private Handler handler;
  private ContentObserver contentObserver;
  private ScreenshotResolver screenshotResolver;

  public static ScreenshotObserver getInstance(Context applicationContext) {
    if (screenshotObserver == null) {
      screenshotObserver = new ScreenshotObserver(applicationContext);
    }

    return screenshotObserver;
  }

  private ScreenshotObserver(Context applicationContext) {
    this.applicationContext = applicationContext;
    screenshotResolver = new ScreenshotResolver(applicationContext, this);
    setupObserver();
  }

  public void enableScreenshotObserver() {
    isEnabled = true;
    screenshotResolver.setupPermissionRequest();
  }

  public void disableScreenshotObserver() {
    isEnabled = false;
  }

  public void startObserving() {
    if (!isEnabled) {
      return;
    }

    applicationContext.getContentResolver()
        .registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true,
            contentObserver);
  }

  public void stopObserving() {
    if (!isEnabled) {
      return;
    }

    applicationContext.getContentResolver().unregisterContentObserver(contentObserver);
  }

  private void setupObserver() {
    handlerThread = new HandlerThread("content_observer");
    handlerThread.start();

    handler = new Handler(handlerThread.getLooper()) {
      @Override public void handleMessage(Message msg) {
        super.handleMessage(msg);
      }
    };

    contentObserver = new ContentObserver(handler) {
      @Override public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
      }

      @Override public void onChange(boolean selfChange) {
        super.onChange(selfChange);
      }

      @Override public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        screenshotResolver.screenshotWasTaken();
        screenshotResolver.resolvePathFrom(uri);
      }
    };
  }

  @Override public void pathResolved(String resolvedPath) {
    Bitmap screenshot = screenshotResolver.resolveBitmapFrom(resolvedPath);
    if (screenshot == null) {
      AppliverySdk.Logger.log("Resolved bitmap is null.");
    }

    ScreenCapture screenCapture = new ScreenCapture(screenshot);
    AppliverySdk.requestForUserFeedBackWith(screenCapture);
  }
}
