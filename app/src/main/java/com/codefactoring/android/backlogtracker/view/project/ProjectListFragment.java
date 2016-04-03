package com.codefactoring.android.backlogtracker.view.project;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.provider.BacklogContract;

import static com.codefactoring.android.backlogtracker.provider.BacklogContract.ProjectEntry;

public class ProjectListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PROJECT_LIST_LOADER = 0;

    private OnFragmentInteractionListener mListener;

    private ProjectAdapter mProjectAdapter;

    private static final String[] PROJECT_COLUMNS = {
            ProjectEntry._ID,
            ProjectEntry.NAME,
            ProjectEntry.PROJECT_KEY,
            ProjectEntry.THUMBNAIL_URL
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PROJECT_LIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mProjectAdapter = new ProjectAdapter(getActivity(), null, 0);

        final View rootView = inflater.inflate(R.layout.fragment_project_list, container, false);

        final ListView listView = (ListView) rootView.findViewById(R.id.listview_project);
        listView.setAdapter(mProjectAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null && mListener != null) {
                    mListener.onFragmentInteraction(BacklogContract.IssuePreviewEntry
                            .buildIssuePreviewsWithProjectId(cursor.getLong(
                                    cursor.getColumnIndex(ProjectEntry._ID))));
                }
            }
        });


        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                ProjectEntry.CONTENT_URI,
                PROJECT_COLUMNS,
                null,
                null,
                ProjectEntry.DEFAULT_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mProjectAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProjectAdapter.swapCursor(null);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}