package com.applivery.applvsdklib.domain.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 7/11/15.
 */
public class AppConfig implements BusinessObject<AppConfig>{

  private String Id;
  private String name;
  private Sdk sdk;
  private int totalDownloads;
  private int buildsCount;
  private Date modified;
  private Date created;
  private List<String> so = new ArrayList<String>();
  private String description;
  private int sitesCount;
  private int crashesCount;
  private int feedBackCount;

  public String getId() {
    return Id;
  }

  public void setId(String id) {
    Id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Sdk getSdk() {
    return sdk;
  }

  public void setSdk(Sdk sdk) {
    this.sdk = sdk;
  }

  public int getTotalDownloads() {
    return totalDownloads;
  }

  public void setTotalDownloads(int totalDownloads) {
    this.totalDownloads = totalDownloads;
  }

  public int getBuildsCount() {
    return buildsCount;
  }

  public void setBuildsCount(int buildsCount) {
    this.buildsCount = buildsCount;
  }

  public Date getModified() {
    return modified;
  }

  public void setModified(Date modified) {
    this.modified = modified;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public List<String> getSo() {
    return so;
  }

  public void setSo(List<String> so) {
    this.so = so;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getSitesCount() {
    return sitesCount;
  }

  public void setSitesCount(int sitesCount) {
    this.sitesCount = sitesCount;
  }

  public int getCrashesCount() {
    return crashesCount;
  }

  public void setCrashesCount(int crashesCount) {
    this.crashesCount = crashesCount;
  }

  public int getFeedBackCount() {
    return feedBackCount;
  }

  public void setFeedBackCount(int feedBackCount) {
    this.feedBackCount = feedBackCount;
  }

  @Override public AppConfig getObject() {
    return this;
  }
}
