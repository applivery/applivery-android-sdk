package com.applivery.applvsdklib.domain.download.app;

import com.applivery.applvsdklib.domain.InteractorCallback;
import com.applivery.applvsdklib.domain.model.DownloadResult;
import com.applivery.applvsdklib.domain.model.ErrorObject;
import com.applivery.applvsdklib.ui.views.ShowErrorAlert;
import com.applivery.applvsdklib.ui.views.update.UpdateView;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 10/1/16.
 */
public class DownloadBuildInteractorCallback implements InteractorCallback<DownloadResult> {

  private final UpdateView updateView;

  public DownloadBuildInteractorCallback(UpdateView updateView) {
    this.updateView = updateView;
  }

  public void updateDownloadProgress(double percent){
    this.updateView.updateProgress(percent);
  }

  @Override public void onSuccess(DownloadResult businessObject) {
    updateView.hideDownloadInProgress();
  }

  @Override public void onError(ErrorObject error) {
    ShowErrorAlert showErrorAlert = new ShowErrorAlert();
    showErrorAlert.showError(error);
  }
}
