
![Applivery Logo](https://www.applivery.com/wp-content/uploads/2019/06/applivery-og.png)

![Android CI](https://github.com/applivery/applivery-android-sdk/workflows/Android%20CI/badge.svg)
[![Download](https://api.bintray.com/packages/applivery/maven/applivery-android-sdk/images/download.svg)](https://bintray.com/applivery/maven/applivery-android-sdk/_latestVersion)
[![Twitter](https://img.shields.io/badge/twitter-@Applivery-blue.svg?style=flat)](https://twitter.com/Applivery)

### Quality checks 
[![codecov.io](https://codecov.io/github/applivery/applivery-android-sdk/coverage.svg?branch=develop)](https://codecov.io/github/applivery/applivery-android-sdk)

Framework to support [Applivery.com Mobile App distribution](http://www.applivery.com) for Android Apps.

### Table of Contents

* [Overview](#overview)
* [Getting Started](#getting-started)
* [SDK Installation](#sdk-installation)
	* [Gradle with jCenter dependency](#gradle-with-jcenter-dependency)
	* [Gradle with JitPack Maven dependency](#gradle-with-jitpack-maven-dependency)
	* [Gradle with Nexus/MavenCentral dependency](#gradle-with-nexusmavencentral-dependency)
* [SDK Setup](#sdk-setup)
	* [Step 1](#step-1)
	* [Step 2](#step-2)
	* [About params](#about-params)
* [Advanced concepts](#advanced-concepts)


## Overview

With Applivery you can massively distribute your Android Apps (both Ad-hoc or In-House/Enterprise) through a customizable distribution site with no need of your users have to be registered in the platform. No matter if your Android Apps are signed using Play Store or debug developer signature, Applivery is perfect not only for beta testing distribute to your QA team, but also for In-House Enterprise distribution for beta testing users, prior to a release, or even for corporative Apps to the employees of a company.

### Features
* **Automatic OTA Updates** when uploading new versions to Applivery.
* **Force update** if App version is lower than the minimum version configured in Applivery.
* **Send feedback**. Your test users can report a bug or send improvements feedback by simply taking a screenshot.

### Support
Applivery SDK is fully compatible with Android v5.0 (API 21) and higher versions. However it will build properly from Android 4.1 (API 16) but the with limited functionality.

## Getting Started

First of all, you should create an account on [Applivery.io](https://dashboard.applivery.io/) and then add a new Application.

### Get your credentials

**APP TOKEN**: that identifies and grants access to your app in order to use the SDK.

You can get your APP TOKEN in your App -> Settings -> Integrations section.

## SDK Installation

### Gradle with jCenter dependency
[ ![Download](https://api.bintray.com/packages/applivery/maven/applivery-android-sdk/images/download.svg) ](https://bintray.com/applivery/maven/applivery-android-sdk/_latestVersion)
```groovy
implementation 'com.applivery:applivery-sdk:3.2.2'
```

### Gradle with JitPack Maven dependency
[![](https://jitpack.io/v/Applivery/applivery-android-sdk.svg)](https://jitpack.io/#Applivery/applivery-android-sdk)

Add the following repository to your's root gradle:
 
 ```groovy
   allprojects {
     repositories {
       maven { url "https://jitpack.io" }
     }
   }
 ````

Add the following dependency to your app gradle:

  ```groovy
    dependencies {
      implementation 'com.github.Applivery:applivery-android-sdk:v3.2.2'
    }
  ```
  
### Gradle with Nexus/MavenCentral dependency

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.applivery/applivery-sdk/badge.svg)](https://search.maven.org/#search%7Cga%7C1%7Capplivery)

add the following dependency to your app gradle:

 ```groovy
  compile 'com.applivery:applivery-android-sdk:3.2.2'
 ```

## SDK Setup

### Step 1
At your application startup, in a class extending from `Application`, you must call the `Applivery.init()` method:

 ```kotlin
class AppliveryApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.BUILD_TYPE != "release") {
            Applivery.init(this, BuildConfig.APPLIVERY_APP_TOKEN)
        }
    }
}
 ```

This method is intended to initialize the Applivery SDK. The only thing you have to take care about is that this call **MUST** be performed in App's `onCreate()` Method.
 
**IMPORTANT: Don't init Applivery on `release` builds** 
 
### Step 2
Once initialized the SDK and **once your App is stable in the Home Screen** you have to call proactivelly the following method in order to check for new updates:
```kotlin
Applivery.checkForUpdates()
```

### About params

- **app**: Your app instance.
- **appToken**: Your app token from applivery dashboard

## Advanced concepts

### Updates
You will find that the following public methods are available:

Manually check for updates:
```java
Applivery.checkForUpdates()
```

Check for updates when coming from background 
```java
Applivery.setCheckForUpdatesBackground(true)
```

### Feedback Reporting

You can either take a screenshot or shake your phone if you want to send activate the Feedback Reproting feature

You can enable or disable the screenshot feedback by using the following methods:

```java
Applivery.enableScreenshotFeedback();
Applivery.disableScreenshotFeedback();
```
... and the shake event by using:

```java
Applivery.enableShakeFeedback();
Applivery.disableShakeFeedback();
```

### Bind user

Programatically login a user in Applivery, for example if the app has a custom login and don't 
want to use Applivery's authentication to track the user in the platform

```java
Applivery.bindUser(@NonNull String email, @Nullable String firstName,
                @Nullable String lastName, @Nullable Collection<String> tags);
```

### Unbind user

Logout a previously binded user

```java
Applivery.unbindUser();
```

### Styling the UI

In order to customize the appearance of the UI, you can make a new resource file called `applivery.xml` under your `res/values` folder overwriting the Applivery default attributes.

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <color name="applivery_primary_color">#ffbb33</color>
  <color name="applivery_secondary_color">#ffffff</color>
  <color name="applivery_primary_font_color">#ffffff</color>
  <color name="applivery_secondary_font_color">#444444</color>
  
  <color name="applivery_drawing_color">#ffbb33</color>

  <string name="appliveryUpdate">Update!</string>
  <string name="appliveryUpdateMsg">There is a new version available for downloadInfo! Do you want to update to the latest version?</string>
  <string name="appliveryMustUpdateAppLocked">You must update.</string>
  
  <string name="appliveryLoginFailDielogTitle">Invalid credentials</string>
  <string name="appliveryLoginFailDielogText">The email or password you entered is not valid</string>
</resources>
```
*res/values/applivery.xml*

You can also override the following drawable resources:
* Feedback tab indicator (should be provided as a 9patch png):<br />
*applivery_selected_tab_button*
* Feedback close button:<br />
*applivery_close*<br />
*applivery_close_pressed*
* Feedback done button:<br />
*applivery_done*<br />
*applivery_done_pressed*
* Feedback send button:<br />
*applivery_send*<br />
*applivery_send_pressed*

### Sample App

As a sample integration you can take a look at [our sample app](https://github.com/applivery/applivery-android-sdk/tree/master/sample)

# Acknowledgements

We would like to mention every open source lib authors:

* Thank's to [Square](http://square.github.io/), we are using several libs they developed (Retrofit 2, OkHttp).
* Thank's to Google, and Android Dev team, obviously, Android SDK, Support Libs ...
* Thank's to [Karumi](http://www.karumi.com/) for his great contributions to developers community in general. We are using [Dexter](https://github.com/Karumi/Dexter) from Karumi as well.

# License
Copyright (C) 2020 Applivery

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
