Github (OctoDroid fork)
=========
This application provides access to [GitHub](https://github.com/) and lets you stay connected with your network.

This is a fork of [OctoDroid](https://github.com/slapperwan/gh4a) which retains it's quality Holo-style interface, with some backported API fixes. Debranding is for personal choice.

The license status of this fork is indeterminate, I have filed a request for clarified licensing [here](https://github.com/slapperwan/gh4a/issues/242), though it appears to be Apache 2 licensed based on the comments on the top of most of the code files.


Main features
-------------

###Repository###
* List repositories
* Watch/unwatch repository
* View branches/tags
* View pull requests
* View contributors
* View watchers/networks
* View issues

###User###
* View basic information
* Activity feeds
* Follow/unfollow user
* View public/watched repositories
* View followers/following
* View organizations (if type is user)
* View members (if type is organization)

###Issue###
* List issues
* Filter by label, assignee or milestone
* Create/edit/close/reopen issue
* Comment on issue
* Manage labels
* Manage milestones

###Commit###
* View commit (shows files changed/added/deleted)
* Diff viewer with colorized HTML
* View commit history on each file

###Tree/File browser###
* Browse source code
* View code with syntax hightlighting

###Gist###
* List public gists
* View gist content

###Explore Github###
* Public timeline
* Trending repos (today, week, month, forever)
* GitHub blog

*..and many more*

How to Build Octodroid
----------------------
- Ensure Android SDK platform version 19 and build-tools version 19.1.0 are installed
- Build using Gradle

```bash
./gradlew assembleDebug
```

- To get a full list of available tasks

```bash
./gradlew tasks
```

Open Source Libraries
---------------------
* [GitHub Java API](https://github.com/maniac103/egit-github/tree/master/org.eclipse.egit.github.core)
* [ViewPagerIndicator](https://github.com/JakeWharton/Android-ViewPagerIndicator)
* [HoloColorPicker](https://github.com/LarsWerkman/HoloColorPicker)
* [ProgressFragment](https://github.com/johnkil/Android-ProgressFragment)
* [PreferenceFragment](https://github.com/kolavar/android-support-v4-preferencefragment)

Contributions
-------------
* pullrequest.svg is released under CC BY 3.0 by Richard Slater: https://thenounproject.com/term/pull-request/116189/

* [kageiit](https://github.com/kageiit) - Improvements and bug fixes
* [maniac103](https://github.com/maniac103) - Improvements, bug fixes and new features
* [ARoiD](https://github.com/ARoiD) - Testing
* [extremis (Steven Mautone)](https://github.com/extremis) - OctoDroid name and the new icon
* [zquestz](https://github.com/zquestz) - Thanks for the application icon
* [cketti](https://github.com/cketti)
