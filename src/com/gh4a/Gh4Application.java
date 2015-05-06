/*
 * Copyright 2011 Azwan Adli Abdullah
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gh4a;

import java.util.HashMap;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.DownloadService;
import org.eclipse.egit.github.core.service.EventService;
import org.eclipse.egit.github.core.service.GistService;
import org.eclipse.egit.github.core.service.GitHubService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.MarkdownService;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.StarService;
import org.eclipse.egit.github.core.service.UserService;
import org.eclipse.egit.github.core.service.WatcherService;
import org.ocpsoft.prettytime.PrettyTime;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.graphics.Typeface;

import com.crashlytics.android.Crashlytics;
import com.gh4a.fragment.SettingsFragment;

/**
 * The Class Gh4Application.
 */
public class Gh4Application extends Application implements OnSharedPreferenceChangeListener {

    public Typeface boldCondensed;
    public Typeface condensed;
    public Typeface regular;
    public Typeface italic;
    public static int THEME = R.style.DefaultTheme;

    public static String STAR_SERVICE = "github.star";
    public static String WATCHER_SERVICE = "github.watcher";
    public static String LABEL_SERVICE = "github.label";
    public static String ISSUE_SERVICE = "github.issue";
    public static String COMMIT_SERVICE = "github.commit";
    public static String REPO_SERVICE = "github.repository";
    public static String USER_SERVICE = "github.user";
    public static String MILESTONE_SERVICE = "github.milestone";
    public static String COLLAB_SERVICE = "github.collaborator";
    public static String DOWNLOAD_SERVICE = "github.download";
    public static String CONTENTS_SERVICE = "github.contents";
    public static String GIST_SERVICE = "github.gist";
    public static String ORG_SERVICE = "github.organization";
    public static String PULL_SERVICE = "github.pullrequest";
    public static String EVENT_SERVICE = "github.event";
    public static String MARKDOWN_SERVICE = "github.markdown";

    private static Gh4Application sInstance;
    private GitHubClient mClient;
    private HashMap<String, GitHubService> mServices;
    private PrettyTime mPt;

    private static final int MAX_TRACKED_URLS = 5;
    private static int sNextUrlTrackingPosition = 0;

    /*
     * (non-Javadoc)
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        boldCondensed = Typeface.createFromAsset(getAssets(), "fonts/Roboto-BoldCondensed.ttf");
        condensed = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Condensed.ttf");
        regular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        italic = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Italic.ttf");

        SharedPreferences prefs = getPrefs();
        selectTheme(prefs.getInt(SettingsFragment.KEY_THEME, Constants.Theme.DARK));
        prefs.registerOnSharedPreferenceChangeListener(this);

        Crashlytics.start(this);

        mPt = new PrettyTime();

        mClient = new DefaultClient();
        mClient.setOAuth2Token(getAuthToken());

        mServices = new HashMap<String, GitHubService>();
        mServices.put(STAR_SERVICE, new StarService(mClient));
        mServices.put(WATCHER_SERVICE, new WatcherService(mClient));
        mServices.put(LABEL_SERVICE, new LabelService(mClient));
        mServices.put(ISSUE_SERVICE, new IssueService(mClient));
        mServices.put(COMMIT_SERVICE, new CommitService(mClient));
        mServices.put(REPO_SERVICE, new RepositoryService(mClient));
        mServices.put(USER_SERVICE, new UserService(mClient));
        mServices.put(MILESTONE_SERVICE, new MilestoneService(mClient));
        mServices.put(COLLAB_SERVICE, new CollaboratorService(mClient));
        mServices.put(DOWNLOAD_SERVICE, new DownloadService(mClient));
        mServices.put(CONTENTS_SERVICE, new ContentsService(mClient));
        mServices.put(GIST_SERVICE, new GistService(mClient));
        mServices.put(ORG_SERVICE, new OrganizationService(mClient));
        mServices.put(PULL_SERVICE, new PullRequestService(mClient));
        mServices.put(EVENT_SERVICE, new EventService(mClient));
        mServices.put(MARKDOWN_SERVICE, new MarkdownService(mClient));
    }

    public GitHubService getService(String name) {
        return mServices.get(name);
    }

    private void selectTheme(int theme) {
        switch (theme) {
            case Constants.Theme.DARK:
                THEME = R.style.DefaultTheme;
                break;
            case Constants.Theme.LIGHT:
                THEME = R.style.LightTheme;
                break;
            case Constants.Theme.LIGHTDARK:
                THEME = R.style.LightDarkTheme;
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPt = new PrettyTime(newConfig.locale);
    }

    /* package */ static void trackVisitedUrl(String url) {
        Crashlytics.setString("github-url-" + sNextUrlTrackingPosition, url);
        Crashlytics.setInt("last-url-position", sNextUrlTrackingPosition);
        if (++sNextUrlTrackingPosition >= MAX_TRACKED_URLS) {
            sNextUrlTrackingPosition = 0;
        }
    }

    public PrettyTime getPrettyTimeInstance() {
        return mPt;
    }

    public String getAuthLogin() {
        return getPrefs().getString(Constants.User.LOGIN, null);
    }

    public String getAuthToken() {
        return getPrefs().getString(Constants.User.AUTH_TOKEN, null);
    }

    public void logout() {
        getPrefs().edit()
                .remove(Constants.User.LOGIN)
                .remove(Constants.User.AUTH_TOKEN)
                .apply();
    }

    private SharedPreferences getPrefs() {
        return getSharedPreferences(SettingsFragment.PREF_NAME, MODE_PRIVATE);
    }

    public static Gh4Application get(Context context) {
        return (Gh4Application) context.getApplicationContext();
    }

    public static Gh4Application get() {
        return sInstance;
    }

    public boolean isAuthorized() {
        return getAuthLogin() != null && getAuthToken() != null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constants.User.AUTH_TOKEN)) {
            mClient.setOAuth2Token(getAuthToken());
        } else if (key.equals(SettingsFragment.KEY_THEME)) {
            selectTheme(sharedPreferences.getInt(key, Constants.Theme.DARK));
        }
    }
}
