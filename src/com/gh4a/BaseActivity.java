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

import java.lang.ref.WeakReference;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gh4a.holder.BreadCrumbHolder;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ScrollingTextView;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.ocpsoft.pretty.time.PrettyTime;

/**
 * The Base activity.
 */
public class BaseActivity extends Activity {

    /** The Constant pt. */
    protected static final PrettyTime pt = new PrettyTime();

    /* (non-Javadoc)
     * @see android.content.ContextWrapper#getApplicationContext()
     */
    @Override
    public Gh4Application getApplicationContext() {
        return (Gh4Application) super.getApplicationContext();
    }

    /**
     * Common function when device search button pressed, then open
     * SearchActivity.
     *
     * @return true, if successful
     */
    @Override
    public boolean onSearchRequested() {
        Intent intent = new Intent().setClass(getApplication(), SearchActivity.class);
        startActivity(intent);
        return true;
    }

    /**
     * Hide keyboard.
     *
     * @param binder the binder
     */
    public void hideKeyboard(IBinder binder) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Creates the breadcrumb.
     *
     * @param subTitle the sub title
     * @param breadCrumbHolders the bread crumb holders
     */
    public void createBreadcrumb(String subTitle, BreadCrumbHolder... breadCrumbHolders) {
        LinearLayout llPart = (LinearLayout) this.findViewById(R.id.ll_part);

        for (int i = 0; i < breadCrumbHolders.length; i++) {
            TextView tvBreadCrumb = new TextView(getApplication());
            SpannableString part = new SpannableString(breadCrumbHolders[i].getLabel());
            part.setSpan(new UnderlineSpan(), 0, part.length(), 0);
            tvBreadCrumb.append(part);
            tvBreadCrumb.setTag(breadCrumbHolders[i]);
            tvBreadCrumb.setBackgroundResource(R.drawable.default_link);
            tvBreadCrumb.setTextAppearance(getApplication(), R.style.default_text_small);
            tvBreadCrumb.setSingleLine(true);
            tvBreadCrumb.setOnClickListener(new OnClickBreadCrumb(this));

            llPart.addView(tvBreadCrumb);

            if (i < breadCrumbHolders.length - 1) {
                TextView slash = new TextView(getApplication());
                slash.setText(" / ");
                slash.setTextAppearance(getApplication(), R.style.default_text_small);
                llPart.addView(slash);
            }
        }

        ScrollingTextView tvSubtitle = (ScrollingTextView) this.findViewById(R.id.tv_subtitle);
        tvSubtitle.setText(subTitle);
    }

    /**
     * Sets the up action bar.
     */
    public void setUpActionBar() {
        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setHomeAction(new IntentAction(this, new Intent(getApplicationContext(),
                DashboardActivity.class), R.drawable.ic_home));
        actionBar.addAction(new IntentAction(this, new Intent(getApplication(),
                SearchActivity.class), R.drawable.ic_search));
    }

    /**
     * The Class OnClickBreadCrumb.
     */
    private static class OnClickBreadCrumb implements OnClickListener {

        /** The target. */
        private WeakReference<BaseActivity> mTarget;

        /**
         * Instantiates a new on click bread crumb.
         *
         * @param activity the activity
         */
        public OnClickBreadCrumb(BaseActivity activity) {
            mTarget = new WeakReference<BaseActivity>(activity);
        }

        /* (non-Javadoc)
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View view) {
            TextView breadCrumb = (TextView) view;
            BreadCrumbHolder b = (BreadCrumbHolder) breadCrumb.getTag();
            String tag = b.getTag();
            HashMap<String, String> data = b.getData();

            BaseActivity baseActivity = mTarget.get();

            if (Constants.User.USER_LOGIN.equals(tag)) {
                mTarget.get().getApplicationContext().openUserInfoActivity(baseActivity,
                        data.get(Constants.User.USER_LOGIN), null);
            }
            else if (Constants.Repository.REPO_NAME.equals(tag)) {
                mTarget.get().getApplicationContext().openRepositoryInfoActivity(baseActivity,
                        data.get(Constants.User.USER_LOGIN),
                        data.get(Constants.Repository.REPO_NAME));
            }
            else if (Constants.Issue.ISSUES.equals(tag)) {
                mTarget.get().getApplicationContext().openIssueListActivity(baseActivity,
                        data.get(Constants.User.USER_LOGIN),
                        data.get(Constants.Repository.REPO_NAME), Constants.Issue.ISSUE_STATE_OPEN);// TODO
                                                                                                    // remove
                                                                                                    // hardcoded
            }
            else if (Constants.Commit.COMMITS.equals(tag)) {
                mTarget.get().getApplicationContext().openBranchListActivity(baseActivity,
                        data.get(Constants.User.USER_LOGIN),
                        data.get(Constants.Repository.REPO_NAME), R.id.btn_commits);
            }
            else if (Constants.PullRequest.PULL_REQUESTS.equals(tag)) {
                mTarget.get().getApplicationContext().openPullRequestListActivity(baseActivity,
                        data.get(Constants.User.USER_LOGIN),
                        data.get(Constants.Repository.REPO_NAME));
            }
            else if (Constants.Object.TREE.equals(tag)) {
                mTarget.get().getApplicationContext().openBranchListActivity(baseActivity,
                        data.get(Constants.User.USER_LOGIN),
                        data.get(Constants.Repository.REPO_NAME), R.id.btn_branches);
            }
            else if (Constants.Commit.COMMIT.equals(tag)) {
                mTarget.get().getApplicationContext().openCommitInfoActivity(baseActivity,
                        data.get(Constants.User.USER_LOGIN),
                        data.get(Constants.Repository.REPO_NAME),
                        data.get(Constants.Object.OBJECT_SHA));
            }
            else if (Constants.Repository.REPO_BRANCH.equals(tag)) {
                Intent intent = new Intent().setClass(mTarget.get(), FileManagerActivity.class);
                if (mTarget.get() instanceof CommitListActivity) {
                    intent = new Intent().setClass(mTarget.get(), CommitListActivity.class);
                }
                intent.putExtra(Constants.Repository.REPO_OWNER, data
                        .get(Constants.User.USER_LOGIN));
                intent.putExtra(Constants.Repository.REPO_NAME, data
                        .get(Constants.Repository.REPO_NAME));
                intent.putExtra(Constants.Object.TREE_SHA, data.get(Constants.Object.TREE_SHA));
                intent.putExtra(Constants.Object.OBJECT_SHA, data.get(Constants.Object.TREE_SHA));
                intent.putExtra(Constants.Object.PATH, "Tree");
                intent.putExtra(Constants.Repository.REPO_BRANCH, data
                        .get(Constants.Repository.REPO_BRANCH));
                intent.putExtra(Constants.VIEW_ID, Integer.parseInt(data.get(Constants.VIEW_ID)));
                mTarget.get().startActivity(intent);
            }
            else if (Constants.Object.BRANCHES.equals(tag)) {
                mTarget.get().getApplicationContext().openBranchListActivity(mTarget.get(),
                        data.get(Constants.User.USER_LOGIN),
                        data.get(Constants.Repository.REPO_NAME), R.id.btn_branches);
            }
            else if (Constants.Object.TAGS.equals(tag)) {
                mTarget.get().getApplicationContext().openTagListActivity(mTarget.get(),
                        data.get(Constants.User.USER_LOGIN),
                        data.get(Constants.Repository.REPO_NAME), R.id.btn_branches);
            }
        }
    };

    /**
     * Show error.
     */
    public void showError() {
        Toast
                .makeText(getApplication(), "An error occured while fetching data",
                        Toast.LENGTH_SHORT).show();
        super.finish();
    }

    /**
     * Show error.
     *
     * @param finishThisActivity the finish this activity
     */
    public void showError(boolean finishThisActivity) {
        Toast
                .makeText(getApplication(), "An error occured while fetching data",
                        Toast.LENGTH_SHORT).show();
        if (finishThisActivity) {
            super.finish();
        }
    }
}