package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter {
    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;
    public double detail_match_id = 0;

    public ScoresAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        String home = cursor.getString(COL_HOME);
        viewHolder.home_team.setText(home);
        viewHolder.home_team.setContentDescription(context.getString(R.string.a11y_home_team, home));

        String away = cursor.getString(COL_AWAY);
        viewHolder.away_team.setText(away);
        viewHolder.away_team.setContentDescription(context.getString(R.string.a11y_away_team, away));

        String matchTime = cursor.getString(COL_MATCHTIME);
        viewHolder.date.setText(matchTime);
        viewHolder.date.setContentDescription(context.getString(R.string.a11y_time, matchTime));


        int homeScore = cursor.getInt(COL_HOME_GOALS);
        int awayScore = cursor.getInt(COL_AWAY_GOALS);
        viewHolder.score.setText(Utilies.getScores(homeScore , awayScore));
        viewHolder.score.setContentDescription(context.getString(R.string.a11y_result,
                homeScore,
                awayScore));

        viewHolder.match_id = cursor.getDouble(COL_ID);

        viewHolder.home_logo.setImageResource(Utilies.getTeamLogoByTeamName(
                cursor.getString(COL_HOME)));
        viewHolder.home_logo.setContentDescription(context.getString(R.string.a11y_home_icon));

        viewHolder.away_logo.setImageResource(Utilies.getTeamLogoByTeamName(
                cursor.getString(COL_AWAY)));
        viewHolder.away_logo.setContentDescription(context.getString(R.string.a11y_away_icon));

        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detail_match_id));
        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);

        if (viewHolder.match_id == detail_match_id) {
            //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");

            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));

            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utilies.getMatchDay(cursor.getInt(COL_MATCHDAY),
                    cursor.getString(COL_LEAGUE)));

            String leagueCode = cursor.getString(COL_LEAGUE);
            String leagueName = Utilies.getLeague(context, leagueCode);
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(leagueName);
            league.setContentDescription(context.getString(R.string.a11y_league, leagueName));

            Button share_button = (Button) v.findViewById(R.id.share_button);
            share_button.setContentDescription(context.getString(R.string.a11y_share));
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(viewHolder.home_team.getText() + " "
                            + viewHolder.score.getText() + " " + viewHolder.away_team.getText() + " "));
                }
            });
        } else {
            container.removeAllViews();
        }

    }

    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }
}
