package com.codefactoring.android.backlogtracker.view.project;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codefactoring.android.backlogtracker.R;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.ProjectEntry;

public class ProjectAdapter extends CursorAdapter {

    public ProjectAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_project, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        final String projectName = cursor.getString(cursor.getColumnIndex(ProjectEntry.NAME));
        viewHolder.projectNameView.setText(projectName);

        final String thumbnailPath = cursor.getString(cursor.getColumnIndex(ProjectEntry.THUMBNAIL_URL));
        Glide.with(view.getContext())
                .load(thumbnailPath)
                .asBitmap()
                .centerCrop()
                .into(new SimpleTarget<Bitmap>(100, 100) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        viewHolder.projectNameView.setCompoundDrawablesWithIntrinsicBounds(
                                new BitmapDrawable(viewHolder.projectNameView.getResources(), resource),
                                null,
                                null,
                                null);
                        viewHolder.projectNameView.setCompoundDrawablePadding(16);
                    }
                });
    }

    private static final class ViewHolder {
        public final TextView projectNameView;

        public ViewHolder(View view) {
            projectNameView = (TextView) view.findViewById(R.id.text_project_name);
        }
    }
}