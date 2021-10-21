/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.android_n.egg.neko;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android_n.egg.R;
import com.android_n.egg.neko.PrefState.PrefsListener;
import com.dede.basic.ShareCatUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//import com.android.internal.logging.MetricsLogger;

@RequiresApi(Build.VERSION_CODES.N)
public class NekoLand extends Activity implements PrefsListener {
    public static boolean DEBUG = false;
    public static boolean DEBUG_NOTIFICATIONS = false;

    private static final int EXPORT_BITMAP_SIZE = 600;

    private static final int STORAGE_PERM_REQUEST = 123;

    private static boolean CAT_GEN = false;
    private PrefState mPrefs;
    private CatAdapter mAdapter;
    private Cat mPendingShareCat;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.neko_activity);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setLogo(Cat.create(this));
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mPrefs = new PrefState(this);
        mPrefs.setListener(this);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.holder);
        mAdapter = new CatAdapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        int numCats = updateCats();
//        MetricsLogger.histogram(this, "egg_neko_visit_gallery", numCats);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPrefs.setListener(null);
    }

    private int updateCats() {
        Cat[] cats;
        if (CAT_GEN) {
            cats = new Cat[50];
            for (int i = 0; i < cats.length; i++) {
                cats[i] = Cat.create(this);
            }
        } else {
            final float[] hsv = new float[3];
            List<Cat> list = mPrefs.getCats();
            Collections.sort(list, new Comparator<Cat>() {
                @Override
                public int compare(Cat cat, Cat cat2) {
                    Color.colorToHSV(cat.getBodyColor(), hsv);
                    float bodyH1 = hsv[0];
                    Color.colorToHSV(cat2.getBodyColor(), hsv);
                    float bodyH2 = hsv[0];
                    return Float.compare(bodyH1, bodyH2);
                }
            });
            cats = list.toArray(new Cat[0]);
        }
        mAdapter.setCats(cats);
        return cats.length;
    }

    private void onCatClick(Cat cat) {
        if (CAT_GEN) {
            mPrefs.addCat(cat);
            new AlertDialog.Builder(NekoLand.this)
                    .setTitle("Cat added")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        } else {
            showNameDialog(cat);
        }
//      noman.notify(1, cat.buildNotification(NekoLand.this).build());
    }

    private void onCatRemove(Cat cat) {
        cat.logRemove(this);
        mPrefs.removeCat(cat);
    }

    private void showNameDialog(final Cat cat) {
        final Context context = new ContextThemeWrapper(this,
                android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        // TODO: Move to XML, add correct margins.
        View view = LayoutInflater.from(context).inflate(R.layout.edit_text, null);
        final EditText text = (EditText) view.findViewById(android.R.id.edit);
        text.setText(cat.getName());
        text.setSelection(cat.getName().length());
        final int size = context.getResources()
                .getDimensionPixelSize(android.R.dimen.app_icon_size);
        Drawable catIcon = cat.createIcon(this, size, size).loadDrawable(this);
        new AlertDialog.Builder(context)
                .setTitle(" ")
                .setIcon(catIcon)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cat.logRename(context);
                        cat.setName(text.getText().toString().trim());
                        mPrefs.addCat(cat);
                    }
                }).show();
    }

    @Override
    public void onPrefsChanged() {
        updateCats();
    }

    private class CatAdapter extends RecyclerView.Adapter<CatHolder> {

        private Cat[] mCats;

        public void setCats(Cat[] cats) {
            mCats = cats;
            notifyDataSetChanged();
        }

        @Override
        public CatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CatHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cat_view, parent, false));
        }

        private void setContextGroupVisible(final CatHolder holder, boolean vis) {
            final View group = holder.contextGroup;
            if (vis && group.getVisibility() != View.VISIBLE) {
                group.setAlpha(0);
                group.setVisibility(View.VISIBLE);
                group.animate().alpha(1.0f).setDuration(333);
                Runnable hideAction = new Runnable() {
                    @Override
                    public void run() {
                        setContextGroupVisible(holder, false);
                    }
                };
                group.setTag(hideAction);
                group.postDelayed(hideAction, 5000);
            } else if (!vis && group.getVisibility() == View.VISIBLE) {
                group.removeCallbacks((Runnable) group.getTag());
                group.animate().alpha(0f).setDuration(250).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        group.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }

        @Override
        public void onBindViewHolder(final CatHolder holder, int position) {
            Context context = holder.itemView.getContext();
            final int size = context.getResources().getDimensionPixelSize(R.dimen.neko_display_size);
            holder.imageView.setImageIcon(mCats[position].createIcon(context, size, size));
            holder.textView.setText(mCats[position].getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCatClick(mCats[holder.getAdapterPosition()]);
                }
            });
            holder.itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setContextGroupVisible(holder, true);
                    return true;
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContextGroupVisible(holder, false);
                    new AlertDialog.Builder(NekoLand.this)
                            .setTitle(getString(R.string.confirm_delete, mCats[position].getName()))
                            .setNegativeButton(android.R.string.cancel, null)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onCatRemove(mCats[holder.getAdapterPosition()]);
                                }
                            })
                            .show();
                }
            });
            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContextGroupVisible(holder, false);
                    Cat cat = mCats[holder.getAdapterPosition()];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        shareCat(cat);
                        return;
                    }
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        mPendingShareCat = cat;
                        requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                STORAGE_PERM_REQUEST);
                        return;
                    }
                    shareCat(cat);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCats.length;
        }
    }

    private void shareCat(Cat cat) {
        Bitmap bitmap = cat.createBitmap(EXPORT_BITMAP_SIZE, EXPORT_BITMAP_SIZE);
        if (bitmap != null) {
            ShareCatUtils.share(this, bitmap, cat.getName());
            cat.logShare(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == STORAGE_PERM_REQUEST) {
            if (mPendingShareCat != null) {
                shareCat(mPendingShareCat);
                mPendingShareCat = null;
            }
        }
    }

    private static class CatHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;
        private final View contextGroup;
        private final View delete;
        private final View share;

        public CatHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.icon);
            textView = (TextView) itemView.findViewById(R.id.title);
            contextGroup = itemView.findViewById(R.id.contextGroup);
            delete = itemView.findViewById(R.id.closeButton);
            share = itemView.findViewById(R.id.shareText);
        }
    }
}
