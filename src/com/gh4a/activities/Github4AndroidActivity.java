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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.eclipse.egit.github.core.Authorization;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.OAuthService;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gh4a.ClientForAuthorization;
import com.gh4a.Constants;
import com.gh4a.Gh4Application;
import com.gh4a.ProgressDialogTask;
import com.gh4a.R;
import com.gh4a.TwoFactorAuthException;
import com.gh4a.fragment.SettingsFragment;
import com.gh4a.utils.IntentUtils;
import com.gh4a.utils.StringUtils;
import com.gh4a.utils.UiUtils;

/**
 * The Github4Android activity.
 */
public class Github4AndroidActivity extends BaseFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Gh4Application app = Gh4Application.get(this);
        if (app.isAuthorized()) {
            Intent intent = IntentUtils.getUserActivityIntent(this, app.getAuthLogin());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.anon_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.login) {
            doLogin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doLogin() {
        EditText loginView = (EditText) findViewById(R.id.et_username_main);
        EditText passwordView = (EditText) findViewById(R.id.et_password_main);

        UiUtils.hideImeForView(loginView);
        UiUtils.hideImeForView(passwordView);

        String username = loginView.getText().toString();
        String password = passwordView.getText().toString();

        if (!StringUtils.checkEmail(username)) {
            new LoginTask(username, password).execute();
        } else {
            Toast.makeText(Github4AndroidActivity.this,
                    getString(R.string.enter_username_toast), Toast.LENGTH_LONG).show();
        }
    }

    private class LoginTask extends ProgressDialogTask<Authorization> {
        private String mUserName;
        private String mPassword;
        private String mOtpCode;

        /**
         * Instantiates a new load repository list task.
         */
        public LoginTask(String userName, String password) {
            super(Github4AndroidActivity.this, R.string.please_wait, R.string.authenticating);
            mUserName = userName;
            mPassword = password;
        }
        
        public LoginTask(String userName, String password, String otpCode) {
            super(Github4AndroidActivity.this, R.string.please_wait, R.string.authenticating);
            mUserName = userName;
            mPassword = password;
            mOtpCode = otpCode;
        }

        public LoginTask(Context context, int resWaitId, int resWaitMsg) {
            super(context, resWaitId, resWaitMsg);
        }

        @Override
        protected Authorization run() throws IOException {
            GitHubClient client = new ClientForAuthorization(mOtpCode);
            client.setCredentials(mUserName, mPassword);
            client.setUserAgent("Octodroid");

            String description = "Octodroid - " + Build.MANUFACTURER + " " + Build.MODEL;
            String fingerprint = getHashedDeviceId();
            int index = 1;

            OAuthService authService = new OAuthService(client);
            for (Authorization authorization : authService.getAuthorizations()) {
                String note = authorization.getNote();
                if ("Gh4a".equals(note)) {
                        authService.deleteAuthorization(authorization.getId());
                    } else if (note != null && note.startsWith("Octodroid")) {
                          if (fingerprint.equals(authorization.getFingerprint())) {
                                authService.deleteAuthorization(authorization.getId());
                                } else if (note.startsWith(description)) {
                                    index++;
                                }
                }
            }

            if (index > 1) {
                description += " #" + index;
            }

            Authorization auth = new Authorization();
                auth.setNote(description);
                auth.setUrl("http://github.com/slapperwan/gh4a");
                auth.setFingerprint(fingerprint);
                auth.setScopes(Arrays.asList("user", "repo", "gist"));

                return authService.createAuthorization(auth);
        }

        @Override
        protected void onError(Exception e) {
            if (e instanceof TwoFactorAuthException) {
                if ("sms".equals(((TwoFactorAuthException) e).getTwoFactorAuthType())) {
                    new DummyPostTask(mUserName, mPassword).execute();
                } else {
                    open2FADialog(mUserName, mPassword);
                }
            } else {
                Toast.makeText(Github4AndroidActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onSuccess(Authorization result) {
            SharedPreferences sharedPreferences = getSharedPreferences(
                    SettingsFragment.PREF_NAME, MODE_PRIVATE);
            Editor editor = sharedPreferences.edit();
            editor.putString(Constants.User.AUTH_TOKEN, result.getToken());
            editor.putString(Constants.User.LOGIN, mUserName);
            editor.apply();

            Intent intent = IntentUtils.getUserActivityIntent(mContext, mUserName);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            finish();
        }

        private String getHashedDeviceId() {
            String androidId = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            if (androidId == null) {
                // shouldn't happen, do a lame fallback in that case
                androidId = Build.FINGERPRINT;
            }

            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                byte[] result = digest.digest(androidId.getBytes("UTF-8"));
                StringBuilder sb = new StringBuilder();
                for (byte b : result) {
                    sb.append(String.format(Locale.US, "%02X", b));
                }
                return sb.toString();
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            }

            return androidId;
        }
    }

    // POST request so that GitHub trigger the SMS for OTP
    private class DummyPostTask extends LoginTask {
        private String mUserName;
        private String mPassword;

        public DummyPostTask(String userName, String password) {
            super(Github4AndroidActivity.this, R.string.please_wait, R.string.authenticating);
            mUserName = userName;
            mPassword = password;
        }

        @Override
        protected Authorization run() throws IOException {
            GitHubClient client = new ClientForAuthorization(null);
            client.setCredentials(mUserName, mPassword);
            client.setUserAgent("Octodroid");

            Authorization auth = new Authorization();
            auth.setNote("Gh4a login dummy");

            OAuthService authService = new OAuthService(client);
            return authService.createAuthorization(auth);
        }

        @Override
        protected void onError(Exception e) {
            if (e instanceof TwoFactorAuthException) {
                open2FADialog(mUserName, mPassword);
            } else {
                Toast.makeText(Github4AndroidActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void open2FADialog(final String username, final String password) {
        LayoutInflater inflater = LayoutInflater.from(Github4AndroidActivity.this);
        View authDialog = inflater.inflate(R.layout.twofactor_auth_dialog, null);
        final EditText authCode = (EditText) authDialog.findViewById(R.id.auth_code);

        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.two_factor_auth)
                .setView(authDialog)
                .setPositiveButton(R.string.verify, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new LoginTask(username, password, authCode.getText().toString()).execute();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}