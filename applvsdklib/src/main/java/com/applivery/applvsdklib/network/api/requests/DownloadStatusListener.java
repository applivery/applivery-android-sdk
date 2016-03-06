package com.applivery.applvsdklib.network.api.requests;

import com.applivery.applvsdklib.domain.model.DownloadResult;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 9/1/16.
 */
public interface DownloadStatusListener {

  void updateDownloadPercentStatus(double percent);

  void downloadCompleted(DownloadResult downloadResult);

}
