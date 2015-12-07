package barqsoft.footballscores;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.sync.FootballScoresSyncAdapter;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilies {
    private static final String LOG_TAG = "Utilies";

    /*public static final String BUNDESLIGA_1 = "BL1";
    public static final String BUNDESLIGA_2 = "BL2";
    public static final String BUNDESLIGA_3 = "BL3";
    public static final String PREMIER_LEGAUE = "PL";
    public static final String LEAGUE_ONE = "EL1";
    public static final String SERIE_A = "SA";
    public static final String SERIE_B = "SB";
    public static final String PRIMERA_DIVISION = "PD";
    public static final String SEGUNDA_DEVISION = "SD";
    public static final String LEAGUE_1 = "FL1";
    public static final String LEAGUE_2 = "FL2";
    public static final String EREDIVISIE = "DED";
    public static final String PRIMEIRA_LIGA = "PPL";
    public static final String SUPER_LEAGUE = "GSL";
    public static final String CHAMPIONS_LEAGUE = "CL";
    public static final String UEFA_CUP = "EL";
    public static final String EUROPEAN_CUP_OF_NATIONS = "EC";
    public static final String WORLD_CUP = "WC";*/

    public static final String BUNDESLIGA_1 = "394";
    public static final String BUNDESLIGA_2 = "395";
    public static final String BUNDESLIGA_3 = "403";
    public static final String PREMIER_LEGAUE = "398";
    public static final String LEAGUE_ONE = "EL1";
    public static final String SERIE_A = "401";
    public static final String SERIE_B = "SB";
    public static final String PRIMERA_DIVISION = "399";
    public static final String SEGUNDA_DEVISION = "400";
    public static final String LEAGUE_1 = "396";
    public static final String LEAGUE_2 = "397";
    public static final String EREDIVISIE = "404";
    public static final String PRIMEIRA_LIGA = "402";
    public static final String SUPER_LEAGUE = "GSL";
    public static final String CHAMPIONS_LEAGUE = "405";
    public static final String UEFA_CUP = "EL";
    public static final String EUROPEAN_CUP_OF_NATIONS = "EC";
    public static final String WORLD_CUP = "WC";


    public static String getLeague(Context c, String league_code) {
        switch (league_code) {
            case BUNDESLIGA_1:
            case BUNDESLIGA_2:
            case BUNDESLIGA_3:
                return c.getString(R.string.bundes_liga);
            case PREMIER_LEGAUE:
                return c.getString(R.string.premiere_league);
            case LEAGUE_ONE:
                return c.getString(R.string.league_one);
            case SERIE_A:
                return c.getString(R.string.serie_a);
            case SERIE_B:
                return c.getString(R.string.serie_b);
            case PRIMERA_DIVISION:
                return c.getString(R.string.segunda_devision);
            case SEGUNDA_DEVISION:
                return c.getString(R.string.segunda_devision);
            case LEAGUE_1:
                return c.getString(R.string.league_1);
            case LEAGUE_2:
                return c.getString(R.string.league_2);
            case EREDIVISIE:
                return c.getString(R.string.eredivisie);
            case PRIMEIRA_LIGA:
                return c.getString(R.string.primeira_liga);
            case SUPER_LEAGUE:
                return c.getString(R.string.super_league);
            case CHAMPIONS_LEAGUE:
                return c.getString(R.string.champions_league);
            case UEFA_CUP:
                return c.getString(R.string.uefa_cup);
            case EUROPEAN_CUP_OF_NATIONS:
                return c.getString(R.string.european_cup_of_nations);
            case WORLD_CUP:
                return c.getString(R.string.world_cup);
            default:
                return "Not known League, Please report";
        }
    }

    public static String getMatchDay(int match_day, String league_code) {
        if (league_code.equals(CHAMPIONS_LEAGUE)) {
            if (match_day <= 6) {
                return "Group Stages, Matchday : 6";
            } else if (match_day == 7 || match_day == 8) {
                return "First Knockout round";
            } else if (match_day == 9 || match_day == 10) {
                return "QuarterFinal";
            } else if (match_day == 11 || match_day == 12) {
                return "SemiFinal";
            } else {
                return "Final";
            }
        } else {
            return "Matchday : " + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals, int awaygoals) {
        if (home_goals < 0 || awaygoals < 0) {
            return " - ";
        } else {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamLogoByTeamName(String teamname) {
        if (teamname == null) {
            return R.drawable.no_icon;
        }
        switch (teamname) { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC":
                return R.drawable.arsenal;
            case "Manchester United FC":
                return R.drawable.manchester_united;
            case "Swansea City":
                return R.drawable.swansea_city_afc;
            case "Leicester City":
                return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC":
                return R.drawable.everton_fc_logo1;
            case "West Ham United FC":
                return R.drawable.west_ham;
            case "Tottenham Hotspur FC":
                return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion":
                return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC":
                return R.drawable.sunderland;
            case "Stoke City FC":
                return R.drawable.stoke_city;
            default:
                return R.drawable.no_icon;
        }
    }

    public static String getTodayDate() {
        Date todayDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(todayDate);
        return date;
    }

    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @SuppressWarnings("ResourceType")
    public static
    @FootballScoresSyncAdapter.ScoresStatus
    int getScoresStatus(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.scores_status_key), FootballScoresSyncAdapter.SCORES_STATUS_UNKNOWN);
    }
}
