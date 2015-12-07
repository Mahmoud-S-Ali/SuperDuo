package barqsoft.footballscores;

import android.database.Cursor;
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
import android.widget.TextView;

import barqsoft.footballscores.data.ScoresContract;
import barqsoft.footballscores.sync.FootballScoresSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public ScoresAdapter mScoresAdapter;
    public static final int SCORES_LOADER = 0;
    private String mFragmentDate;
    private ListView mScoreList;

    private int mPosition = ListView.INVALID_POSITION;
    private static final String POSITION_KEY = "POSITION KEY";

    public MainScreenFragment() {
    }

    private void update_scores() {
        /*Intent service_start = new Intent(getActivity(), myFetchService.class);
        getActivity().startService(service_start);*/
        FootballScoresSyncAdapter.syncImmediately(getActivity());
    }

    public void setFragmentDate(String date) {
        mFragmentDate = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(POSITION_KEY);
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        update_scores();

        mScoreList = (ListView) rootView.findViewById(R.id.scores_list);
        mScoresAdapter = new ScoresAdapter(getActivity(), null, 0);
        mScoreList.setAdapter(mScoresAdapter);
        mScoreList.setEmptyView(rootView.findViewById(R.id.emptyview));

        getLoaderManager().initLoader(SCORES_LOADER, null, this);

        mScoresAdapter.detail_match_id = MainActivity.mSelectedMatchId;
        mScoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder selectedItem = (ViewHolder) view.getTag();
                mScoresAdapter.detail_match_id = selectedItem.match_id;
                MainActivity.mSelectedMatchId = (int) selectedItem.match_id;
                mScoresAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(POSITION_KEY, mPosition);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] selectionArgs = {mFragmentDate};
        return new CursorLoader(getActivity(), ScoresContract.ScoresTable.buildScoreWithDate(),
                null, null, selectionArgs, ScoresContract.ScoresTable.TIME_COL + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        updateEmptyView();

        if (mPosition != ListView.INVALID_POSITION) {
            mScoreList.smoothScrollToPosition(mPosition);
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            cursor.moveToNext();
        }

        mScoresAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mScoresAdapter.swapCursor(null);
    }

    private void updateEmptyView() {
        if (mScoresAdapter.getCount() == 0) {
            TextView tv = (TextView) getView().findViewById(R.id.emptyview);
            if (null != tv) {

                int message = R.string.empty_scores_list;
                @FootballScoresSyncAdapter.ScoresStatus int scores = Utilies.getScoresStatus(getActivity());
                switch (scores) {
                    case FootballScoresSyncAdapter.SCORES_STATUS_SERVER_DOWN:
                        message = R.string.empty_scores_list_server_down;
                        break;
                    case FootballScoresSyncAdapter.SCORES_STATUS_SERVER_INVALID:
                        message = R.string.empty_scores_list_server_error;
                        break;
                    default:
                        if (!Utilies.isNetworkAvailable(getActivity())) {
                            message = R.string.empty_scores_list_no_network;
                        }
                }
                tv.setText(message);
            }

        }
    }
}
