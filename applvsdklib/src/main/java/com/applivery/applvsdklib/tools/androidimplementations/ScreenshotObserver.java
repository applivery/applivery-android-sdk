package com.applivery.applvsdklib.tools.androidimplementations;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;

/**
 * Created by Andres Hernandez on 5/11/16.
 */
public class ScreenshotObserver {

  private static ScreenshotObserver screenshotObserver;

  private Context applicationContext;
  private HandlerThread handlerThread;
  private Handler handler;
  private ContentObserver contentObserver;

  public static ScreenshotObserver getInstance(Context applicationContext) {
    if (screenshotObserver == null) {
      screenshotObserver = new ScreenshotObserver(applicationContext);
    }

    return screenshotObserver;
  }

  private ScreenshotObserver(Context applicationContext) {
    this.applicationContext = applicationContext;
    setupObserver();
  }

  public void startObserving() {
    applicationContext.getContentResolver()
        .registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true,
            contentObserver);
  }

  public void stopObserving() {
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
      }
    };
  }
}
