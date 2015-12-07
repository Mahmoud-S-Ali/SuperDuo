package it.jaschke.alexandria;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import it.jaschke.alexandria.services.BookService;

/**
 * Created by Toty on 11/22/2015.
 */
public class Utils {
    public static String getPrefHomePage(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_startScreen_key),
                context.getString(R.string.pref_startScreen_default));
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting())
            return true;

        return false;
    }

    @SuppressWarnings("ResourceType")
    public static @BookService.AddBookStatus
    int getAddBookStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(context.getString(R.string.add_book_status_key),
                BookService.ADD_BOOK_STATUS_UNKNOWN);
    }

    static public void resetBookStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.add_book_status_key), BookService.ADD_BOOK_STATUS_UNKNOWN);
        spe.apply();
    }
}
