![Applivery Logo](https://www.applivery.com/wp-content/uploads/2021/08/logo-dark-app.svg)

![Android CI](https://github.com/applivery/applivery-android-sdk/workflows/Android%20CI/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.applivery/applivery-sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.applivery/applivery-sdk/)

Framework to support [Applivery.com Mobile App distribution](http://www.applivery.com) for Android
Apps.

### Table of Contents

* [Overview](#overview)
* [Getting Started](#getting-started)
* [SDK Installation](#sdk-installation)
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

Applivery SDK is designed for non-production builds (debug builds or other testing build variants).

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.applivery/applivery-sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.applivery/applivery-sdk/)

Add the following dependency to your app's `build.gradle` file:

 ```kotlin
// debugImplementation because Applivery SDK should only run in debug builds.
debugImplementation("com.applivery:applivery-sdk:${latestVersion}")
 ```

### No-op artifact

Applivery SDK exclusion in release builds may require complex source set configuration. To ease this
scenario we also publish a no-op artifact with the same public API as the SDK so you do not have
to make any changes in your code rather than including it as dependency in the compile
configurations you do not want Applivery SDK to run.

```kotlin
releaseImplementation("com.applivery:applivery-sdk-no-op:${latestVersion}")
 ```

This approach lets you call Applivery SDK methods throughout your codebase without affecting release
builds.

## SDK Setup

### Step 1: Initialize the SDK

Call `Applivery.start()` method whenever you want to start Applivery SDK, typically in your
`Application.onCreate()` method:

 ```kotlin
import com.applivery.android.sdk.Applivery
import com.applivery.android.sdk.start

...
Applivery.start(APPLIVERY_TOKEN)
 ```

For private Applivery instances, tenants can be configured passing it as parameter in the
`Applivery.start()` method:

 ```kotlin
import com.applivery.android.sdk.Applivery
import com.applivery.android.sdk.start

...
Applivery.start(APPLIVERY_TOKEN, TENANT)
 ```

#### SDK Configuration

An optional `Configuration` class can be passed in `Applivery.start()` method to configure
certain SDK behavior.

```kotlin
   val configuration = Configuration(
    postponeDurations = listOf(
        2.hours,
        30.minutes,
        5.minutes,
    ),
    enforceAuthentication = false
)
```

`postponeDurations`: List of postponing options to be shown in the dialog when an update is
available.
`enforceAuthentication`: If set to true and `Require authentication` option is enabled in the
Dashboard, users will be forced to login into Applivery before using the SDK. Set it to false 
(default) to allow users to cancel the login process.

#### Java Integration

For Java users (version 4.0.0+), use the `AppliveryInterop` class to access the SDK functionality.

### Step 2: Check for Updates

After initializing the SDK, check for available updates by calling:

```kotlin
import com.applivery.android.sdk.Applivery
import com.applivery.android.sdk.getInstance

...
Applivery.getInstance().checkForUpdates()
```

This will verify if there are newer versions of your app available on Applivery and prompt users to
update if needed.

#### Postpone updates

Everytime `checkForUpdates()` is called, a prompt dialog will show up to the user if there are
updates available. This dialog showing logic can be customized in order for users to allow
postponing the update passing your postponing options to the `Configuration` object in the SDK
initialization method.

**NOTE:** only up to three options are supported. If you pass more than three options, the first
ones will be used.

### Required Permissions

Applivery SDK requires certain permissions to function properly. You need to request these
permissions in your app:

- POST_NOTIFICATIONS: Used to display ongoing notifications during update downloads and screen
  recording.
- READ_MEDIA_IMAGES: Required to detect screenshots for the feedback feature.
- SYSTEM_ALERT_WINDOW: Required for screen recording feedback report.

Note: On Android 14+, users can grant partial access to media files. For screenshot detection to
work properly, Applivery needs full access. Follow
the [official documentation](https://developer.android.com/about/versions/14/changes/partial-photo-video-access)
for properly requesting these permissions.

## Advanced concepts

### Update Management

The SDK provides several methods to control the update experience:

Manually check for updates:

```kotlin
// Manually check for available updates
Applivery.getInstance().checkForUpdates()

// Enable automatic update checks when app returns from background
Applivery.getInstance().setCheckForUpdatesBackground(true)

// Trigger immediate download of the latest version
Applivery.getInstance().update()
```

### Feedback Reporting

Applivery offers two ways for users to submit feedback:

You can enable or disable the screenshot feedback by using the following methods:

```kotlin
Applivery.getInstance().enableScreenshotFeedback()
Applivery.getInstance().disableScreenshotFeedback()
```

and the feedback event by using:

```kotlin
Applivery.getInstance().feedbackEvent()
```

where behavior is one of the following:

- `Normal`: Opens feedback form without attachment
- `Record screen`: Allows you to record a video and then opens the feedback form with the recording attached

### User Management

Track users in the Applivery platform:

```kotlin
// Associate a user with the current app session
Applivery.getInstance().bindUser(email, firstName, lastName, tags)

// Remove user association
Applivery.getInstance().unbindUser()

// Retrieve the current user profile
Applivery.getInstance().getUser(getUserCallback)
```

### Styling the UI

Create a custom `applivery.xml` file in your `res/values folder to override default colors and
strings:

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
    <string name="appliveryLoginRequiredText">Please log-in to Applivery before using this app</string>

</resources>
```

*res/values/applivery.xml*

### Sample App

For a complete implementation example,
check [our sample app](https://github.com/applivery/applivery-android-sdk/tree/master/sample).

### Troubleshooting

If you encounter issues implementing the SDK:

- Verify you're using the latest SDK version
- Ensure your APP TOKEN is correct
- Check that permissions are properly requested in your app
- Review Android logcat for Applivery-related logs

### Support

For additional assistance:

- Visit Applivery Documentation
- Open an issue on GitHub
- Contact Applivery Support support@applivery.com