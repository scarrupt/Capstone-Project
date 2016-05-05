package com.codefactoring.android.backlogtracker.view.issue;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.provider.BacklogContract;
import com.codefactoring.android.backlogtracker.view.util.DateTimeUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentAdapterViewHolder> {

    private final String LOG_TAG = CommentAdapter.class.getSimpleName();

    private Cursor mCursor;

    private final Context mContext;

    private final View mEmptyView;

    public class CommentAdapterViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.text_comment_author)
        TextView authorView;
        @Bind(R.id.text_comment_description)
        TextView contentView;

        public CommentAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public CommentAdapter(Context context, View emptyView) {
        mContext = context;
        mEmptyView = emptyView;
    }

    public CommentAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewTyped) {
        if (viewGroup instanceof RecyclerView) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment, viewGroup, false);
            view.setFocusable(true);
            return new CommentAdapterViewHolder(view);
        } else {
            Log.e(LOG_TAG, "Not an instance of RecyclerView");
            throw new RuntimeException("Not bound to a RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(final CommentAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        final String author = mCursor.getString(mCursor.getColumnIndex(BacklogContract.UserEntry.NAME));
        final String createdDate = mCursor.getString(mCursor.getColumnIndex(BacklogContract.CommentEntry.CREATED));
        final String formattedDate = DateTimeUtils.convertUTCDateTimeToLocalDateTime(mContext, createdDate);
        holder.authorView.setText(mContext.getString(R.string.format_author_comment, author, formattedDate));

        final String authorThumbnailPath = mCursor.getString(mCursor.getColumnIndex(BacklogContract.UserEntry.THUMBNAIL_URL));
        Glide.with(mContext)
                .load(authorThumbnailPath)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.person_image_empty)
                .into(new SimpleTarget<Bitmap>(200, 200) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        holder.authorView.setCompoundDrawablesWithIntrinsicBounds(
                                new BitmapDrawable(holder.authorView.getResources(), resource),
                                null,
                                null,
                                null);
                        holder.authorView.setCompoundDrawablePadding(16);
                    }
                });
        holder.authorView.setContentDescription(author);

        final String content = mCursor.getString(mCursor.getColumnIndex(BacklogContract.CommentEntry.CONTENT));
        holder.contentView.setText(content);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}