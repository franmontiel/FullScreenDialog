FullScreenDialog
=================
A DialogFragment that implements the Full-screen dialog pattern defined in [the Material Design guidelines](https://material.io/guidelines/components/dialogs.html#dialogs-full-screen-dialogs).

![FullScreenDialog Sample GIF](https://github.com/franmontiel/FullScreenDialog/raw/master/fullscreendialog.gif)

Download
--------
Step 1. Add the JitPack repository in your root build.gradle at the end of repositories:
```groovy
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
Step 2. Add the dependency
```groovy
dependencies {
        compile 'com.github.franmontiel:FullScreenDialog:1.0.1'
}
```
Usage
-----
Just create a new `FullScreenDialogFragment` using the `Builder`
```java
new FullScreenDialogFragment.Builder(MainActivity.this)
                        .setTitle(R.string.dialog_title)
                        .setConfirmButton(R.string.dialog_positive_button)
                        .setOnConfirmListener(onConfirmListener)
                        .setOnDiscardListener(onDiscardListener)
                        .setContent(ContentFragment.class, argumentsBundle)
                        .build();
```
### The content
The content of the dialog must be a `Fragment` that implements the `FullScreenDialogContent` interface.

The content will be wrapped by the `FullScreenDialogFragment`. This allows to use as a dialog any `Fragment` without the need of making it inherit from `DialogFragment`.

Through the `FullScreenDialogContent` interface the content Fragment is able to receive events and control the dialog.

### Styling the dialog
It is possible to show the dialog in a default dialog window calling `setFullScreen(false)` on the `Builder`.

To style the dialog Toolbar a style called `FullScreenDialogToolbar` must be created.
```xml
<style name="FullScreenDialogToolbar">
    <item name="android:background">@color/colorPrimaryDark</item>
    <item name="android:theme">@style/ThemeOverlay.AppCompat.Dark</item>
</style>
```

### Listening to events
To be notified when the dialog is closed due to a confirmation or a dismiss action it is possible set a `OnConfirmListener` and a `OnDiscardListener`.

`FullScreenDialogContent` interface allows the content Fragment to receive the dialog click events through the `onConfirmClick` and `onDiscardClick` methods.

### Back key press handling
To notify the back key press to the dialog, what will fire the discard button click event notification to the content, the `onBackPressed` method of `FullScreenDialogFragment` must be invoked from the Activity method of the same name. This is only needed if the dialog is in fullscreen due to the Fragments lack of ability to detect back key press.
```java
@Override
public void onBackPressed() {
    if (dialogFragment != null && dialogFragment.isAdded()) {
        dialogFragment.onBackPressed();
    } else {
        super.onBackPressed();
    }
}
```
License
-------
    Copyright (C) 2017 Francisco José Montiel Navarro

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
