package com.applivery.applvsdklib.ui.views.update;

import com.applivery.applvsdklib.AppliverySdk;
import com.applivery.applvsdklib.ui.model.UpdateInfo;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 3/1/16.
 */
public class UpdateViewPresenter {

  private UpdateView updateView;
  private final UpdateListener updateListener;

  public UpdateViewPresenter(UpdateListener updateListener) {
    this.updateListener = updateListener;
  }
  public void showForcedUpdate(String appName, String updateMessage) {
    if (AppliverySdk.isContextAvailable()){
      UpdateInfo updateInfo = new UpdateInfo(appName, updateMessage);
      this.updateView = new MustUpdateViewImpl(updateInfo, updateListener);
      this.updateListener.setUpdateView(updateView);
      this.updateView.showUpdateDialog();
    }
  }

  public void showSuggestedUpdate(String appName, String updateMessage) {
    if (AppliverySdk.isContextAvailable()) {
      UpdateInfo updateInfo = new UpdateInfo(appName, updateMessage);
      this.updateView = new SuggestedUpdateViewImpl(updateInfo, updateListener);
      this.updateListener.setUpdateView(updateView);
      this.updateView.showUpdateDialog();
    }
  }

}
