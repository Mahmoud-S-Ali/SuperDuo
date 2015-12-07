package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.data.ScoresContract;

/**
 * Created by Toty on 12/5/2015.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();
    private static final String[] MATCH_COLUMNS = {
            ScoresContract.ScoresTable._ID,
            ScoresContract.ScoresTable.HOME_COL,
            ScoresContract.ScoresTable.AWAY_COL,
            ScoresContract.ScoresTable.HOME_GOALS_COL,
            ScoresContract.ScoresTable.AWAY_GOALS_COL,
            ScoresContract.ScoresTable.TIME_COL,
    };
    // these indices must match the projection
    private static final int INDEX_ID = 0;
    private static final int INDEX_HOME = 1;
    private static final int INDEX_AWAY = 2;
    private static final int INDEX_HOME_GOALS = 3;
    private static final int INDEX_AWAY_GOALS = 4;
    private static final int INDEX_TIME = 5;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                Uri scoresUri = ScoresContract.ScoresTable.buildScoreWithDate();
                data = getContentResolver().query(scoresUri,
                        MATCH_COLUMNS,
                        ScoresContract.ScoresTable.DATE_COL + " = ? ",
                        new String[] {Utilies.getTodayDate()},
                        ScoresContract.ScoresTable.TIME_COL + " ASC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);

                // Extract the match data from the cursor
                String home = data.getString(INDEX_HOME);
                String away = data.getString(INDEX_AWAY);
                int homeGoals = data.getInt(INDEX_HOME_GOALS);
                int awayGoals = data.getInt(INDEX_AWAY_GOALS);
                String matchTime = data.getString(INDEX_TIME);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, null);
                }

                views.setImageViewResource(R.id.widget_home_icon, Utilies.getTeamLogoByTeamName(home));
                views.setImageViewResource(R.id.widget_away_icon, Utilies.getTeamLogoByTeamName(away));
                views.setImageViewResource(R.id.widget_icon, R.drawable.ic_launcher);

                views.setTextViewText(R.id.widget_home, home);
                views.setTextViewText(R.id.widget_away, away);
                views.setTextViewText(R.id.widget_score, Utilies.getScores(homeGoals, awayGoals));
                views.setTextViewText(R.id.widget_time, matchTime);

                final Intent fillInIntent = new Intent();
                fillInIntent.setData(ScoresContract.ScoresTable.buildScoreWithDate());
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_icon, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
