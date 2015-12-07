package barqsoft.footballscores;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ViewHolder
{
    public TextView home_team;
    public TextView away_team;
    public TextView score;
    public TextView date;
    public ImageView home_logo;
    public ImageView away_logo;
    public double match_id;
    public ViewHolder(View view)
    {
        home_team = (TextView) view.findViewById(R.id.home_name);
        away_team = (TextView) view.findViewById(R.id.away_name);
        score     = (TextView) view.findViewById(R.id.score_textview);
        date      = (TextView) view.findViewById(R.id.time_textview);
        home_logo = (ImageView) view.findViewById(R.id.home_crest);
        away_logo = (ImageView) view.findViewById(R.id.away_crest);
    }
}
