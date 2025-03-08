![Applivery Logo](https://www.applivery.com/wp-content/uploads/2021/08/logo-dark-app.svg)

![Android CI](https://github.com/applivery/applivery-android-sdk/workflows/Android%20CI/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.applivery/applivery-sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.applivery/applivery-sdk/)
[![](https://jitpack.io/v/Applivery/applivery-android-sdk.svg)](https://jitpack.io/#Applivery/applivery-android-sdk)
[![Twitter](https://img.shields.io/badge/twitter-@Applivery-blue.svg?style=flat)](https://twitter.com/Applivery)

Framework to support [Applivery.com Mobile App distribution](http://www.applivery.com) for Android
Apps.

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
* [Advanced concepts](#advanced-concepts)

## Overview

With Applivery you can massively distribute your Android Apps (both Ad-hoc or In-House/Enterprise)
through a customizable distribution site with no need of your users have to be registered in the
platform. No matter if your Android Apps are signed using Play Store or debug developer signature,
Applivery is perfect not only for beta testing distribute to your QA team, but also for In-House
Enterprise distribution for beta testing users, prior to a release, or even for corporative Apps to
the employees of a company.

### Features

* **Automatic OTA Updates** when uploading new versions to Applivery.
* **Force update** if App version is lower than the minimum version configured in Applivery.
* **Send feedback**. Your test users can report a bug or send improvements feedback by simply taking
  a screenshot.

### Support

Applivery SDK is fully compatible with Android 7 (API level 24) and higher versions.

## Getting Started

First of all, you should create an account on [Applivery.io](https://dashboard.applivery.io/) and
then add a new Application.

### Get your credentials

**APP TOKEN**: that identifies and grants access to your app in order to use the SDK.

You can get your APP TOKEN in your App -> Settings -> Integrations section.

## SDK Installation

Applivery SDK is meant to be used in your non production builds, either debug only builds on any
other build type you use to build your testing builds.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.applivery/applivery-sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.applivery/applivery-sdk/)

add the following dependency to your app gradle:

 ```kotlin
// debugImplementation because Applivery SDK should only run in debug builds.
debugImplementation("com.applivery:applivery-sdk:${latestVersion}")
 ```

### No op artifact

Applivery SDK release builds exclusion may require complex source set configuration. To ease this
scenario we also publish a no-op artifact with the same public API as the SDK so you do not have
to make any changes in your code rather than including it as dependency in the compile
configurations
you do not want Applivery SDK to run.

```kotlin
releaseImplementation("com.applivery:applivery-sdk-no-op:${latestVersion}")
 ```

This way you can call Applivery SDK methods in your main source set having a no-op version in the
build types you need.

## SDK Setup

### Step 1

Call `Applivery.init()` method whenever you want to start Applivery SDK, typically in your
`Application.onCreate()` method:

 ```kotlin
import com.applivery.android.sdk.Applivery
import com.applivery.android.sdk.init
...
Applivery.init(APPLIVERY_TOKEN)
 ```

For private Applivery instances, tenants can be configured passing it as parameter in the
`Applivery.init()` method:

 ```kotlin
Applivery.init(APPLIVERY_TOKEN, TENANT)
 ```

#### Java users

Starting from version 4.0.0, everything is written in Kotlin, so Java users now have a `AppliveryInterop`
class to use the SDK.

### Step 2

Once initialized the SDK you have to call proactivelly the following method in order to check for
new updates:

```kotlin
import com.applivery.android.sdk.Applivery
import com.applivery.android.sdk.getInstance
...
Applivery.getInstance().checkForUpdates()
```

### Permissions

Applivery SDK uses some permissions in order for some features to work. As the SDK can not
disturb your users showing permissions prompts without any previous context, you are in charge of
requesting the permissions to your users.

- POST_NOTIFICATIONS: Used to show an ongoing notification when downloading updates
- READ_MEDIA_IMAGES: Used to observe for screenshots. Note that starting on Android 14
  there is a new READ_MEDIA_VISUAL_USER_SELECTED permission for users to grant partial access to
  some folders or files. Applivery SDK needs to have access to all files in order to observe for
  screenshots properly. You can follow the instructions on
  the [official documentation](https://developer.android.com/about/versions/14/changes/partial-photo-video-access)
  on how to request this kind of permissions properly

## Advanced concepts

### Updates

You will find that the following public methods are available:

Manually check for updates:

```kotlin
Applivery.getInstance().checkForUpdates()
```

Check for updates when coming from background

```kotlin
Applivery.getInstance().setCheckForUpdatesBackground(true)
```

Start the download of the last app version:

```kotlin
Applivery.getInstance().update()
```

### Notice for versions prior 3.5.2

Versions up to 3.5.1 contains a FileProvider needed to support in-app updates.
This FileProvider builds its authorities by using the host app package name plus ".fileprovider"
literal, giving us something like "com.foo.bar.fileprovider".
If you are using another FileProvider in your app, check that its authorities are not conflicting
with the SDK's ones, if so please change it to avoid issues.

Versions 3.5.2 and above do not have this limitation.

### Feedback Reporting

You can either take a screenshot or shake your phone if you want to send activate the Feedback
Reporting feature

You can enable or disable the screenshot feedback by using the following methods:

```kotlin
Applivery.getInstance().enableScreenshotFeedback()
Applivery.getInstance().disableScreenshotFeedback()
```

... and the shake event by using:

```kotlin
Applivery.getInstance().enableShakeFeedback()
Applivery.getInstance().disableShakeFeedback()
```

### Bind user

Programatically login a user in Applivery, for example if the app has a custom login and don't
want to use Applivery's authentication to track the user in the platform

```kotlin
Applivery.getInstance().bindUser(email, firstName, lastName, tags)
```

### Unbind user

Logout a previously binded user

```kotlin
Applivery.getInstance().unbindUser()
```

### Get user

This public method returns the user profile from Applivery API.
It will be required that the user has previously logged in or has been bound to the platform through
the `bindUser() method. In case it does not exist it will return an unauthorized error.

```kotlin
Applivery.getInstance().getUser(getUserCallback)
```

### Styling the UI

In order to customize the appearance of the UI, you can make a new resource file called
`applivery.xml` under your `res/values` folder overwriting default values for colors and strings.

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <!--Main colors-->
  <color name="applivery_primary_color">#FF0241E3</color>
  <color name="applivery_accent_color">#FF0241E3</color>
  <color name="applivery_foreground_color">#FF010258</color>

  <!--Update texts-->
  <string name="appliveryUpdateMsg">There is a new version available for download. Do you want to
    update to the latest version?
  </string>
  <string name="appliveryMustUpdateAppLocked">Sorry this App is outdated. Please, update the App
    to continue using it
  </string>

  <!--Login prompt texts-->
  <string name="appliveryLogin">Login</string>
  <string name="appliveryLoginRequiredText">Please log-in before using this app</string>

</resources>
```

*res/values/applivery.xml*

### Sample App

As a sample integration you can take a look
at [our sample app](https://github.com/applivery/applivery-android-sdk/tree/master/sample)
