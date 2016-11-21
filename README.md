#Applivery Android SDK  
![Language](https://img.shields.io/badge/Language-Java-orange.svg)
![Version](https://img.shields.io/badge/version-2.0-blue.svg)
[![Build Status](https://travis-ci.org/applivery/applivery-android-sdk.svg?branch=develop)](https://travis-ci.org/applivery/applivery-android-sdk) 
[![codecov.io](https://codecov.io/github/applivery/applivery-android-sdk/coverage.svg?branch=develop)](https://codecov.io/github/applivery/applivery-android-sdk) ![](https://img.shields.io/badge/Min%20SDK-14-green.svg)

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

And that's all, applivery SDK is now ready to use in your project. But don't forget that using this method you have to import the following libraries that applivery sdk is using:

```groovy
  compile 'com.android.support:appcompat-v7:23.1.1'
  compile 'com.squareup.retrofit2:retrofit:2.0.0'
  compile 'com.squareup.retrofit2:converter-gson:2.0.0'
  compile 'com.squareup.okhttp3:logging-interceptor:3.2.0'
  compile 'com.google.code.gson:gson:2.6.2'
  compile 'com.karumi:dexter:2.2.2'
```

## Using gradle with jitpack Maven dependency
[![](https://jitpack.io/v/Applivery/applivery-android-sdk.svg)](https://jitpack.io/#Applivery/applivery-android-sdk)

Add the following repository to your's root gradle:
 
 ```groovy
   allprojects {
     repositories {
       ...
       maven { url "https://jitpack.io" }
     }
   }
 ````

Add the following dependency to your's app gradle:

  ```groovy
    dependencies {
      compile 'com.github.Applivery:applivery-android-sdk:v2.0'
    }
  ```
  * Note that Jitpack will be used for **Release Candidate** versions and Nexus for final releases, so be concerned about possible bugs in Jitpack versions

## Using gradle with nexus dependency

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.applivery/applivery-android-sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.applivery/applivery-android-sdk)

add the following dependency to your's app gradle:

 ```groovy
  compile 'com.applivery:applivery-android-sdk:1.1.0'
 ```
### Ok! Let's go!

At your application start up, in a class extending from Application, you must call the init method:

 ```java
 public class AppliveryApplication extends Application{
 @Override public void onCreate() {
   super.onCreate();
   Applivery.init(this, BuildConfig.APP_ID, BuildConfig.ACCOUNT_API_KEY, false);
   Applivery.setUpdateCheckingInterval(21600);
 }
 }
 ```
 
 There we have two Applivery Methods, let's start with `Applivery.init(this, BuildConfig.APP_KEY, BuildConfig.APP_SECRET, false);`. This is the only call you have to make in order to have Applivery sdk integrated, the only thing you have to be worried about is that this call **MUST** be performed in app's `onCreate` Method.
 
**IMPORTANT I:** As you can suspect, you should replace the strings `APP_KEY` and `APP_SECRET` with you api key and your app id respectively. Easy! Don't you think so?, they look like this in my build.gradle:

 ```groovy
 buildConfigField "String", "APP_ID", '"XXXXXXXXXXXXXXXXXXXXXXXX"'
 buildConfigField "String", "ACCOUNT_API_KEY", '"YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY"'
 ```

## About params

- **app**: Your app instance.
- **applicationId**: Your application's ID
- **appClientToken**: Your developer's Api Key
- **isPlayStoreRelease**: is the last param and the aim of this flag is to mark if the build will be submitted to Play Store. This is needed to prevent unwanted behavior like prompt to a final user that a new version is available on Applivery.com.
	* True: Applivery SDK will not trigger automatic updates anymore. **Use this for Play Store**
	* False: Applivery SDK will normally. Use this with builds distributed through Applivery. 

The second call `Applivery.setUpdateCheckingInterval(21600);` indicates Applivery Sdk that the checking for new versions will be executed after 6 hours (21600 seconds) when the app will came back from background mode. Anyway if app is destroyed and app `init` method is called again the checking for new versions will be executed again.

### Feedback

You can either take a screenshot or shake your phone if you want to send Applivery some feedback about your App. By taking a screenshot or shaking your phone you will get a screen like the following:

![FILES](https://github.com/applivery/applivery-android-sdk/blob/master/documentation/new_feedback.png)

Here you can add the screenshot of the screen you were on if you woud like. You can say something interesting about the screen and type your feedback as a bug or simply something you want to give as feedback. By pressing the small screenshot you will have the same image bigger just in case you would like to check something. Then if you tap on the top right button you will send the info, or you could press the close button in the upper left corner otherwise.

Additionally, if you go into the screenshot detail you can actually freehand draw over it.

You can enable or disable the screenshot feedback by using the following methods:

```java
Applivery.enableScreenshotFeedback();
Applivery.disableScreenshotFeedback();
```

... and the shake feedback by using:

```java
Applivery.enableShakeFeedback();
Applivery.disableShakeFeedback();
```

Call it having your app in foreground whenever you want.

## Sample

As an example of integration you can have a look at: [our sample app](https://github.com/applivery/applivery-android-sample-app)

# Acknowledgements

We would like to mention every open source lib authors:

* Thank's to [Square](http://square.github.io/), we are using several libs they developed (Retrofit 2, OkHttp).
* Thank's to Google, and Android Dev team, obviously, Android SDK, Support Libs ...
* Thank's to [Karumi](http://www.karumi.com/) for his great contributions to developers community in general. We are using [Dexter](https://github.com/Karumi/Dexter) from Karumi as well.

License
=======

    Copyright (C) 2016 Applivery

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
