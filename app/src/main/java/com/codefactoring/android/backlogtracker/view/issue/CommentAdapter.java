package com.codefactoring.android.backlogtracker.view.issue;

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
import com.codefactoring.android.backlogtracker.provider.BacklogContract;

public class CommentAdapter extends CursorAdapter {

    public CommentAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        final String author = cursor.getString(cursor.getColumnIndex(BacklogContract.UserEntry.NAME));
        final String createdDate = cursor.getString(cursor.getColumnIndex(BacklogContract.CommentEntry.CREATED));
        viewHolder.authorView.setText(context.getString(R.string.format_author, "commented", author, createdDate));

        final String authorThumbnailPath = cursor.getString(cursor.getColumnIndex(BacklogContract.UserEntry.THUMBNAIL_URL));
        Glide.with(view.getContext())
                .load(authorThumbnailPath)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.person_image_empty)
                .into(new SimpleTarget<Bitmap>(200, 200) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        viewHolder.authorView.setCompoundDrawablesWithIntrinsicBounds(
                                new BitmapDrawable(viewHolder.authorView.getResources(), resource),
                                null,
                                null,
                                null);
                        viewHolder.authorView.setCompoundDrawablePadding(16);
                    }
                });
        viewHolder.authorView.setContentDescription(author);

        final String content = cursor.getString(cursor.getColumnIndex(BacklogContract.CommentEntry.CONTENT));
        viewHolder.contentView.setText(content);
    }

    private static final class ViewHolder {
        public final TextView authorView;
        public final TextView contentView;

        public ViewHolder(View view) {
            authorView = (TextView) view.findViewById(R.id.text_comment_author);
            contentView = (TextView) view.findViewById(R.id.text_comment_description);
        }
    }
}
