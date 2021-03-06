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
package com.gh4a.adapter;

import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.User;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gh4a.Gh4Application;
import com.gh4a.R;
import com.gh4a.utils.StringUtils;

public class GistAdapter extends RootAdapter<Gist> {
    private String mOwnerLogin;

    public GistAdapter(Context context, String owner) {
        super(context);
        mOwnerLogin = owner;
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.row_gist, parent, false);
        Gh4Application app = (Gh4Application) mContext.getApplicationContext();
        ViewHolder viewHolder = new ViewHolder();

        viewHolder.tvTitle = (TextView) v.findViewById(R.id.tv_title);
        viewHolder.tvDesc = (TextView) v.findViewById(R.id.tv_desc);
        viewHolder.tvDesc.setTypeface(app.boldCondensed);
        viewHolder.tvExtra = (TextView) v.findViewById(R.id.tv_extra);

        v.setTag(viewHolder);
        return v;
    }

    @Override
    protected void bindView(View v, Gist gist) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();

        viewHolder.tvTitle.setText(gist.getId());
        if (StringUtils.isBlank(gist.getDescription())) {
            viewHolder.tvDesc.setVisibility(View.GONE);
        } else {
            viewHolder.tvDesc.setText(gist.getDescription());
            viewHolder.tvDesc.setVisibility(View.VISIBLE);
        }

        String count = v.getResources().getQuantityString(R.plurals.file,
                gist.getFiles().size(), gist.getFiles().size());
        User user = gist.getUser();
        int extraResId = user != null && TextUtils.equals(user.getLogin(), mOwnerLogin)
                ? R.string.gist_extradata_own : R.string.gist_extradata;
        String extra = mContext.getString(extraResId,
                user != null ? user.getLogin() : mContext.getString(R.string.unknown),
                StringUtils.formatRelativeTime(mContext, gist.getCreatedAt(), false), count);
        viewHolder.tvExtra.setText(StringUtils.applyBoldTags(extra, null));
    }

    private static class ViewHolder {
        public TextView tvTitle;
        public TextView tvDesc;
        public TextView tvExtra;
    }
}
