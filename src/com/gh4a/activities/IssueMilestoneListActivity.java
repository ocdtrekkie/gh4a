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
package com.gh4a.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.gh4a.Constants;
import com.gh4a.Gh4Application;
import com.gh4a.LoadingFragmentPagerActivity;
import com.gh4a.R;
import com.gh4a.fragment.IssueMilestoneListFragment;
import com.gh4a.utils.IntentUtils;
import com.gh4a.utils.UiUtils;

public class IssueMilestoneListActivity extends LoadingFragmentPagerActivity {
    private String mRepoOwner;
    private String mRepoName;

    private static final int[] TITLES = new int[] {
        R.string.open, R.string.closed
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasErrorView()) {
            return;
        }

        mRepoOwner = getIntent().getExtras().getString(Constants.Repository.OWNER);
        mRepoName = getIntent().getExtras().getString(Constants.Repository.NAME);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.issue_manage_milestones);
        actionBar.setSubtitle(mRepoOwner + "/" + mRepoName);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected int[] getTabTitleResIds() {
        return TITLES;
    }

    @Override
    protected Fragment getFragment(int position) {
        return IssueMilestoneListFragment.newInstance(mRepoOwner, mRepoName,
                position == 1 ? Constants.Issue.STATE_CLOSED : Constants.Issue.STATE_OPEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Gh4Application.get(this).isAuthorized()) {
            MenuItem createItem = menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, R.string.issue_milestone_new)
                    .setIcon(UiUtils.resolveDrawable(this, R.attr.newIcon));
            MenuItemCompat.setShowAsAction(createItem, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected Intent navigateUp() {
        return IntentUtils.getIssueListActivityIntent(this,
                mRepoOwner, mRepoName, Constants.Issue.STATE_OPEN);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case Menu.FIRST:
            Intent intent = new Intent(this, IssueMilestoneEditActivity.class);
            intent.putExtra(Constants.Repository.OWNER, mRepoOwner);
            intent.putExtra(Constants.Repository.NAME, mRepoName);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}