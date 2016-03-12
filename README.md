#Applivery Android SDK  
[![Build Status](https://travis-ci.org/applivery/applivery-android-sdk.svg?branch=master)](https://travis-ci.org/applivery/applivery-android-sdk) 
[![codecov.io](https://codecov.io/github/applivery/applivery-android-sdk/coverage.svg?branch=master)](https://codecov.io/github/applivery/applivery-android-sdk)  ![Language](https://img.shields.io/badge/Language-Java-orange.svg)  ![Version](https://img.shields.io/badge/version-1.0 RC1-blue.svg)

Framework to support [Applivery.com Mobile App distribution](http://www.applivery.com) for Android Apps.

## Overview

With Applivery you can massively distribute your Android Apps (both Ad-hoc or In-House/Enterprise) through a customizable distribution site with no need of your users have to be registered in the platform. No matter if your Android Apps are signed using Play Store or debug developer signature, Applivery is perfect not only for beta testing distribute to your QA team, but also for In-House Enterprise distribution for beta testing users, prior to a release, or even for corporative Apps to the employees of a company.

**Features:**
* **Automatic OTA Updates** when uploading new versions to Applivery.
* **Force update** if App version is lower than the minimum version configured in Applivery.

# Getting Started

First of all, you should create an account on [Applivery.com](https://dashboard.applivery.com/register) and then add a new Application.

### Get your credentials

**ACCOUNT API KEY**: that identifies and grants access to your account in order to use the [Applivery API](http://www.applivery.com/developers/api/). The API will aallow you to easily create an script to integrate your CI system with Applivery, but also is needed for this SDK.

You can get your ACCOUNT API KEY in the `Developers` section (left side menu).

![Developers section](https://github.com/applivery/applivery-android-sdk/blob/master/documentation/developers_section.png)

**APP ID**: Is your application identifier. You can find it in the App details, going to `Applications` -> Click desired App -> (i) Box

![APP ID](https://github.com/applivery/applivery-android-sdk/blob/master/documentation/application_id.png)

## SDK Installation

Sdk installation is quite simple, and you can follow one of this three methods, choose your favourite. Despite the following guide is oriented to be used with Android Studio/intelIJ feel free to integrate the SDK into your favourite IDE and send us a pull to request that we will include somewhere.

## Downloading source and use the project as a project module

Dowload the zip called [Zipped SDK](https://github.com/applivery/applivery-android-sdk/blob/master/downloads/applvsdklib.zip "applvsdklib.zip") from [downloads](https://github.com/applivery/applivery-android-sdk/blob/master/downloads "downloads")
 folder. 
 
 Unzip all the content at the same level than your "app" folder.  
 
 ![FILES](https://github.com/applivery/applivery-android-sdk/blob/master/documentation/project_files.png)
 
 Go to settings.gradle file: 
 
 ![Settings](https://github.com/applivery/applivery-android-sdk/blob/master/documentation/setting_gradle.png)
 
 And include your new module as follow:
 
 ```groovy
 include ':app', ':applvsdklib'
 ```
 Now include this line into your app folder buld.gradle dependencies 

 ```groovy
 compile project(":applvsdklib")
 ```

And that's all, applivery SDK is now ready to use in your project

## Downloading aar package as a lib

Dowload the zip aar called [aar SDK](https://github.com/applivery/applivery-android-sdk/blob/master/downloads/applvsdklib.aar "applvsdklib.aar") from [downloads](https://github.com/applivery/applivery-android-sdk/blob/master/downloads "downloads") folder. 
Now you can import a local aar file using the File --> New --> New Module -->Import .JAR/.AAR Package option in Android Studio and add this dependency to your app build.gradle in this way:

 ```groovy
  compile project(":applvsdklib")
 ```

And that's all, applivery SDK is now ready to use in your project

## Using gradle with nexus dependency

add the following dependency to your's app gradle:

 ```groovy
  compile ‘com.applivery.applivery-android-sdk:1.0-RC1'
 ```
### Ok! Let's go!

At your application start up, in a class extending from Application, you must call the init method:

 ```java
 public class AppliveryApplication extends Application{
 @Override public void onCreate() {
   super.onCreate();
   Applivery.init(this, BuildConfig.APP_KEY, BuildConfig.APP_SECRET, false);
 }
 }
 ```
 
**IMPORTANT I:** As you can suspect, you should replace the strings `APP_KEY` and `APP_SECRET` with you api key and your app id respectively. Easy! Don't you think so?, they look like this in my build.gradle:

 ```groovy
 buildConfigField "String", "APP_KEY", '"569bf0f4a81d1598273a2bd1"'
 buildConfigField "String", "APP_SECRET", '"f0e46ebc297e6e9a8fa427e15e42336d4d24a33b"'
 ```

## About params

- **app**: Your app instance.
- **apiKey**: Your developer's Api Key
- **appId**: Your application's ID
- **isPlayStoreRelease**: is the last param and the aim of this flag is to mark if the build will be submitted to Play Store. This is needed to prevent unwanted behavior like prompt to a final user that a new version is available on Applivery.com.
	* True: Applivery SDK will not trigger automatic updates anymore. **Use this for Play Store**
	* False: Applivery SDK will normally. Use this with builds distributed through Applivery. 

** NOTE :: Readme is under manteinace**

** NOTE :: Gradle dependency is not available on nexus repo yet **

License
=======

    Copyright 2015 Applivery

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
